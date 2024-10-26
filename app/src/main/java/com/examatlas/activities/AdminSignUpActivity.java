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
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminSignUpActivity extends AppCompatActivity {

    TextView txtLogin;
    ImageView eyePassword, eyeRePassword;
    ProgressBar progressBar;
    LinearLayout parentLayout;
    ArrayAdapter<String> arrayAdapter;
    MaterialButton btnSignUp;
    EditText edtName, edtEmail, edtMobile, edtPassword, edtRePassword;
    MaterialCheckBox checkBox;
    boolean isAllFieldsChecked = false, isPasswordVisible = false, isRePasswordVisible = false;
    private final String serverUrl = Constant.BASE_URL + "user/createUser";
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);

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
                AdminSignUpActivity.super.onBackPressed();
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

        eyePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordVisibility(edtPassword, eyePassword);
            }
        });

        eyeRePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordVisibility(edtRePassword, eyeRePassword);
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
            jsonObject.put("role", "admin");
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
                                Toast.makeText(AdminSignUpActivity.this, message, Toast.LENGTH_SHORT).show();

                                String authToken = response.getString("token");
                                String user_id = response.getString("userId");

                                JSONObject userDataJson = response.getJSONObject("data");
                                String name = userDataJson.getString("name");
                                String email = userDataJson.getString("email");
                                String mobile = userDataJson.getString("mobile");
                                String role = userDataJson.getString("role");
                                String createdAt = userDataJson.getString("createdAt");
                                String updatedAt = userDataJson.getString("updateAt");
                                sessionManager.saveLoginDetails(user_id,name,email,mobile,role,authToken,createdAt,updatedAt);
                                Intent intent = new Intent(AdminSignUpActivity.this, AdminDashboardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if (status.equals("false")) {
                                String message = response.getString("message");
                                Toast.makeText(AdminSignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(AdminSignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        String responseData = new String(error.networkResponse.data, "UTF-8");
                        errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                        errorMessage += "\nResponse Data: " + responseData;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(AdminSignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("AdminSignUpActivity", errorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        MySingleton.getInstance(AdminSignUpActivity.this).addToRequestQueue(jsonObjectRequest);
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
    private void togglePasswordVisibility(EditText editText, ImageView imageView) {
        if (isPasswordVisible) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            isPasswordVisible = false;
            Glide.with(getApplicationContext()).load(R.drawable.eye_open).into(imageView);
        } else {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            isPasswordVisible = true;
            Glide.with(getApplicationContext()).load(R.drawable.eye_close).into(imageView);
        }
    }
}