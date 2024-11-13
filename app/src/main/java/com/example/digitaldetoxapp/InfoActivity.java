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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoActivity extends AppCompatActivity {

    private Button homeButton;
    private TextView userNameTextView, userEmailTextView, userGoalDisplayTextView;
    private Spinner goalSpinner;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        homeButton = findViewById(R.id.button_home);
        userNameTextView = findViewById(R.id.user_name);
        userEmailTextView = findViewById(R.id.user_email);
        userGoalDisplayTextView = findViewById(R.id.user_goal_display); // New TextView for displaying the goal
        goalSpinner = findViewById(R.id.goal_spinner);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Get current user ID and fetch Firestore data
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference docRef = firestore.collection("users").document(userId);

            // Fetch and set data to text views
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    String goal = documentSnapshot.getString("goal");

                    userNameTextView.setText("이름: " + name);
                    userEmailTextView.setText("이메일: " + email);
                    userGoalDisplayTextView.setText("목표: " + (goal != null ? goal : "선택된 목표가 없습니다."));
                }
            }).addOnFailureListener(e -> {
                userNameTextView.setText("정보를 불러올 수 없습니다.");
                userEmailTextView.setText("정보를 불러올 수 없습니다.");
                userGoalDisplayTextView.setText("정보를 불러올 수 없습니다.");
            });

            // Goal Spinner selection listener
            goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedGoal = parent.getItemAtPosition(position).toString();
                    userGoalDisplayTextView.setText("목표: " + selectedGoal); // Update the display in UI

                    // Update Firestore with selected goal
                    docRef.update("goal", selectedGoal)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(InfoActivity.this, "목표가 업데이트되었습니다!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(InfoActivity.this, "목표 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        // Home button listener
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}