package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private Button backToLoginButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // UI 요소 초기화
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.signup_button);
        backToLoginButton = findViewById(R.id.back_to_login_button);

        // Firebase 초기화
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 회원가입 버튼 클릭 리스너 설정
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(name, email, password);
                }
            }
        });

        // 로그인 화면으로 돌아가기 버튼 클릭 리스너 설정
        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserInfo(user, name, password);
                        Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        finish(); // 회원가입 후 액티비티 종료
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Log.w("Register", "회원가입 실패: " + e.getMessage());
                            Toast.makeText(RegisterActivity.this, "회원가입 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserInfo(FirebaseUser user, String name, String password) {
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", name);
            userMap.put("email", user.getEmail());
            userMap.put("password", password); // 비밀번호 저장
            userMap.put("uid", userId); // 사용자 고유 ID 저장

            db.collection("users").document(userId)
                    .set(userMap)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "User info saved successfully"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error saving user info", e));
        }
    }
}
