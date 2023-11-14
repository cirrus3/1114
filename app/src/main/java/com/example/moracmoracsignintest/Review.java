package com.example.moracmoracsignintest;

public class Review {
    private String title;
    private String content;
    private float rating;
    private String markerTitle;
    private String email;

    public Review() {
        // Default constructor required for Firebase
    }

    public Review(String title, String content, float rating, String email) {
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.email = email;
    }

    public Review(String title, String content) {
        this.title = title;
        this.content = content;
        this.rating = 0.0f;
        this.markerTitle = "";
        this.email = "";
    }

    // Getters and setters

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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getMarkerTitle() {
        return markerTitle;
    }

    public void setMarkerTitle(String markerTitle) {
        this.markerTitle = markerTitle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}