package com.examatlas.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginWithEmailActivity extends AppCompatActivity {
    EditText edtEmail;
    TextView signUpTxt;
    Button btnLogin;
    Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email);

        edtEmail = findViewById(R.id.edtEmail);
        signUpTxt = findViewById(R.id.adminNextBtn);
        btnLogin = findViewById(R.id.btnLogin);

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginWithEmailActivity.this, SignUpActivity1.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtEmail.getText().toString().trim().isEmpty()) {
                    edtEmail.setError("Email is required");
                    return;
                }
                sendingOTP();
            }
        });
    }

    private void sendingOTP() {
        String verifyEmailUrl = Constant.BASE_URL + "v1/otp/email";

        Log.e("sendingOTP method", verifyEmailUrl);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",edtEmail.getText().toString().trim());
            jsonObject.put("action","login");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        progressDialog = new Dialog(LoginWithEmailActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_bar_drawer);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, verifyEmailUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("success");
                            String message = response.getString("message");
                            Toast.makeText(LoginWithEmailActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (status.equals("true")) {
                                Log.e("Success log",response.toString());
                                Intent intent = new Intent(LoginWithEmailActivity.this, OtpActivity.class);
                                intent.putExtra("task","login");
                                intent.putExtra("email",edtEmail.getText().toString().trim());
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginWithEmailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginWithEmailActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(LoginWithEmailActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.e("BlogFetchError", errorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        MySingleton.getInstance(LoginWithEmailActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}