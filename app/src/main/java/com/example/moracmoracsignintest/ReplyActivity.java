package com.example.moracmoracsignintest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ReplyActivity extends AppCompatActivity {

    private EditText editTextReply;
    private Button btnConfirm;
    private MyReview selectedReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        editTextReply = findViewById(R.id.edit_text_reply);
        btnConfirm = findViewById(R.id.btn_confirm);

        selectedReview = getIntent().getParcelableExtra("selected_review");

        editTextReply.setText(selectedReview.getReply());

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replyContent = editTextReply.getText().toString();

                selectedReview.setReply(replyContent);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_review", selectedReview);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
