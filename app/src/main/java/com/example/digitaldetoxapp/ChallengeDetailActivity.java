package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.digitaldetoxapp.CircularTimerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChallengeDetailActivity extends AppCompatActivity {

    private CircularTimerView circularTimerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // Set up the timer
        int totalSeconds = getIntent().getIntExtra("selectedTimeInSeconds", 0);
        circularTimerView = findViewById(R.id.circularTimerView);
        circularTimerView.setTotalTime(totalSeconds);

        // Timer finished listener
        circularTimerView.setOnTimerFinishedListener(new CircularTimerView.OnTimerFinishedListener() {
            @Override
            public void onTimerFinished() {
                updateChallengeTimeInFirebase(totalSeconds);
            }
        });
    }

    private void updateChallengeTimeInFirebase(int completedTime) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.update("totalChallengeTime", FieldValue.increment(completedTime))
                .addOnSuccessListener(aVoid -> {
                    checkForLevelUp(userRef);
                })
                .addOnFailureListener(e -> {
                    Log.e("ChallengeDetail", "Failed to update challenge time", e);
                });
    }

    private void checkForLevelUp(DocumentReference userRef) {
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long totalTime = documentSnapshot.getLong("totalChallengeTime");
                if (totalTime != null) {
                    String newLevel = calculateLevel(totalTime);
                    userRef.update("level", newLevel)
                            .addOnSuccessListener(aVoid -> {
                                // Navigate to success or level-up screen
                                Intent intent;
                                if (newLevel != null) {
                                    intent = new Intent(ChallengeDetailActivity.this, LevelUpActivity.class);
                                    intent.putExtra("newLevel", newLevel);
                                } else {
                                    intent = new Intent(ChallengeDetailActivity.this, SuccessActivity.class);
                                }
                                startActivity(intent);
                                finish();
                            });
                }
            }
        });
    }

    private String calculateLevel(Long totalTime) {
        if (totalTime >= 43200) return "Challenger";
        if (totalTime >= 36000) return "Diamond";
        if (totalTime >= 28800) return "Platinum";
        if (totalTime >= 21600) return "Gold";
        if (totalTime >= 14400) return "Silver";
        if (totalTime >= 7200) return "Bronze";
        return null;
    }

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
