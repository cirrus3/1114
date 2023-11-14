package com.example.moracmoracsignintest;

import java.util.HashMap;

public class MarkerData {
    private String id;
    private String title;
    private String content;
    private String openingTime; // 오픈 시간
    private String closingTime; // 마감 시간

    private HashMap<String, String> openingHours;
    private String registrationDate; // 등록 날짜
    private String userId; // 사용자 ID
    private double latitude;
    private double longitude;
    private String email; // 사용자 이메일
    private float rating; // 평점

    public MarkerData() {
        // Default constructor required for Firebase
    }

    public MarkerData(String id, String title, String content, String openingTime, String closingTime,
                      String registrationDate, String userId, double latitude, double longitude, String email, float rating) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.registrationDate = registrationDate;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.email = email;
        this.rating = rating;
    }

    public String getId() {
        return id;
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

    public String getOpeningTime() {
        return openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getUserId() {
        return userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getRating() {
        return rating;
    }

    public HashMap<String, String> getOpeningHours() {
        return openingHours;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
