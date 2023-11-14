package com.example.moracmoracsignintest;

import android.os.Bundle;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class UserReviewListActivity extends AppCompatActivity {

    private ListView reviewListView;
    private List<MyReview> reviewList;
    private StoreReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review_list);

        reviewListView = findViewById(R.id.review_list_view);
        reviewList = new ArrayList<>();
        reviewAdapter = new StoreReviewAdapter(this, reviewList);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userEmail = user.getEmail();

            DatabaseReference userReviewsRef = FirebaseDatabase.getInstance().getReference("store_reviews");

            userReviewsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reviewList.clear();

                    for (DataSnapshot storeSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot userSnapshot : storeSnapshot.child(userEmail.replace(".", ",")).getChildren()) {
                            MyReview review = userSnapshot.getValue(MyReview.class);
                            if (review != null) {
                                reviewList.add(review);
                            }
                        }
                    }
                    reviewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 처리 중 오류 발생 시 처리할 내용을 여기에 추가
                }
            });
        }

        reviewListView.setAdapter(reviewAdapter);
    }
}
