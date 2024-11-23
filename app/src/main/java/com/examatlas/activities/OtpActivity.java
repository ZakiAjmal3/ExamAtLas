package com.examatlas.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.chaos.view.PinView;
import com.examatlas.R;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OtpActivity extends AppCompatActivity {
    MaterialButton btnVerifyOtp;
    ProgressBar progressBar;
    LinearLayout parentLayout;
    TextView numberTxt;
    PinView otpView;
    String task, token, otp;
    String userNumber,userEmail,userFirstName,userLastName,userPhone,userState,userCity;
    private final String serverUrl = Constant.BASE_URL + "auth/createUser";
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        numberTxt = findViewById(R.id.numberTxt);
        otpView = findViewById(R.id.otpView);
        parentLayout = findViewById(R.id.parentLayout);
        progressBar = findViewById(R.id.progressBar);

        sessionManager = new SessionManager(this);
        task = getIntent().getStringExtra("task");
        userEmail = getIntent().getStringExtra("email");
        if (getIntent().getStringExtra("firstName") != null) {
            userFirstName = getIntent().getStringExtra("firstName");
            userLastName = getIntent().getStringExtra("lastName");
            userNumber = getIntent().getStringExtra("phone");
            userState = getIntent().getStringExtra("state");
            userCity = getIntent().getStringExtra("city");
        }

        numberTxt.setText(userNumber);

        otpView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {
                    btnVerifyOtp.setEnabled(true);
                    verifyOtp(otpView.getText().toString().trim());
                } else {
                    btnVerifyOtp.setEnabled(false);
                }
            }
        });

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

    public void verifyOtp( String otp) {
        String verifyEmailUrl = Constant.BASE_URL + "otp/verifyEmail";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",userEmail);
            jsonObject.put("otp",otp);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(OtpActivity.this);
        progressDialog.setMessage("Verifying OTP...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, verifyEmailUrl, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status = response.getString("success");
                                    String message = response.getString("message");
                                    Toast.makeText(OtpActivity.this, message, Toast.LENGTH_SHORT).show();

                                    if (status.equals("true")) {
                                        progressDialog.dismiss();
                                        if (task.equals("signUp")) {
                                            createUser();
                                        }else {
                                            signInUser();
                                        }
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(OtpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(OtpActivity.this, message, Toast.LENGTH_LONG).show();
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
                MySingleton.getInstance(OtpActivity.this).addToRequestQueue(jsonObjectRequest);
            }
        },1000);
    }

    private void signInUser() {
        String verifyEmailUrl = Constant.BASE_URL + "auth/signin";

        Log.e("sendingOTP method", verifyEmailUrl);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",userEmail);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(OtpActivity.this);
        progressDialog.setMessage("Verifying USER...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, verifyEmailUrl, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                try {
                                    Log.e("Success log", response.toString());
                                    String status = response.getString("success");
                                    String message = response.getString("message");
                                    Toast.makeText(OtpActivity.this, message, Toast.LENGTH_SHORT).show();

                                    if (status.equals("true")) {
                                        String authToken = response.getString("token");
                                        JSONObject userDataJson = response.getJSONObject("data");
                                        String firstName = userDataJson.getString("firstname");
                                        String lastName = userDataJson.getString("lastname");
                                        String email = userDataJson.getString("email");
                                        String user_id = userDataJson.getString("_id");
                                        String role = userDataJson.getString("role");
                                        String isActive = userDataJson.getString("isActive");
                                        String createdAt = userDataJson.getString("createdAt");
                                        String updatedAt = userDataJson.getString("updatedAt");
                                        String step = userDataJson.getString("step");
                                        JSONObject addressObj = userDataJson.getJSONObject("address");
                                        String state = addressObj.getString("state");
                                        String city = addressObj.getString("city");

                                        // Step 8: Process the "organisation" array from the response
                                        JSONArray organisationArray = userDataJson.getJSONArray("organisation");
                                        for (int i = 0; i < organisationArray.length(); i++) {
                                            // If you want to store or process organisation IDs
                                            String organisationId = organisationArray.getString(i);
                                            Log.d("Organisation", "Organisation ID: " + organisationId);
                                        }
                                        sessionManager.saveLoginDetails2(user_id, firstName, lastName, email, state, city, role, isActive, step, authToken, createdAt, updatedAt, null);
                                        if (isActive.equals("true")) {
                                            Intent intent = new Intent(OtpActivity.this, DashboardActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (step.equals("1")) {
                                            Intent intent = new Intent(OtpActivity.this, SignUpActivity5CategorySelect.class);
                                            intent.putExtra("task", task);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                } catch (JSONException e) {

                                    Toast.makeText(OtpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(OtpActivity.this, message, Toast.LENGTH_LONG).show();
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
                MySingleton.getInstance(OtpActivity.this).addToRequestQueue(jsonObjectRequest);
            }
        },1000);
    }
    public void createUser() {

        JSONObject addressObject = new JSONObject();
        try {
            addressObject.put("state",userState);
            addressObject.put("city",userCity);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstname", userFirstName);
            jsonObject.put("lastname", userLastName);
            jsonObject.put("phone", userPhone);
            jsonObject.put("email", userEmail);
            jsonObject.put("address", addressObject);
            jsonObject.put("step", 1);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("LoginPayload", jsonObject.toString());
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(OtpActivity.this);
        progressDialog.setMessage("Creating User...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, serverUrl, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                try {
                                    Log.e("Create User response",response.toString());
                                    String status = response.getString("status");
                                    String message = response.getString("message");
                                    Toast.makeText(OtpActivity.this, message, Toast.LENGTH_SHORT).show();

                                    if (status.equals("true")) {
                                        String authToken = null;
                                        JSONObject userDataJson = response.getJSONObject("data");
                                        String firstName = userDataJson.getString("firstname");
                                        String lastName = userDataJson.getString("lastname");
//                                String mobile = userDataJson.getString("phone");
                                        String email = userDataJson.getString("email");
                                        String user_id = userDataJson.getString("_id");
                                        String role = userDataJson.getString("role");
                                        String isActive = userDataJson.getString("isActive");
                                        String createdAt = userDataJson.getString("createdAt");
                                        String updatedAt = userDataJson.getString("updatedAt");
                                        String step = userDataJson.getString("step");
                                        JSONObject addressObj = userDataJson.getJSONObject("address");
                                        String state = addressObj.getString("state");
                                        String city = addressObj.getString("city");
                                        sessionManager.saveLoginDetails2(user_id,firstName,lastName,email,state,city,role,isActive,step,authToken,createdAt,updatedAt,null);
                                        Intent intent = new Intent(OtpActivity.this, SignUpActivity5CategorySelect.class);
                                        startActivity(intent);
                                    }
                                } catch (JSONException e) {

                                    Toast.makeText(OtpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                Toast.makeText(OtpActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
                                Toast.makeText(OtpActivity.this, message, Toast.LENGTH_LONG).show();
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
                MySingleton.getInstance(OtpActivity.this).addToRequestQueue(jsonObjectRequest);
            }
        },1000);
    }
}