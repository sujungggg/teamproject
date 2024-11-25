package com.example.digitaldetoxapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FirebaseManager {

    private static FirebaseManager instance;
    private final FirebaseDatabase database;
    private final DatabaseReference messageReference;
    private final FirebaseFirestore firestore;

    private FirebaseManager() {
        database = FirebaseDatabase.getInstance();
        messageReference = database.getReference("m essage");
        firestore = FirebaseFirestore.getInstance();
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public DatabaseReference getMessageReference() {
        return messageReference;
    }

    public void sendMessage(String message) {
        messageReference.setValue(message);
    }


    public void readMessage(ValueEventListener listener) {
        messageReference.addListenerForSingleValueEvent(listener);
    }
    //데이터 읽기 기능 구현

    public void saveUsageData(Map<String, Long> appUsage) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId != null) {
            Map<String, Object> usageData = new HashMap<>();
            usageData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
            usageData.put("data", appUsage);

            firestore.collection("usageStats").document(userId)
                    .set(usageData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("사용 시간 데이터 저장 성공");
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("사용 시간 데이터 저장 실패: " + e.getMessage());
                    });
        }
    }

    public void saveUsageData(String userId, Map<String, Long> usageData) {

    }
}


//FirebaseManager는 싱글톤 패턴을 사용하여 Firebase 인스턴스를 관리합니다. sendMessage 메서드를 통해 데이터를 데이터베이스에 저장할 수 있습니다.