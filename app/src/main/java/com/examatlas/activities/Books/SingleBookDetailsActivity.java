package com.examatlas.activities.Books;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.HardBookECommPurchaseAdapter;
import com.examatlas.adapter.books.BookForUserAdapter;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.HardBookECommPurchaseModel;
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

public class SingleBookDetailsActivity extends AppCompatActivity {
    String bookId;
    float ratingValue;
    ImageView productDetailsImg,backBtn;
    ViewPager2 bookImgViewPager;
    RelativeLayout productDetailsClickRL;
    LinearLayout productDetailsLinearLayout;
    boolean isProductDetailsExpanded = false;
    TextView ratingTxtDisplay,bookTitle,bookPriceInfo,bookTitleTxtDisplay,authorTxtDisplay,bindingTxtDisplay,publishingDateTxtDisplay,publisherTxtDisplay,editionDisplay, stockTxtDisplay,languageTxtDisplay;
    private final String bookURL = Constant.BASE_URL + "book/getAllBooks";
    private RecyclerView booksRecyclerView;
    private HardBookECommPurchaseAdapter hardBookECommPurchaseAdapter;
    private ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList;
    SessionManager sessionManager;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_book_details);

        getWindow().setStatusBarColor(ContextCompat.getColor(SingleBookDetailsActivity.this, R.color.white));
        // Change the content (icons and text) color of the status bar to black
        ViewCompat.getWindowInsetsController(getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        ratingValue = 3.5f;

        CustomRatingBar customRatingBar = findViewById(R.id.customRatingBar);
        customRatingBar.setRating(ratingValue);  // Set rating dynamically

        ratingTxtDisplay = findViewById(R.id.ratingTxtDisplay);
        ratingTxtDisplay.setText(String.valueOf(ratingValue));

        productDetailsImg = findViewById(R.id.productDetailsImg);
        productDetailsClickRL = findViewById(R.id.productDetailsClickRL);
        productDetailsLinearLayout = findViewById(R.id.productDetailsLinearLayout);
        backBtn = findViewById(R.id.backBtn);

        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");

        bookId = getIntent().getStringExtra("bookID");

        booksRecyclerView = findViewById(R.id.similarBookRecycler);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        bookImgViewPager = findViewById(R.id.bookImg);

        bookTitle = findViewById(R.id.bookTitle);
        bookPriceInfo = findViewById(R.id.bookPriceInfo);
        bookTitleTxtDisplay = findViewById(R.id.bookTitleTxtDisplay);
        authorTxtDisplay = findViewById(R.id.authorTxtDisplay);
        bindingTxtDisplay = findViewById(R.id.bindingTxtDisplay);
        publishingDateTxtDisplay = findViewById(R.id.publishingDateTxtDisplay);
        publisherTxtDisplay = findViewById(R.id.publisherTxtDisplay);
        editionDisplay = findViewById(R.id.editionDisplay);
        stockTxtDisplay = findViewById(R.id.stockTxtDisplay);
        languageTxtDisplay = findViewById(R.id.languageTxtDisplay);

        hardBookECommPurchaseModelArrayList = new ArrayList<>();
        productDetailsClickRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isProductDetailsExpanded){
                    productDetailsLinearLayout.setVisibility(View.GONE);
                    productDetailsImg.setImageResource(R.drawable.ic_down);
                    isProductDetailsExpanded = false;
                }else {
                    productDetailsLinearLayout.setVisibility(View.VISIBLE);
                    productDetailsImg.setImageResource(R.drawable.ic_up);
                    isProductDetailsExpanded = true;
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getSingleBook();
        getAllBooks();

    }

    private void getSingleBook() {
        String singleBookURL = Constant.BASE_URL + "book/getBookyId/" + bookId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, singleBookURL , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("book");
                                // Parse books directly here
                                ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();
                                JSONArray jsonArray1 = jsonObject.getJSONArray("images");

                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                                    BookImageModels bookImageModels = new BookImageModels(
                                            jsonObject3.getString("url"),
                                            jsonObject3.getString("filename")
                                    );
                                    bookImageArrayList.add(bookImageModels);
                                }

                                BookImageAdapter bookImageAdapter = new BookImageAdapter(bookImageArrayList);
                                bookImgViewPager.setAdapter(bookImageAdapter);

                                String id = jsonObject.getString("_id");
                                String type = jsonObject.getString("type");
                                String title = jsonObject.getString("title");
                                bookTitle.setText(title);
                                bookTitleTxtDisplay.setText(title);
                                String keyword = jsonObject.getString("keyword");
                                String stock = jsonObject.getString("stock");
                                stockTxtDisplay.setText(stock);
                                String price = jsonObject.getString("price");
                                String sellPrice = jsonObject.getString("sellPrice");

                                // Calculate prices and discount
