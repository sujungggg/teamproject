package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class PostEditActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSaveChanges;
    private Button buttonBackToPostDetail; // 뒤로가기 버튼 추가

    private FirebaseFirestore db;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonBackToPostDetail = findViewById(R.id.buttonBackToPostDetail); // 버튼 초기화

        db = FirebaseFirestore.getInstance();

        postId = getIntent().getStringExtra("postId");

        if (postId != null) {
            loadPostDetails(postId);
        }

        buttonSaveChanges.setOnClickListener(v -> saveChanges());

        // 뒤로가기 버튼 클릭 시 PostDetailActivity로 이동
        buttonBackToPostDetail.setOnClickListener(v -> navigateBackToPostDetail());
    }

    private void loadPostDetails(String postId) {
        db.collection("posts").document(postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editTextTitle.setText(documentSnapshot.getString("title"));
                        editTextContent.setText(documentSnapshot.getString("content"));
                    }
                });
    }

    private void saveChanges() {
        String updatedTitle = editTextTitle.getText().toString().trim();
        String updatedContent = editTextContent.getText().toString().trim();

        if (updatedTitle.isEmpty() || updatedContent.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("posts").document(postId)
                .update("title", updatedTitle, "content", updatedContent)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PostEditActivity.this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    navigateBackToPostDetail();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PostEditActivity.this, "수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateBackToPostDetail() {
        Intent intent = new Intent(PostEditActivity.this, PostDetailActivity.class);
        intent.putExtra("postId", postId); // 게시글 ID 전달
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }
}
