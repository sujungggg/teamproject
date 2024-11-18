package com.example.digitaldetoxapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.digitaldetoxapp.CircularTimerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChallengeDetailActivity extends AppCompatActivity {

    private CircularTimerView circularTimerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // 선택된 챌린지 리스트 가져오기 (null일 경우 빈 리스트로 대체)
        ArrayList<String> selectedChallenges = getIntent().getStringArrayListExtra("selectedChallenges");
        if (selectedChallenges == null) {
            selectedChallenges = new ArrayList<>(); // null 체크 추가
        }

        // 선택된 챌린지 리스트 표시
        ListView selectedChallengeList = findViewById(R.id.selectedChallengeList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selectedChallenges);
        selectedChallengeList.setAdapter(adapter);

        // 타이머 설정 받기
        int totalSeconds = getIntent().getIntExtra("selectedTimeInSeconds", 0);
        circularTimerView = findViewById(R.id.circularTimerView);
        circularTimerView.setTotalTime(totalSeconds);

        // 타이머 종료 후 Firestore 업데이트 및 SuccessActivity로 이동
        circularTimerView.setOnTimerFinishedListener(() -> {
            updateChallengeTimeInFirebase(totalSeconds);
        });
    }

    /**
     * Updates the total challenge time in Firebase Firestore.
     */
    private void updateChallengeTimeInFirebase(int completedTime) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection("Users").document(userId);

        // Increment the totalChallengeTime field in Firestore
        userRef.update("totalChallengeTime", FieldValue.increment(completedTime))
                .addOnSuccessListener(aVoid -> {
                    Log.d("ChallengeDetail", "Challenge time successfully updated.");
                    // Navigate to SuccessActivity
                    Intent intent = new Intent(ChallengeDetailActivity.this, SuccessActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("ChallengeDetail", "Error updating challenge time", e);
                });
    }
}