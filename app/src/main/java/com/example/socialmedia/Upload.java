package com.example.socialmedia;

public class Upload {
    private String imageUrl;
    private String caption;
    private String uid;

    public Upload(String imageUrl, String caption, String uid) {
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public String getUid() {
        return uid;
    }
}

