package com.example.socialmedia;

public class FollowRequest {
    private String userId;
    private boolean accepted;

    public FollowRequest() {}

    public FollowRequest(String userId, boolean accepted) {
        this.userId = userId;
        this.accepted = accepted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}

