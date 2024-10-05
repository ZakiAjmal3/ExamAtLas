package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.examatlas.R;
import com.google.android.material.button.MaterialButton;

public class SecondActivity extends AppCompatActivity {
    MaterialButton btnNext;
    TextView txtSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        btnNext = findViewById(R.id.btnNext);
        txtSignUp = findViewById(R.id.txtSignUp);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this, LoginActivity.class));
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this, SignUpActivity.class));
            }
        });

    }
}
