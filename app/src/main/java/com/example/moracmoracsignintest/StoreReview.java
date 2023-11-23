package com.example.moracmoracsignintest;

public class StoreReview {
    private String storeName;
    private double averageRating;

    public StoreReview() {
        // Firebase에서 객체를 읽어올 때 필요한 기본 생성자
    }

    public StoreReview(String storeName, double averageRating) {
        this.storeName = storeName;
        this.averageRating = averageRating;
    }

    public String getStoreName() {
        return storeName;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}