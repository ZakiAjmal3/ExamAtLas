package com.examatlas.activities.Books.EBooks;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.Books.SearchingBooksActivity;
import com.examatlas.adapter.books.AllEBookHomepageAdapter;
import com.examatlas.adapter.books.SearchingActivityAdapter;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.WishListModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EBookHomePageActivity extends AppCompatActivity {
    ImageView backBtn;
    EditText searchEditText;
    String searchText;
    RecyclerView allBooksRecyclerView;
    AllEBookHomepageAdapter hardBookECommPurchaseAdapter;
    ArrayList<AllBooksModel> allBooksModelArrayList;
    SessionManager sessionManager;
    String token;
    LinearLayout englishLL,hindiLL,rs_100_200_LinearLayout;
    TextView englishTxt,hindiTxt,priceTxt,noBookInThisCategory;
    Boolean isEnglish = false,isHindi = false,isPrice100_200 = false;
    int totalPage,totalItems;
    ShimmerFrameLayout itemsShimmerFrameLayout;
    String bookURL = Constant.BASE_URL + "v1/books?type=ebook";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook_home_page);
        getWindow().setStatusBarColor(ContextCompat.getColor(EBookHomePageActivity.this,R.color.md_theme_dark_surfaceTint));

        itemsShimmerFrameLayout = findViewById(R.id.shimmer_for_user_container);
        itemsShimmerFrameLayout.startShimmer();
        itemsShimmerFrameLayout.setVisibility(View.VISIBLE);

        searchEditText = findViewById(R.id.search_icon);
        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");
        allBooksRecyclerView = findViewById(R.id.allBookRecycler);
        allBooksRecyclerView.setVisibility(View.GONE);
        allBooksRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        allBooksModelArrayList = new ArrayList<>();

        rs_100_200_LinearLayout = findViewById(R.id.RS_100_200_LL);
        englishLL = findViewById(R.id.englishLL);
        hindiLL = findViewById(R.id.hindiLL);
        englishTxt = findViewById(R.id.englishTxt);
        hindiTxt = findViewById(R.id.hindiTxt);
        priceTxt = findViewById(R.id.priceTxt);

        noBookInThisCategory = findViewById(R.id.noBookInThisCategory);

        Animation english = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left_anim_for_hindi_english_search);
        englishLL.startAnimation(english);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation hindi = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left_anim_for_hindi_english_search);
                hindiLL.startAnimation(hindi);
                hindiLL.setVisibility(View.VISIBLE);
            }
        },350);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation rs = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left_anim_for_hindi_english_search);
                rs_100_200_LinearLayout.startAnimation(rs);
                rs_100_200_LinearLayout.setVisibility(View.VISIBLE);
            }
        },700);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchText = editable.toString();
                getAllBooks(bookURL + "?searchQuery=" + searchText);
            }
        });
        englishLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEnglish) {
                    englishTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.seed));
                    englishLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_selected);
                    isEnglish = true;
                    priceTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    rs_100_200_LinearLayout.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isPrice100_200 = false;
                    hindiTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    hindiLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isHindi = false;
                    allBooksRecyclerView.setVisibility(View.GONE);
                    noBookInThisCategory.setVisibility(View.GONE);
                    itemsShimmerFrameLayout.setVisibility(View.VISIBLE);
                    itemsShimmerFrameLayout.startShimmer();
                    getAllBooks(bookURL + "?language=English");
                }else {
                    englishTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    englishLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isEnglish = false;
                    hindiTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    hindiLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isHindi = false;
                    priceTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    rs_100_200_LinearLayout.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isPrice100_200 = false;
                }
                if (!isEnglish && !isHindi && !isPrice100_200){
                    itemsShimmerFrameLayout.setVisibility(View.VISIBLE);
                    itemsShimmerFrameLayout.startShimmer();
                    allBooksRecyclerView.setVisibility(View.GONE);
                    noBookInThisCategory.setVisibility(View.GONE);
                    getAllBooks(bookURL);
                }
            }
        });
        hindiLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHindi) {
                    hindiTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.seed));
                    hindiLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_selected);
                    isHindi = true;
                    englishTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    englishLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isEnglish = false;
                    priceTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    rs_100_200_LinearLayout.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isPrice100_200 = false;
                    allBooksRecyclerView.setVisibility(View.GONE);
                    noBookInThisCategory.setVisibility(View.GONE);
                    itemsShimmerFrameLayout.setVisibility(View.VISIBLE);
                    itemsShimmerFrameLayout.startShimmer();
                    getAllBooks(bookURL + "?language=Hindi");
                }else {
                    hindiTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    hindiLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isHindi = false;
                    englishTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    englishLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isEnglish = false;
                    priceTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    rs_100_200_LinearLayout.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isPrice100_200 = false;
                }
                if (!isEnglish && !isHindi && !isPrice100_200){
                    itemsShimmerFrameLayout.setVisibility(View.VISIBLE);
                    itemsShimmerFrameLayout.startShimmer();
                    allBooksRecyclerView.setVisibility(View.GONE);
                    noBookInThisCategory.setVisibility(View.GONE);
                    getAllBooks(bookURL);
                }
            }
        });
        rs_100_200_LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPrice100_200) {
                    priceTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.seed));
                    rs_100_200_LinearLayout.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_selected);
                    isPrice100_200 = true;
                    englishTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    englishLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isEnglish = false;
                    hindiTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    hindiLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isHindi = false;
                    allBooksRecyclerView.setVisibility(View.GONE);
                    noBookInThisCategory.setVisibility(View.GONE);
                    itemsShimmerFrameLayout.setVisibility(View.VISIBLE);
                    itemsShimmerFrameLayout.startShimmer();
                    getAllBooks(bookURL + "?fromPrice=100" + "&toPrice=200");
                }else {
                    priceTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    rs_100_200_LinearLayout.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isPrice100_200 = false;
                    hindiTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    hindiLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isHindi = false;
                    englishTxt.setTextColor(ContextCompat.getColor(EBookHomePageActivity.this, R.color.black));
                    englishLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isEnglish = false;
                }
                if (!isEnglish && !isHindi && !isPrice100_200){
                    itemsShimmerFrameLayout.setVisibility(View.VISIBLE);
                    itemsShimmerFrameLayout.startShimmer();
                    allBooksRecyclerView.setVisibility(View.GONE);
                    noBookInThisCategory.setVisibility(View.GONE);
                    getAllBooks(bookURL);
                }
            }
        });
        openKeyboard();
        getAllBooks(bookURL);
    }
    private void openKeyboard() {
//        searchView.setIconified(false);
        searchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    private void getAllBooks(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
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
                                if (allBooksModelArrayList.isEmpty()){
                                    noBookInThisCategory.setVisibility(View.VISIBLE);
                                    allBooksRecyclerView.setVisibility(View.GONE);
                                    itemsShimmerFrameLayout.stopShimmer();
                                    itemsShimmerFrameLayout.setVisibility(View.GONE);
                                }else {
                                    ArrayList<WishListModel> wishListModelArrayList = new ArrayList<>(sessionManager.getWishListBookIdArrayList());
                                    ArrayList<Boolean> heartToggleStates = new ArrayList<>(Collections.nCopies(allBooksModelArrayList.size(), false));
                                    if (!wishListModelArrayList.isEmpty()) {
                                        for (int i = 0; i < allBooksModelArrayList.size(); i++) {
                                            for (int j = 0; j < wishListModelArrayList.size(); j++) {
                                                if (allBooksModelArrayList.get(i).getString("_id").equals(wishListModelArrayList.get(j).getProductId())) {
                                                    heartToggleStates.set(i, true);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    hardBookECommPurchaseAdapter = new AllEBookHomepageAdapter(EBookHomePageActivity.this, allBooksModelArrayList,heartToggleStates);
                                    allBooksRecyclerView.setAdapter(hardBookECommPurchaseAdapter);
                                    noBookInThisCategory.setVisibility(View.GONE);
                                    allBooksRecyclerView.setVisibility(View.VISIBLE);
                                    itemsShimmerFrameLayout.stopShimmer();
                                    itemsShimmerFrameLayout.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(EBookHomePageActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(EBookHomePageActivity.this, message, Toast.LENGTH_LONG).show();
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
}