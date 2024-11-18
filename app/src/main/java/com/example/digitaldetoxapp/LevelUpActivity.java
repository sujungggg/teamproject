package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LevelUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);

        // Fetch user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch total challenge time from Firebase
        FirebaseFirestore.getInstance().collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long totalChallengeTime = documentSnapshot.getLong("totalChallengeTime");

                        if (totalChallengeTime != null) {
                            // Determine levels based on totalChallengeTime
                            String currentLevel = calculateLevel(totalChallengeTime);
                            String previousLevel = getPreviousLevel(currentLevel);

                            // Update UI
                            updateUI(previousLevel, currentLevel);

                            // Automatically return to SuccessActivity after 3 seconds
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(LevelUpActivity.this, SuccessActivity.class);
                                startActivity(intent);
                                finish();
                            }, 3000); // 3-second delay
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle Firebase errors
                    TextView currentLevelText = findViewById(R.id.tv_current_level);
                    currentLevelText.setText("Error fetching data from Firebase.");
                });
    }

    /**
     * Determine the current level based on total challenge time.
     */
    private String calculateLevel(Long totalTime) {
        if (totalTime >= 43200) return "Challenger";
        if (totalTime >= 36000) return "Diamond";
        if (totalTime >= 28800) return "Platinum";
        if (totalTime >= 21600) return "Gold";
        if (totalTime >= 14400) return "Silver";
        if (totalTime >= 7200) return "Bronze";
        return "None"; // Default for users with insufficient time
    }

    /**
     * Get the previous level based on the current level.
     */
    private String getPreviousLevel(String currentLevel) {
        switch (currentLevel) {
            case "Bronze":
                return "None"; // No previous level
            case "Silver":
                return "Bronze";
            case "Gold":
                return "Silver";
            case "Platinum":
                return "Gold";
            case "Diamond":
                return "Platinum";
            case "Challenger":
                return "Diamond";
            default:
                return "None";
        }
    }

    /**
     * Update the UI with previous and current levels.
     */
    private void updateUI(String previousLevel, String currentLevel) {
        // Update current level text
        TextView currentLevelText = findViewById(R.id.tv_current_level);
        currentLevelText.setText("현재 레벨: " + currentLevel);

        // Set previous level medal
        ImageView previousLevelIcon = findViewById(R.id.iv_previous_level);
        setMedalImage(previousLevel, previousLevelIcon);

        // Set current level medal
        ImageView currentLevelIcon = findViewById(R.id.iv_current_level);
        setMedalImage(currentLevel, currentLevelIcon);
    }

    /**
     * Updates the medal image for a given level.
     */
    private void setMedalImage(String level, ImageView icon) {
        switch (level) {
            case "Bronze":
                icon.setImageResource(R.drawable.medal_bronze);
                break;
            case "Silver":
                icon.setImageResource(R.drawable.medal_silver);
                break;
            case "Gold":
                icon.setImageResource(R.drawable.medal_gold);
                break;
            case "Platinum":
                icon.setImageResource(R.drawable.medal_platinum);
                break;
            case "Diamond":
                icon.setImageResource(R.drawable.medal_diamond);
                break;
            case "Challenger":
                icon.setImageResource(R.drawable.medal_challenger);
                break;
            default:
                throw new IllegalArgumentException("Unknown level: " + level);
        }
    }
}