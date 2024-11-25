package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostDetailActivity extends AppCompatActivity {

    private TextView textViewPostTitle;
    private TextView textViewPostContent;
    private Button buttonEditPost;
    private Button buttonDeletePost;
    private Button buttonBackToUserPosts;

    private FirebaseFirestore db;
    private String postId;
    private String postUserId; // 게시글 작성자의 ID
    private DocumentReference postRef; // Firestore DocumentReference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        textViewPostTitle = findViewById(R.id.textViewPostTitle);
        textViewPostContent = findViewById(R.id.textViewPostContent);
        buttonEditPost = findViewById(R.id.buttonEditPost);
        buttonDeletePost = findViewById(R.id.buttonDeletePost);
        buttonBackToUserPosts = findViewById(R.id.buttonBackToUserPosts);

        db = FirebaseFirestore.getInstance();

        // Intent로 전달받은 게시물 ID 가져오기
        postId = getIntent().getStringExtra("postId");

        if (postId != null) {
            postRef = db.collection("posts").document(postId);
            setupPostListener(); // 리스너 등록
        } else {
            Toast.makeText(this, "게시글 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
        }

        // 뒤로가기 버튼 클릭 시 UserPostActivity로 이동
        buttonBackToUserPosts.setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailActivity.this, UserPostActivity.class);
            startActivity(intent);
            finish();
        });

        // 수정 버튼 클릭 시 PostEditActivity로 이동
        buttonEditPost.setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailActivity.this, PostEditActivity.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        });

        // 삭제 버튼 클릭 시 게시글 삭제
        buttonDeletePost.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    // Firestore 리스너를 설정하여 실시간으로 게시글 내용 반영
    private void setupPostListener() {
        postRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(PostDetailActivity.this, "게시글 업데이트를 불러오는 중 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String title = documentSnapshot.getString("title");
                String content = documentSnapshot.getString("content");
                postUserId = documentSnapshot.getString("userId");

                textViewPostTitle.setText(title);
                textViewPostContent.setText(content);

                // 현재 로그인한 사용자와 게시글 작성자 비교
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null && postUserId != null && postUserId.equals(currentUser.getUid())) {
                    buttonEditPost.setVisibility(View.VISIBLE);
                    buttonDeletePost.setVisibility(View.VISIBLE);
                } else {
                    buttonEditPost.setVisibility(View.GONE);
                    buttonDeletePost.setVisibility(View.GONE);
                }
            } else {
                // 게시글이 삭제된 경우
                Toast.makeText(PostDetailActivity.this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("게시글 삭제")
                .setMessage("정말로 이 게시글을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> deletePost())
                .setNegativeButton("취소", null)
                .show();
    }

    private void deletePost() {
        postRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PostDetailActivity.this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PostDetailActivity.this, UserPostActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PostDetailActivity.this, "게시글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }
}

