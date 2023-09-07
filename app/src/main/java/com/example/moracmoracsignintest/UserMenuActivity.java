package com.example.moracmoracsignintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

    public DatabaseReference mDatabase;

    public String id;

    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    MyAdapterUser adapter;

    ImageButton review_btn;

    TextView userCeoname, userPhonenum, userStorename, userHtpay, userCategory;
    //이미지추가
    ImageView imageView;
    //공유하기
    ImageView shareicon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        try {
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
                                // 딥 링크 처리 메서드 호출
                                handleDeepLink(deepLink);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("링크/에러", String.valueOf(e));
                    }
                });


        //intent로 title 값 받기
        //Intent intent = getIntent();
        //String title = intent.getStringExtra("title");

        //intent로 id 값 받기
        Intent intent = getIntent();
        String gid = intent.getStringExtra("id");

        userCeoname = findViewById(R.id.userceoname);
        userPhonenum = findViewById(R.id.userphonenum);
        userStorename = findViewById(R.id.userstorename);
        userHtpay = findViewById(R.id.userhtpay);
        userCategory = findViewById(R.id.usercategory);
        review_btn = findViewById(R.id.review_btn);
        //이미지 추가
        imageView = findViewById(R.id.storeimgorigin);
        //공유하기 추가
        shareicon = findViewById(R.id.shareicon);

        review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMenuActivity.this, StoreReviewListActivity.class);
                startActivity(intent);
            }
        });

        //공유하기 버튼 눌렀을 때
        shareicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDynamicLink();
            }
        });

        //핵심
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("markers");
        Query query1 = reference1.orderByChild("content").equalTo(gid);

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String cont = snapshot.child("id").getValue(String.class);
                //Log.v("showDatacont", "cont: " +cont);
                if (snapshot.exists()) {
                    // 첫 번째 매칭된 데이터 스냅샷을 가져옵니다
                    DataSnapshot firstMatchSnapshot = snapshot.getChildren().iterator().next();

                    // 해당 데이터의 id 값을 가져옵니다
                    id = firstMatchSnapshot.child("id").getValue(String.class);
                    Log.v("idValue", "id : " + id);
                    showData(id); //title값 받아오기

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
                    // 매칭된 데이터가 없는 경우 처리
                    Log.v("idValue", "No data found with content 'Hotdog'");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    public void showData(String id) {



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("store data");

        //Log.v("showDatatitle", "title : " + title);
        //Query query = reference.orderByChild("title").equalTo(title);//쿼리문

        Log.v("showDataid", "id : " + id);
        Query query = reference.orderByChild("id").equalTo(id);//쿼리문
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // 기존 데이터를 지우고 새로 받아오기 위해 데이터를 초기화하는 과정은 필요할까?
                userCeoname.setText("");
                userPhonenum.setText("");
                userStorename.setText("");
                userHtpay.setText("");
                userCategory.setText("");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // 데이터를 읽어오는 로직을 작성합니다.
                    String ceoName = snapshot.child("ceoname").getValue(String.class);
                    String phoneNum = snapshot.child("phonenum").getValue(String.class);
                    String storeName = snapshot.child("storename").getValue(String.class);
                    String htPay = snapshot.child("htpay").getValue(String.class);
                    String cateGory = snapshot.child("category").getValue(String.class);
                    // 이미지 URL 가져오기
                    String imageUrl = snapshot.child("dataImage").getValue(String.class);

                    // 읽어온 데이터를 활용하여 작업을 수행합니다.
                    userCeoname.setText(ceoName);
                    userPhonenum.setText(phoneNum);
                    userStorename.setText(storeName);
                    userHtpay.setText(htPay);
                    userCategory.setText(cateGory);
                    // 이미지 가져오기
                    Glide.with(UserMenuActivity.this).load(imageUrl).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 읽기 작업이 취소된 경우의 처리를 수행합니다.
            }
        };

        query.addValueEventListener(valueEventListener);
    }
    public void Create_DynamicLink(String PageURL, String ImgUrl){
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(PageURL))
                .setDomainUriPrefix("https://moracmoracsignintest")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(getPackageName())
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("친구에게 공유하기 테스트해볼까?")
                                .setImageUrl(Uri.parse(ImgUrl))
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri ShortLink = task.getResult().getShortLink();
                            try {
                                Intent Sharing_Intent = new Intent();
                                Sharing_Intent.setAction(Intent.ACTION_SEND);
                                //Sharing_Intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                Sharing_Intent.putExtra(Intent.EXTRA_TEXT, ShortLink.toString());
                                Sharing_Intent.setType("text/plain");
                                startActivity(Intent.createChooser(Sharing_Intent, "친구에게 공유하기"));
                            }
                            catch (Exception e) {
                            }
                        }
                    }
                });
    }
    public void createDynamicLink() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.naver.com/"))
                .setDomainUriPrefix("https://moracmoracsignintest.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setFallbackUrl(Uri.parse("YOUR_FALLBACK_URL")).build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()

                                .setTitle("모락 모락")
                                .setDescription("푸드트럭 정보 안내 앱")
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        Uri shortLink = shortDynamicLink.getShortLink();
                        createShareContent(shortLink);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserMenuActivity.this, "공유 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void createShareContent(Uri dynamicLink) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, dynamicLink.toString()); // 동적 링크를 공유 메시지로 설정
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "공유하기");
        startActivity(shareIntent);
    }

    private void handleDeepLink(Uri deepLink) {
        // 여기에서 딥 링크에 대한 처리를 진행합니다.
        // 예를 들어, 딥 링크에서 필요한 정보를 추출하거나 특정 화면으로 이동하는 등의 작업을 수행할 수 있습니다.
        // 현재 페이지로 이동하는 코드 예시:
        Intent intent = new Intent(this, MapsActivity.class);
        //Intent intent = new Intent(this, UserMenuActivity.class);
        //Intent intent1 = getIntent();
        //String id = intent1.getStringExtra("gid");
        //intent.putExtra("id", id);
        startActivity(intent);
        // 딥 링크에서 쿼리 파라미터를 가져오기
        String itemId = deepLink.getQueryParameter("item_id");
        String itemName = deepLink.getQueryParameter("item_name");


        if (itemId != null && itemName != null) {
            // 쿼리 파라미터 정보를 로그로 출력
            Log.d("DeepLink", "Received item ID: " + itemId);
            Log.d("DeepLink", "Received item name: " + itemName);

            // 가져온 정보를 화면에 표시
            Toast.makeText(this, "Received item ID: " + itemId + "\nReceived item name: " + itemName, Toast.LENGTH_LONG).show();
        } else {
            // 쿼리 파라미터가 없는 경우
            Log.d("DeepLink", "No valid query parameters found in the deep link.");
        }
    }


}