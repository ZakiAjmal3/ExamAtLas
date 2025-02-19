package com.examatlas.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.examatlas.R;
import com.examatlas.activities.Admin.AdminDashboardActivity;
import com.examatlas.utils.Constant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class AdminOtpActivity extends AppCompatActivity {
    MaterialButton btnVerifyOtp;
    ProgressBar progressBar;
    LinearLayout parentLayout;
    TextView txtOtp;
    PinView otpView;
    String activity, token, otp;
    private final String serverUrl = Constant.BASE_URL + "user/loginUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_otp);

        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        txtOtp = findViewById(R.id.txtOtp);
        otpView = findViewById(R.id.otpView);
        parentLayout = findViewById(R.id.parentLayout);
        progressBar = findViewById(R.id.progressBar);

        txtOtp.setText("Enter The Verification Code Below\nYour OTP Is: ");

        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otpView.length() == 0) {
                    Snackbar.make(parentLayout, "Please Enter OTP", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
                } else {
                    if (otpView.length() < 6) {
                        Snackbar.make(parentLayout, "Please Enter Valid OTP", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
                    } else {
                        verifyOtp(otpView.getText().toString().trim());
                    }
                }
            }
        });
    }

    private void verifyOtp( String otp) {
        Intent intent = new Intent(AdminOtpActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String Response = jsonObject.getString("response");
//                            if (Response.equals("Success")){
//                                Intent intent = new Intent(AdminOtpActivity.this,AdminDashboardActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }else if (Response.equals("User does not exist")){
//                                Toast.makeText(AdminOtpActivity.this, "No user found with these credentials", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Toast.makeText(AdminOtpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(AdminOtpActivity.this, error.toString(), Toast.LENGTH_LONG).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("otp", otp);
//                return params;
//            }
//        };
//        MySingleton.getInstance(AdminOtpActivity.this).addToRequestQueue(stringRequest);
    }
}