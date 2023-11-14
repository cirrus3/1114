package com.example.moracmoracsignintest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CEOReviewAdapter extends BaseAdapter {

    private Context context;
    private List<MyReview> reviewList;

    public CEOReviewAdapter(Context context, List<MyReview> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.review_list_item_ceo, parent, false);
            holder = new ViewHolder();
            holder.textTitle = convertView.findViewById(R.id.text_title_ceo);
            holder.textRating = convertView.findViewById(R.id.text_rating_ceo);
            holder.textContent = convertView.findViewById(R.id.text_content_ceo);
            holder.textReplies = convertView.findViewById(R.id.text_replies_ceo); // 리뷰 답글 텍스트뷰 추가
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MyReview review = reviewList.get(position);

        holder.textTitle.setText(review.getTitle());
        holder.textRating.setText(String.valueOf(review.getRating()));
        holder.textContent.setText(review.getContent());

        // 리뷰 답글 텍스트뷰에 답글 내용 표시
        List<String> replies = review.getReplies();
        if (replies != null && !replies.isEmpty()) {
            StringBuilder replyText = new StringBuilder("답글: ");
            for (String reply : replies) {
                replyText.append(reply).append(", ");
            }
            // 마지막 ", " 제거
            replyText.setLength(replyText.length() - 2);
            holder.textReplies.setText(replyText.toString());
        } else {
            holder.textReplies.setText("");
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView textTitle;
        TextView textRating;
        TextView textContent;
        TextView textReplies; // 리뷰 답글 텍스트뷰 추가
    }

    // 추가: reviewList를 반환하는 메서드
    public List<MyReview> getReviewList() {
        return reviewList;
    }
}