//                                    String originalPrice = currentBook.getPrice();
                                int discount = Integer.parseInt(sellPrice) * 100 / Integer.parseInt(price);
                                discount = 100 - discount;

                                // Create a SpannableString for the original price with strikethrough
                                SpannableString spannableOriginalPrice = new SpannableString("₹" + price);
                                spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);

                                // Create the discount text
                                String discountText = "(-" + discount + "%)";
                                SpannableStringBuilder spannableText = new SpannableStringBuilder();
                                spannableText.append("₹" + sellPrice + " ");
                                spannableText.append(spannableOriginalPrice);
                                spannableText.append(" " + discountText);

                                // Set the color for the discount percentage
                                int startIndex = spannableText.length() - discountText.length();
                                spannableText.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                bookPriceInfo.setText(spannableText);

                                String content = jsonObject.getString("content");
                                String author = jsonObject.getString("author");
                                authorTxtDisplay.setText(author);
                                String categoryId = jsonObject.getString("categoryId");
                                String subCategoryId = jsonObject.getString("subCategoryId");
                                String subjectId = jsonObject.getString("subjectId");
                                String tags = parseTags(jsonObject.getJSONArray("tags"));
                            } else {
                                Toast.makeText(SingleBookDetailsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SingleBookDetailsActivity.this, message, Toast.LENGTH_LONG).show();
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
        String paginatedURL = bookURL + "?type=book&page=" + 1 + "&per_page=" + 5;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            booksRecyclerView.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("books");
                                hardBookECommPurchaseModelArrayList.clear();

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();
                                    JSONArray jsonArray1 = jsonObject2.getJSONArray("images");

                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                                        BookImageModels bookImageModels = new BookImageModels(
                                                jsonObject3.getString("url"),
                                                jsonObject3.getString("filename")
                                        );
                                        bookImageArrayList.add(bookImageModels);
                                    }
                                    HardBookECommPurchaseModel model = new HardBookECommPurchaseModel(
                                            jsonObject2.getString("_id"),
                                            jsonObject2.getString("type"),
                                            jsonObject2.getString("title"),
                                            jsonObject2.getString("keyword"),
                                            jsonObject2.getString("stock"),
                                            jsonObject2.getString("price"),
                                            jsonObject2.getString("sellPrice"),
                                            jsonObject2.getString("content"),
                                            jsonObject2.getString("author"),
                                            jsonObject2.getString("categoryId"),
                                            jsonObject2.getString("subCategoryId"),
                                            jsonObject2.getString("subjectId"),
                                            parseTags(jsonObject2.getJSONArray("tags")), // Ensure this method is implemented correctly
                                            jsonObject2.getString("bookUrl"),
                                            bookImageArrayList,
                                            jsonObject2.getString("createdAt"),
                                            jsonObject2.getString("updatedAt"),
                                            jsonObject2.getString("isInCart"),
                                            jsonObject2.getString("isInWishList"),
                                            0,0,0,0
                                    );
                                    hardBookECommPurchaseModelArrayList.add(model);
                                }
                                booksRecyclerView.setAdapter(new BookForUserAdapter(SingleBookDetailsActivity.this,hardBookECommPurchaseModelArrayList));
                            } else {
                                Toast.makeText(SingleBookDetailsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SingleBookDetailsActivity.this, message, Toast.LENGTH_LONG).show();
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
}