package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.examatlas.R;
import com.google.android.material.button.MaterialButton;

public class SecondActivity extends AppCompatActivity {
    MaterialButton userNextBtn,adminNextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        userNextBtn = findViewById(R.id.userNextBtn);
        adminNextBtn = findViewById(R.id.adminNextBtn);

        userNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this, LoginActivity.class));
            }
        });

        adminNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this, AdminLoginActivity.class));
            }
        });

    }
}
