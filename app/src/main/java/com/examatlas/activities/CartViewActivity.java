package com.examatlas.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.BlogAdapter;
import com.examatlas.adapter.CartViewAdapter;
import com.examatlas.fragment.BlogFragment;
import com.examatlas.models.BlogModel;
import com.examatlas.models.CartViewModel;
import com.examatlas.models.extraModels.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.MySingletonFragment;
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
    RelativeLayout noDataLayout;
    ProgressBar progressBar;
    Button goToBillingBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);

        toolbar = findViewById(R.id.hardbook_ecomm_cart_toolbar);
        bookCartRecyclerView = findViewById(R.id.cartItemRecycler);
        noDataLayout = findViewById(R.id.noDataLayout);
        progressBar = findViewById(R.id.cartProgress);
        goToBillingBtn = findViewById(R.id.goToBillingBtn);

        sessionManager = new SessionManager(this);
        cartViewModelArrayList = new ArrayList<>();

        bookCartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartUrl = Constant.BASE_URL + "cart/get/" + sessionManager.getUserData().get("user_id");
        authToken = sessionManager.getUserData().get("authToken");
        setupToolbar();
        fetchCartItems();
    }
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }
    private void fetchCartItems() {
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
                                                jsonObject4.getString("filename"),
                                                jsonObject4.getString("contentType"),
                                                jsonObject4.getString("size"), // Assuming size is an integer
                                                jsonObject4.getString("uploadDate"),
                                                jsonObject4.getString("_id")
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
                                        noDataLayout.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        cartViewAdapter = new CartViewAdapter(CartViewActivity.this, cartViewModelArrayList);
                                        bookCartRecyclerView.setAdapter(cartViewAdapter);
                                    } else {
                                        cartViewAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                bookCartRecyclerView.setVisibility(View.GONE);
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
                        String responseData = new String(error.networkResponse.data, "UTF-8");
                        errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                        errorMessage += "\nResponse Data: " + responseData;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(CartViewActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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