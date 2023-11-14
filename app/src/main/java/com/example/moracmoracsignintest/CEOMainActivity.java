package com.example.moracmoracsignintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CEOMainActivity extends AppCompatActivity {

    public DatabaseReference mDatabase;

    TextView signupCeoname, signupPhonenum, signupStorename, signupHtpay, signupCategory;

    Button fab2;
    ImageButton note;
    ImageButton menuBtn;
    ImageButton gps;
    ImageButton menu_button;
    ImageButton ceoReviewButton;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceomain);

        signupCeoname = findViewById(R.id.signupceoname);
        signupPhonenum = findViewById(R.id.signupphonenum);
        signupStorename = findViewById(R.id.signupstorename);
        signupHtpay = findViewById(R.id.signuphtpay);
        signupCategory = findViewById(R.id.signupcategory);

        showData();
        menuBtn = findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener((v) -> showMenu());

        note = findViewById(R.id.note_button);
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CEOMainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        gps = findViewById(R.id.gps_button);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CEOMainActivity.this, MapsEditActivity.class);
                startActivity(intent);
            }
        });

        fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CEOMainActivity.this, CEOUploadActivity.class);
                startActivity(intent);
            }
        });

        menu_button = findViewById(R.id.menu_button);

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CEOMainActivity.this, menuActivity.class);
                startActivity(intent);
            }
        });

        ceoReviewButton = findViewById(R.id.ceo_review);
        ceoReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CEOMainActivity.this, CEOReviewActivity.class);
                // store data 정보 전달
                intent.putExtra("ceoName", signupCeoname.getText().toString());
                intent.putExtra("phoneNum", signupPhonenum.getText().toString());
                intent.putExtra("storeName", signupStorename.getText().toString());
                intent.putExtra("htPay", signupHtpay.getText().toString());
                intent.putExtra("cateGory", signupCategory.getText().toString());
                startActivity(intent);
            }
        });
    }

    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(CEOMainActivity.this, menuBtn);
        popupMenu.getMenu().add("사용자 맵");
        popupMenu.getMenu().add("Logout");

        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle().equals("Logout")) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(CEOMainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                } else if (menuItem.getTitle().equals("사용자 맵")) {
                    startActivity(new Intent(CEOMainActivity.this, MapsActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    public void showData() {
        // 현재 로그인된 사용자의 ID 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getEmail();
            String email = userID.replace(".com", "_com");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("store data");

            Query query = reference.orderByChild("id").equalTo(email);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String ceoName = snapshot.child("ceoname").getValue(String.class);
                        String phoneNum = snapshot.child("phonenum").getValue(String.class);
                        String storeName = snapshot.child("storename").getValue(String.class);
                        String htPay = snapshot.child("htpay").getValue(String.class);
                        String cateGory = snapshot.child("category").getValue(String.class);

                        signupCeoname.setText(ceoName);
                        signupPhonenum.setText(phoneNum);
                        signupStorename.setText(storeName);
                        signupHtpay.setText(htPay);
                        signupCategory.setText(cateGory);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 데이터 읽기 작업이 취소된 경우의 처리를 수행합니다.
                }
            });
        }
    }
}
