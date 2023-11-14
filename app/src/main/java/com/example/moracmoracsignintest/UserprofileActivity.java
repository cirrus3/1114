package com.example.moracmoracsignintest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserprofileActivity extends AppCompatActivity {

    TextView profile;
    Button profilereview; // 추가: 리뷰 보기 버튼
    Button profilefavor; // 추가: 찜 목록으로 이동하는 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        profile = findViewById(R.id.profileemail);
        profilereview = findViewById(R.id.profilereview); // 추가: 리뷰 보기 버튼
        profilefavor = findViewById(R.id.profilefavor); // 추가: 찜 목록으로 이동하는 버튼

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getEmail();
            String email = userID;
            profile.setText(email);
        }

        // 추가: 리뷰 보기 버튼 클릭 리스너
        profilereview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 리뷰 보기 화면으로 이동하는 인텐트를 추가합니다.
                Intent intent = new Intent(UserprofileActivity.this, UserReviewListActivity.class);
                // 사용자의 이메일 주소를 인텐트에 추가
                intent.putExtra("userEmail", user.getEmail());
                startActivity(intent);
            }
        });

        // 추가: 찜 목록으로 이동하는 버튼 클릭 리스너
        profilefavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserprofileActivity.this, FavoriteListActivity.class);
                startActivity(intent);
            }
        });
    }
}
