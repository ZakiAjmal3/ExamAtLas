package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.examatlas.R;
import com.examatlas.activities.Books.CartViewActivity;
import com.examatlas.utils.SessionManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.this.getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));

        sessionManager = new SessionManager(MainActivity.this);
        logo = findViewById(R.id.logo);

        // Delay for animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the animation
                Animation logo_object = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_activity_logo_rotation);
                logo.startAnimation(logo_object);
            }
        }, 0);  // Animation delay (if any)

        // Post delay to navigate to the next screen after animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sessionManager.setCartItemQuantity();
                Intent intent;
                intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();  // Finish the MainActivity to prevent back navigation
            }
        }, 1600);  // Match the duration of the logo animation
    }
}