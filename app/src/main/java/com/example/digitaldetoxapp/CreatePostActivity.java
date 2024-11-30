package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.digitaldetoxapp.model.Post;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class CreatePostActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSubmitPost;
    private Button buttonBackToUserPosts;

    private FirebaseFirestore db;

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

        // 글 작성 버튼 클릭 리스너
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
        // SharedPreferences에서 로그인한 사용자 정보 불러오기
        String username = getSharedPreferences("UserSession", MODE_PRIVATE)
                .getString("username", null); // 사용자 이름 가져오기

        if (username == null) {
            Toast.makeText(CreatePostActivity.this, "로그인 후 글을 작성해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firestore에 글 저장
        Date timestamp = new Date();  // 현재 시간

        // Post 객체 생성 (이메일 대신 사용자 이름을 저장)
        Post post = new Post(null, username, title, content, timestamp);

        // Firestore에 저장
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