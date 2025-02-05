package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.examatlas.R;
import com.examatlas.activities.Books.CartViewActivity;
import com.examatlas.activities.Books.SearchingBooksActivity;
import com.examatlas.adapter.books.AllBookShowingAdapter;
import com.examatlas.adapter.books.CategoryAdapter;
import com.examatlas.models.Books.CategoryModel;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HardBookECommPurchaseActivity extends AppCompatActivity {
    ImageView cartBtn,logo;
    private ImageSlider slider;
    ArrayList<SlideModel> sliderArrayList;
    private RecyclerView booksRecyclerView,categoryRecyclerView,bookForUserRecyclerView,bookBestSellerRecyclerView;
    private ArrayList<AllBooksModel> allBooksModelArrayList;
    private SessionManager sessionManager;
    private String token;
    private RelativeLayout noDataLayout;
    private EditText searchView;
    private final String bookURL = Constant.BASE_URL + "v1/books";
    private final String categoryURL = Constant.BASE_URL + "v1/category";
    int totalPage,totalItems;
    ArrayList<CategoryModel> categoryArrayList = new ArrayList<>();
    NestedScrollView nestedScrollView;
    RelativeLayout toolbarRelativeLayout;
    FrameLayout searchViewFrameLayout;
    ShimmerFrameLayout categoryShimmerLayout,bookForUsrShimmerLayout,bookBestSellerShimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_book_ecomm_purchase);

        getWindow().setStatusBarColor(ContextCompat.getColor(HardBookECommPurchaseActivity.this,R.color.md_theme_dark_surfaceTint));

        initializeViews();
        setupImageSlider();
        getAlLCategory();
        getAllBooks();
    }
    private void initializeViews() {
        logo = findViewById(R.id.logo);
        cartBtn = findViewById(R.id.cartBtn);
        slider = findViewById(R.id.slider);
        nestedScrollView = findViewById(R.id.nestScrollView);
        searchViewFrameLayout = findViewById(R.id.searchViewFrameLayout);
        toolbarRelativeLayout = findViewById(R.id.hardbook_ecomm_purchase_toolbar);
        booksRecyclerView = findViewById(R.id.booksRecycler);

        categoryRecyclerView = findViewById(R.id.categoryRecycler);
        categoryShimmerLayout = findViewById(R.id.shimmer_category_container);
        categoryRecyclerView.setVisibility(View.GONE);
        categoryShimmerLayout.setVisibility(View.VISIBLE);
        categoryShimmerLayout.startShimmer();

        bookForUserRecyclerView = findViewById(R.id.booksForUserRecycler);
        bookForUsrShimmerLayout = findViewById(R.id.shimmer_for_user_container);
        bookForUserRecyclerView.setVisibility(View.GONE);
        bookForUsrShimmerLayout.setVisibility(View.VISIBLE);
        bookForUsrShimmerLayout.startShimmer();

        bookBestSellerRecyclerView = findViewById(R.id.booksBestSellerRecycler);
        bookBestSellerShimmerLayout = findViewById(R.id.shimmer_Best_selling_container);
        bookBestSellerRecyclerView.setVisibility(View.GONE);
        bookBestSellerShimmerLayout.setVisibility(View.VISIBLE);
        bookBestSellerShimmerLayout.startShimmer();

        noDataLayout = findViewById(R.id.noDataLayout);

        searchView = findViewById(R.id.search_icon);
        searchView.setFocusable(false);
        searchView.setClickable(true);
//        cartIcon = findViewById(R.id.cartBtn);
        allBooksModelArrayList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");


        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        bookForUserRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        bookBestSellerRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HardBookECommPurchaseActivity.this, SearchingBooksActivity.class);
                startActivity(intent);
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HardBookECommPurchaseActivity.this, CartViewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupImageSlider() {
        sliderArrayList = new ArrayList<>();
        sliderArrayList.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));
        slider.setImageList(sliderArrayList);
    }

    private void getAlLCategory() {
        String paginatedURL = categoryURL;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            categoryRecyclerView.setVisibility(View.VISIBLE);
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
                                if (categoryArrayList.size()>10){
                                    categoryRecyclerView.setLayoutManager(new GridLayoutManager(HardBookECommPurchaseActivity.this,2,GridLayoutManager.HORIZONTAL,false));
                                }else {
                                    categoryRecyclerView.setLayoutManager(new GridLayoutManager(HardBookECommPurchaseActivity.this,5,GridLayoutManager.VERTICAL,false));
                                }
                                // Update UI and adapters
                                categoryRecyclerView.setAdapter(new CategoryAdapter(categoryArrayList, HardBookECommPurchaseActivity.this));
                                categoryShimmerLayout.stopShimmer();
                                categoryShimmerLayout.setVisibility(View.GONE);
                                categoryRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(HardBookECommPurchaseActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(HardBookECommPurchaseActivity.this, message, Toast.LENGTH_LONG).show();
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
        String paginatedURL = bookURL;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            booksRecyclerView.setVisibility(View.VISIBLE);
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

                                // Update UI and adapters
                                bookForUserRecyclerView.setAdapter(new AllBookShowingAdapter(HardBookECommPurchaseActivity.this, allBooksModelArrayList));
                                bookForUsrShimmerLayout.stopShimmer();
                                bookForUsrShimmerLayout.setVisibility(View.GONE);
                                bookForUserRecyclerView.setVisibility(View.VISIBLE);
                                bookBestSellerRecyclerView.setAdapter(new AllBookShowingAdapter(HardBookECommPurchaseActivity.this, allBooksModelArrayList));
                                bookBestSellerShimmerLayout.stopShimmer();
                                bookBestSellerShimmerLayout.setVisibility(View.GONE);
                                bookBestSellerRecyclerView.setVisibility(View.VISIBLE);

                                if (allBooksModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    booksRecyclerView.setVisibility(View.GONE);
                                } else {
                                    booksRecyclerView.setAdapter(new AllBookShowingAdapter(HardBookECommPurchaseActivity.this, allBooksModelArrayList));
                                }
                            } else {
                                Toast.makeText(HardBookECommPurchaseActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(HardBookECommPurchaseActivity.this, message, Toast.LENGTH_LONG).show();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

