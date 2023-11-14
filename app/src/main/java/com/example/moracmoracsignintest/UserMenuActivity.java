package com.example.moracmoracsignintest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserMenuActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private RecyclerView recyclerView;
    private List<DataClass> dataList;
    private MyAdapterUser adapter;

    private ImageButton reviewButton;
    private Button favoriteButton;
    private TextView userCeoname, userPhonenum, userStorename, userHtpay, userCategory;
    private ImageView imageView;
    private ImageView shareIcon;

    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private static final String PREF_SELECTED_STORE_NAME_KEY = "selected_store_name";
    private static final String PREF_IS_FAVORITE_KEY = "is_favorite";

    private DatabaseReference favoritesRef;
    private boolean isFavorite = false;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        favoritesRef = FirebaseDatabase.getInstance().getReference("favorites");

        userCeoname = findViewById(R.id.userceoname);
        userPhonenum = findViewById(R.id.userphonenum);
        userStorename = findViewById(R.id.userstorename);
        userHtpay = findViewById(R.id.userhtpay);
        userCategory = findViewById(R.id.usercategory);
        reviewButton = findViewById(R.id.review_btn);
        favoriteButton = findViewById(R.id.favorite_button);
        imageView = findViewById(R.id.storeimgorigin);
        shareIcon = findViewById(R.id.shareicon);

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMenuActivity.this, StoreReviewListActivity.class);
                intent.putExtra("EXTRA_SELECTED_STORE_NAME", userStorename.getText().toString());

                // 추가: 가게 이름을 SharedPreferences에 저장
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PREF_SELECTED_STORE_NAME_KEY, userStorename.getText().toString());
                editor.apply();

                startActivity(intent);
            }
        });

        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDynamicLink();
            }
        });

        Intent intent = getIntent();
        String gid = intent.getStringExtra("id");
        userEmail = firebaseAuth.getCurrentUser().getEmail();

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("markers");
        Query query1 = reference1.orderByChild("content").equalTo(gid);

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot firstMatchSnapshot = snapshot.getChildren().iterator().next();
                    String id = firstMatchSnapshot.child("id").getValue(String.class);
                    showData(id);
                    recyclerView = findViewById(R.id.recyclerView);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(UserMenuActivity.this, 1);
                    recyclerView.setLayoutManager(gridLayoutManager);

                    AlertDialog.Builder builder = new AlertDialog.Builder(UserMenuActivity.this);
                    builder.setCancelable(false);
                    builder.setView(R.layout.progress_layout);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    dataList = new ArrayList<>();

                    adapter = new MyAdapterUser(UserMenuActivity.this, dataList);
                    recyclerView.setAdapter(adapter);

                    databaseReference = FirebaseDatabase.getInstance().getReference("Store Menu");
                    Query query2 = databaseReference.orderByChild("id").equalTo(id);
                    dialog.show();

                    eventListener = query2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            dataList.clear();
                            for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                                DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                                dataClass.setKey(itemSnapshot.getKey());
                                dataList.add(dataClass);
                            }
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    // Handle the case when store information is not found.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors.
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    removeFromFavorites();
                } else {
                    addToFavorites();
                }
            }
        });

        isFavorite = sharedPreferences.getBoolean(PREF_IS_FAVORITE_KEY, false);
        updateFavoriteButton();
    }

    public void showData(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("store data");

        Query query = reference.orderByChild("id").equalTo(id);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCeoname.setText("");
                userPhonenum.setText("");
                userStorename.setText(""); // 기존 가게 이름 초기화
                userHtpay.setText("");
                userCategory.setText("");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String ceoName = snapshot.child("ceoname").getValue(String.class);
                    String phoneNum = snapshot.child("phonenum").getValue(String.class);
                    String storeName = snapshot.child("storename").getValue(String.class);
                    String htPay = snapshot.child("htpay").getValue(String.class);
                    String cateGory = snapshot.child("category").getValue(String.class);
                    String imageUrl = snapshot.child("dataImage").getValue(String.class);

                    userCeoname.setText(ceoName);
                    userPhonenum.setText(phoneNum);
                    userStorename.setText(storeName); // 새로운 가게 이름 설정
                    userHtpay.setText(htPay);
                    userCategory.setText(cateGory);
                    Glide.with(UserMenuActivity.this).load(imageUrl).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors.
            }
        };

        query.addValueEventListener(valueEventListener);
    }


    private void addToFavorites() {
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String storeName = userStorename.getText().toString();

        String firebasePath = getFirebasePath(userEmail);

        DatabaseReference userRef = favoritesRef.child(firebasePath).child(storeName);

        userRef.setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserMenuActivity.this, "찜 목록에 가게가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            isFavorite = true;
                            updateFavoriteButton();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(PREF_IS_FAVORITE_KEY, true);
                            editor.apply();
                        } else {
                            Toast.makeText(UserMenuActivity.this, "찜 목록에 가게를 추가하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void removeFromFavorites() {
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String storeName = userStorename.getText().toString();
        String firebasePath = getFirebasePath(userEmail);

        DatabaseReference userRef = favoritesRef.child(firebasePath).child(storeName);

        userRef.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserMenuActivity.this, "찜 목록에서 가게가 제거되었습니다.", Toast.LENGTH_SHORT).show();
                            isFavorite = false;
                            updateFavoriteButton();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(PREF_IS_FAVORITE_KEY, false);
                            editor.apply();
                        } else {
                            Toast.makeText(UserMenuActivity.this, "찜 목록에서 가게를 제거하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getFirebasePath(String userEmail) {
        return userEmail.replaceAll("[.#$\\[\\]]", "_");
    }

    private void updateFavoriteButton() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_IS_FAVORITE_KEY, isFavorite);
        editor.apply();

        if (isFavorite) {
            favoriteButton.setText("찜 해제");
        } else {
            favoriteButton.setText("찜하기");
        }
    }

    public void createDynamicLink() {
        Uri deepLink = Uri.parse("https://example.com/deeplink");

        createShareContent(deepLink);
    }

    public void createShareContent(Uri deepLink) {
        String storeName = userStorename.getText().toString();
        String shareText = "모락 모락 앱을 통해 " + storeName + "의 정보를 확인해보세요! " + deepLink.toString();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null && databaseReference != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }
}
