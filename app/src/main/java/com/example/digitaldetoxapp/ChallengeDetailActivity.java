package com.example.digitaldetoxapp;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.digitaldetoxapp.CircularTimerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class ChallengeDetailActivity extends AppCompatActivity {
    private CircularTimerView circularTimerView;
    private TextView selectedTimeTextView;
    private boolean isAccessibilitySettingsOpened = false;

    @Override
    protected void onResume() {
        super.onResume();

        // 설정 화면이 이미 열리지 않았다면
        if (!isAccessibilitySettingsOpened) {
            goAccessibilitySetting();             //접근 권한 설정 화면을 띄움
            isAccessibilitySettingsOpened = true; // 설정 화면을 열었음을 표시
        }

    }

    private void goAccessibilitySetting(){
        // 접근 권한 안내 메시지
        Toast.makeText(this, "디지털 디톡스 앱의 접근 권한을 허용해 주세요.", Toast.LENGTH_LONG).show();
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
        selectedTimeTextView.setText("선택 시간: " + time);

        // 타이머 종료 후 Firestore에 성공 시간 저장 및 SuccessActivity로 이동
        circularTimerView.setOnTimerFinishedListener(() -> {
            saveChallengeTimeToFirestore(totalSeconds); // Firestore 저장
        });

        // 챌린지 중단 버튼 설정
        Button stopChallengeButton = findViewById(R.id.stopChallengeButton);
        stopChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 타이머 멈추기
                if (circularTimerView != null) {
                    circularTimerView.resetTimer(); //타이머 시간 리셋
                    circularTimerView.stopTimer();  //타이머 정지
                }

                // 차단된 앱 해제
                resetBlockedApps();

                // 챌린지 중단 로직
                Toast.makeText(ChallengeDetailActivity.this, "진행중인 챌린지가 중단되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChallengeDetailActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });
    }

    private void saveChallengeTimeToFirestore(int completedTimeInSeconds) {
        // Firebase 인증에서 사용자 UID 가져오기
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore 인스턴스 가져오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        // Firestore에 성공 시간 업데이트
        userRef.update("totalChallengeTime", FieldValue.increment(completedTimeInSeconds))
                .addOnSuccessListener(aVoid -> {
                    // Firestore 업데이트 성공 시 제한 해제 및 SuccessActivity로 이동
                    Toast.makeText(this, "챌린지 성공 시간이 저장되었습니다!", Toast.LENGTH_SHORT).show();
                    resetBlockedApps(); // 차단 앱 초기화
                    navigateToSuccessActivity();
                })
                .addOnFailureListener(e -> {
                    // Firestore 업데이트 실패 시 에러 메시지 표시
                    Toast.makeText(this, "챌린지 시간이 저장되지 않았습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // 차단된 앱 초기화 메서드 호출
    private void resetBlockedApps() {
        SharedPreferences sharedPreferences = getSharedPreferences("ChallengePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("selectedChallenges", new HashSet<>()); // 앱 차단 목록을 비웁니다
        editor.apply();
        Log.d("ChallengeDetailActivity", "차단된 앱 목록이 초기화되었습니다.");
    }


    private void navigateToSuccessActivity() {
        Intent intent = new Intent(ChallengeDetailActivity.this, SuccessActivity.class);
        startActivity(intent);
        finish();
    }
}
