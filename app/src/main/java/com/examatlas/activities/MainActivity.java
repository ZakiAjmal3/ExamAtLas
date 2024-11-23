package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.examatlas.R;
import com.examatlas.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(MainActivity.this);

        logo = findViewById(R.id.logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation logo_object = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_activity_logo_rotation);
                logo.startAnimation(logo_object);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (sessionManager.IsLoggedIn()) {
                            if (sessionManager.getUserData().get("role").equalsIgnoreCase("user")) {
                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                Log.e("Dashboard","Dashboard");
                                startActivity(intent);
                                finish();
                            } else if (sessionManager.getUserData().get("role").equalsIgnoreCase("admin")) {
                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                Log.e("AdminDashboard","AdminDashboard");
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                            Log.e("SecondActivity","SecondActivity");
                            startActivity(intent);
                            finish();
                        }// Use the same duration as the animation
                    }
                }, 1600);
            }
        },0);
    }
}