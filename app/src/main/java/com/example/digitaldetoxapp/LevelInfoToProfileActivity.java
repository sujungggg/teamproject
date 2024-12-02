package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LevelInfoToProfileActivity extends AppCompatActivity {

    private static final String TAG = "LevelInfoToProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_info);

        // Customize each level item with images and names
        customizeLevelItem(findViewById(R.id.item_bronze), "브론즈", R.drawable.medal_bronze);
        customizeLevelItem(findViewById(R.id.item_silver), "실버", R.drawable.medal_silver);
        customizeLevelItem(findViewById(R.id.item_gold), "골드", R.drawable.medal_gold);
        customizeLevelItem(findViewById(R.id.item_platinum), "플래티넘", R.drawable.medal_platinum);
        customizeLevelItem(findViewById(R.id.item_diamond), "다이아", R.drawable.medal_diamond);
        customizeLevelItem(findViewById(R.id.item_challenger), "챌린저", R.drawable.medal_challenger);

        // Fetch the current level from Firestore and highlight it
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String currentLevel = documentSnapshot.getString("level");
                        if (currentLevel != null) {
                            Log.d(TAG, "Fetched current level: " + currentLevel);
                            resetAllLevels(); // Reset all levels before highlighting
                            highlightCurrentLevel(currentLevel);
                            TextView currentLevelTextView = findViewById(R.id.tv_current_level);
                            currentLevelTextView.setText("현재 레벨: " + currentLevel);
                        } else {
                            Log.w(TAG, "Level field is null.");
                        }
                    } else {
                        Log.w(TAG, "User document does not exist.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch user level", e));

        // Set up the back button
        Button backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> navigateToInfoActivity());
    }

    private void customizeLevelItem(View item, String levelName, int drawableRes) {
        // Find the views
        ImageView icon = item.findViewById(R.id.iv_level_icon);
        TextView name = item.findViewById(R.id.tv_level_name);

        // Set the image dynamically
        if (icon != null) {
            icon.setImageResource(drawableRes);
            icon.setAlpha(0.5f); // Set default transparency
        }
        if (name != null) {
            name.setText(levelName);
            name.setTextColor(getResources().getColor(android.R.color.darker_gray)); // Default color
            name.setTextSize(16); // Default size
        }
    }

    private void resetAllLevels() {
        int[] levelIds = {
                R.id.item_bronze,
                R.id.item_silver,
                R.id.item_gold,
                R.id.item_platinum,
                R.id.item_diamond,
                R.id.item_challenger
        };

        for (int levelId : levelIds) {
            View item = findViewById(levelId);
            if (item != null) {
                ImageView icon = item.findViewById(R.id.iv_level_icon);
                TextView name = item.findViewById(R.id.tv_level_name);

                if (icon != null) {
                    icon.setAlpha(0.5f); // Dim the image
                }
                if (name != null) {
                    name.setTextColor(getResources().getColor(android.R.color.darker_gray)); // Default color
                    name.setTextSize(16); // Default size
                }
            }
        }
    }

    private void highlightCurrentLevel(String currentLevel) {
        switch (currentLevel) {
            case "Bronze":
                highlightLevel(findViewById(R.id.item_bronze));
                break;
            case "Silver":
                highlightLevel(findViewById(R.id.item_silver));
                break;
            case "Gold":
                highlightLevel(findViewById(R.id.item_gold));
                break;
            case "Platinum":
                highlightLevel(findViewById(R.id.item_platinum));
                break;
            case "Diamond":
                highlightLevel(findViewById(R.id.item_diamond));
                break;
            case "Challenger":
                highlightLevel(findViewById(R.id.item_challenger));
                break;
            default:
                Log.w(TAG, "Unknown level: " + currentLevel);
                break;
        }
    }

    private void highlightLevel(View item) {
        if (item == null) {
            Log.e(TAG, "Item is null, cannot highlight.");
            return;
        }

        ImageView icon = item.findViewById(R.id.iv_level_icon);
        TextView name = item.findViewById(R.id.tv_level_name);

        if (icon != null) {
            icon.setAlpha(1.0f); // Make the icon fully visible
        }
        if (name != null) {
            name.setTextColor(getResources().getColor(android.R.color.holo_orange_light)); // Highlight text
            name.setTextSize(18); // Increase text size
        }
    }

    private void navigateToInfoActivity() {
        // Navigate back to SuccessActivity
        Intent intent = new Intent(LevelInfoToProfileActivity.this, InfoActivity.class);
        startActivity(intent);
        finish();
    }
}