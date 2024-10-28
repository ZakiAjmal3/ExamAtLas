package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookOrderPaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    String orderId,razorpayOrderID;
    int totalAmount;
    SessionManager sessionManager;
    String authToken,userMobile,userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_payment);

        Checkout.preload(getApplicationContext());
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_Py5aXtaPQ5j9nu");

        totalAmount = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("totalAmount")));
        orderId = Objects.requireNonNull(getIntent().getStringExtra("orderId"));
        razorpayOrderID = Objects.requireNonNull(getIntent().getStringExtra("razorpay_order_id"));

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");
        userMobile = sessionManager.getUserData().get("mobile");
        userEmail = sessionManager.getUserData().get("email");

        try {
            JSONObject options = new JSONObject();

            options.put("name", "ExamAtlas");
            options.put("description", "Reference No. #123456");
            options.put("image", "http://example.com/image/rzp.jpg");
            options.put("order_id", orderId);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", totalAmount);//pass amount in currency subunits
            options.put("prefill.email", userEmail);
            options.put("prefill.contact",userMobile);
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(this, options);

        } catch(Exception e) {
        }
    }
    @Override
    public void onPaymentSuccess(String razorpayPaymentID, PaymentData paymentData) {
//        verifyPaymentStatus(razorpayPaymentID,paymentData);
        getOrderDetails(razorpayPaymentID,paymentData);
        Toast.makeText(this, "Payment SuccessFull", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BookOrderPaymentActivity.this, CreateDeliveryAddressActivity.class);
        startActivity(intent);
        finish();
    }

    private void verifyPaymentStatus(String razorpayPaymentID, PaymentData paymentData) {
        String orderDetailsURL = Constant.BASE_URL + "payment/paymentverification";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("razorpay_payment_id", razorpayPaymentID);
            jsonBody.put("razorpay_order_id", razorpayOrderID);
            jsonBody.put("razorpay_signature", paymentData.getSignature());
//            jsonBody.put("userId", sessionManager.getUserData().get("user_id"));
            // Add any other data needed for verification

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, orderDetailsURL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("responseData", response.toString());
                            try {
                                String success = response.getString("success");
                                if (success.equals("true")) {
//                                    getOrderDetails(razorpayPaymentID,paymentData);
                                } else {
                                    // Handle failure case
                                }
                            } catch (JSONException e) {
                                Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = "Error: " + error.toString();
                    if (error.networkResponse != null) {
                        try {
                            // Parse the error response
                            String jsonError = new String(error.networkResponse.data);
                            JSONObject jsonObject = new JSONObject(jsonError);
                            String message = jsonObject.optString("message", "Unknown error");
                            // Now you can use the message
                            Toast.makeText(BookOrderPaymentActivity.this, message, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + authToken);
                    return headers;
                }
            };
            MySingleton.getInstance(BookOrderPaymentActivity.this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error creating JSON: " + e.getMessage());
        }
    }

    private void getOrderDetails(String razorpayPaymentID, PaymentData paymentData) {

        String orderDetailsURL = "https://examatlas-backend.onrender.com/order/" + orderId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, orderDetailsURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responseData",response.toString());
                        try {
                            String status = response.getString("success");
                            if (status.equals("true")){

                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.toString();
                Toast.makeText(BookOrderPaymentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse", errorMessage);
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(BookOrderPaymentActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        MySingleton.getInstance(BookOrderPaymentActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}