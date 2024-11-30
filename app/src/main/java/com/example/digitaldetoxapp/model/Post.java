package com.example.digitaldetoxapp.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Post {
    private String postId;
    private String username;  // userEmail -> username으로 변경
    private String title;
    private String content;
    private Date timestamp;

    // 기존 생성자 (userEmail 대신 username 사용)
    public Post(String postId, String username, String title, String content, Date timestamp) {
        this.postId = postId;
        this.username = username;  // userId 대신 username으로 변경
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }


    // Getter 및 Setter 추가
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUsername() {  // userId 대신 username으로 변경
        return username;
    }

    public void setUsername(String username) {  // userId 대신 username으로 변경
        this.username = username;
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

    // 수정된 시간 포맷 반환
    public String getFormattedTimestamp() {
        if (timestamp == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E) HH:mm");
        return sdf.format(timestamp);
    }
}