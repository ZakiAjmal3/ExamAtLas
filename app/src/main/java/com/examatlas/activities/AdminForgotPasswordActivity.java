package com.examatlas.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.examatlas.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminForgotPasswordActivity extends AppCompatActivity {

    TextView txtLogin;
    MaterialButton btnRequestOtp;
    EditText emailEditTxt;
    private final String serverUrl = Constant.BASE_URL + "auth/forgotpassword";
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_forgot_password);

        txtLogin = findViewById(R.id.txtLogin);
        btnRequestOtp = findViewById(R.id.btnRequestOtp);
        emailEditTxt = findViewById(R.id.edtNumber);

        sessionManager = new SessionManager(this);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminForgotPasswordActivity.super.onBackPressed();
            }
        });
        btnRequestOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailEditTxt.length() != 0 && !(emailEditTxt.getText().length() < 10)) {
                    forgotPassword(emailEditTxt.getText().toString().trim());
                }
            }
        });
    }

    private void forgotPassword(String email) {
        // Create JSON object for the request
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // Early return if JSON creation fails
        }

        Log.d("LoginPayload", jsonObject.toString());

        // Show progress dialog while logging in (you can replace this with a ProgressBar in the UI)
        ProgressDialog progressDialog = new ProgressDialog(AdminForgotPasswordActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // This line should be called before the startActivity() to avoid the WindowLeaked error
        progressDialog.show();

        // Create JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, serverUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Dismiss the ProgressDialog here before finishing or transitioning to a new activity
                        progressDialog.dismiss();

                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(AdminForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                            // Start the AdminDashboardActivity and finish the current activity
                            Intent intent = new Intent(AdminForgotPasswordActivity.this, AdminLoginActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            Toast.makeText(AdminForgotPasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Dismiss the ProgressDialog here as well
                progressDialog.dismiss();

                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response and display the message
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        Toast.makeText(AdminForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.e("AdminLoginActivity", errorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        MySingleton.getInstance(AdminForgotPasswordActivity.this).addToRequestQueue(jsonObjectRequest);
    }

}