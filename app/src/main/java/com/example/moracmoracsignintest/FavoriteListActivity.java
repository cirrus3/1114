package com.example.moracmoracsignintest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
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

public class FavoriteListActivity extends AppCompatActivity {

    private ListView favoriteListView;
    private List<String> storeNameList;
    private ArrayAdapter<String> storeNameAdapter;

    private DatabaseReference favoritesRef;
    private FirebaseUser currentUser;

    private static final String TAG = "FavoriteListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        favoriteListView = findViewById(R.id.favorite_list_view);
        storeNameList = new ArrayList<>();
        storeNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storeNameList);
        favoriteListView.setAdapter(storeNameAdapter);

        favoritesRef = FirebaseDatabase.getInstance().getReference().child("favorites");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loadFavoriteStoreNames();
    }

    private void loadFavoriteStoreNames() {
        if (currentUser == null) {
            Log.e(TAG, "사용자가 로그인되어 있지 않습니다.");
            return;
        }

        String firebasePath = getFirebasePath(currentUser.getEmail());

        favoritesRef.child(firebasePath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storeNameList.clear();

                for (DataSnapshot storeSnapshot : dataSnapshot.getChildren()) {
                    String storeEmail = storeSnapshot.getKey(); // 이메일을 키로 사용
                    if (storeEmail != null) {
                        String[] parts = storeEmail.split("@");
                        if (parts.length > 0) {
                            String storeName = parts[0];
                            storeNameList.add(storeName);
                        }
                    }
                }

                // 어댑터에 데이터 변경을 알리고 리스트뷰를 업데이트합니다.
                storeNameAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "찜한 가게 이름을 불러오는데 오류 발생", databaseError.toException());
            }
        });
    }

    private String getFirebasePath(String userEmail) {
        return userEmail.replaceAll("[.#$\\[\\]]", "_");
    }
}
