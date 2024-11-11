package com.examatlas.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.BookOrderHistoryAdapter;
import com.examatlas.fragment.BlogFragment;
import com.examatlas.fragment.HomeFragment;
import com.examatlas.fragment.LiveCoursesFragment;
import com.examatlas.models.BookOrderHistoryModel;
import com.examatlas.models.extraModels.BookOrderItemsArrayListModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookOrderHistoryActivity extends AppCompatActivity {
    RelativeLayout topBar;
    ImageView imgMenu;
    public String currentFrag = "ORDER";
    SessionManager sessionManager;
    String authToken,userID;
    RecyclerView orderItemsSummaryRecyclerView;
    BookOrderHistoryModel orderHistoryModel;
    BookOrderHistoryAdapter orderHistoryAdapter;
    ArrayList<BookOrderHistoryModel> orderHistoryModelArrayList;
    ArrayList<BookOrderItemsArrayListModel> orderItemsArrayListModelArrayList;
    ProgressBar progressBar;
    TextView noDataTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_history);

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");
        userID = sessionManager.getUserData().get("user_id");

        progressBar = findViewById(R.id.progressBar);
        noDataTxt = findViewById(R.id.noDataTxt);

        topBar = findViewById(R.id.topBar);
        imgMenu = findViewById(R.id.imgMenu);

        orderItemsSummaryRecyclerView = findViewById(R.id.orderHistoryRecyclerView);
        orderItemsSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderHistoryModelArrayList = new ArrayList<>();

        fetchAllOrderHistory();

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void fetchAllOrderHistory(){
        String fetchOrderURl = Constant.BASE_URL + "payment/getOrdersByUserId/" + userID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fetchOrderURl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responseData", response.toString());
                        try {
                            String status = response.getString("success");
                            if (status.equals("true")) {

                                JSONArray jsonArray1 = response.getJSONArray("orders");
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);

                                    String orderID = jsonObject1.getString("_id");
                                    String totalAmount = jsonObject1.getString("totalAmount");
                                    String paymentMethod = jsonObject1.getString("paymentMethod");
                                    String paymentStatus = jsonObject1.getString("status");
                                    String billingIdOfThisOrder = jsonObject1.getString("billingDetailId");

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

                                    JSONArray jsonArray2 = jsonObject1.getJSONArray("items");
                                    orderItemsArrayListModelArrayList = new ArrayList<>();
                                    for (int j = 0; j < jsonArray2.length(); j++) {
                                        JSONObject jsonObject3 = jsonArray2.getJSONObject(j);

                                        String cartId = jsonObject3.getString("cartId");
                                        String itemId = jsonObject3.getString("itemId");
                                        String itemName = jsonObject3.getString("title");
                                        String itemPrice = jsonObject3.getString("sellPrice");
                                        String itemQuantity = jsonObject3.getString("quantity");
                                        String bookId = jsonObject3.getString("bookId");

                                        BookOrderItemsArrayListModel orderItemsArrayListModel = new BookOrderItemsArrayListModel(cartId,itemId,itemName,itemPrice,itemQuantity,bookId);
                                        orderItemsArrayListModelArrayList.add(orderItemsArrayListModel);
                                    }

                                    String razorpay_order_id = jsonObject1.getString("razorpay_order_id");
                                    String razorpay_payment_id = jsonObject1.getString("razorpay_payment_id");
                                    String razorpay_signature = jsonObject1.getString("razorpay_signature");

                                    orderHistoryModel = new BookOrderHistoryModel(orderID,totalAmount,paymentMethod,paymentStatus,billingIdOfThisOrder,completeName,completeAddress,razorpay_order_id,razorpay_payment_id,razorpay_signature,orderItemsArrayListModelArrayList);
                                    orderHistoryModelArrayList.add(orderHistoryModel);
                                }if (!orderHistoryModelArrayList.isEmpty()) {
                                    progressBar.setVisibility(View.GONE);
                                    noDataTxt.setVisibility(View.GONE);
                                    orderItemsSummaryRecyclerView.setVisibility(View.VISIBLE);
                                    orderHistoryAdapter = new BookOrderHistoryAdapter(orderHistoryModelArrayList, BookOrderHistoryActivity.this);
                                    orderItemsSummaryRecyclerView.setAdapter(orderHistoryAdapter);
                                }else {
                                    progressBar.setVisibility(View.GONE);
                                    orderItemsSummaryRecyclerView.setVisibility(View.GONE);
                                    noDataTxt.setVisibility(View.VISIBLE);
                                }
                            }else {
                                progressBar.setVisibility(View.GONE);
                                orderItemsSummaryRecyclerView.setVisibility(View.GONE);
                                noDataTxt.setVisibility(View.VISIBLE);
                                Toast.makeText(BookOrderHistoryActivity.this, "Order retrieval failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(BookOrderHistoryActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BookOrderHistoryActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(BookOrderHistoryActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}