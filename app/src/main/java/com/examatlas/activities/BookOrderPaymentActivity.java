package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.extraAdapter.BookOrderSummaryItemsDetailsRecyclerViewAdapter;
import com.examatlas.models.extraModels.BookOrderSummaryItemsDetailsRecyclerViewModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookOrderPaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    String orderId,razorpayOrderID;
    int totalAmount;
    SessionManager sessionManager;
    String authToken,userID,userMobile,userEmail;
    TextView referenceNoId,orderIdTxt,totalAmountTxt,statusTxt,paymentMethodTxt,nameTxt,shippingToTxt;
    RecyclerView itemsRecyclerView;
    BookOrderSummaryItemsDetailsRecyclerViewModel bookOrderSummaryItemsDetailsRecyclerViewModel;
    BookOrderSummaryItemsDetailsRecyclerViewAdapter bookOrderSummaryItemsDetailsRecyclerViewAdapter;
    ArrayList<BookOrderSummaryItemsDetailsRecyclerViewModel> bookOrderSummaryItemsDetailsRecyclerViewModelArrayList = new ArrayList<>();
    ImageView backImgBtn;
    Button backToHomeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_payment);

        Checkout.preload(getApplicationContext());
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_Py5aXtaPQ5j9nu");

        referenceNoId = findViewById(R.id.referenceNoTxt);
        orderIdTxt = findViewById(R.id.priceDetailsTxt);
        totalAmountTxt = findViewById(R.id.priceTxt);
        statusTxt = findViewById(R.id.paidTxt);
        paymentMethodTxt = findViewById(R.id.deliveryTxt);
        nameTxt = findViewById(R.id.nameTxt);
        shippingToTxt = findViewById(R.id.deliveryAddressTxt);

        backImgBtn = findViewById(R.id.backImgBtn);
        backToHomeBtn = findViewById(R.id.backToHomebtn);

        totalAmount = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("totalAmount")));
        orderId = Objects.requireNonNull(getIntent().getStringExtra("orderId"));
        razorpayOrderID = Objects.requireNonNull(getIntent().getStringExtra("razorpay_order_id"));

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");
        userMobile = sessionManager.getUserData().get("mobile");
        userEmail = sessionManager.getUserData().get("email");
        userID = sessionManager.getUserData().get("user_id");

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookOrderPaymentActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookOrderPaymentActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });


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
        verifyPaymentStatus(razorpayPaymentID,paymentData);
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
            jsonBody.put("isApp", true);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, orderDetailsURL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("responseData", response.toString());
                            try {
                                String success = response.getString("success");
                                if (success.equals("true")) {
                                    String message = response.getString("message");
                                    Toast.makeText(BookOrderPaymentActivity.this, message, Toast.LENGTH_SHORT).show();
                                    getOrderDetails(razorpayPaymentID,paymentData);
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

        String orderDetailsURL = "https://examatlas-backend.onrender.com/api/payment/getOneOrderByUserId/" + userID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, orderDetailsURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responseData",response.toString());
                        try {
                            String status = response.getString("success");
                            if (status.equals("true")) {

                                JSONObject jsonObject1 = response.getJSONObject("order");
                                String orderID = jsonObject1.getString("_id");
                                String totalAmount = jsonObject1.getString("totalAmount");
                                String paymentMethod = jsonObject1.getString("paymentMethod");
                                String paymentStatus = jsonObject1.getString("status");

                                orderID = "Order ID: " + orderID;

                                referenceNoId.setText(orderID);
                                orderIdTxt.setText(orderID);

                                totalAmountTxt.setText("₹ " +totalAmount);

                                statusTxt.setText(paymentStatus);
                                if (paymentStatus.equalsIgnoreCase("paid")){
                                    statusTxt.setTextColor(getResources().getColor(R.color.green));
                                }else if(paymentStatus.equalsIgnoreCase("pending")){
                                    statusTxt.setTextColor(getResources().getColor(R.color.mat_yellow));
                                }else {
                                    statusTxt.setTextColor(getResources().getColor(R.color.red));
                                }
                                paymentMethodTxt.setText(paymentMethod);

                                JSONObject jsonObject2 = jsonObject1.getJSONObject("billingDetail");

                                String firstName = jsonObject2.getString("firstName");
                                String lastName = jsonObject2.getString("lastName");
                                String country = jsonObject2.getString("country");
                                String streetAddress = jsonObject2.getString("streetAddress");
                                String apartment = jsonObject2.getString("apartment");
                                String city = jsonObject2.getString("city");
                                String state = jsonObject2.getString("state");
                                String zipCode = jsonObject2.getString("pinCode");
                                String phone = jsonObject2.getString("phone");
                                String email = jsonObject2.getString("email");

                                String completeName = firstName + " " + lastName;
                                String completeAddress = apartment + ", " + streetAddress + ", " + city + ", " + state + ", " + zipCode + ", " + country + ", " + phone + ", " + email + ".";

                                nameTxt.setText(completeName);
                                shippingToTxt.setText(completeAddress);

                                JSONArray jsonArray1 = jsonObject1.getJSONArray("items");
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jsonObject3 = jsonArray1.getJSONObject(i);
                                    String itemName = jsonObject3.getString("title");
                                    String itemPrice = jsonObject3.getString("sellPrice");
                                    String itemQuantity = jsonObject3.getString("quantity");
                                    bookOrderSummaryItemsDetailsRecyclerViewModel = new BookOrderSummaryItemsDetailsRecyclerViewModel(itemName,itemPrice,itemQuantity);
                                    bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.add(bookOrderSummaryItemsDetailsRecyclerViewModel);
                                }
                                bookOrderSummaryItemsDetailsRecyclerViewAdapter = new BookOrderSummaryItemsDetailsRecyclerViewAdapter(BookOrderPaymentActivity.this,bookOrderSummaryItemsDetailsRecyclerViewModelArrayList);
                                itemsRecyclerView.setAdapter(bookOrderSummaryItemsDetailsRecyclerViewAdapter);

                            } else {
                                // Handle the case where success is not true
                                Toast.makeText(BookOrderPaymentActivity.this, "Order retrieval failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(BookOrderPaymentActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", "Error: " + error.toString());
                String errorMessage = "Error retrieving order details.";
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        errorMessage = message; // Update error message if available
                    } catch (Exception e) {
                        Log.e("JSON_ERROR", "Error parsing error JSON: " + e.getMessage());
                    }
                }
                Toast.makeText(BookOrderPaymentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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