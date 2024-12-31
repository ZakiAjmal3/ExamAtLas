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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                if (sessionManager.IsLoggedIn()) {
                    String role = sessionManager.getUserData().get("role");
                    Intent intent;
                    if (role.equalsIgnoreCase("student")) {
                        if (Objects.equals(sessionManager.getUserData().get("step"), "1") || Objects.equals(sessionManager.getUserData().get("step"), (Integer.parseInt("1")))) {
                            intent = new Intent(MainActivity.this, SignUpActivity5CategorySelect.class);
                            Log.e("SignUp2", "User logged in, navigating to SignUp2");
                        }else {
                            intent = new Intent(MainActivity.this, DashboardActivity.class);
                            Log.e("Dashboard", "User logged in, navigating to Dashboard");
                        }
                    } else if (role.equalsIgnoreCase("admin")) {
                        intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                        Log.e("AdminDashboard", "Admin logged in, navigating to Admin Dashboard");
                    } else {
                        intent = new Intent(MainActivity.this, DashboardActivity.class);
                        Log.e("RoleError", "Role not recognized, navigating to Dashboard");
                    }
                    startActivity(intent);
                    finish();  // Finish the MainActivity to prevent back navigation
                } else {
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    Log.e("NotLoggedIn", "User not logged in, navigating to Dashboard");
                    startActivity(intent);
                    finish();
                }
            }
        }, 1600);  // Match the duration of the logo animation
    }
}