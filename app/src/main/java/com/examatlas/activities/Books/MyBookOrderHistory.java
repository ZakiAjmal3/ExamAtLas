package com.examatlas.activities.Books;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.adapter.books.MyOrdersItemsAdapter;
import com.examatlas.adapter.books.SearchingActivityAdapter;
import com.examatlas.fragment.BookFilteringOrdersFragment;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.MyOrdersItemModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyBookOrderHistory extends AppCompatActivity {
    ImageView backBtn,searchIcon,cartBtn;
    TextView cartItemQuantityTxt,noMoreOrderTxt,resetFilterTxt;
    EditText searchOrderEdtTxt;
    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView orderHistoryRV;
    ArrayList<MyOrdersItemModel> myOrdersItemModelArrayList;
    ArrayList<AllBooksModel> booksModelArrayList;
    MyOrdersItemsAdapter myOrdersItemsAdapter;
    SessionManager sessionManager;
    String token;
    RelativeLayout filterRL,orderHistoryRL,searchingFilterRL,noMoreOrderRL;
    FrameLayout filterFrameLayout;
    String pageNumber = "1",pageSize = "10",searchQuery = "", orderFilterDate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_order_history);

        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");

        backBtn = findViewById(R.id.backBtn);
        searchIcon = findViewById(R.id.searchIcon);
        cartBtn = findViewById(R.id.cartBtn);
        cartItemQuantityTxt = findViewById(R.id.cartItemCountTxt);

        shimmerFrameLayout = findViewById(R.id.shimmer_container);
        orderHistoryRV = findViewById(R.id.orderHistoryRV);

        searchOrderEdtTxt = findViewById(R.id.edtSearch);

        filterRL = findViewById(R.id.filterRL);
        searchingFilterRL = findViewById(R.id.searchFilterRL);
        searchingFilterRL.setVisibility(View.VISIBLE);
        orderHistoryRL = findViewById(R.id.orderHistoryRL);
        orderHistoryRL.setVisibility(View.VISIBLE);
        filterFrameLayout = findViewById(R.id.filterFragment);
        filterFrameLayout.setVisibility(View.GONE);
        noMoreOrderRL = findViewById(R.id.noMoreOrderRL);
        noMoreOrderRL.setVisibility(View.VISIBLE);

        noMoreOrderTxt = findViewById(R.id.noMoreOrderTxt);
        resetFilterTxt = findViewById(R.id.resetFilterTxt);

        orderHistoryRV.setLayoutManager(new LinearLayoutManager(this));
        booksModelArrayList = new ArrayList<>();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        orderHistoryRV.setVisibility(View.GONE);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyBookOrderHistory.this, CartViewActivity.class);
                startActivity(intent);
            }
        });
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyBookOrderHistory.this,SearchingBooksActivity.class));
            }
        });
        String quantity = sessionManager.getCartQuantity();
        if (!quantity.equals("0")) {
            cartItemQuantityTxt.setVisibility(View.VISIBLE);
            cartItemQuantityTxt.setText(quantity);
        }else {
            cartItemQuantityTxt.setVisibility(View.GONE);
        }
        filterRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderHistoryRL.setVisibility(View.GONE);
                noMoreOrderRL.setVisibility(View.GONE);
                searchingFilterRL.setVisibility(View.GONE);
                filterFrameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.filterFragment, new BookFilteringOrdersFragment())  // Replace the container with the new fragment
                        .commit();
            }
        });

        searchOrderEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                myOrdersItemsAdapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        resetFilterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchQuery = "";
                orderFilterDate = "";
                resetFilterTxt.setVisibility(View.GONE);
                noMoreOrderTxt.setVisibility(View.VISIBLE);
                getAllOrderHistory();
            }
        });

        getAllOrderHistory();
    }
    private void getAllOrderHistory() {
        String paginatedURL = Constant.BASE_URL + "v1/order?pageNumber=" + pageNumber + "&pageSize=" + pageSize + "&status=" + searchQuery + "&orderDate=" + orderFilterDate;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                myOrdersItemModelArrayList = new ArrayList<>();
                                JSONArray dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataObject = dataArray.getJSONObject(i);
                                    JSONArray itemsArray = dataObject.getJSONArray("items");
                                    ArrayList<String> imageUrlArrayList = new ArrayList<>();
                                    // Safely check if 'items' array is present and not empty
                                    if (itemsArray != null && itemsArray.length() > 0) {
                                        // Get the first item from 'items' array
                                        JSONObject bookItemObj = itemsArray.getJSONObject(0);
                                        JSONObject productObj = bookItemObj.getJSONObject("product");
                                        String bookTitle = productObj.getString("title");
                                        String product_id = productObj.getString("_id");

                                        // Extracting product image
                                        JSONArray bookImagesArray = productObj.getJSONArray("images");
                                        String bookImgURL = bookImagesArray.getJSONObject(0).getString("url");

                                        // Extracting order details
                                        String orderStatus = dataObject.getString("status");
                                        String orderId = dataObject.getString("orderId");

                                        // Extracting review information (if present)
                                        String reviewId = "", reviewRating = "", reviewHeadline = "", reviewTxt = "", reviewerUserId = "";
                                        JSONArray reviewJsonArray = dataObject.optJSONArray("reviewData");
                                        if (reviewJsonArray != null && reviewJsonArray.length() > 0) {
                                            JSONObject reviewObj = reviewJsonArray.getJSONObject(0);
                                            reviewId = reviewObj.optString("_id", "");
                                            reviewRating = reviewObj.optString("rating", "");
                                            reviewHeadline = reviewObj.optString("headline", "");
                                            reviewTxt = reviewObj.optString("review", "");

                                            JSONObject createdByObj = reviewObj.optJSONObject("createdBy");
                                            if (createdByObj != null) {
                                                reviewerUserId = createdByObj.optString("userId", "");
                                            }

                                            JSONArray imageObj = reviewObj.optJSONArray("images");
                                            if (imageObj != null) {
                                                for (int k = 0; k < imageObj.length(); k++) {
                                                    String imageUrl = imageObj.getJSONObject(k).optString("url", "");
                                                    imageUrlArrayList.add(imageUrl);
                                                }
                                            }
                                        }

                                        // Extracting tracking details if available
                                        String shipment_id = "";
                                        JSONObject trackingDetail = dataObject.optJSONObject("trackingDetail");
                                        if (trackingDetail != null) {
                                            shipment_id = trackingDetail.optString("shipment_id", "");
                                        }

                                        // Add the order item to the list
                                        myOrdersItemModelArrayList.add(new MyOrdersItemModel(orderStatus, orderId, product_id, shipment_id, bookTitle, bookImgURL, reviewId, reviewRating, reviewHeadline, reviewTxt, reviewerUserId, imageUrlArrayList));
                                    } else {
                                        // Handle empty 'items' array
                                        Log.e("JSON_ERROR", "No items found in order " + dataObject.getString("orderId"));
                                    }
                                }

                                // Update the RecyclerView adapter
                                myOrdersItemsAdapter = new MyOrdersItemsAdapter(MyBookOrderHistory.this, myOrdersItemModelArrayList);
                                orderHistoryRV.setAdapter(myOrdersItemsAdapter);
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                orderHistoryRV.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(MyBookOrderHistory.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(MyBookOrderHistory.this, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
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
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }



//    Dialog filterDialog;
//    boolean isLast30daysSelected = false, isLast90daysSelected = false, isLast180daysSelected = false, isYear2025Selected = false, isYear2024Selected = false, isYear2023Selected = false, isOlderSelected = false;
//    private void openFilterDialog() {
//        filterDialog = new Dialog(this);
//        filterDialog.setContentView(R.layout.filtering_my_orders_dailog_layout);
//
//        filterDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        filterDialog.getWindow().setGravity(Gravity.BOTTOM);
//        filterDialog.getWindow().setDimAmount(0.7f);
//        filterDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        filterDialog.findViewById(R.id.filtering_layout_root).setPadding(0, 0, 0, 0);
//        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
//        filterDialog.findViewById(R.id.filtering_layout_root).startAnimation(slideUp);
//        filterDialog.show();
//    }

    public void filteringMethod(String orderType, String orderDate){
        orderHistoryRL.setVisibility(View.VISIBLE);
        noMoreOrderRL.setVisibility(View.VISIBLE);
        searchingFilterRL.setVisibility(View.VISIBLE);
        filterFrameLayout.setVisibility(View.GONE);
        searchQuery = orderType;
        orderFilterDate = orderDate;
        myOrdersItemModelArrayList.clear();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        orderHistoryRV.setVisibility(View.GONE);
        noMoreOrderTxt.setVisibility(View.GONE);
        resetFilterTxt.setVisibility(View.VISIBLE);
        getAllOrderHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myOrdersItemModelArrayList = new ArrayList<>();
        getAllOrderHistory();
    }
}