package com.example.moracmoracsignintest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CEOUploadActivity extends AppCompatActivity {

    EditText signupCeoname, signupPhonenum, signupStorename, signupHtpay, signupCategory;
    // TextView loginRedirectText 필요없는 코드
    //Button signupButton => Button storeButton
    Button storeButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    //가게 이미지 저장
    ImageView storeuploadImage;
    String imageURL;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceoupload);

        signupCeoname = findViewById(R.id.signup_ceoname);
        signupPhonenum = findViewById(R.id.signup_phonenum);
        signupStorename = findViewById(R.id.signup_storename);
        signupHtpay = findViewById(R.id.signup_htpay);
        signupCategory = findViewById(R.id.signup_category);
        //loginRedirectText = findViewById(R.id.loginRedirectText) 필요없는 코드
        storeButton = findViewById(R.id.store_btn);
        //이미지 추가
        storeuploadImage = findViewById(R.id.storeimg);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData(); //선택한 이미지의 URI를 가져옴
                            storeuploadImage.setImageURI(uri); //이미지 뷰에 선택한 이미지를 설정
                        } else {
                            Toast.makeText(CEOUploadActivity.this, "가게 이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        storeuploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker); //갤러리 이미지 선택
            }
        });

        //리얼타임 파이어베이스에 데이터 전송
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();

            }
        });

    }

    public void saveData() {//이미지 추가

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(CEOUploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData() {
        // 현재 로그인된 사용자의 ID 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getEmail();
            // userId를 사용하여 필요한 작업 수행
            String id = userID;

            String ceoname = signupCeoname.getText().toString();
            String phonenum = signupPhonenum.getText().toString();
            String storename = signupStorename.getText().toString();
            String htpay = signupHtpay.getText().toString();
            String category = signupCategory.getText().toString();

            // users = store data 데이터베이스 레퍼런스 설정
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("store data");

            //하위 경로
            //String childPath = id + "/" + storename;

            //데이터 객체 생성

            id = id.replace(".com", "_com");
            HelperClass helperClass = new HelperClass(ceoname, phonenum, storename, htpay, category, id, imageURL);//이미지 추가
            //데이터 업로드
            reference.child(id).setValue(helperClass);


            Toast.makeText(CEOUploadActivity.this, "저장 성공!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CEOUploadActivity.this, CEOMainActivity.class);
            startActivity(intent);
        }
    }
}