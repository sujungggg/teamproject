package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LevelUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);

        // Get the new level from intent
        String newLevel = getIntent().getStringExtra("newLevel");

        // Display the new level
        TextView levelUpTextView = findViewById(R.id.tv_achieved_level);
        levelUpTextView.setText("Congratulations! You've reached the " + newLevel + " level!");

        // Automatically return to SuccessActivity after 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LevelUpActivity.this, SuccessActivity.class);
            startActivity(intent);
            finish();
        }, 3000); // 3-second delay
    }
}