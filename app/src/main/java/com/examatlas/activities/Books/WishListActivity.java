package com.examatlas.activities.Books;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.books.WishListAdapter;
import com.examatlas.models.Books.BookImageModels;
import com.examatlas.models.Books.WishListModel;
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

public class WishListActivity extends AppCompatActivity {
    SessionManager sessionManager;
    String authToken;
    ImageView backBtn;
    ArrayList<WishListModel> wishListModelArrayList;
    RecyclerView wishListBookRecyclerView;
    TextView noBookTxt;
    ShimmerFrameLayout shimmerFrameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        backBtn = findViewById(R.id.backBtn);
        noBookTxt = findViewById(R.id.noItemTxt);
        noBookTxt.setVisibility(View.GONE);
        shimmerFrameLayout = findViewById(R.id.shimmer_for_user_container);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        wishListBookRecyclerView = findViewById(R.id.wishListItemRecyclerView);
        wishListBookRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        wishListBookRecyclerView.setVisibility(View.GONE);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (sessionManager.IsLoggedIn()) {
            wishListModelArrayList = new ArrayList<>();
            getWishListItem();
        }else {
            wishListModelArrayList = new ArrayList<>(sessionManager.getWishListBookIdArrayList());
            if (!wishListModelArrayList.isEmpty()) {
                wishListBookRecyclerView.setAdapter(new WishListAdapter(WishListActivity.this, wishListModelArrayList));
                wishListBookRecyclerView.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                noBookTxt.setVisibility(View.GONE);
            }else {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                wishListBookRecyclerView.setVisibility(View.GONE);
                noBookTxt.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getWishListItem() {
        String url = Constant.BASE_URL + "v1/wishlist";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONArray dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataObject = dataArray.getJSONObject(i);
                                    String wishListId = dataObject.getString("_id");
                                    String userId = dataObject.getString("userId");
                                    JSONObject productObject = dataObject.getJSONObject("productId");
                                    String productId = productObject.getString("_id");
                                    String categoryId = productObject.getString("categoryId");
                                    String subCategoryId = productObject.getString("subCategoryId");
                                    String bookTitle = productObject.getString("title");
                                    String bookAuthor = productObject.getString("author");
                                    String bookPrice = productObject.getString("price");
                                    String bookSellingPrice = productObject.getString("sellingPrice");
                                    ArrayList<BookImageModels> imageUrlArraylist = new ArrayList<>();
                                    JSONArray imagesArray = productObject.getJSONArray("images");
                                    for (int j = 0; j < imagesArray.length(); j++) {
                                        JSONObject imageObject = imagesArray.getJSONObject(j);
                                        String imageUrl = imageObject.getString("url");
                                        imageUrlArraylist.add(new BookImageModels(imageUrl,null));
                                    }
                                    wishListModelArrayList.add(new WishListModel(wishListId, userId, productId, categoryId, subCategoryId, bookTitle,bookAuthor, bookPrice, bookSellingPrice, imageUrlArraylist));
                                }
                                if (wishListModelArrayList.isEmpty()){
                                    shimmerFrameLayout.stopShimmer();
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    wishListBookRecyclerView.setVisibility(View.GONE);
                                    noBookTxt.setVisibility(View.VISIBLE);
                                }else {
                                    shimmerFrameLayout.stopShimmer();
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    wishListBookRecyclerView.setAdapter(new WishListAdapter(WishListActivity.this, wishListModelArrayList));
                                    wishListBookRecyclerView.setVisibility(View.VISIBLE);
                                    noBookTxt.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(WishListActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(WishListActivity.this, message, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("getWishListItem", errorMessage);
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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}