package com.example.socialmedia;

public class User {
    private String name;
    private String bio;
    private String location;
    private String number;

    public User() {}

    public User(String name, String bio, String location, String number) {
        this.name = name;
        this.bio = bio;
        this.location = location;
        this.number = number;
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
}
