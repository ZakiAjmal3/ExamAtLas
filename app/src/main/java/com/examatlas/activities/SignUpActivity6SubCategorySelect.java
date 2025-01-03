package com.examatlas.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;

public class SignUpActivity6SubCategorySelect extends AppCompatActivity {
    RecyclerView subCategoryRecyclerView;
    ProgressBar progressBar;
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity6_sub_category_select);

        subCategoryRecyclerView = findViewById(R.id.subCategoryRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        nextBtn = findViewById(R.id.userNextBtn);

        subCategoryRecyclerView.setLayoutManager(new GridLayoutManager(this,2));

    }
}