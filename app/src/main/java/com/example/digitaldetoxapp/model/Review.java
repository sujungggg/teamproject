package com.example.digitaldetoxapp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Review {
    private String userId;
    private String userEmail;
    private String content;

    @ServerTimestamp
    private Date timestamp; // timestamp 필드 추가

    public Review() {}

    public Review(String userId, String userEmail, String content) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

