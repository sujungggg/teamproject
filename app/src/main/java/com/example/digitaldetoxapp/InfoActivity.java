package com.example.digitaldetoxapp;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private BarChart barChart;
    List<String> appNames = Arrays.asList
            ("Youtube", "메시지", "전화", "카메라", "갤러리");


    private static final String TAG = "InfoActivity";

    private Button homeButton, logoutButton;
    private TextView userNameTextView, userEmailTextView, userGoalDisplayTextView;
    private Spinner goalSpinner;

    private FirebaseFirestore firestore;
    private DocumentReference userDocRef;

    @Override
    protected void onResume() {
        super.onResume();

        // 앱 사용 접근 권한 확인
        if (!hasUsageStatsPermission()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    private boolean hasUsageStatsPermission() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        return usageStatsManager != null && !usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, 0, System.currentTimeMillis()).isEmpty();
    }

    private void goAccessibilitySetting(){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        // Bar Chart 초기화
        barChart = findViewById(R.id.app_usage_bar_chart);
        setupBarChart();
        loadBarChartData();

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        homeButton = findViewById(R.id.button_home);
        logoutButton = findViewById(R.id.button_logout);
        userNameTextView = findViewById(R.id.user_name);
        userEmailTextView = findViewById(R.id.user_email);
        userGoalDisplayTextView = findViewById(R.id.user_goal_display);
        goalSpinner = findViewById(R.id.goal_spinner);

        // Check for valid session
        String username = getSharedPreferences("UserSession", MODE_PRIVATE)
                .getString("username", null);

        if (username == null) {
            Log.e(TAG, "No valid session found. Redirecting to login screen.");
            redirectToLogin();
            return;
        }

        Log.d(TAG, "Fetching data for user: " + username);

        // Fetch and display user data
        fetchUserData(username);

        // Home button listener
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Logout button listener
        logoutButton.setOnClickListener(v -> logout());
    }
    private void setupBarChart() {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        // X축 설정
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // 최소 간격 1로 설정
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new AppNameValueFormatter(appNames)); // 앱 이름 포맷터 설정
        xAxis.setDrawGridLines(false);

        // Y축 설정
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void loadBarChartData() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        // 앱 패키지 이름과 X축 레이블 매핑
        List<String> appPackages = Arrays.asList(
                "com.google.android.youtube", // YouTube
                "com.samsung.android.messaging", // 메시지
                "com.samsung.android.dialer", // 전화
                "com.sec.android.app.camera", // 카메라
                "com.sec.android.gallery3d" // 갤러리
        );

//        // 예시 데이터 (앱별 사용 시간)
//        entries.add(new BarEntry(1, 5)); // YouTube: 5시간
//        entries.add(new BarEntry(2, 3)); // 메시지: 3시간
//        entries.add(new BarEntry(3, 8)); // 전화: 8시간
//        entries.add(new BarEntry(4, 2)); // 카메라: 2시간
//        entries.add(new BarEntry(5, 4)); // 갤러리: 4시간

        for (int i = 0; i < appPackages.size(); i++) {
            String packageName = appPackages.get(i);
            long usageTimeMs = UsageStatsManagerHelper.getAppUsageTime(this, packageName);

            // 시간 단위로 변환 (밀리초 → 시간)
            //float usageTimeHours = usageTimeMs / (1000f * 60 * 60);

            // 분 단위로 변환 (밀리초 -> 분)
            float usageTimeMinutes = usageTimeMs / (1000f * 60);
            // 소수점 없이 정수로 처리
            int usageTimeMinutesInt = (int) usageTimeMinutes; // 소수점 버림
            entries.add(new BarEntry(i + 1, usageTimeMinutesInt));

//            // 초 단위로 변환 (밀리초)
//            float usageTimeSeconds = usageTimeMs / 1000f;
//            entries.add(new BarEntry(i + 1, usageTimeSeconds));
        }

       // BarDataSet dataSet = new BarDataSet(entries, "앱 사용 시간");
        BarDataSet dataSet = new BarDataSet(entries, "앱 사용 시간 (분)");
        //BarDataSet dataSet = new BarDataSet(entries, "앱 사용 시간 (초)");
        dataSet.setColors(new int[]{R.color.teal_200, R.color.purple_200, R.color.yellow, R.color.red, R.color.blue}, this);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new MyValueFormatter()); // 값 포맷터 설정

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        // Y축 포맷터 설정
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyValueFormatter()); // MyValueFormatter 사용

        // 애니메이션 추가
        barChart.animateY(1000);
    }

    private void fetchUserData(String username) {
        firestore.collection("users")
                .whereEqualTo("name", username)
                .get(Source.SERVER)
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        userDocRef = querySnapshot.getDocuments().get(0).getReference();

                        userDocRef.get().addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                Log.d(TAG, "Document data: " + doc.getData());
                                String name = doc.getString("name");
                                String email = doc.getString("email");
                                String goal = doc.getString("goal");

                                userNameTextView.setText("이름: " + (name != null ? name : "정보 없음"));
                                userEmailTextView.setText("이메일: " + (email != null ? email : "정보 없음"));
                                userGoalDisplayTextView.setText("목표: " + (goal != null ? goal : "선택된 목표가 없습니다."));

                                // Setup goal spinner
                                setupGoalSpinner(goal);
                            } else {
                                Toast.makeText(this, "사용자 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Error fetching user document: ", e);
                            Toast.makeText(this, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(this, "유효하지 않은 사용자입니다.", Toast.LENGTH_SHORT).show();
                        redirectToLogin();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error connecting to Firestore", e);
                    Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupGoalSpinner(String currentGoal) {
        String[] goals = getResources().getStringArray(R.array.goal_options);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(adapter);

        // Set the current goal in the spinner
        for (int i = 0; i < goals.length; i++) {
            if (goals[i].equals(currentGoal)) {
                goalSpinner.setSelection(i);
                break;
            }
        }

        // Listener for goal changes
        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGoal = parent.getItemAtPosition(position).toString();
                updateGoalInFirestore(selectedGoal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateGoalInFirestore(String selectedGoal) {
        userDocRef.update("goal", selectedGoal)
                .addOnSuccessListener(aVoid -> {
                    userGoalDisplayTextView.setText("목표: " + selectedGoal);
                    Toast.makeText(this, "목표가 성공적으로 업데이트되었습니다!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating goal: ", e);
                    Toast.makeText(this, "목표 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void logout() {
        // Clear session
        getSharedPreferences("UserSession", MODE_PRIVATE).edit().clear().apply();

        // Redirect to login screen
        Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }
}