package com.example.moracmoracsignintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StoreReviewActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private RatingBar ratingBar;
    private Button submitButton;

    private DatabaseReference databaseReference; // Firebase Realtime Database Reference
    private SharedPreferences sharedPreferences; // SharedPreferences 변수

    private static final String PREF_SELECTED_STORE_NAME_KEY = "selected_store_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_review);

        titleEditText = findViewById(R.id.review_title_text);
        contentEditText = findViewById(R.id.review_content_text);
        ratingBar = findViewById(R.id.review_rating_bar);
        submitButton = findViewById(R.id.submit_review_button);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Firebase Realtime Database Reference 설정
        databaseReference = FirebaseDatabase.getInstance().getReference("store_reviews"); // "store_reviews"는 리뷰 데이터를 저장할 위치입니다.

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered review information
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                float rating = ratingBar.getRating();

                // Get the selected store's name from SharedPreferences
                String storeName = sharedPreferences.getString(PREF_SELECTED_STORE_NAME_KEY, "");

                // Check if the storeName is empty or "가게 이름"
                if (storeName == null || storeName.isEmpty()) {
                    Toast.makeText(StoreReviewActivity.this, "가게 이름을 설정해주세요.", Toast.LENGTH_SHORT).show();
                    return; // 가게 이름이 없으면 리뷰를 저장하지 않고 종료
                }

                // Save the review information
                saveReview(title, content, rating, storeName);
            }
        });
    }

    private void saveReview(String title, String content, float rating, String storeName) {
        // Get the logged-in user's email
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail(); // 사용자 이메일 가져오기

            // Create a new review object
            Review review = new Review(title, content, rating, email);

            // Generate a unique key for the review
            String reviewId = databaseReference.child(storeName).push().getKey(); // 고유한 리뷰 키 생성

            // Set the review with the generated key under the "store_reviews" location
            databaseReference
                    .child(storeName) // 가게 이름 아래에 저장
                    .child(email.replace(".", ",")) // 이메일을 고유 키로 사용 (점을 콤마로 대체)
                    .child(reviewId) // 생성된 리뷰 키 아래에 저장
                    .setValue(review)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Display a success message when the review is successfully saved
                            Toast.makeText(StoreReviewActivity.this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show();

                            // Create an Intent to move to the StoreReviewListActivity
                            Intent intent = new Intent(StoreReviewActivity.this, StoreReviewListActivity.class);
                            // 가게 이름을 StoreReviewListActivity로 전달
                            intent.putExtra(PREF_SELECTED_STORE_NAME_KEY, storeName);
                            startActivity(intent);

                            // Finish the current activity
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Display an error message if there was an error while saving the review
                            Toast.makeText(StoreReviewActivity.this, "리뷰 저장 중에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case when the user is not logged in
            Toast.makeText(StoreReviewActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
