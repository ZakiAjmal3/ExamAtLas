package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.examatlas.R;

public class SignUpActivity1 extends AppCompatActivity {
    Button nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        nextBtn = findViewById(R.id.userNextBtn);

        nextBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity1.this, SignUpActivity2DetailsInput.class));
        });
    }
}