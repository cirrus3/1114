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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;
import java.util.List;

public class UserMenuActivity extends AppCompatActivity {

    public DatabaseReference mDatabase; //지웠던거

    public String id; //지웠던거

    //public String rid; //다이나믹링크
    public String recontent = null;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private RecyclerView recyclerView;
    private List<DataClass> dataList;
    private MyAdapterUser adapter;

    private ImageButton reviewButton; //리뷰 버튼 바뀜
    private Button favoriteButton; //즐겨찾기 버튼
    private TextView userCeoname, userPhonenum, userStorename, userHtpay, userCategory;
    private ImageView imageView;
    private ImageView shareIcon;

    private SharedPreferences sharedPreferences; //ch
    private FirebaseAuth firebaseAuth;//ch
    private static final String PREF_SELECTED_STORE_NAME_KEY = "selected_store_name";//ch
    private static final String PREF_IS_FAVORITE_KEY = "is_favorite";//ch

    private DatabaseReference favoritesRef;//ch
    private boolean isFavorite = false;//ch

    private String userEmail;//ch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);


        //다이나믹링크 지워진 것 추가함
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        try {
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
                                //딥 링크 처리 메서드 호출
                                handleDeepLink(deepLink);

                                String recontent = handleDeepLink(deepLink);
                                //recontent오는지
                                Log.d("MainActivity", "recontent from deep link: " + recontent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //다른 화면으로 가도록 추가해보기
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("링크/에러", String.valueOf(e));
                    }
                });


        Intent intent = getIntent();
        String gid = intent.getStringExtra("id");

        //다이나믹 링크로 들어오면 파라미터 값의 유무에 따라 달라짐
        //intent로 rec 값 받기
        if(gid == null) {
            Intent intent2 = getIntent();
            recontent = intent2.getStringExtra("rec");
            Log.d("recontent", "recontent: " + recontent);
            gid = recontent;
        }

        String rid = gid; //다이나믹 링크로 넘겨줌
        Log.v("gidValue", "gid + "+ gid);
        Log.v("ridValue", "rid + "+ gid);


        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        favoritesRef = FirebaseDatabase.getInstance().getReference("favorites");

        userCeoname = findViewById(R.id.userceoname);
        userPhonenum = findViewById(R.id.userphonenum);
        userStorename = findViewById(R.id.userstorename);
        userHtpay = findViewById(R.id.userhtpay);
        userCategory = findViewById(R.id.usercategory);
        //리뷰버튼 추가
        reviewButton = findViewById(R.id.review_btn);
        //즐겨찾기버튼 추가
        favoriteButton = findViewById(R.id.favorite_button);
        //이미지 추가
        imageView = findViewById(R.id.storeimgorigin);
        //공유하기 추가
        shareIcon = findViewById(R.id.shareicon);

        //리뷰 버튼 눌렀을 때
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

        //공유하기 버튼 눌렀을 때
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDynamicLink(rid);
            }
        });


        userEmail = firebaseAuth.getCurrentUser().getEmail();

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("markers");
        Query query1 = reference1.orderByChild("content").equalTo(gid).limitToLast(1);

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot lastMatchSnapshot = snapshot.getChildren().iterator().next();

                    id = lastMatchSnapshot.child("id").getValue(String.class);
                    Log.v("idValue1", "id : " + id);
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
                    Log.v("idValue1", "No data found with content 'Hotdog'");
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

        Log.v("showDataid", "id : " + id);
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

    //변수까지 자세히 봐야함 교묘하게 바꿔버림
    public void createDynamicLink(String rid) {
        Log.v("ridValue1", "rid + "+ rid);
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                //파라미터 생성
                //.setLink(Uri.parse("https://moracmorac?getc=" +  rid))
                .setLink(Uri.parse("https://www.mmu.ac.kr/S1/board/78/read/76403?getc="+ rid)) //기본링크
                .setDomainUriPrefix("https://moracmoracsignintest.page.link") //도메인
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setFallbackUrl(Uri.parse("YOUR_FALLBACK_URL")).build()) //안드로이드 매개변수 설정
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("모락 모락")
                                .setDescription("푸드트럭 정보 안내 앱")
                                .build())
                .setGoogleAnalyticsParameters(
                        new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource("campaign-2023")
                                .setMedium("social")
                                .setCampaign("example-promo")
                                .build())
                .setNavigationInfoParameters(
                        new DynamicLink.NavigationInfoParameters.Builder()
                                .setForcedRedirectEnabled(true)
                                .build())

                //단축 동적링크 생성
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        // 성공하면 단축링크 생성
                        Uri shortLink = shortDynamicLink.getShortLink();
                        createShareContent(shortLink);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DynamicLink", "Dynamic link creation failed", e);
                        Toast.makeText(UserMenuActivity.this, "공유 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void createShareContent(Uri dynamicLink) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, dynamicLink.toString()); //동적 링크를 공유 메시지로 설정
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "공유하기");
        startActivity(shareIntent);
    }

    private String handleDeepLink(Uri deepLink) {
        // 여기에서 딥 링크에 대한 처리를 진행합니다.
        // 예를 들어, 딥 링크에서 필요한 정보를 추출하거나 특정 화면으로 이동하는 등의 작업을 수행할 수 있습니다.
        // 현재 페이지로 이동하는 코드 예시:

        //Intent intent = new Intent(this, UserMenuActivity.class);
        //Intent intent1 = getIntent();
        //String id = intent1.getStringExtra("gid");
        //intent.putExtra("id", id);

        // 딥 링크에서 쿼리 파라미터를 가져오기
        String recontent = deepLink.getQueryParameter("getc");
        // 로그로 디버깅 정보 출력
        Log.d("getc", "Deep link handled. getc: " + recontent);

        if (recontent != null) {
            // 쿼리 파라미터 정보를 로그로 출력
            Log.d("DeepLink", "Received item ID: " + recontent);
            Intent intent = new Intent(this, UserMenuActivity.class);
            intent.putExtra("rec", recontent);
            startActivity(intent);
        } else {
            // 쿼리 파라미터가 없는 경우
            Log.d("DeepLink", "No valid query parameters found in the deep link.");
        }
        
        return recontent; //recontent값 반환
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null && databaseReference != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }
}
