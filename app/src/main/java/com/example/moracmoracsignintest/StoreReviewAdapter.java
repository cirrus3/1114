package com.example.moracmoracsignintest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class StoreReviewAdapter extends BaseAdapter {
    private List<MyReview> reviewList;
    private LayoutInflater inflater;

    public StoreReviewAdapter(Context context, List<MyReview> reviewList) {
        this.reviewList = reviewList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_review, parent, false);
        }

        TextView titleTextView = view.findViewById(R.id.title_text);
        TextView contentTextView = view.findViewById(R.id.content_text);
        TextView ratingTextView = view.findViewById(R.id.rating_text);
        TextView replyTextView = view.findViewById(R.id.reply_text);

        MyReview review = reviewList.get(position);
        titleTextView.setText(review.getTitle());
        contentTextView.setText(review.getContent());
        ratingTextView.setText(String.valueOf(review.getRating()));

        // 리뷰 답변이 있으면 텍스트뷰에 표시
        String reply = review.getReply();
        if (reply != null && !reply.isEmpty()) {
            replyTextView.setText("답변: " + reply);
        } else {
            replyTextView.setText(""); // 리뷰 답변이 없으면 빈 텍스트로 설정
        }

        return view;
    }


    // 추가된 메서드: 리뷰 목록을 반환하는 메서드
    public List<MyReview> getReviewList() {
        return reviewList;
    }
}
