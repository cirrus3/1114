package com.example.moracmoracsignintest;

public class ReviewData {
    private String storeName;
    private int rating;
    // 다른 필드도 필요한 경우 추가

    public ReviewData() {
        // Default constructor required for Firebase
    }

    public ReviewData(String storeName, int rating) {
        this.storeName = storeName;
        this.rating = rating;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getRating() {
        return rating;
    }


    public void setRating(int rating) {
        this.rating = rating;
    }
    // 다른 필드의 getter 및 setter 메서드도 추가
}
