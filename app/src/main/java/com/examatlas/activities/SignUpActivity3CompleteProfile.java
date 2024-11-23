package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.examatlas.R;

public class SignUpActivity3CompleteProfile extends AppCompatActivity {
    Button nextBtn;
    EditText edtGoal, edtWhyThisGoal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity3_complete_profile);

        nextBtn = findViewById(R.id.userNextBtn);

        nextBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity3CompleteProfile.this, SignUpActivity4LanguageSelect.class));
        });
    }
}