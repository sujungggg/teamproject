package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;

    private FirebaseFirestore db;

    // Hardcoded admin credentials
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.signup_button);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Login button click listener
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check for admin account
            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                Toast.makeText(LoginActivity.this, "관리자 계정으로 로그인 성공", Toast.LENGTH_SHORT).show();
                saveUserSession(username); // Save session for admin
                navigateToMainActivity();
            } else {
                // Validate user credentials in Firestore
                loginUser(username, password);
            }
        });

        // Sign up button click listener
        signUpButton.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String username, String password) {
        db.collection("users")
                .whereEqualTo("name", username) // Query Firestore for username
                .get(Source.SERVER) // Force fresh data fetch
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();

                        if (!result.isEmpty()) {
                            // Get the first matching document
                            DocumentSnapshot document = result.getDocuments().get(0);
                            String storedPassword = document.getString("password");

                            if (storedPassword != null && storedPassword.equals(password)) {
                                // Successful login
                                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                                // Save session for the user
                                saveUserSession(username);

                                // Navigate to MainActivity instead of InfoActivity
                                navigateToMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, "유효하지 않은 비밀번호입니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "유효하지 않은 사용자 이름입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Firestore query failed
                        Log.e(TAG, "Firestore query failed: ", task.getException());
                        Toast.makeText(LoginActivity.this, "로그인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Log and handle Firestore connection errors
                    Log.e(TAG, "Error connecting to Firestore", e);
                    Toast.makeText(LoginActivity.this, "네트워크 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserSession(String username) {
        // Save session information (for debugging, using SharedPreferences)
        getSharedPreferences("UserSession", MODE_PRIVATE)
                .edit()
                .putString("username", username)
                .apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}