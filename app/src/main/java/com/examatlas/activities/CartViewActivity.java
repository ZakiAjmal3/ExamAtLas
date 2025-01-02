package com.examatlas.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.CartViewAdapter;
import com.examatlas.models.CartViewModel;
import com.examatlas.models.Books.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartViewActivity extends AppCompatActivity {
    RecyclerView bookCartRecyclerView;
    CartViewAdapter cartViewAdapter;
    CartViewModel cartViewModel;
    ArrayList<CartViewModel> cartViewModelArrayList;
    Toolbar toolbar;
    SessionManager sessionManager;
    String cartUrl,authToken;
    RelativeLayout noDataLayout, priceDetailRelativeLayout,bottomStickyButtonLayout;
    ProgressBar progressBar;
    TextView priceItemsTxt,priceOriginalTxt,totalDiscountTxt,deliveryTxt,totalAmountTxt1,totalAmountTxt2;
    Button goToCheckOutBTn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);

        toolbar = findViewById(R.id.hardbook_ecomm_cart_toolbar);
        bookCartRecyclerView = findViewById(R.id.cartItemRecycler);
        noDataLayout = findViewById(R.id.noDataLayout);
        priceDetailRelativeLayout = findViewById(R.id.priceRelativeLayout);
        bottomStickyButtonLayout = findViewById(R.id.bottomStickyRelativeLayout);
        progressBar = findViewById(R.id.cartProgress);
        goToCheckOutBTn = findViewById(R.id.gotoCheckOut);

        priceItemsTxt = findViewById(R.id.priceAndItemstxt);
        priceOriginalTxt = findViewById(R.id.priceTxt);
        totalDiscountTxt = findViewById(R.id.discountTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalAmountTxt1 = findViewById(R.id.totalAmountPriceTxt);
        totalAmountTxt2 = findViewById(R.id.bottomStickyAmountTxt);

        sessionManager = new SessionManager(this);
        cartViewModelArrayList = new ArrayList<>();

        bookCartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartUrl = Constant.BASE_URL + "cart/get/" + sessionManager.getUserData().get("user_id");
        authToken = sessionManager.getUserData().get("authToken");
        setupToolbar();
        fetchCartItems();

        goToCheckOutBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartViewActivity.this, CreateDeliveryAddressActivity.class);
                startActivity(intent);
            }
        });

    }

    @SuppressLint("ResourceType")
    private void setUpPriceDetails() {

        int totalItems,totalOriginalPrice = 0,totalSellPrice = 0,totalDiscount = 0,totalDelivery = 0;

        totalItems = cartViewModelArrayList.size();

        for (int i = 0; i<cartViewModelArrayList.size(); i++){
            int origPrice = Integer.parseInt(cartViewModelArrayList.get(i).getQuantity()) * Integer.parseInt(cartViewModelArrayList.get(i).getPrice());
            int sellPrice = Integer.parseInt(cartViewModelArrayList.get(i).getQuantity()) * Integer.parseInt(cartViewModelArrayList.get(i).getSellPrice());
            totalOriginalPrice = totalOriginalPrice + origPrice;
            totalSellPrice = totalSellPrice + sellPrice;
        }

        totalDiscount = totalOriginalPrice - totalSellPrice;

        priceItemsTxt.setText("Price (" + totalItems + " items)");
        priceOriginalTxt.setText("₹ " +totalOriginalPrice);
        totalDiscountTxt.setText("- ₹ " +totalDiscount);
        totalDiscountTxt.setTextColor(Color.GREEN);
        totalAmountTxt1.setText("₹ " +totalSellPrice);
        totalAmountTxt2.setText("₹ " +totalSellPrice);

        if (totalSellPrice > 1000){
            deliveryTxt.setText("FREE DELIVERY");
            deliveryTxt.setTextColor(Color.GREEN);
        }else {
            deliveryTxt.setText("₹ " +100);
        }

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }
    public void fetchCartItems() {
        progressBar.setVisibility(View.VISIBLE);
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, cartUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");

                            if (status) {
                                bookCartRecyclerView.setVisibility(View.VISIBLE);
                                priceDetailRelativeLayout.setVisibility(View.VISIBLE);
                                bottomStickyButtonLayout.setVisibility(View.VISIBLE);
                                noDataLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);

                                JSONObject jsonObject = response.getJSONObject("cart");
                                String cartId = jsonObject.getString("_id");
                                JSONArray jsonArray = jsonObject.getJSONArray("items");
                                cartViewModelArrayList.clear(); // Clear the list before adding new items
                                ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String bookIdData = jsonObject2.getString("bookId");
                                    if (bookIdData == null || bookIdData.equals("null"))
                                        continue;
                                    String itemId = jsonObject2.getString("_id");
                                    String quantity = jsonObject2.getString("quantity");

                                    JSONObject jsonObject3 = jsonObject2.getJSONObject("bookId");
                                    String bookId = jsonObject3.getString("_id");
                                    String type  = jsonObject3.getString("type");
                                    String title = jsonObject3.getString("title");
                                    String keyword = jsonObject3.getString("keyword");
                                    String stock = jsonObject3.getString("stock");
                                    String price = jsonObject3.getString("price");
                                    String sellPrice = jsonObject3.getString("sellPrice");
                                    String content = jsonObject3.getString("content");
                                    String author = jsonObject3.getString("author");
                                    String categoryId = jsonObject3.getString("categoryId");
                                    String subCategoryId = jsonObject3.getString("subCategoryId");
                                    String subjectId = jsonObject3.getString("subjectId");
                                    String createdDate = jsonObject3.getString("createdAt");
                                    String updatedAt = jsonObject3.getString("updatedAt");

                                    JSONArray jsonArray3 = jsonObject3.getJSONArray("images");

                                    for (int j = 0; j < jsonArray3.length(); j++) {
                                        JSONObject jsonObject4 = jsonArray3.getJSONObject(j);
                                        BookImageModels bookImageModels = new BookImageModels(
                                                jsonObject4.getString("url"),
                                                jsonObject4.getString("filename")
                                        );
                                        bookImageArrayList.add(bookImageModels);
                                    }

                                    // Use StringBuilder for tags
                                    StringBuilder tags = new StringBuilder();
                                    JSONArray jsonArray1 = jsonObject3.getJSONArray("tags");
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        String singleTag = jsonArray1.getString(j);
                                        tags.append(singleTag).append(", ");
                                    }
                                    // Remove trailing comma and space if any
                                    if (tags.length() > 0) {
                                        tags.setLength(tags.length() - 2);
                                    }

                                    cartViewModel = new CartViewModel(cartId, itemId, bookId,type, title, keyword,stock, price, sellPrice, content, author, categoryId,subCategoryId,subjectId, tags.toString(),bookImageArrayList, createdDate, updatedAt, quantity);
                                    cartViewModelArrayList.add(cartViewModel);
                                }
                                // If you have already created the adapter, just notify the change
                                if (cartViewModelArrayList.isEmpty()) {
                                    Toast.makeText(CartViewActivity.this, "654", Toast.LENGTH_LONG).show();

                                    noDataLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (cartViewAdapter == null) {

                                        bookCartRecyclerView.setVisibility(View.VISIBLE);
                                        priceDetailRelativeLayout.setVisibility(View.VISIBLE);
                                        bottomStickyButtonLayout.setVisibility(View.VISIBLE);
                                        noDataLayout.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        cartViewAdapter = new CartViewAdapter(CartViewActivity.this, cartViewModelArrayList);
                                        bookCartRecyclerView.setAdapter(cartViewAdapter);
                                    } else {
                                        cartViewAdapter.notifyDataSetChanged();
                                    }
                                }
                                setUpPriceDetails();
                            } else {
                                bookCartRecyclerView.setVisibility(View.GONE);
                                priceDetailRelativeLayout.setVisibility(View.GONE);
                                bottomStickyButtonLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                noDataLayout.setVisibility(View.VISIBLE);
                                // Handle the case where status is false
                                String message = response.getString("message");
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
                        Toast.makeText(CartViewActivity.this, message, Toast.LENGTH_LONG).show();

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
        MySingleton.getInstance(CartViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}