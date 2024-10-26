package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.examatlas.R;
import com.examatlas.utils.Constant;
import com.google.android.material.button.MaterialButton;

public class AdminForgotPasswordActivity extends AppCompatActivity {

    TextView txtLogin;
    MaterialButton btnRequestOtp;
    ProgressBar progressBar;
    EditText edtNumber;
    private final String serverUrl = Constant.BASE_URL + "user/loginUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_forgot_password);

        txtLogin = findViewById(R.id.txtLogin);
        btnRequestOtp = findViewById(R.id.btnRequestOtp);
        progressBar = findViewById(R.id.progressBar);
        edtNumber = findViewById(R.id.edtNumber);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminForgotPasswordActivity.super.onBackPressed();
            }
        });
        btnRequestOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtNumber.length() != 0 && !(edtNumber.getText().length() < 10)) {
                    forgotPassword(edtNumber.getText().toString().trim());
                }
            }
        });
    }

    private void forgotPassword(String mobile) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(AdminForgotPasswordActivity.this, AdminOtpActivity.class);
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
//                                Intent intent = new Intent(AdminForgotPasswordActivity.this,AdminOtpActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }else if (Response.equals("User does not exist")){
//                                Toast.makeText(AdminForgotPasswordActivity.this, "No user found with these credentials", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Toast.makeText(AdminForgotPasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(AdminForgotPasswordActivity.this, error.toString(), Toast.LENGTH_LONG).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("mobile", mobile);
//                return params;
//            }
//        };
//        MySingleton.getInstance(AdminForgotPasswordActivity.this).addToRequestQueue(stringRequest);
    }
}