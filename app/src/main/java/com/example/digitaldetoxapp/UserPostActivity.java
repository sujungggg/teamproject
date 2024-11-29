package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.digitaldetoxapp.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class UserPostActivity extends AppCompatActivity {

    private static final int REQUEST_CREATE_POST = 1;

    private ListView listViewPosts;
    private Button buttonCreatePost, buttonBackToCommunity;
    private ArrayList<Post> postList;
    private ArrayAdapter<String> adapter;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CollectionReference postsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        listViewPosts = findViewById(R.id.listViewPosts);
        buttonCreatePost = findViewById(R.id.buttonCreatePost);
        buttonBackToCommunity = findViewById(R.id.buttonBackToCommunity);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        postsRef = db.collection("posts");

        postList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listViewPosts.setAdapter(adapter);

        loadAllPosts();

        buttonCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(UserPostActivity.this, CreatePostActivity.class);
            startActivityForResult(intent, REQUEST_CREATE_POST);
        });

        listViewPosts.setOnItemClickListener((parent, view, position, id) -> {
            Post post = postList.get(position);
            String postId = post.getPostId();

            Intent intent = new Intent(UserPostActivity.this, PostDetailActivity.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        });

        buttonBackToCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(UserPostActivity.this, CommunityActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllPosts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CREATE_POST && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String content = data.getStringExtra("content");

            if (title != null && content != null) {
                FirebaseUser user = auth.getCurrentUser();
                String userId = user != null ? user.getUid() : "Anonymous";
                String userEmail = user != null ? user.getEmail() : "Anonymous";

                postsRef.add(new Post(null, userId, title, content, new Date(), userEmail))
                        .addOnSuccessListener(documentReference -> {
                            // Firestore에서 자동으로 생성된 문서 ID 가져오기
                            String postId = documentReference.getId();

                            // postId를 포함하여 새 Post 객체 생성
                            Post newPost = new Post(postId, userId, title, content, new Date(), userEmail);

                            // Firestore에 postId 업데이트
                            documentReference.update("postId", postId)
                                    .addOnSuccessListener(aVoid -> {
                                        postList.add(newPost);
                                        updatePostList();
                                        Toast.makeText(UserPostActivity.this, "글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(UserPostActivity.this, "postId 업데이트 실패", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(UserPostActivity.this, "글 작성 실패", Toast.LENGTH_SHORT).show();
                        });

            }
        }
    }

    private void loadAllPosts() {
        postsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String content = document.getString("content");
                            String postId = document.getId();
                            String userId = document.getString("userId");
                            String userEmail = document.getString("userEmail");
                            Date timestamp = document.getDate("timestamp");

                            Post post = new Post(postId, userId, title, content, timestamp, userEmail);
                            postList.add(post);
                        }
                        updatePostList();
                    } else {
                        Toast.makeText(UserPostActivity.this, "게시글을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePostList() {
        ArrayList<String> displayList = new ArrayList<>();
        for (Post post : postList) {
            displayList.add("제목: " + post.getTitle() +
                    "\n작성자: " + post.getUserEmail() +  // 이메일 추가
                    "\n작성일: " + post.getFormattedTimestamp());
        }
        adapter.clear();
        adapter.addAll(displayList);
        adapter.notifyDataSetChanged();
    }
}
