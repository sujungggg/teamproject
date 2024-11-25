package com.example.digitaldetoxapp.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Post {
    private String postId;
    private String userId;
    private String title;
    private String content;
    private Date timestamp;
    private String userEmail; // 새로 추가된 필드

    // 기존 생성자 (userEmail 없이)
    public Post(String postId, String userId, String title, String content, Date timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    // 새로 추가된 생성자 (userEmail 포함)
    public Post(String postId, String userId, String title, String content, Date timestamp, String userEmail) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.userEmail = userEmail; // userEmail 초기화
    }

    // Getter 및 Setter 추가
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    // 수정된 시간 포맷 반환
    public String getFormattedTimestamp() {
        if (timestamp == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E) HH:mm");
        return sdf.format(timestamp);
    }
}


