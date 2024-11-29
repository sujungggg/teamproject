package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private TextView textViewRanking;
    private Button buttonBackToCommunity; // 뒤로가기 버튼 추가
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // UI 요소 초기화
        textViewRanking = findViewById(R.id.textViewRanking);
        buttonBackToCommunity = findViewById(R.id.buttonBackToCommunity); // 뒤로가기 버튼 연결

        // Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance();

        // 실시간으로 랭킹을 업데이트하는 함수 호출
        showRankingRealTime();

        // 뒤로가기 버튼 클릭 리스너 설정
        buttonBackToCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로가기 버튼 클릭 시 CommunityActivity로 이동
                Intent intent = new Intent(RankingActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish(); // 현재 RankingActivity 종료
            }
        });
    }

    private void showRankingRealTime() {
        db.collection("users")
                .orderBy("level", Query.Direction.DESCENDING) // 레벨 기준 내림차순 정렬
                .limit(30)
                .addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("RankingActivity", "Listen failed.", e);
                            textViewRanking.setText("Failed to load rankings.");
                            return;
                        }

                        List<String> rankings = new ArrayList<>();
                        int rank = 1;

                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            String username = document.getString("username");
                            Long level = document.getLong("level");

                            // 각 유저의 순위 및 레벨 정보 포맷
                            rankings.add(rank + ". " + username + " - Level: " + level);
                            rank++;
                        }

                        // 랭킹 정보를 텍스트로 표시
                        textViewRanking.setText(String.join("\n", rankings));
                    }
                });  // <- 괄호가 닫히는 부분이 추가되었습니다.
    }
}
