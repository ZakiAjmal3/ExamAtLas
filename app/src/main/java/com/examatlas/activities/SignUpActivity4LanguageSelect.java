package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.examatlas.R;

public class SignUpActivity4LanguageSelect extends AppCompatActivity {
    TextView hindiCardTxt, englishCardTxt, hinglishCardTxt;
    Button nextBtn;
    String languageSTR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity4_language_select);
        hindiCardTxt = findViewById(R.id.hindiCardTxt);
        englishCardTxt = findViewById(R.id.englishCardTxt);
        hinglishCardTxt = findViewById(R.id.hinglishCardTxt);
        nextBtn = findViewById(R.id.userNextBtn);

        hindiCardTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageSTR = "Hindi";
                hindiCardTxt.setBackgroundResource(R.drawable.rounded_corner_for_language_select_with_border);
                englishCardTxt.setBackgroundResource(R.drawable.rounded_corner_for_language_select_without_border);
                hinglishCardTxt.setBackgroundResource(R.drawable.rounded_corner_for_language_select_without_border);
            }
        });
        englishCardTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageSTR = "English";
                englishCardTxt.setBackgroundResource(R.drawable.rounded_corner_for_language_select_with_border);
                hindiCardTxt.setBackgroundResource(R.drawable.rounded_corner_for_language_select_without_border);
                hinglishCardTxt.setBackgroundResource(R.drawable.rounded_corner_for_language_select_without_border);
            }
        });
        hinglishCardTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageSTR = "Hinglish";
                hinglishCardTxt.setBackgroundResource(R.drawable.rounded_corner_for_language_select_with_border);
                englishCardTxt.setBackgroundResource(R.drawable.rounded_corner_for_language_select_without_border);
                hindiCardTxt.setBackgroundResource(R.drawable.rounded_corner_for_language_select_without_border);
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (languageSTR == null){
                    Toast.makeText(SignUpActivity4LanguageSelect.this, "Please select a language", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(SignUpActivity4LanguageSelect.this, SignUpActivity5CategorySelect.class));
            }
        });
    }
}