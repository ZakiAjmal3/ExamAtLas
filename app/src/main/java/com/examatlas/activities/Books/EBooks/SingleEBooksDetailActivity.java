package com.examatlas.activities.Books.EBooks;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.Books.CartViewActivity;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.activities.LoginWithEmailActivity;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.BookImageModels;
import com.examatlas.models.Books.WishListModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleEBooksDetailActivity extends AppCompatActivity {
    String bookIdByIntent;
    RelativeLayout backBtnRL;
    LinearLayout wholeButtonLayout,addToCartLL,goToCartLL,buyNowLL;
    WebView introDescriptionWebView;
    ImageView coverImg,bookImg;
    boolean isBuyNowClicked = false;
    TextView bookTitleTxt,bookAuthorTxt, bookCreatedDate,bookPriceTxt,introductionTxt;
    ShimmerFrameLayout shimmerFrameLayout;
    NestedScrollView nestedScrollView;
    private ArrayList<AllBooksModel> allBooksModelArrayList;
    private ArrayList<WishListModel> wishListModelArrayList;
    ArrayList<BookImageModels> bookImageArrayList;
    SessionManager sessionManager;
    String token;
    int totalPage, totalItems;
    Dialog progressDialog;
    String bookTitleStr = "",bookAuthorStr = "",bookSellingPriceStr = "",bookOriginalPriceStr = "",bookDescriptionTxt = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_ebooks_detail);
        // Make the layout extend under the status bar but keep it visible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            getWindow().getDecorView().getWindowInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
        } else {
            // For Android 10 and below
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Allow the layout to extend under the status bar
            );
        }
        // Ensures layout extends under status bar, but keeps status bar visible
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        bookIdByIntent = getIntent().getStringExtra("bookId");

        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");
        shimmerFrameLayout = findViewById(R.id.shimmer_for_user_container);
        shimmerFrameLayout.startShimmer();
        nestedScrollView = findViewById(R.id.nestScrollView);
        nestedScrollView.setVisibility(View.GONE);

        backBtnRL = findViewById(R.id.backBtnRl);
        coverImg = findViewById(R.id.coverImg);
        bookImg = findViewById(R.id.bookImg);
        bookTitleTxt = findViewById(R.id.bookTitleTxt);
        bookAuthorTxt = findViewById(R.id.bookAuthorTxt);
        bookCreatedDate = findViewById(R.id.createdDateTxt);
        bookPriceTxt = findViewById(R.id.priceTxt);
        introductionTxt = findViewById(R.id.introductionTxt);
        introDescriptionWebView = findViewById(R.id.webViewDescription);
        WebSettings webSettings = introDescriptionWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        wholeButtonLayout = findViewById(R.id.buttonLayout);
        wholeButtonLayout.setVisibility(View.GONE);
        addToCartLL = findViewById(R.id.addToCartLL);
        goToCartLL = findViewById(R.id.goToCartLL);
        buyNowLL = findViewById(R.id.buyNowLL);

        backBtnRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        addToCartLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new Dialog(SingleEBooksDetailActivity.this);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                addItemToCart();
            }
        });
        goToCartLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SingleEBooksDetailActivity.this,CartViewActivity.class));
            }
        });
        buyNowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new Dialog(SingleEBooksDetailActivity.this);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                addItemToCart();
                isBuyNowClicked = true;
            }
        });
        getEBookbyId();
    }

    public void addItemToCart() {

        if (sessionManager.IsLoggedIn()) {

            String addToCartUrl = Constant.BASE_URL + "v1/cart";

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("productId", bookIdByIntent);
                jsonObject.put("quantity", 1);
                jsonObject.put("type", "book");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addToCartUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("success");
                                if (status.equals("true")) {
                                    progressDialog.dismiss();
                                    sessionManager.setCartItemQuantity();
                                    if (isBuyNowClicked){
                                        Intent intent = new Intent(SingleEBooksDetailActivity.this, CartViewActivity.class);
                                        intent.putExtra("bookId",bookIdByIntent);
                                        intent.putExtra("buyNow",true);
                                        isBuyNowClicked = false;
                                        startActivity(intent);
                                    }else {
                                        goToCartLL.setVisibility(View.VISIBLE);
                                        addToCartLL.setVisibility(View.GONE);
                                        Toast.makeText(SingleEBooksDetailActivity.this, "Item Added to Cart", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                Toast.makeText(SingleEBooksDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
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
                    Toast.makeText(SingleEBooksDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("LoginActivity", errorMessage);
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
            MySingleton.getInstance(SingleEBooksDetailActivity.this).addToRequestQueue(jsonObjectRequest);
        } else {
            new MaterialAlertDialogBuilder(SingleEBooksDetailActivity.this)
                    .setTitle("Login")
                    .setMessage("You need to login to add items to cart")
                    .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(SingleEBooksDetailActivity.this, LoginWithEmailActivity.class);
                            startActivity(intent);
                            finish();
                            progressDialog.dismiss();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(SingleEBooksDetailActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).show();
        }
    }

    private void getEBookbyId() {
        String singleBookURL = Constant.BASE_URL + "v1/booksByID?id=" + bookIdByIntent;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, singleBookURL , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("data");
                                boolean isInCart = response.getBoolean("isInCart");
                                if (isInCart) {
                                    addToCartLL.setVisibility(View.GONE);
                                    goToCartLL.setVisibility(View.VISIBLE);
                                }else {
                                    addToCartLL.setVisibility(View.VISIBLE);
                                    goToCartLL.setVisibility(View.GONE);
                                }
                                bookImageArrayList = new ArrayList<>();
                                // Parse books directly here
                                JSONArray jsonArray1 = jsonObject.getJSONArray("images");

                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                                    BookImageModels bookImageModels = new BookImageModels(
                                            jsonObject3.getString("url"),
                                            jsonObject3.getString("filename")
                                    );
                                    bookImageArrayList.add(bookImageModels);
                                }

//                                BookImageAdapter bookImageAdapter = new BookImageAdapter(bookImageArrayList,bookImgViewPager,indicatorLayout);
//                                bookImgViewPager.setAdapter(bookImageAdapter);
                                Glide.with(SingleEBooksDetailActivity.this)
                                        .load(bookImageArrayList.get(0).getUrl())
                                        .error(R.drawable.noimage)
                                        .into(bookImg);
                                Glide.with(SingleEBooksDetailActivity.this)
                                        .load(bookImageArrayList.get(0).getUrl())
                                        .error(R.drawable.noimage)
                                        .into(coverImg);
                                String id = jsonObject.getString("_id");
                                bookTitleStr = jsonObject.getString("title");
                                bookTitleTxt.setText(bookTitleStr);
                                String publication = jsonObject.getString("publication");
                                bookAuthorStr = jsonObject.getString("author");
                                bookCreatedDate.setText(publication);
//                                stockTxtDisplay.setText(stock);
                                bookOriginalPriceStr = jsonObject.getString("price");
                                bookSellingPriceStr = jsonObject.getString("sellingPrice");
                                bookDescriptionTxt = jsonObject.getString("description");

                                introDescriptionWebView.loadData(bookDescriptionTxt, "text/html", "UTF-8");

                                // Calculate prices and discount
//                                    String originalPrice = currentBook.getPrice();
                                int discount = Integer.parseInt(bookSellingPriceStr) * 100 / Integer.parseInt(bookOriginalPriceStr);
                                discount = 100 - discount;

                                // Create a SpannableString for the original price with strikethrough
                                SpannableString spannableOriginalPrice = new SpannableString("₹" + bookOriginalPriceStr);
                                spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);

                                // Create the discount text
                                String discountText = "(-" + discount + "%)";
                                SpannableStringBuilder spannableText = new SpannableStringBuilder();
                                spannableText.append("₹" + bookSellingPriceStr + " ");
                                spannableText.append(spannableOriginalPrice);
                                spannableText.append(" " + discountText);

                                // Set the color for the discount percentage
                                int startIndex = spannableText.length() - discountText.length();
                                spannableText.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                bookPriceTxt.setText(spannableText);

                                bookAuthorTxt.setText(bookAuthorStr);

                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                nestedScrollView.setVisibility(View.VISIBLE);
                                wholeButtonLayout.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(SingleEBooksDetailActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SingleEBooksDetailActivity.this, message, Toast.LENGTH_LONG).show();
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
}
