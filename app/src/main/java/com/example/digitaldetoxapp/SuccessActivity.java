package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SuccessActivity extends AppCompatActivity {

    private static final String TAG = "SuccessActivity";
    private Button homeButton;
    private Button levelInfoButton;
    private Button retryChallengeButton;
    private boolean isCheckingLevelUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_success);

        homeButton = findViewById(R.id.button_home);
        levelInfoButton = findViewById(R.id.button_level_status);
        retryChallengeButton = findViewById(R.id.button_retry);

        homeButton.setOnClickListener(v -> navigateToMainActivity());
        levelInfoButton.setOnClickListener(v -> navigateToLevelInfoActivity());
        retryChallengeButton.setOnClickListener(v -> navigateToStartActivity());

        checkAndHandleLevelUp();
    }

    private void checkAndHandleLevelUp() {
        if (isCheckingLevelUp) return;
        isCheckingLevelUp = true;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long totalChallengeTime = documentSnapshot.getLong("totalChallengeTime");
                        String savedLevel = documentSnapshot.getString("level");

                        if (totalChallengeTime == null) totalChallengeTime = 0L; // 기본 값
                        if (savedLevel == null) savedLevel = "Bronze"; // 기본 값

                        Log.d(TAG, "Fetched totalChallengeTime: " + totalChallengeTime);
                        Log.d(TAG, "Saved level from Firestore: " + savedLevel);

                        String currentLevel = calculateLevel(totalChallengeTime);
                        Log.d(TAG, "Calculated currentLevel: " + currentLevel);

                        if (!savedLevel.equals(currentLevel)) {
                            navigateToLevelUpActivity(savedLevel, currentLevel, userId);
                        }
                    } else {
                        Log.e(TAG, "Document does not exist. Initializing user data.");
                        // 새 유저의 초기 값 설정
                        initializeUserLevel(userId);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching data from Firestore.", e));
    }

    private void initializeUserLevel(String userId) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .set(new UserData(0L, "Bronze")) // 초기 데이터 설정
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User data initialized successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to initialize user data.", e));
    }

    private String calculateLevel(Long totalChallengeTime) {
        if (totalChallengeTime >= 36000) return "Challenger";
        if (totalChallengeTime >= 28800) return "Diamond";
        if (totalChallengeTime >= 21600) return "Platinum";
        if (totalChallengeTime >= 120) return "Gold";
        if (totalChallengeTime >= 60) return "Silver";
        return "Bronze";
    }

    private void navigateToLevelUpActivity(String previousLevel, String currentLevel, String userId) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .update("level", currentLevel)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Level updated successfully in Firestore.");
                    Intent intent = new Intent(SuccessActivity.this, LevelUpActivity.class);
                    intent.putExtra("previousLevel", previousLevel);
                    intent.putExtra("currentLevel", currentLevel);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update level in Firestore.", e));
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLevelInfoActivity() {
        Intent intent = new Intent(SuccessActivity.this, LevelInfoActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToStartActivity() {
        Intent intent = new Intent(SuccessActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    public static class UserData {
        public Long totalChallengeTime;
        public String level;

        public UserData(Long totalChallengeTime, String level) {
            this.totalChallengeTime = totalChallengeTime;
            this.level = level;
        }
    }
}