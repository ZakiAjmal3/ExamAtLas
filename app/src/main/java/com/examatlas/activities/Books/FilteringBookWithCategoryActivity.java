package com.examatlas.activities.Books;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.books.FilteringCategoryAdapter;
import com.examatlas.adapter.books.SearchingActivityAdapter;
import com.examatlas.models.Books.CategoryModel;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilteringBookWithCategoryActivity extends AppCompatActivity {
    RecyclerView booksRecycler;
    ArrayList<CategoryModel> categoryArrayList = new ArrayList<>();
    private ArrayList<AllBooksModel> allBooksModelArrayList;
    private final String bookURL = Constant.BASE_URL + "v1/booksByCategory?categoryId=";
    private final String categoryURL = Constant.BASE_URL + "v1/category";
    private SessionManager sessionManager;
    private String token,categoryName,categoryID;
    TextView categoryNameTxtView,cartItemQuantityTxt,viewAllCategoryTxt,noDataTxt;
    ImageView backBtn,cartBtn;
    int totalPage,totalItems;
    ShimmerFrameLayout bookShimmerLayout;
    EditText searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering_book_with_category);

        getWindow().setStatusBarColor(ContextCompat.getColor(FilteringBookWithCategoryActivity.this,R.color.md_theme_dark_surfaceTint));

        booksRecycler = findViewById(R.id.booksRecycler);
        bookShimmerLayout = findViewById(R.id.shimmer_for_user_container);
        booksRecycler.setVisibility(View.GONE);
        bookShimmerLayout.setVisibility(View.VISIBLE);
        bookShimmerLayout.startShimmer();
        backBtn = findViewById(R.id.backBtn);
        cartBtn = findViewById(R.id.cartBtn);
        booksRecycler.setLayoutManager(new GridLayoutManager(this,2));
        allBooksModelArrayList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");
        categoryNameTxtView = findViewById(R.id.showingCategoryDisplayNameText);

        cartItemQuantityTxt = findViewById(R.id.cartItemCountTxt);
        String quantity = sessionManager.getCartQuantity();
        if (!quantity.equals("0")) {
            cartItemQuantityTxt.setVisibility(View.VISIBLE);
            cartItemQuantityTxt.setText(quantity);
        }else {
            cartItemQuantityTxt.setVisibility(View.GONE);
        }

        viewAllCategoryTxt = findViewById(R.id.viewAllCategoryTxt);
        noDataTxt = findViewById(R.id.noDataTxt);
        searchView = findViewById(R.id.search_icon);
        searchView.setFocusable(false);
        searchView.setClickable(true);

        categoryName = getIntent().getStringExtra("name");
        categoryID = getIntent().getStringExtra("id");
        categoryNameTxtView.setText(categoryName);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FilteringBookWithCategoryActivity.this, CartViewActivity.class));
            }
        });
        viewAllCategoryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrawerDialog();
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilteringBookWithCategoryActivity.this, SearchingBooksActivity.class);
                startActivity(intent);
            }
        });
        getAllBooks();
    }
    Dialog drawerDialog;
    RecyclerView drawerCategoryRecycler;
    MaterialCardView cardBack;
    ShimmerFrameLayout drawerCategoryShimmerLayout;
    private void showDrawerDialog() {
        drawerDialog = new Dialog(FilteringBookWithCategoryActivity.this);
        drawerDialog.setContentView(R.layout.filtering_book_category_drawer_layout);
        drawerDialog.setCancelable(true);

        drawerCategoryRecycler = drawerDialog.findViewById(R.id.categoryNameRecycler);
        cardBack = drawerDialog.findViewById(R.id.cardBack);

        drawerCategoryShimmerLayout = drawerDialog.findViewById(R.id.shimmer_category_container);
        drawerCategoryShimmerLayout.startShimmer();
        drawerCategoryRecycler.setVisibility(View.GONE);

        drawerCategoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        drawerCategoryRecycler.setAdapter(new FilteringCategoryAdapter(categoryArrayList, FilteringBookWithCategoryActivity.this));

        cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
            }
        });

        getAlLCategory();
        drawerDialog.show();
        drawerDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        drawerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        drawerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        drawerDialog.getWindow().setGravity(Gravity.TOP);
        drawerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawerDialog.getWindow().setStatusBarColor(getColor(R.color.seed));
        }
    }

    public void setCategoryName(String name){
        categoryNameTxtView.setText(name);
        drawerDialog.dismiss();
    }
    private void getAlLCategory() {
        String paginatedURL = categoryURL;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            drawerCategoryRecycler.setVisibility(View.VISIBLE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                categoryArrayList.clear();
                                totalPage = Integer.parseInt(response.getString("totalPage"));
                                totalItems = Integer.parseInt(response.getString("totalItems"));

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    // Convert the book object into a Map to make it dynamic
                                    Map<String, Object> categoryData = new Gson().fromJson(jsonObject2.toString(), Map.class);

                                    // Extract image URL from the "imageUrl" field, handle missing image gracefully
                                    JSONObject imgObj = jsonObject2.optJSONObject("imageUrl");
                                    String imageURL = null;
                                    if (imgObj != null) {
                                        imageURL = imgObj.optString("url", ""); // Default to empty string if URL is not present
                                    }
                                    // Handle missing image gracefully
                                    if (imageURL == null || imageURL.isEmpty()) {
                                        imageURL = "default_image_url"; // Placeholder for missing images
                                    }

                                    CategoryModel model = new CategoryModel(categoryData,imageURL); // Pass the map to the model
                                    // Add the model to the list
                                    categoryArrayList.add(model);
                                }

                                // Update UI and adapters
                                drawerCategoryRecycler.setAdapter(new FilteringCategoryAdapter(categoryArrayList, FilteringBookWithCategoryActivity.this));
                                drawerCategoryShimmerLayout.stopShimmer();
                                drawerCategoryShimmerLayout.setVisibility(View.GONE);
                                drawerCategoryRecycler.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(FilteringBookWithCategoryActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
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
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                Toast.makeText(FilteringBookWithCategoryActivity.this, message, Toast.LENGTH_LONG).show();
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
    private void getAllBooks() {
        String paginatedURL = bookURL + categoryID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                allBooksModelArrayList.clear();
                                totalPage = Integer.parseInt(response.getString("totalPage"));
                                totalItems = Integer.parseInt(response.getString("totalItems"));

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    // Convert the book object into a Map to make it dynamic
                                    Map<String, Object> bookData = new Gson().fromJson(jsonObject2.toString(), Map.class);
                                    AllBooksModel model = new AllBooksModel(bookData); // Pass the map to the model

                                    allBooksModelArrayList.add(model);
                                }
                                if (!allBooksModelArrayList.isEmpty()) {
                                    booksRecycler.setAdapter(new SearchingActivityAdapter(FilteringBookWithCategoryActivity.this, allBooksModelArrayList));
                                    bookShimmerLayout.stopShimmer();
                                    bookShimmerLayout.setVisibility(View.GONE);
                                    booksRecycler.setVisibility(View.VISIBLE);
                                }else {
                                    booksRecycler.setVisibility(View.GONE);
                                    bookShimmerLayout.stopShimmer();
                                    bookShimmerLayout.setVisibility(View.GONE);
                                    noDataTxt.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(FilteringBookWithCategoryActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
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
                                Toast.makeText(FilteringBookWithCategoryActivity.this, message, Toast.LENGTH_LONG).show();
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
    public void setCategoryID(String id){
        categoryID = id;
        booksRecycler.setVisibility(View.GONE);
        noDataTxt.setVisibility(View.GONE);
        bookShimmerLayout.setVisibility(View.VISIBLE);
        bookShimmerLayout.startShimmer();
        getAllBooks();
    }
}