package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.digitaldetoxapp.CircularTimerView;

import java.util.ArrayList;

public class ChallengeDetailActivity extends AppCompatActivity {

    private CircularTimerView circularTimerView;
    private TextView selectedTimeTextView;
    private boolean isAccessibilitySettingsOpened = false;

    @Override
    protected void onResume() {
        super.onResume();

        // 설정 화면이 이미 열리지 않았다면
        if (!isAccessibilitySettingsOpened) {
            goAccessibilitySetting();
            isAccessibilitySettingsOpened = true; // 설정 화면을 열었음을 표시
        }

    }

    private void goAccessibilitySetting(){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // 선택된 챌린지 리스트 가져오기 (null일 경우 빈 리스트로 대체)
        ArrayList<String> selectedChallenges = getIntent().getStringArrayListExtra("selectedChallenges");
        if (selectedChallenges == null) {
            selectedChallenges = new ArrayList<>(); // null 체크 추가
        }

        // 선택된 챌린지 리스트 표시
        ListView selectedChallengeList = findViewById(R.id.selectedChallengeList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selectedChallenges);
        selectedChallengeList.setAdapter(adapter);

        // 타이머 설정 받기
        int totalSeconds = getIntent().getIntExtra("selectedTimeInSeconds", 0);
        circularTimerView = findViewById(R.id.circularTimerView);
        circularTimerView.setTotalTime(totalSeconds);

        // 선택된 시간 표시 텍스트뷰 설정
        selectedTimeTextView = findViewById(R.id.selectedTimeTextView);

        // 시간과 분 계산
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;

        // "시간:분" 형식으로 표시
        String time = String.format("%02d:%02d", hours, minutes);
        selectedTimeTextView.setText("선택된 시간: " + time);

        // 타이머 종료 후 SuccessActivity로 이동
        circularTimerView.setOnTimerFinishedListener(new CircularTimerView.OnTimerFinishedListener() {
            @Override
            public void onTimerFinished() {
                Intent intent = new Intent(ChallengeDetailActivity.this, SuccessActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
