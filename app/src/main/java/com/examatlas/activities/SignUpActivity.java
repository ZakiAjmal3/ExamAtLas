package com.examatlas.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.utils.Constant;
import com.examatlas.utils.SessionManager;
import com.examatlas.utils.MySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    TextView txtLogin;
    ImageView eyePassword, eyeRePassword;
    ProgressBar progressBar;
    LinearLayout parentLayout;
    ArrayAdapter<String> arrayAdapter;
    MaterialButton btnSignUp;
    EditText edtName, edtEmail, edtMobile, edtPassword, edtRePassword;
    MaterialCheckBox checkBox;
    boolean isAllFieldsChecked = false, isPasswordVisible = false, isRePasswordVisible = false;
    private final String serverUrl = Constant.BASE_URL + "auth/createUser";
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtLogin = findViewById(R.id.txtLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtMobile = findViewById(R.id.edtMobile);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        checkBox = findViewById(R.id.checkBox);
        parentLayout = findViewById(R.id.parentLayout);
        eyePassword = findViewById(R.id.eyePassword);
        eyeRePassword = findViewById(R.id.eyeRePassword);
        progressBar = findViewById(R.id.progressBar);

        sessionManager = new SessionManager(this);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.super.onBackPressed();
            }
        });

        eyePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible) {
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isPasswordVisible = false;
                    Glide.with(getApplicationContext())
                            .load(R.drawable.eye_open)
                            .into(eyePassword);
                } else {
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isPasswordVisible = true;
                    Glide.with(getApplicationContext())
                            .load(R.drawable.eye_close)
                            .into(eyePassword);
                }
            }
        });

        eyeRePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRePasswordVisible) {
                    edtRePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isRePasswordVisible = false;
                    Glide.with(getApplicationContext())
                            .load(R.drawable.eye_open)
                            .into(eyeRePassword);
                } else {
                    edtRePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isRePasswordVisible = true;
                    Glide.with(getApplicationContext())
                            .load(R.drawable.eye_close)
                            .into(eyeRePassword);
                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllFieldsChecked = checkAllFields();
                if (isAllFieldsChecked) {
                    signUpUser(edtName.getText().toString().trim()
                            , edtEmail.getText().toString().trim()
                            , edtMobile.getText().toString().trim()
                            , edtPassword.getText().toString().trim()
                            , edtRePassword.getText().toString().trim());
                }
            }
        });
    }

    private void signUpUser(String name, String email, String mobile, String password, String repassword) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("email", email);
            jsonObject.put("mobile", mobile);
            jsonObject.put("password", password);
            jsonObject.put("confirmPassword", repassword);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("SignUpPayload", jsonObject.toString());
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, serverUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("status");
                            if (status.equals("true")) {
                                String message = response.getString("message");
                                Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if (status.equals("false")) {
                                    String message = response.getString("message");
                                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                        } catch (JSONException e) {
                            Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("SignUpActivity", errorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        MySingleton.getInstance(SignUpActivity.this).addToRequestQueue(jsonObjectRequest);
    }
    private boolean checkAllFields() {
        if (edtName.length() == 0) {
            Snackbar.make(parentLayout, "Please Enter Name", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
            return false;
        }
        if (edtEmail.length() == 0) {
            Snackbar.make(parentLayout, "Please Enter Email", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
            return false;
        }
        if (edtMobile.length() == 0) {
            Snackbar.make(parentLayout, "Please Enter Mobile Number", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
            return false;
        }
        if (edtPassword.length() == 0) {
            Snackbar.make(parentLayout, "Please Enter Password", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
            return false;
        }
        if (edtRePassword.length() == 0) {
            Snackbar.make(parentLayout, "Please Re-Enter Password", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
            return false;
        }
        if (!edtPassword.getText().toString().trim().equals(edtRePassword.getText().toString().trim())) {
            Snackbar.make(parentLayout, "Password not match. Please check your password", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
            return false;
        }
        if (!checkBox.isChecked()) {
            Snackbar.make(parentLayout, "Please Agree To Our Terms & Conditions", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show();
            return false;
        }
        return true;
    }
}