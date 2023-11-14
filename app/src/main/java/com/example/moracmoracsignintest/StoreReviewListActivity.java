package com.example.moracmoracsignintest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StoreReviewListActivity extends AppCompatActivity {

    private ListView reviewListView;
    private Button writeReviewButton;
    private TextView titleTextView;

    private DatabaseReference databaseReference;
    private StoreReviewAdapter reviewAdapter;
    private FirebaseUser currentUser;

    private SharedPreferences sharedPreferences;

    private static final int REQUEST_REPLY = 1;
    private static final String PREF_SELECTED_STORE_NAME_KEY = "selected_store_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_review_list);

        reviewListView = findViewById(R.id.review_list_view);
        writeReviewButton = findViewById(R.id.add_review_button);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        loadReviews();

        String selectedStoreName = sharedPreferences.getString(PREF_SELECTED_STORE_NAME_KEY, "");

        titleTextView = findViewById(R.id.title_text);
        titleTextView.setText(selectedStoreName + " 리뷰 및 평점 목록");

        writeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreReviewListActivity.this, StoreReviewActivity.class);
                startActivity(intent);
            }
        });

        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyReview selectedReview = (MyReview) reviewAdapter.getItem(position);

                if (selectedReview != null) {
                    List<String> replies = selectedReview.getReplies();
                    if (replies != null && !replies.isEmpty()) {
                        Toast.makeText(StoreReviewListActivity.this, "리뷰에 답글이 있습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StoreReviewListActivity.this, "답글이 작성되지 않은 리뷰입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REPLY && resultCode == RESULT_OK) {
            MyReview updatedReview = data.getParcelableExtra("selected_review");

            if (updatedReview != null) {
                List<MyReview> reviewList = reviewAdapter.getReviewList();
                int index = reviewList.indexOf(updatedReview);
                if (index != -1) {
                    reviewList.set(index, updatedReview);
                    reviewAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void loadReviews() {
        String selectedStoreName = sharedPreferences.getString(PREF_SELECTED_STORE_NAME_KEY, "");

        if (selectedStoreName == null || selectedStoreName.isEmpty()) {
            Toast.makeText(this, "가게 이름이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reviewsRef = databaseReference.child("store_reviews").child(selectedStoreName);

        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<MyReview> reviewList = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot reviewSnapshot : userSnapshot.getChildren()) {
                        MyReview review = reviewSnapshot.getValue(MyReview.class);
                        if (review != null) {
                            reviewList.add(review);
                        }
                    }
                }

                reviewAdapter = new StoreReviewAdapter(StoreReviewListActivity.this, reviewList);
                reviewListView.setAdapter(reviewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StoreReviewListActivity.this, "리뷰를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
