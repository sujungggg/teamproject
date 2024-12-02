package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TopTenActivity extends AppCompatActivity {

    private ListView listViewTopTen;
    private Button buttonBackToCommunity;
    private ArrayList<User> userList;
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore db;

    // 레벨 순위 매핑
    private static final Map<String, Integer> LEVEL_ORDER = new HashMap<>();
    static {
        LEVEL_ORDER.put("Challenger", 1);
        LEVEL_ORDER.put("Diamond", 2);
        LEVEL_ORDER.put("Platinum", 3);
        LEVEL_ORDER.put("Gold", 4);
        LEVEL_ORDER.put("Silver", 5);
        LEVEL_ORDER.put("Bronze", 6);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);

        // UI 요소 초기화
        listViewTopTen = findViewById(R.id.listViewTopTen);
        buttonBackToCommunity = findViewById(R.id.buttonBackToCommunity);

        // Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance();

        // 유저 레벨 데이터를 가져오는 메서드 호출
        fetchTopTenUsers();

        // 뒤로 가기 버튼 클릭 시 CommunityActivity로 이동
        buttonBackToCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopTenActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchTopTenUsers() {
        // Firestore에서 유저 정보를 가져옴
        db.collection("users")
                .orderBy("level", Query.Direction.ASCENDING) // 정렬을 위한 준비
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String username = document.getString("name");
                                String level = document.getString("level");
                                Long totalChallengeTime = document.getLong("totalChallengeTime");

                                // 로그 추가
                                Log.d("TopTenActivity", "Document ID: " + document.getId() + ", Name: " + username + ", Level: " + level);

                                if (username != null && level != null && LEVEL_ORDER.containsKey(level)) {
                                    userList.add(new User(username, level, totalChallengeTime != null ? totalChallengeTime : 0));
                                }
                            }

                            // 레벨 순서에 따라 정렬
                            Collections.sort(userList, new Comparator<User>() {
                                @Override
                                public int compare(User u1, User u2) {
                                    return Integer.compare(LEVEL_ORDER.get(u1.getLevel()), LEVEL_ORDER.get(u2.getLevel()));
                                }
                            });

                            // 상위 10명의 유저를 표시할 리스트 생성
                            ArrayList<String> topTenList = new ArrayList<>();
                            for (int i = 0; i < Math.min(10, userList.size()); i++) {
                                User user = userList.get(i);
                                topTenList.add((i + 1) + ". " + user.getName() + " - 레벨 :  " + user.getLevel() +
                                        "\n - 챌린지 성공시간 :  " + user.getTotalChallengeTime());
                            }

                            // 어댑터 설정
                            adapter = new ArrayAdapter<>(TopTenActivity.this, android.R.layout.simple_list_item_1, topTenList);
                            listViewTopTen.setAdapter(adapter);

                        } else {
                            Log.d("TopTenActivity", "Query snapshot is null");
                        }
                    } else {
                        Toast.makeText(TopTenActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                        Log.e("TopTenActivity", "Error getting documents", task.getException());
                    }
                });
    }

    // User 모델 클래스
    static class User {
        private String name;
        private String level;
        private long totalChallengeTime;

        public User(String name, String level, long totalChallengeTime) {
            this.name = name;
            this.level = level;
            this.totalChallengeTime = totalChallengeTime;
        }

        public String getName() {
            return name;
        }

        public String getLevel() {
            return level;
        }

        public long getTotalChallengeTime() {
            return totalChallengeTime;
        }
    }
}


