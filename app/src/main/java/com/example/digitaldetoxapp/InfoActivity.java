package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    private Button homeButton;
    private TextView userNameTextView, userEmailTextView, userGoalDisplayTextView;
    private Spinner goalSpinner;
    private FirebaseAuth auth;
    private BarChart usageChart; // 차트를 표시할 BarChart

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            // 사용자 인증이 되지 않은 경우
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish(); // 현재 Activity 종료
            return;
        }

        // 사용자 정보 가져오기
        String userId = auth.getCurrentUser().getUid();
        // Firestore 또는 FirebaseRealtimeDatabase에서 사용자 데이터를 가져오는 로직 추가
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        homeButton = findViewById(R.id.button_home);
        userNameTextView = findViewById(R.id.user_name);
        userEmailTextView = findViewById(R.id.user_email);
        userGoalDisplayTextView = findViewById(R.id.user_goal_display);
        goalSpinner = findViewById(R.id.goal_spinner);
        usageChart = findViewById(R.id.usage_chart); // 차트 초기화

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();

            // Load user details
            loadUserDetails(userId);

            // Load usage data for chart
            loadUsageData(userId);
        }

        // Home button listener
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Load user details from Firestore and display them.
     */
    private void loadUserDetails(String userId) {
        FirebaseDatabase.getInstance().getReference("users").child(userId).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String goal = dataSnapshot.child("goal").getValue(String.class);

                        userNameTextView.setText("이름: " + (name != null ? name : "알 수 없음"));
                        userEmailTextView.setText("이메일: " + (email != null ? email : "알 수 없음"));
                        userGoalDisplayTextView.setText("목표: " + (goal != null ? goal : "선택된 목표가 없습니다."));
                    }
                })
                .addOnFailureListener(e -> {
                    userNameTextView.setText("정보를 불러올 수 없습니다.");
                    userEmailTextView.setText("정보를 불러올 수 없습니다.");
                    userGoalDisplayTextView.setText("정보를 불러올 수 없습니다.");
                });
    }

    /**
     * Load usage data from Firebase and display it in the chart.
     */
    private void loadUsageData(String userId) {
        DatabaseReference usageStatsRef = FirebaseDatabase.getInstance().getReference("usageStats").child(userId);

        usageStatsRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                int index = 0;

                for (DataSnapshot appData : dataSnapshot.getChildren()) {
                    String appName = appData.getKey();
                    Long usageTime = appData.getValue(Long.class);

                    if (usageTime != null) {
                        entries.add(new BarEntry(index, usageTime));
                        labels.add(appName);
                        index++;
                    }
                }

                // Populate the chart
                setupChart(entries, labels);
            } else {
                Toast.makeText(this, "사용 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Setup chart with given data.
     */
    private void setupChart(List<BarEntry> entries, List<String> labels) {
        BarDataSet dataSet = new BarDataSet(entries, "앱 사용 시간 (분)");
        BarData barData = new BarData(dataSet);
        usageChart.setData(barData);

        usageChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return labels.get((int) value);
            }
        });

        usageChart.invalidate(); // Refresh chart
    }
}
