package com.examatlas.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.HardBookECommPurchaseAdapter;
import com.examatlas.adapter.WishListAdapter;
import com.examatlas.models.HardBookECommPurchaseModel;
import com.examatlas.models.WishListModel;
import com.examatlas.models.extraModels.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WishlistActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView wishlistRecyclerView;
    private WishListAdapter wishListAdapter;
    private ArrayList<WishListModel> wishListModelArrayList;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private String token;
    private RelativeLayout noDataLayout;
    private SearchView searchView;
    private String wishListUrl = Constant.BASE_URL + "wishlist/getWishlist/";
    ImageView cartIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupSearchView();
        getAllWishlistItems();

    }


    private void initializeViews() {
        toolbar = findViewById(R.id.hardbook_ecomm_wishlist_toolbar);
        progressBar = findViewById(R.id.progressBar);
        wishlistRecyclerView = findViewById(R.id.wishListRecycler);
        noDataLayout = findViewById(R.id.noDataLayout);
        searchView = findViewById(R.id.search_icon);
        cartIcon = findViewById(R.id.cartBtn);
        wishListModelArrayList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");
        wishListUrl = wishListUrl + sessionManager.getUserData().get("user_id");
        progressBar.setVisibility(View.VISIBLE);
        wishlistRecyclerView.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
    }
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }
    private void setupRecyclerView() {
        wishlistRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSearchView() {
        searchView.setOnClickListener(view -> openKeyboard());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (wishListAdapter != null) {
                    wishListAdapter.filter(newText);
                }
                return true;
            }
        });
    }

    private void openKeyboard() {
        searchView.setIconified(false);
        searchView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    private void getAllWishlistItems() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, wishListUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            wishlistRecyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("wishlistItems");
                                wishListModelArrayList.clear();

                                // Parse wishlist items
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String itemId = jsonObject2.getString("_id");
                                    JSONObject bookObject = jsonObject2.getJSONObject("bookId");

                                    ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();
                                    JSONArray jsonArray3 = bookObject.getJSONArray("images");
                                    for (int j = 0; j < jsonArray3.length(); j++) {
                                        JSONObject jsonObject3 = jsonArray3.getJSONObject(j);
                                        BookImageModels bookImageModels = new BookImageModels(
                                                jsonObject3.getString("url"),
                                                jsonObject3.getString("filename"),
                                                jsonObject3.getString("contentType"),
                                                jsonObject3.getString("size"), // Assuming size is an integer
                                                jsonObject3.getString("uploadDate"),
                                                jsonObject3.getString("_id")
                                        );
                                        bookImageArrayList.add(bookImageModels);
                                    }

                                    WishListModel wishListModel = new WishListModel(
                                            itemId,
                                            bookObject.getString("_id"),
                                            bookObject.getString("type"),
                                            bookObject.getString("title"),
                                            bookObject.getString("keyword"),
                                            bookObject.getString("stock"),
                                            bookObject.getString("price"),
                                            bookObject.getString("sellPrice"),
                                            bookObject.getString("content"),
                                            bookObject.getString("author"),
                                            bookObject.getString("categoryId"),
                                            bookObject.getString("subCategoryId"),
                                            bookObject.getString("subjectId"),
                                            parseTags(bookObject.getJSONArray("tags")),
                                            bookObject.getString("bookUrl"),
                                            bookImageArrayList,
                                            bookObject.getString("createdAt"),
                                            bookObject.getString("updatedAt")
                                    );
                                    wishListModelArrayList.add(wishListModel);
                                }

                                if (wishListModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                } else {
                                    if (wishListAdapter == null) {
                                        wishListAdapter = new WishListAdapter(WishlistActivity.this, wishListModelArrayList);
                                        wishlistRecyclerView.setAdapter(wishListAdapter);
                                    } else {
                                        wishListAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                Toast.makeText(WishlistActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(WishlistActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("JSON_ERROR", "Error parsing JSON response: " + e.getMessage());
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
                        Toast.makeText(WishlistActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(WishlistActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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

    private String parseTags(JSONArray tagsArray) throws JSONException {
        StringBuilder tags = new StringBuilder();
        for (int j = 0; j < tagsArray.length(); j++) {
            tags.append(tagsArray.getString(j)).append(", ");
        }
        if (tags.length() > 0) {
            tags.setLength(tags.length() - 2); // Remove trailing comma and space
        }
        return tags.toString();
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