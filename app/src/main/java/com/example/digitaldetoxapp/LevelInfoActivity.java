package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LevelInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_info);

        // 앱 이름 설정
        TextView appNameTextView = findViewById(R.id.tv_app_name);
        appNameTextView.setText("Digital Detox Challenge");

        // 현재 레벨 설정 (예시로 골드로 설정)
        TextView currentLevelTextView = findViewById(R.id.tv_current_level);
        currentLevelTextView.setText("현재 레벨: 골드");


        // 레벨별 이미지 설정
        ImageView bronzeMedal = findViewById(R.id.iv_bronze);
        bronzeMedal.setImageResource(R.drawable.medal_bronze);

        ImageView silverMedal = findViewById(R.id.iv_silver);
        silverMedal.setImageResource(R.drawable.medal_silver);

        ImageView goldMedal = findViewById(R.id.iv_gold);
        goldMedal.setImageResource(R.drawable.medal_gold);

        ImageView platinumMedal = findViewById(R.id.iv_platinum);
        platinumMedal.setImageResource(R.drawable.medal_platinum);

        ImageView diamondMedal = findViewById(R.id.iv_diamond);
        diamondMedal.setImageResource(R.drawable.medal_diamond);

        ImageView challengerMedal = findViewById(R.id.iv_challenger);
        challengerMedal.setImageResource(R.drawable.medal_challenger);

        // 돌아가기 버튼 설정
        Button backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SuccessActivity로 이동
                Intent intent = new Intent(LevelInfoActivity.this, SuccessActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}