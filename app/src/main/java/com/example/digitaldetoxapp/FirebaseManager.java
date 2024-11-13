package com.example.digitaldetoxapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseManager {

    private static FirebaseManager instance;
    private final FirebaseDatabase database;
    private final DatabaseReference messageReference;

    private FirebaseManager() {
        database = FirebaseDatabase.getInstance();
        messageReference = database.getReference("m essage");
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

}
//FirebaseManager는 싱글톤 패턴을 사용하여 Firebase 인스턴스를 관리합니다. sendMessage 메서드를 통해 데이터를 데이터베이스에 저장할 수 있습니다.