package com.example.moracmoracsignintest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class CEOReviewActivity extends AppCompatActivity {
    private ListView reviewListView;
    private DatabaseReference databaseReference;
    private CEOReviewAdapter reviewAdapter;
    private String userEmail;
    private String storeName;

    private SharedPreferences sharedPreferences;

    private static final int REQUEST_REPLY = 1;

    // ActivityResultLauncher를 선언
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceo_review);

        reviewListView = findViewById(R.id.review_list_view_ceo);
        TextView textStoreEmail = findViewById(R.id.text_store_email);
        TextView textStoreName = findViewById(R.id.text_store_name);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String originalEmail = user.getEmail();
            String email = originalEmail.replace(".", "_");

            databaseReference = FirebaseDatabase.getInstance().getReference("store data");

            sharedPreferences = getSharedPreferences("user_reviews", MODE_PRIVATE);

            databaseReference.child(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        storeName = dataSnapshot.child("storename").getValue(String.class);
                        userEmail = originalEmail;

                        setTitle("가게 이름: " + storeName + " (" + userEmail + ")");
                        textStoreEmail.setText("이메일: " + userEmail);
                        textStoreName.setText("가게 이름: " + storeName);
                        loadReviews();
                    } else {
                        Toast.makeText(CEOReviewActivity.this, "권한이 없습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 처리 중 오류 발생 시 처리할 내용을 여기에 추가
                }
            });

            // ActivityResultLauncher 초기화
            launcher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                Intent data = result.getData();
                                if (data != null) {
                                    MyReview updatedReview = data.getParcelableExtra("selected_review");
                                    handleUpdatedReview(updatedReview);
                                }
                            }
                        }
                    });

            reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyReview selectedReview = (MyReview) reviewAdapter.getItem(position);

                    Intent intent = new Intent(CEOReviewActivity.this, ReplyActivity.class);
                    intent.putExtra("selected_review", selectedReview);
                    // 이제 launcher를 사용하여 활동을 시작
                    launcher.launch(intent);
                }
            });
        }
    }

    private void loadReviews() {
        databaseReference = FirebaseDatabase.getInstance().getReference("store_reviews").child(storeName);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<MyReview> reviewList = new ArrayList<>();

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot reviewSnapshot : userSnapshot.getChildren()) {
                            String id = reviewSnapshot.getKey();
                            String title = reviewSnapshot.child("title").getValue(String.class);
                            String content = reviewSnapshot.child("content").getValue(String.class);
                            Float rating = reviewSnapshot.child("rating").getValue(Float.class);

                            // 리뷰 답변은 SharedPreferences에서 가져오기
                            String reply = sharedPreferences.getString(id, null);

                            if (id != null && title != null && content != null && rating != null) {
                                MyReview review = new MyReview(id, title, content, rating);
                                if (reply != null) {
                                    review.setReply(reply);
                                }
                                reviewList.add(review);
                            }
                        }
                    }

                    reviewAdapter = new CEOReviewAdapter(CEOReviewActivity.this, reviewList);
                    reviewListView.setAdapter(reviewAdapter);
                } else {
                    Toast.makeText(CEOReviewActivity.this, "리뷰를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 처리 중 오류 발생 시 처리할 내용을 여기에 추가
            }
        });
    }

    // onActivityResult 메소드를 오버라이드하여 ReplyActivity에서 결과를 처리합니다
    private void handleUpdatedReview(MyReview updatedReview) {
        if (updatedReview != null) {
            for (int i = 0; i < reviewAdapter.getCount(); i++) {
                MyReview review = (MyReview) reviewAdapter.getItem(i);
                if (review.getId().equals(updatedReview.getId())) {
                    review.setReply(updatedReview.getReply());
                    reviewAdapter.notifyDataSetChanged();

                    // 업데이트된 답변을 SharedPreferences에 저장
                    sharedPreferences.edit().putString(review.getId(), updatedReview.getReply()).apply();

                    break;
                }
            }
        }
    }
}
