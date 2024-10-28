package com.examatlas.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class LoginActivity extends AppCompatActivity {
    TextView txtSignUp, txtForgotPass;
    LinearLayout parentLayout;
    ProgressBar progressBar;
    EditText edtNumber, edtPassword;
    MaterialButton btnLogin;
    private ImageView eyePassword;
    private boolean isPasswordVisible = false;
    private final String serverUrl = Constant.BASE_URL + "auth/loginUser";
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtSignUp = findViewById(R.id.adminNextBtn);
        txtForgotPass = findViewById(R.id.txtForgotPass);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        edtNumber = findViewById(R.id.edtNumber);
        edtPassword = findViewById(R.id.edtPassword);
        eyePassword = findViewById(R.id.eyePassword);
        parentLayout = findViewById(R.id.parentLayout);

        sessionManager = new SessionManager(this);

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        eyePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible) {
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isPasswordVisible = false;
                    eyePassword.setImageResource(R.drawable.eye_open); // Set the eye open drawable
                } else {
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isPasswordVisible = true;
                    eyePassword.setImageResource(R.drawable.eye_close); // Set the eye close drawable
                }
                edtPassword.setSelection(edtPassword.getText().length()); // Move cursor to the end
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = edtNumber.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("mobile", number);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                Log.d("LoginPayload", jsonObject.toString());
                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Logging in...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, serverUrl, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                try {
                                    String status = response.getString("status");
                                    String message = response.getString("message");
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                                    if (status.equals("true")) {
                                        String user_id = response.getString("userId");
                                        String authToken = response.getString("token");
                                        JSONObject userDataJson = response.getJSONObject("data");
                                        String name = userDataJson.getString("name");
                                        String mobile = userDataJson.getString("mobile");
                                        String email = userDataJson.getString("email");
                                        String role = userDataJson.getString("role");
                                        String createdAt = userDataJson.getString("createdAt");
                                        String updatedAt = userDataJson.getString("updatedAt");
                                        sessionManager.saveLoginDetails(user_id,name,email,mobile,role,authToken,createdAt,updatedAt);
                                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
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
                MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);
            }
        });
    }
}