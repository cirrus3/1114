package com.example.moracmoracsignintest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserprofileActivity extends AppCompatActivity {

    TextView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        profile = findViewById(R.id.profileemail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getEmail();
            // userId를 사용하여 필요한 작업 수행

            String email = userID;

            // 사용자 이메일을 profile 텍스트뷰에 설정
            profile.setText(email);


        }
    }
}