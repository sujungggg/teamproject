package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {

    private Button homeButton;
    private Button levelInfoButton;
    private Button retryChallengeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_success);

        // 홈 버튼 설정
        homeButton = findViewById(R.id.button_home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메인 화면으로 이동
                Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 레벨 현황 버튼 설정
//        levelInfoButton = findViewById(R.id.button_level_status);
        levelInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 레벨 현황 화면으로 이동
                Intent intent = new Intent(SuccessActivity.this, LevelInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 챌린지 재시작 버튼 설정
        retryChallengeButton = findViewById(R.id.button_retry);
        retryChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 챌린지 시작 화면으로 이동
                Intent intent = new Intent(SuccessActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
