package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";

    private Button homeButton, logoutButton;
    private TextView userNameTextView, userEmailTextView, userGoalDisplayTextView;
    private Spinner goalSpinner;

    private FirebaseFirestore firestore;
    private DocumentReference userDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

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