package com.examatlas.activities.Books;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.examatlas.R;

public class OrderSuccessFullyPlacedActivity extends AppCompatActivity {
    ImageView orderSuccessIV;
    Button gotoMyOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success_fully_placed);

        orderSuccessIV = findViewById(R.id.orderSuccessIV);
        gotoMyOrders = findViewById(R.id.gotoMyOrders);

        // Delay for animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the animation
                Animation logo_object = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_activity_logo_rotation);
                orderSuccessIV.startAnimation(logo_object);
            }
        }, 0);

        gotoMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderSuccessFullyPlacedActivity.this, MyBookOrderHistory.class);
                startActivity(intent);
                finish();
            }
        });
    }
}