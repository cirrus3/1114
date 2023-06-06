package com.example.moracmoracsignintest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 마커를 클릭했을 때 실행되는 코드
                // 마커에 대한 정보를 표시하는 로직을 구현하세요

                // 예시: AlertDialog를 사용하여 마커 정보를 표시
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsActivity.this);

                dialogBuilder.setTitle("푸드트럭 정보");

                StringBuilder dialogMessage = new StringBuilder();
                dialogMessage.append("푸드트럭 이름: ").append(marker.getTitle()).append("\n");
                dialogMessage.append("푸드트럭 설명: ").append(marker.getSnippet()).append("\n");

                Object tag = marker.getTag();
                if (tag instanceof HashMap) {
                    HashMap<String, String> openingHours = (HashMap<String, String>) tag;
                    StringBuilder openingHoursText = new StringBuilder("영업 시간:\n");
                    String[] daysOfWeek = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};
                    boolean hasOpeningHours = false;
                    for (String day : daysOfWeek) {
                        if (openingHours.containsKey(day) && !openingHours.get(day).isEmpty()) {
                            String hours = openingHours.get(day);
                            openingHoursText.append(day).append(": ").append(hours).append("\n");
                            hasOpeningHours = true;
                        }
                    }
                    if (hasOpeningHours) {
                        dialogMessage.append(openingHoursText.toString());
                    }
                }

                dialogBuilder.setMessage(dialogMessage.toString());

                dialogBuilder.setPositiveButton("자세히 보기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MapsActivity.this, UserMenuActivity.class);
                        // 여기에 마커 정보를 전달하고, DetailActivity에서 사용할 수 있도록 처리
                        //intent.putExtra("id", marker.getSnippet());
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
                        Intent intent = new Intent(MapsActivity.this, UserNoteActivity.class);
                        // 마커 등록자의 이메일 정보를 ReviewActivity로 전달
                        intent.putExtra("id", marker.getSnippet());
                        startActivity(intent);
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

                return true;

            }



        });

        // Check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission is granted
            mMap.setMyLocationEnabled(true);

            // Move to current location
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
            }
        }

        // Display all markers initially
        displayAllMarkers();
    }

    // Display all markers initially
    private void displayAllMarkers() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("markers");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    MarkerData markerData = markerSnapshot.getValue(MarkerData.class);
                    if (markerData != null) {
                        LatLng location = new LatLng(markerData.getLatitude(), markerData.getLongitude());
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(markerData.getTitle()) // 타이틀 설정
                                .snippet(markerData.getContent()));

                        // 수정: openingTime을 가져와서 설정
                        marker.setTag(markerData.getOpeningHours());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read marker data from Firebase Realtime Database: " + databaseError.getMessage());
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        searchEditText = findViewById(R.id.search_edittext);
        searchButton = findViewById(R.id.search_button);
        displayAllMarkersButton = findViewById(R.id.display_all_markers_button);

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

                        mMap.clear(); // Clear all existing markers

                        for (MarkerData markerData : markerList) {
                            LatLng location = new LatLng(markerData.getLatitude(), markerData.getLongitude());
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(markerData.getTitle()) // 타이틀 설정
                                    .snippet(markerData.getContent()));
                            marker.setTag(markerData.getOpeningHours());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Failed to read marker data from Firebase Realtime Database: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read marker data from Firebase Realtime Database: " + databaseError.getMessage());
            }
        });
    }
}