package com.example.moracmoracsignintest;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap mMap;
    private Marker currentMarker;
    private EditText searchEditText;
    private Button searchButton;
    private Button displayAllMarkersButton;
    private ImageView userprofile;

    String markerId;
    String title1;
    double averageRating;

    private List<StoreReview> storeReviewsList = new ArrayList<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                dialogBuilder.setTitle("푸드트럭 정보");

                StringBuilder dialogMessage = new StringBuilder();
                dialogMessage.append("푸드트럭 이름: ").append(marker.getTitle()).append("\n");
                dialogMessage.append("푸드트럭 설명: ").append(marker.getSnippet()).append("\n");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("markers");
                title1 = marker.getSnippet();
                Query query = databaseReference.orderByChild("content").equalTo(title1);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot markerSnapshot : snapshot.getChildren()) {
                            String idid = markerSnapshot.child("content").getValue(String.class);
                            String openingTime = markerSnapshot.child("openingTime").getValue(String.class);
                            String closingTime = markerSnapshot.child("closingTime").getValue(String.class);
                            String registrationDate = markerSnapshot.child("registrationDate").getValue(String.class);

                            if (idid != null) {
                                markerId = idid;
                                dialogMessage.append("오픈 시간: ").append(openingTime).append("\n");
                                dialogMessage.append("마감 시간: ").append(closingTime).append("\n");
                                dialogMessage.append("영업 날짜: ").append(registrationDate).append("\n");
                            }
                        }

                        // Set the message after fetching additional data
//                        dialogBuilder.setMessage(dialogMessage.toString());
//
//                        // Create and show the AlertDialog
//                        AlertDialog alertDialog = dialogBuilder.create();
//                        alertDialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.v("showDatafail", "id : " + markerId);
                    }
                });



                DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("store_reviews");
                Query reviewsQuery = reviewsRef.orderByChild("storeName").equalTo(marker.getTitle());

                reviewsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int totalRating = 0;
                        int numberOfReviews = 0;

                        for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                            ReviewData reviewData = reviewSnapshot.getValue(ReviewData.class);
                            if (reviewData != null) {
                                if (reviewData.getStoreName().equals(marker.getTitle())) {
                                    int rating = reviewData.getRating();
                                    totalRating += rating;
                                    numberOfReviews++;
                                }
                            }
                        }

                        averageRating = (numberOfReviews > 0) ? (double) totalRating / numberOfReviews : 0.0;

                        dialogMessage.append("평균 평점: ").append(averageRating).append("\n");

                        dialogBuilder.setMessage(dialogMessage.toString());

                        dialogBuilder.setPositiveButton("자세히 보기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MapsActivity.this, UserMenuActivity.class);
                                intent.putExtra("id", marker.getSnippet());
                                startActivity(intent);
                            }
                        });

                        dialogBuilder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        dialogBuilder.setNeutralButton("공지 보기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent noticeIntent = new Intent(MapsActivity.this, UserNoteActivity.class);
                                startActivity(noticeIntent);
                            }
                        });

                        dialogBuilder.setNegativeButton("길 안내", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LatLng markerLocation = marker.getPosition();
                                String label = marker.getTitle();
                                String uriString = "google.navigation:q=" + markerLocation.latitude + "," + markerLocation.longitude + "&mode=d";
                                Uri gmmIntentUri = Uri.parse(uriString);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                } else {
                                    Toast.makeText(MapsActivity.this, "구글 지도 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        dialogBuilder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.v(TAG, "Firebase Realtime Database에서 리뷰 데이터를 읽는 데 실패했습니다: " + error.getMessage());
                    }
                });

                return true;
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
            }
        }

        displayAllMarkers();
    }

    private void displayAllMarkers() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("markers");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    MarkerData markerData = markerSnapshot.getValue(MarkerData.class);
                    if (markerData != null) {
                        LatLng location = new LatLng(markerData.getLatitude(), markerData.getLongitude());
                        BitmapDescriptor resizedStoreIcon = resizeStoreImage(64, 64, R.drawable.store);

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(markerData.getTitle())
                                .snippet(markerData.getContent())
                                .icon(resizedStoreIcon));

                        marker.setTag(markerData.getOpeningHours());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Realtime Database에서 마커 데이터를 읽는 데 실패했습니다: " + databaseError.getMessage());
            }
        });
    }

    private void loadStoreReviewsFromFirebase() {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("store_reviews");
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storeReviewsList.clear();

                for (DataSnapshot storeSnapshot : dataSnapshot.getChildren()) {
                    String storeName = storeSnapshot.getKey();

                    for (DataSnapshot reviewSnapshot : storeSnapshot.getChildren()) {
                        ReviewData reviewData = reviewSnapshot.getValue(ReviewData.class);
                        if (reviewData != null) {
                            StoreReview storeReview = new StoreReview(storeName, reviewData.getRating());
                            storeReviewsList.add(storeReview);
                        }
                    }
                }

                displayAllMarkers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Realtime Database에서 'store_reviews' 데이터를 읽는 데 실패했습니다: " + databaseError.getMessage());
            }
        });
    }

    private void searchMarkers(String query) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("markers");
        Query searchQuery = databaseReference.orderByChild("name").startAt(query).endAt(query + "\uf8ff");
        Query contentQuery = databaseReference.orderByChild("content").startAt(query).endAt(query + "\uf8ff");

        List<MarkerData> markerList = new ArrayList<>();

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    MarkerData markerData = markerSnapshot.getValue(MarkerData.class);
                    if (markerData != null && !markerList.contains(markerData)) {
                        markerList.add(markerData);
                    }
                }

                contentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                            MarkerData markerData = markerSnapshot.getValue(MarkerData.class);
                            if (markerData != null && !markerList.contains(markerData)) {
                                markerList.add(markerData);
                            }
                        }

                        mMap.clear();

                        for (MarkerData markerData : markerList) {
                            LatLng location = new LatLng(markerData.getLatitude(), markerData.getLongitude());


                            BitmapDescriptor storeIcon = resizeStoreImage(64, 64, R.drawable.store);
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(markerData.getTitle())
                                    .snippet(markerData.getContent())
                                    .icon(storeIcon));

                            marker.setTag(markerData.getOpeningHours());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Firebase Realtime Database에서 마커 데이터를 읽는 데 실패했습니다: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Realtime Database에서 마커 데이터를 읽는 데 실패했습니다: " + databaseError.getMessage());
            }
        });
    }

    private BitmapDescriptor resizeStoreImage(int width, int height, int resourceId) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        searchEditText = findViewById(R.id.search_edittext);
        searchButton = findViewById(R.id.search_button);
        displayAllMarkersButton = findViewById(R.id.display_all_markers_button);
        userprofile = findViewById(R.id.userprofileicon);

        userprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, UserprofileActivity.class);
                startActivity(intent);
            }
        });

        FirebaseApp.initializeApp(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(searchQuery)) {
                    searchMarkers(searchQuery);
                }
            }
        });

        displayAllMarkersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAllMarkers();
            }
        });
    }
}