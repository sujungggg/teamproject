package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.digitaldetoxapp.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class CreatePostActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSubmitPost;
    private Button buttonBackToUserPosts;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // UI 요소 초기화
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSubmitPost = findViewById(R.id.buttonSubmitPost);
        buttonBackToUserPosts = findViewById(R.id.buttonBackToUserPosts);

        // Firebase 인스턴스 초기화
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // 글 작성 버튼
        buttonSubmitPost.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String content = editTextContent.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(CreatePostActivity.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (content.isEmpty()) {
                Toast.makeText(CreatePostActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            savePostToFirestore(title, content);
        });

        // 사용자 게시글로 돌아가는 버튼
        buttonBackToUserPosts.setOnClickListener(v -> {
            Intent intent = new Intent(CreatePostActivity.this, UserPostActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void savePostToFirestore(String title, String content) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(CreatePostActivity.this, "로그인 후 글을 작성해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String userEmail = user.getEmail();
        Date timestamp = new Date();  // 현재 시간

        // Post 객체에 이메일 포함
        Post post = new Post(null, userId, title, content, timestamp, userEmail);

        db.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CreatePostActivity.this, "글이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();

                    // UserPostActivity로 돌아가면서 새로고침
                    Intent intent = new Intent(CreatePostActivity.this, UserPostActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreatePostActivity.this, "글 등록에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                });
    }
}
