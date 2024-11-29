package com.example.digitaldetoxapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CommunityActivity extends AppCompatActivity {

    private Button homeButton;
    private LinearLayout userPost, top10, review;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community); // 커뮤니티 레이아웃 설정

        homeButton = findViewById(R.id.button_home);
        userPost = findViewById(R.id.button_user_post);
        top10 = findViewById(R.id.button_top10);
        review = findViewById(R.id.button_review);

        // 홈 버튼 클릭 리스너 설정
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity로 이동
                Intent intent = new Intent(CommunityActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // User Post Card 클릭 리스너 설정
        userPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, UserPostActivity.class);
                startActivity(intent);
            }
        });

        // Top 10 Card 클릭 리스너 설정
        top10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, TopTenActivity.class);
                startActivity(intent);
            }
        });

        // Review Card 클릭 리스너 설정
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, ReviewActivity.class);
                startActivity(intent);
            }
        });
    }
}
