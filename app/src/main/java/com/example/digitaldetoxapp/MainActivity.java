package com.example.digitaldetoxapp;

import android.app.usage.UsageStats;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button startChallengeButton;
    private Button myInfoButton;
    private Button communityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        startChallengeButton = findViewById(R.id.button1); // Start button
        myInfoButton = findViewById(R.id.button2);         // Info button
        communityButton = findViewById(R.id.button3);      // Community button

        // Set listeners for each button
        startChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

        myInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        });

        // Send app usage data to Firebase on create
        sendUsageDataToFirebase();
    }

    /**
     * Collects app usage data and sends it to Firebase using FirebaseManager.
     */
    private void sendUsageDataToFirebase() {
        UsageStatsManagerHelper usageHelper = new UsageStatsManagerHelper(this);
        List<UsageStats> statsList = usageHelper.getTodayUsageStats();

        if (statsList != null && !statsList.isEmpty()) {
            Map<String, Long> appUsage = new HashMap<>();
            for (UsageStats stats : statsList) {
                long usageTimeMinutes = stats.getTotalTimeInForeground() / 1000 / 60; // Convert to minutes
                if (usageTimeMinutes > 0) { // Only record apps with usage time
                    appUsage.put(stats.getPackageName(), usageTimeMinutes);
                }
            }

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (userId != null) {
                FirebaseManager.getInstance().saveUsageData(appUsage);
                Toast.makeText(this, "오늘의 사용 시간이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "사용자 인증이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "오늘의 사용 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 앱 사용 시간 데이터를 Firebase에 전송
        sendUsageDataToFirebase();
    }
}
