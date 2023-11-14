package com.example.moracmoracsignintest;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class MyReview implements Parcelable {
    private String id; // Firestore 문서 ID
    private String title;
    private String content;
    private float rating;
    private List<String> replies;

    // 기본 생성자 추가
    public MyReview() {
        // 기본 생성자 내용 없음
    }

    public MyReview(String id, String title, String content, float rating) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.replies = new ArrayList<>();
    }

    // reply를 포함한 생성자 오버로딩
    public MyReview(String id, String title, String content, float rating, List<String> replies) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.replies = replies;
    }

    protected MyReview(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        rating = in.readFloat();
        replies = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeFloat(rating);
        dest.writeStringList(replies);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyReview> CREATOR = new Creator<MyReview>() {
        @Override
        public MyReview createFromParcel(Parcel in) {
            return new MyReview(in);
        }

        @Override
        public MyReview[] newArray(int size) {
            return new MyReview[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public float getRating() {
        return rating;
    }

    public List<String> getReplies() {
        return replies;
    }

    public void addReply(String reply) {
        replies.add(reply);
    }

    public void setReply(String reply) {
        replies.clear(); // 이전 답변을 지웁니다.
        replies.add(reply); // 새로운 답변을 추가합니다.
    }

    public String getReply() {
        // replies 목록이 비어있지 않다면, 첫 번째 답글을 반환합니다.
        if (replies != null && !replies.isEmpty()) {
            return replies.get(0);
        } else {
            return null; // 답글이 없는 경우 null을 반환합니다.
        }
    }
}
