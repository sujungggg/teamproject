package com.example.digitaldetoxapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.digitaldetoxapp.model.Review;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    private EditText editTextReview;
    private Button buttonSubmitReview, buttonBackToCommunity;
    private ListView listViewReviews;

    private FirebaseFirestore db;
    private ArrayList<String> reviewsList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // UI 요소 초기화
        editTextReview = findViewById(R.id.editTextReview);
        buttonSubmitReview = findViewById(R.id.buttonSubmitReview);
        listViewReviews = findViewById(R.id.listViewReviews);
        buttonBackToCommunity = findViewById(R.id.buttonBackToCommunity);

        // Firebase Firestore 초기화
        db = FirebaseFirestore.getInstance();

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // 리뷰 리스트 초기화
        reviewsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reviewsList);
        listViewReviews.setAdapter(adapter);

        // 리뷰 목록 불러오기
        loadReviews();

        // 리뷰 제출 버튼 클릭
        buttonSubmitReview.setOnClickListener(v -> {
            String newReview = editTextReview.getText().toString().trim();
            if (!newReview.isEmpty()) {
                saveReviewToFirestore(newReview);
            } else {
                Toast.makeText(ReviewActivity.this, "리뷰를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 뒤로가기 버튼 클릭
        buttonBackToCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewActivity.this, CommunityActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Firestore에 리뷰 저장
    private void saveReviewToFirestore(String reviewContent) {
        // SharedPreferences에서 사용자 이름 가져오기
        String userName = sharedPreferences.getString("username", "익명");

        // Review 객체 생성
        Review review = new Review(null, userName, reviewContent);

        db.collection("reviews").add(review)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(ReviewActivity.this, "리뷰가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    editTextReview.setText(""); // 입력 필드 초기화
                    loadReviews(); // 새로 추가된 리뷰를 리스트에 반영
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ReviewActivity.this, "리뷰 제출에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                });
    }

    // Firestore에서 리뷰 목록 불러오기
    private void loadReviews() {
        // 리뷰를 timestamp 기준으로 내림차순 정렬
        db.collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            reviewsList.clear(); // 기존 리스트 초기화
                            for (DocumentChange doc : querySnapshot.getDocumentChanges()) {
                                Review review = doc.getDocument().toObject(Review.class);
                                appendReviewToList(review);
                            }
                            adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알리기
                        }
                    } else {
                        Toast.makeText(ReviewActivity.this, "리뷰를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 리뷰를 리스트에 추가
    private void appendReviewToList(Review review) {
        String displayUser = (review.getUserEmail() != null) ? review.getUserEmail() : "익명";
        String reviewContent = "작성자: " + displayUser + "\n" + review.getContent();
        reviewsList.add(reviewContent); // 리스트에 리뷰 추가
    }
}
