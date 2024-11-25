package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TopTenActivity extends AppCompatActivity {

    private ListView listViewTopTen;
    private Button buttonBackToCommunity;
    private ArrayList<User> userList;
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore db;

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
        // Firestore에서 유저 정보를 내림차순으로 정렬하여 가져옴
        db.collection("users")
                .orderBy("level", Query.Direction.DESCENDING) // 레벨을 기준으로 내림차순 정렬
                .limit(10) // 상위 10명만 가져옴
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : querySnapshot) {
                            String username = document.getString("username");
                            Long level = document.getLong("level");

                            if (username != null && level != null) {
                                userList.add(new User(username, level.intValue()));
                            }
                        }

                        // 유저 리스트를 레벨 순으로 정렬
                        Collections.sort(userList, new Comparator<User>() {
                            @Override
                            public int compare(User u1, User u2) {
                                return Integer.compare(u2.getLevel(), u1.getLevel());
                            }
                        });

                        // 상위 10명의 유저를 표시할 리스트 생성
                        ArrayList<String> topTenList = new ArrayList<>();
                        for (int i = 0; i < Math.min(10, userList.size()); i++) {
                            User user = userList.get(i);
                            topTenList.add((i + 1) + ". " + user.getName() + " - Level: " + user.getLevel());
                        }

                        // 어댑터 설정
                        adapter = new ArrayAdapter<>(TopTenActivity.this, android.R.layout.simple_list_item_1, topTenList);
                        listViewTopTen.setAdapter(adapter);

                    } else {
                        Toast.makeText(TopTenActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // User 모델 클래스
    static class User {
        private String name;
        private int level;

        public User(String name, int level) {
            this.name = name;
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }
    }
}
