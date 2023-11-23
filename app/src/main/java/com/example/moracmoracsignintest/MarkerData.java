package com.example.moracmoracsignintest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarkerData {
    private String id;
    private String title;
    private String content;
    private String openingTime;
    private String closingTime;

    private HashMap<String, String> openingHours;
    private String registrationDate;
    private String userId;
    private double latitude;
    private double longitude;
    private String email;
    private float rating;
    private List<Float> reviewRatings;
    private double averageRating;

    public MarkerData() {
        // Default constructor required for Firebase
        reviewRatings = new ArrayList<>();
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
        this.reviewRatings = new ArrayList<>();
        this.averageRating = rating; // 초기 평균 평점 설정
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

    public List<Float> getReviewRatings() {
        return reviewRatings;
    }

    public void addReviewRating(float reviewRating) {
        reviewRatings.add(reviewRating);
        updateAverageRating();
    }
    public double getAverageRating() {
        float totalRating = 0;
        for (float reviewRating : reviewRatings) {
            totalRating += reviewRating;
        }
        int totalNumberOfRatings = reviewRatings.size();
        double averageRating = (totalNumberOfRatings > 0) ? totalRating / totalNumberOfRatings : 0.0;
        return averageRating;
    }

    private void updateAverageRating() {
        float totalRating = 0;
        for (float reviewRating : reviewRatings) {
            totalRating += reviewRating;
        }
        int totalNumberOfRatings = reviewRatings.size();
        averageRating = (totalNumberOfRatings > 0) ? totalRating / totalNumberOfRatings : 0.0;
    }


    public HashMap<String, String> getOpeningHours() {
        return openingHours;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}