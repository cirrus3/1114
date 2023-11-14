package com.example.moracmoracsignintest;

public class StoreReview {
    private String storeName;
    private int rating;
    // 다른 필드도 추가할 수 있음

    public StoreReview() {
        // Default constructor required for calls to DataSnapshot.getValue(StoreReview.class)
    }

    public StoreReview(String storeName, int rating) {
        this.storeName = storeName;
        this.rating = rating;
        // 다른 필드 초기화
    }

    public String getStoreName() {
        return storeName;
    }

    public int getRating() {
        return rating;
    }

    // 다른 필드에 대한 getter/setter 메서드 추가
}
