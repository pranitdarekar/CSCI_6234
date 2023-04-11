package com.example.socialmedia;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String name;
    private String bio;
    private String location;
    private String number;
    private String uid;
    private String email;

    public User() {}

    public User(String name, String bio, String location, String number, String uid, String email) {
        this.name = name;
        this.bio = bio;
        this.location = location;
        this.number = number;
        this.uid = uid;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getNumber() {
        return number;
    }

    public String getUid() { return uid;}

    public String getEmail() { return email;}
}
