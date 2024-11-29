package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LevelUpActivity extends AppCompatActivity {

    private static final String TAG = "LevelUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);

        // Fetch level details from Intent
        String previousLevel = getIntent().getStringExtra("previousLevel");
        String currentLevel = getIntent().getStringExtra("currentLevel");

        Log.d(TAG, "Previous Level: " + previousLevel);
        Log.d(TAG, "Current Level: " + currentLevel);

        if (previousLevel != null && currentLevel != null) {
            updateUI(previousLevel, currentLevel);

            // Automatically return to SuccessActivity after 3 seconds
            new Handler().postDelayed(() -> {
                Log.d(TAG, "Returning to SuccessActivity.");
                Intent intent = new Intent(LevelUpActivity.this, SuccessActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }, 3000);
        } else {
            Log.e(TAG, "Level data is missing. Returning to SuccessActivity.");
            Intent intent = new Intent(LevelUpActivity.this, SuccessActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Updates the UI with the previous level and current level images and text.
     */
    private void updateUI(String previousLevel, String currentLevel) {
        // Update the current level text
        TextView currentLevelText = findViewById(R.id.tv_current_level);
        currentLevelText.setText("현재 레벨: " + currentLevel);

        // Update the previous level and current level icons
        ImageView previousLevelIcon = findViewById(R.id.iv_previous_level);
        ImageView currentLevelIcon = findViewById(R.id.iv_current_level);

        setMedalImage(previousLevel, previousLevelIcon);
        setMedalImage(currentLevel, currentLevelIcon);
    }

    /**
     * Sets the appropriate medal image based on the level name.
     */
    private void setMedalImage(String level, ImageView icon) {
        int medalResource;
        switch (level) {
            case "Bronze":
                medalResource = R.drawable.medal_bronze;
                break;
            case "Silver":
                medalResource = R.drawable.medal_silver;
                break;
            case "Gold":
                medalResource = R.drawable.medal_gold;
                break;
            case "Platinum":
                medalResource = R.drawable.medal_platinum;
                break;
            case "Diamond":
                medalResource = R.drawable.medal_diamond;
                break;
            case "Challenger":
                medalResource = R.drawable.medal_challenger;
                break;
            default:
                medalResource = R.drawable.medal_bronze; // Default to Bronze if level is unknown
                break;
        }
        icon.setImageResource(medalResource);
    }
}