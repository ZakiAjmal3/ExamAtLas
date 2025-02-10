package com.examatlas.activities.Books;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.LoginWithEmailActivity;
import com.examatlas.adapter.books.AllBookShowingAdapter;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.BookImageModels;
import com.examatlas.models.Books.WishListModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleBookDetailsActivity extends AppCompatActivity {
    String bookId;
    float ratingValue;
    ImageView productDetailsImg, backBtn,searchIcon,wishListIcon,shareIcon,cartBtn;
    boolean isWishListClicked = false;
    ViewPager2 bookImgViewPager;
    RelativeLayout productDetailsClickRL;
    LinearLayout addToCartLL,goToCartLL,buyNowLL,addBuyNowLinearLayout, productDetailsLinearLayout, indicatorLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    NestedScrollView nestedScrollView;
    boolean isProductDetailsExpanded = false,isBuyNowClicked = false;
    TextView rateProductTxtBtn, cartItemQuantityTxt,ratingTxtDisplay, bookTitleTxt, bookPriceInfo, bookTitleTxtDisplay, authorTxtDisplay, publisherTxt, publishingDateTxtDisplay, publisherTxtDisplay, editionDisplay, stockTxtDisplay, languageTxtDisplay;
    private final String bookURL = Constant.BASE_URL + "v1/books";
    private RecyclerView booksRecyclerView;
    private ArrayList<AllBooksModel> allBooksModelArrayList;
    private ArrayList<WishListModel> wishListModelArrayList;
    ArrayList<BookImageModels> bookImageArrayList;
    SessionManager sessionManager;
    String token;
    int totalPage, totalItems;
    String shareBookURL;
    Dialog progressDialog;
    String bookTitleStr = "",bookAuthorStr = "",bookSellingPriceStr = "",bookOriginalPriceStr = "";
    ArrayList<BookImageModels> bookImageModelsArrayList;
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

        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");

        ratingTxtDisplay = findViewById(R.id.ratingTxtDisplay);
        ratingTxtDisplay.setText(String.valueOf(ratingValue));

        goToCartLL = findViewById(R.id.goToCartLL);
        addToCartLL = findViewById(R.id.addToCartLL);
        buyNowLL = findViewById(R.id.buyNowLL);
        addBuyNowLinearLayout = findViewById(R.id.buttonLayout);
        addBuyNowLinearLayout.setVisibility(View.GONE);
        productDetailsImg = findViewById(R.id.productDetailsImg);
        productDetailsClickRL = findViewById(R.id.productDetailsClickRL);
        productDetailsLinearLayout = findViewById(R.id.productDetailsLinearLayout);
        backBtn = findViewById(R.id.backBtn);
        searchIcon = findViewById(R.id.searchIcon);
        wishListIcon = findViewById(R.id.heartIcon);
        shareIcon = findViewById(R.id.shareIcon);
        cartBtn = findViewById(R.id.cartBtn);
        rateProductTxtBtn = findViewById(R.id.rateProductTxtBtn);
        cartItemQuantityTxt = findViewById(R.id.cartItemCountTxt);
        nestedScrollView = findViewById(R.id.mainNestedContainer);
        nestedScrollView.setVisibility(View.GONE);
        shimmerFrameLayout = findViewById(R.id.shimmer_for_user_container);
        shimmerFrameLayout.startShimmer();
        String quantity = sessionManager.getCartQuantity();
        if (!quantity.equals("0")) {
            cartItemQuantityTxt.setVisibility(View.VISIBLE);
            cartItemQuantityTxt.setText(quantity);
        }else {
            cartItemQuantityTxt.setVisibility(View.GONE);
        }

        bookId = getIntent().getStringExtra("bookId");

        booksRecyclerView = findViewById(R.id.similarBookRecycler);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        bookImgViewPager = findViewById(R.id.bookImg);
        indicatorLayout = findViewById(R.id.indicatorLayout);

        bookTitleTxt = findViewById(R.id.bookTitle);
        bookPriceInfo = findViewById(R.id.bookPriceInfo);
        bookTitleTxtDisplay = findViewById(R.id.bookTitleTxtDisplay);
        authorTxtDisplay = findViewById(R.id.authorTxtDisplay);
        publisherTxtDisplay = findViewById(R.id.publisherTxtDisplay);
//        publishingDateTxtDisplay = findViewById(R.id.publishingDateTxtDisplay);
//        publisherTxtDisplay = findViewById(R.id.publisherTxtDisplay);
//        editionDisplay = findViewById(R.id.editionDisplay);
//        stockTxtDisplay = findViewById(R.id.stockTxtDisplay);
//        languageTxtDisplay = findViewById(R.id.languageTxtDisplay);

        allBooksModelArrayList = new ArrayList<>();

        wishListModelArrayList = new ArrayList<>(sessionManager.getWishListBookIdArrayList());
        for (int i = 0; i<wishListModelArrayList.size();i++){
            if (wishListModelArrayList.get(i).getProductId().equals(bookId)) {
                wishListIcon.setImageResource(R.drawable.ic_heart_red);
                isWishListClicked = true;
                break;
            }
        }

        productDetailsClickRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isProductDetailsExpanded) {
                    productDetailsLinearLayout.setVisibility(View.GONE);
                    productDetailsImg.setImageResource(R.drawable.ic_down);
                    isProductDetailsExpanded = false;
                } else {
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

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.IsLoggedIn()) {
                    startActivity(new Intent(SingleBookDetailsActivity.this, CartViewActivity.class));
                }else {
                    new MaterialAlertDialogBuilder(SingleBookDetailsActivity.this)
                            .setTitle("Login")
                            .setMessage("You need to login to view cart")
                            .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(SingleBookDetailsActivity.this, LoginWithEmailActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(SingleBookDetailsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
            }
        });

        rateProductTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleBookDetailsActivity.this, CreatingReviewActivity.class);
                intent.putExtra("bookId",bookId);
                startActivity(intent);
            }
        });

        goToCartLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SingleBookDetailsActivity.this, CartViewActivity.class));
            }
        });
        addToCartLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new Dialog(SingleBookDetailsActivity.this);
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
        buyNowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new Dialog(SingleBookDetailsActivity.this);
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

        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareBookURL = "https://www.examatlas.com/books/History/" + bookId;
                shareBookFunction();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SingleBookDetailsActivity.this,SearchingBooksActivity.class));
            }
        });
        wishListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new Dialog(SingleBookDetailsActivity.this);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                if (sessionManager.IsLoggedIn()) {
                    if (!isWishListClicked) {
                        sessionManager.setAddedItemWishList(new WishListModel(null, null, bookId, null, null, null, bookAuthorStr, bookOriginalPriceStr, bookSellingPriceStr, bookImageArrayList));
                        addToWishList();
                        isWishListClicked = true;
                    } else {
                        removeWishList();
                        isWishListClicked = false;
                    }
                }else {
                    if (!isWishListClicked) {
                        sessionManager.setAddedItemWishList(new WishListModel(null, null, bookId, null, null, null, bookAuthorStr, bookOriginalPriceStr, bookSellingPriceStr, bookImageArrayList));
                        wishListIcon.setImageResource(R.drawable.ic_heart_red);
                        isWishListClicked = true;
                        Toast.makeText(SingleBookDetailsActivity.this, "Item added to WishList", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }else {
                        sessionManager.removeWishListBookIdArrayList(bookId);
                        wishListIcon.setImageResource(R.drawable.ic_heart_grey);
                        isWishListClicked = false;
                        Toast.makeText(SingleBookDetailsActivity.this, "Item removed from WishList", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                    }
                }
            }
        });
        getSingleBook();
        getAllBooks();
    }

    private void removeWishList() {
        String addToWishListURL = Constant.BASE_URL + "v1/wishlist/delete/" + bookId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addToWishListURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            wishListIcon.setImageResource(R.drawable.ic_heart_grey);
                            progressDialog.dismiss();
                            Toast.makeText(SingleBookDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(SingleBookDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(SingleBookDetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
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
                if (!TextUtils.isEmpty(token)) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };
        MySingleton.getInstance(SingleBookDetailsActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String quantity = sessionManager.getCartQuantity();
        if (!quantity.equals("0")) {
            cartItemQuantityTxt.setVisibility(View.VISIBLE);
            cartItemQuantityTxt.setText(quantity);
        }else {
            cartItemQuantityTxt.setVisibility(View.GONE);
        }
        getSingleBook();
    }
    private void addToWishList() {
        String addToWishListURL = Constant.BASE_URL + "v1/wishlist";

        JSONArray productIdsArray = new JSONArray();
        productIdsArray.put(bookId);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productIds",productIdsArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addToWishListURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            wishListIcon.setImageResource(R.drawable.ic_heart_red);
                            progressDialog.dismiss();
                            Toast.makeText(SingleBookDetailsActivity.this, "Item added to wishlist", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(SingleBookDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(SingleBookDetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(SingleBookDetailsActivity.this).addToRequestQueue(jsonObjectRequest);
    }
    private void setCartItemTxt() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String quantity = sessionManager.getCartQuantity();
                if (!quantity.equals("0")) {
                    cartItemQuantityTxt.setVisibility(View.VISIBLE);
                    cartItemQuantityTxt.setText(quantity);
                    progressDialog.dismiss();
                }else {
                    cartItemQuantityTxt.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }            }
        },3000);
    }

    private void shareBookFunction() {
        Intent shareBookIntent = new Intent(Intent.ACTION_SEND);
        shareBookIntent.setType("text/plain");

        String shareMessage = "Check out this book: " + bookTitleTxt.getText().toString() + " " + shareBookURL;

        shareBookIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        startActivity(Intent.createChooser(shareBookIntent, "Share via"));
    }

    public void addItemToCart() {

        if (sessionManager.IsLoggedIn()) {

            String addToCartUrl = Constant.BASE_URL + "v1/cart";

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("productId", bookId);
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
                                    sessionManager.setCartItemQuantity();
                                    if (isBuyNowClicked){
                                        Intent intent = new Intent(SingleBookDetailsActivity.this, CartViewActivity.class);
                                        intent.putExtra("bookId",bookId);
                                        intent.putExtra("buyNow",true);
//                                        intent.putExtra("quantity",);
                                        isBuyNowClicked = false;
                                        setCartItemTxt();
                                        startActivity(intent);
                                    }else {
                                        goToCartLL.setVisibility(View.VISIBLE);
                                        addToCartLL.setVisibility(View.GONE);
                                        setCartItemTxt();
                                        Toast.makeText(SingleBookDetailsActivity.this, "Item Added to Cart", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                Toast.makeText(SingleBookDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SingleBookDetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
            MySingleton.getInstance(SingleBookDetailsActivity.this).addToRequestQueue(jsonObjectRequest);
        } else {
            new MaterialAlertDialogBuilder(SingleBookDetailsActivity.this)
                    .setTitle("Login")
                    .setMessage("You need to login to add items to cart")
                    .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(SingleBookDetailsActivity.this, LoginWithEmailActivity.class);
                            startActivity(intent);
                            finish();
                            progressDialog.dismiss();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(SingleBookDetailsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).show();
        }
    }

    private void getSingleBook() {
        String singleBookURL = Constant.BASE_URL + "v1/booksByID?id=" + bookId;
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

                                BookImageAdapter bookImageAdapter = new BookImageAdapter(bookImageArrayList,bookImgViewPager,indicatorLayout);
                                bookImgViewPager.setAdapter(bookImageAdapter);

                                String id = jsonObject.getString("_id");
                                bookTitleStr = jsonObject.getString("title");
                                bookTitleTxt.setText(bookTitleStr);
                                bookTitleTxtDisplay.setText(bookTitleStr);
                                String publication = jsonObject.getString("publication");
                                bookAuthorStr = jsonObject.getString("author");
                                publisherTxtDisplay.setText(publication);
//                                stockTxtDisplay.setText(stock);
                                bookOriginalPriceStr = jsonObject.getString("price");
                                bookSellingPriceStr = jsonObject.getString("sellingPrice");

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

                                bookPriceInfo.setText(spannableText);

                                String author = jsonObject.getString("author");
                                authorTxtDisplay.setText(author);
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                nestedScrollView.setVisibility(View.VISIBLE);
                                addBuyNowLinearLayout.setVisibility(View.VISIBLE);
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
                                booksRecyclerView.setAdapter(new AllBookShowingAdapter(SingleBookDetailsActivity.this, allBooksModelArrayList));
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