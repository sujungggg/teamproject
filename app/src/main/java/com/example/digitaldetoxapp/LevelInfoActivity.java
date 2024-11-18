package com.example.digitaldetoxapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LevelInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_info);

        // Fetch current level from Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userLevel = documentSnapshot.getString("level");
                        if (userLevel != null) {
                            displayCurrentLevel(userLevel); // Update the UI with the user's level
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("LevelInfoActivity", "Failed to fetch user level", e));
    }

    /**
     * Display the current level and highlight it in the UI.
     */
    private void displayCurrentLevel(String userLevel) {
        TextView currentLevelTextView = findViewById(R.id.tv_current_level);
        currentLevelTextView.setText("현재 레벨: " + userLevel);

        // Reset styles for all medals and text
        resetAllMedals();

        // Highlight the current level
        switch (userLevel) {
            case "Bronze":
                highlightMedal(R.id.iv_bronze, R.id.tv_level_bronze);
                break;
            case "Silver":
                highlightMedal(R.id.iv_silver, R.id.tv_level_silver);
                break;
            case "Gold":
                highlightMedal(R.id.iv_gold, R.id.tv_level_gold);
                break;
            case "Platinum":
                highlightMedal(R.id.iv_platinum, R.id.tv_level_platinum);
                break;
            case "Diamond":
                highlightMedal(R.id.iv_diamond, R.id.tv_level_diamond);
                break;
            case "Challenger":
                highlightMedal(R.id.iv_challenger, R.id.tv_level_challenger);
                break;
        }
    }

    /**
     * Reset all medals and text to their default (dimmed) state.
     */
    private void resetAllMedals() {
        int[] medalIds = {
                R.id.iv_bronze, R.id.iv_silver, R.id.iv_gold,
                R.id.iv_platinum, R.id.iv_diamond, R.id.iv_challenger
        };
        int[] textIds = {
                R.id.tv_level_bronze, R.id.tv_level_silver, R.id.tv_level_gold,
                R.id.tv_level_platinum, R.id.tv_level_diamond, R.id.tv_level_challenger
        };

        for (int medalId : medalIds) {
            ImageView medal = findViewById(medalId);
            medal.setAlpha(0.5f); // Dimmed for non-current levels
        }

        for (int textId : textIds) {
            TextView text = findViewById(textId);
            text.setTextColor(getResources().getColor(android.R.color.darker_gray)); // Default text color
            text.setTextSize(14); // Default text size
        }
    }

    /**
     * Highlight the medal and text for the current level.
     */
    private void highlightMedal(int medalId, int textId) {
        ImageView medal = findViewById(medalId);
        TextView text = findViewById(textId);

        medal.setAlpha(1.0f); // Fully visible
        text.setTextColor(getResources().getColor(android.R.color.holo_orange_light)); // Highlighted color
        text.setTextSize(18); // Enlarged text size
        text.setTypeface(null, android.graphics.Typeface.BOLD); // Bold text
    }
}