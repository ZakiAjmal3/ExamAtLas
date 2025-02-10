package com.examatlas.activities.Books;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.adapter.books.CartViewItemAdapter;
import com.examatlas.models.Books.BookImageModels;
import com.examatlas.models.Books.CartItemModel;
import com.examatlas.models.Books.DeliveryAddressItemModel;
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

public class CartViewActivity extends AppCompatActivity {
    SessionManager sessionManager;
    String authToken,singleBookId = "";
    String singleBookTitle;
    RecyclerView cartItemRecycler;
    private ArrayList<CartItemModel> allBooksModelArrayList;
    TextView priceItemsTxt,priceOriginalTxt,totalDiscountTxt,deliveryTxt,totalAmountTxt1,totalAmountTxt2;
    TextView singleBookTitleTxt,singleBookAuthorTxt,singleBookPriceTxt,singleBookQuantityTxt,addNewAddressTxt,changeAddressBtn;
    ImageView backBtn,bookImgView;
    String singleBookPriceStr = "", singleBookSellingPriceStr = "";
    String singleBookQuantity = "1";
    ProgressBar quantityProgressBar;
    RelativeLayout mainRelativeLayout, noItemRL,singleBookRL,pageIndicatorRL, deliveryAddressFullRL,priceDetailsRL,bottomButtonRL;
    ShimmerFrameLayout shimmerFrameLayout;
    boolean buyNow = false,isEBookPresent = false;
    private final String[] quantityArray = {"1", "2", "3", "more"};
    ArrayList<DeliveryAddressItemModel> deliveryAddressModelArrayList;
    TextView deliveryName,deliveryAddress,deliveryPhone;
    String billingIdStr = "";
    Button placeOrderBtn;
    int totalItems = 0, totalCostPrice = 0, totalSellingPrice = 0, totalDiscount = 0, finalDiscountedAmount = 0, shippingCharges = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view2);

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        mainRelativeLayout = findViewById(R.id.main);
        noItemRL = findViewById(R.id.noItemRL);
        noItemRL.setVisibility(View.GONE);
        shimmerFrameLayout = findViewById(R.id.shimmer_container);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        cartItemRecycler = findViewById(R.id.itemRecycler);
        cartItemRecycler.setLayoutManager(new LinearLayoutManager(this));
        allBooksModelArrayList = new ArrayList<>();

        backBtn = findViewById(R.id.backBtn);
        priceItemsTxt = findViewById(R.id.priceAndItemstxt);
        priceOriginalTxt = findViewById(R.id.priceTxt);
        totalDiscountTxt = findViewById(R.id.discountTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalAmountTxt1 = findViewById(R.id.totalAmountPriceTxt);
        totalAmountTxt2 = findViewById(R.id.bottomStickyAmountTxt);
        singleBookTitleTxt = findViewById(R.id.bookTitle);
        singleBookAuthorTxt = findViewById(R.id.bookAuthor);
        singleBookPriceTxt = findViewById(R.id.bookPriceInfo);
        singleBookQuantityTxt = findViewById(R.id.quantityTxtView);
        quantityProgressBar = findViewById(R.id.quantityProgressbar);
        bookImgView = findViewById(R.id.bookImage);
        singleBookRL = findViewById(R.id.singleItemRL);
        pageIndicatorRL = findViewById(R.id.indicatorRL);

        deliveryAddressFullRL = findViewById(R.id.deliveryFullRL);
        deliveryAddressFullRL.setVisibility(View.GONE);

        priceDetailsRL = findViewById(R.id.priceRelativeLayout);
        bottomButtonRL = findViewById(R.id.bottomStickyRelativeLayout);

        addNewAddressTxt = findViewById(R.id.addAddressTxt);
        addNewAddressTxt.setVisibility(View.GONE);

        deliveryName = findViewById(R.id.deliveryName);
        deliveryAddress = findViewById(R.id.deliveryAddress);
        deliveryPhone = findViewById(R.id.deliveryPhone);
        changeAddressBtn = findViewById(R.id.changeAddressBtn);

        placeOrderBtn = findViewById(R.id.gotoCheckOut);

        if (getIntent().getBooleanExtra("buyNow",false)){
            buyNow = true;
            singleBookId = getIntent().getStringExtra("bookId");
            getSingleBook();
        }else {
            buyNow = false;
            getAllCartItems();
        }
        singleBookQuantityTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuantityOptions();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        addNewAddressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartViewActivity.this, DeliveryAddressInputActivity.class);
                intent.putExtra("addAddress",true);
                startActivity(intent);
            }
        });
        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartViewActivity.this, DeliveryAddressInputActivity.class);
                intent.putExtra("id",billingIdStr);
                startActivity(intent);
            }
        });
        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!billingIdStr.equals("")){
                    Intent intent = new Intent(CartViewActivity.this,ChoosingPaymentActivity.class);
                    intent.putExtra("totalAmount",totalSellingPrice);
                    intent.putExtra("shippingCharges",shippingCharges);
                    intent.putExtra("discounts",totalDiscount);
                    intent.putExtra("finalAmount",totalSellingPrice);
                    intent.putExtra("addressId",billingIdStr);
                    intent.putExtra("productId",singleBookId);
                    intent.putExtra("quantity", singleBookQuantity);
                    intent.putExtra("itemCount",allBooksModelArrayList.size());
                    intent.putExtra("isEBookPresent",isEBookPresent);
                    startActivity(intent);
                }else {
                    Toast.makeText(CartViewActivity.this, "Please add delivery address", Toast.LENGTH_SHORT).show();
                }

//                showPriceDialog();
            }
        });
        deliveryAddressModelArrayList = new ArrayList<>();
        getDeliveryAddress(Constant.BASE_URL + "v1/address");
    }
//    RecyclerView itemDetailsRecyclerView;
//    BookOrderSummaryItemsDetailsRecyclerViewModel bookOrderSummaryItemsDetailsRecyclerViewModel;
//    BookOrderSummaryItemsDetailsRecyclerViewAdapter bookOrderSummaryItemsDetailsRecyclerViewAdapter;
//    ArrayList<BookOrderSummaryItemsDetailsRecyclerViewModel> bookOrderSummaryItemsDetailsRecyclerViewModelArrayList;
//    TextView totalAmountTxt;
//    Button proceedToPaymentBtn;
//    ImageView orderSummaryCrossBtn;
//    Dialog checkOutProgressDialog;
//    private void showPriceDialog() {
//        Dialog orderSummaryDialog = new Dialog(this);
//        orderSummaryDialog.setContentView(R.layout.book_order_summary_before_payment_dialog_box);
//
//        itemDetailsRecyclerView = orderSummaryDialog.findViewById(R.id.orderSummaryRecyclerView);
//        itemDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        totalAmountTxt = orderSummaryDialog.findViewById(R.id.totalAmountPriceTxt);
//        proceedToPaymentBtn = orderSummaryDialog.findViewById(R.id.proceedToPayment);
//        orderSummaryCrossBtn = orderSummaryDialog.findViewById(R.id.crossBtn);
//
//        bookOrderSummaryItemsDetailsRecyclerViewModelArrayList = new ArrayList<>();
//        if (allBooksModelArrayList.isEmpty()){
//            bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.add(new BookOrderSummaryItemsDetailsRecyclerViewModel(singleBookTitle,String.valueOf(singleBookSellingPriceStr),String.valueOf(singleBookQuantityInt)));
//        }else {
//            for (int i = 0; i < allBooksModelArrayList.size(); i++) {
//                bookOrderSummaryItemsDetailsRecyclerViewModel = new BookOrderSummaryItemsDetailsRecyclerViewModel(allBooksModelArrayList.get(i).getTitle(), allBooksModelArrayList.get(i).getSellingPrice(), allBooksModelArrayList.get(i).getQuantity());
//                bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.add(bookOrderSummaryItemsDetailsRecyclerViewModel);
//            }
//        }
//        bookOrderSummaryItemsDetailsRecyclerViewAdapter = new BookOrderSummaryItemsDetailsRecyclerViewAdapter(this,bookOrderSummaryItemsDetailsRecyclerViewModelArrayList);
//        itemDetailsRecyclerView.setAdapter(bookOrderSummaryItemsDetailsRecyclerViewAdapter);
//
//        totalAmountTxt.setText("₹ " + finalDiscountedAmount);
//
//        orderSummaryCrossBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                orderSummaryDialog.dismiss();
//            }
//        });
//
//        proceedToPaymentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkOutProgressDialog = new Dialog(CartViewActivity.this);
//                checkOutProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                checkOutProgressDialog.setContentView(R.layout.progress_bar_drawer);
//                checkOutProgressDialog.setCancelable(false);
//                checkOutProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                checkOutProgressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
//                checkOutProgressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
//                checkOutProgressDialog.show();
//                orderCheckOut();
//            }
//        });
//
//        orderSummaryDialog.show();
//        WindowManager.LayoutParams params = orderSummaryDialog.getWindow().getAttributes();
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.gravity = Gravity.CENTER;
//
//        // Set the window attributes
//        orderSummaryDialog.getWindow().setAttributes(params);
//
//        // Now, to set margins, you'll need to set it in the root view of the dialog
//        FrameLayout layout = (FrameLayout) orderSummaryDialog.findViewById(android.R.id.content);
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
//
//        layoutParams.setMargins(0, 50, 0, 50);
//        layout.setLayoutParams(layoutParams);
//
//        // Background and animation settings
//        orderSummaryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        orderSummaryDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//
//    }


    private void getSingleBook() {
        String singleBookURL = Constant.BASE_URL + "v1/booksByID?id=" + singleBookId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, singleBookURL , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                             JSONObject jsonObject = response.getJSONObject("data");
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
                                if (!bookImageArrayList.isEmpty()) {
                                    Glide.with(CartViewActivity.this)
                                            .load(bookImageArrayList.get(0).getUrl())
                                            .into(bookImgView);
                                }
                                String id = jsonObject.getString("_id");
                                String type = jsonObject.getString("type");
                                if (type.equals("ebook")){
                                    isEBookPresent = true;
                                }
                                singleBookTitle= jsonObject.getString("title");
                                singleBookTitleTxt.setText(singleBookTitle);
                                String author = jsonObject.getString("author");
                                singleBookAuthorTxt.setText(author);
                                singleBookPriceStr = jsonObject.getString("price");
                                singleBookSellingPriceStr = jsonObject.getString("sellingPrice");

                                // Calculate prices and discount
//                                    String originalPrice = currentBook.getPrice();
                                int discount = Integer.parseInt(singleBookSellingPriceStr) * 100 / Integer.parseInt(singleBookPriceStr);
                                discount = 100 - discount;

                                // Create a SpannableString for the original price with strikethrough
                                SpannableString spannableOriginalPrice = new SpannableString("₹" + singleBookPriceStr);
                                spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);

                                // Create the discount text
                                String discountText = "(-" + discount + "%)";
                                SpannableStringBuilder spannableText = new SpannableStringBuilder();
                                spannableText.append("₹" + singleBookSellingPriceStr + " ");
                                spannableText.append(spannableOriginalPrice);
                                spannableText.append(" " + discountText);

                                // Set the color for the discount percentage
                                int startIndex = spannableText.length() - discountText.length();
                                spannableText.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                singleBookPriceTxt.setText(spannableText);
                                singleBookQuantityTxt.setText("Qty: " + singleBookQuantity);
                                setUpSingleBookPriceDetails();
                                pageIndicatorRL.setVisibility(View.VISIBLE);
                                priceDetailsRL.setVisibility(View.VISIBLE);
                                bottomButtonRL.setVisibility(View.VISIBLE);
                                cartItemRecycler.setVisibility(View.GONE);
                                shimmerFrameLayout.setVisibility(View.GONE);
                                singleBookRL.setVisibility(View.VISIBLE);
                                mainRelativeLayout.setBackgroundColor(ContextCompat.getColor(CartViewActivity.this, R.color.light_dark_grey));
                                noItemRL.setVisibility(View.GONE);

                            } else {
                                Toast.makeText(CartViewActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CartViewActivity.this, message, Toast.LENGTH_LONG).show();
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
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void showQuantityOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartViewActivity.this);
        builder.setTitle("Select Quantity")
                .setItems(quantityArray, (dialog, which) -> {
                    String selectedQuantity = quantityArray[which];

                    if (selectedQuantity.equals("more")) {
                        showCustomQuantityDialog();
                    } else {
                        try {
                            // Try to parse the selected quantity as an integer
                            int quantity = Integer.parseInt(selectedQuantity);
                            singleBookQuantityTxt.setText("Qty: " + selectedQuantity);
                            updateQuantity(quantity);
                        } catch (NumberFormatException e) {
                            // Handle the case where the quantity is not a valid integer
                            Log.e("CartViewActivity", "Invalid quantity format: " + selectedQuantity, e);
                            // Optionally, show a message to the user
                            Toast.makeText(CartViewActivity.this, "Invalid quantity selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        builder.create().show();
    }


    private void showCustomQuantityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CartViewActivity.this);
        builder.setTitle("Enter Quantity");

        // Create a LinearLayout to hold the EditText
        LinearLayout layout = new LinearLayout(CartViewActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText input = new EditText(CartViewActivity.this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        // Set margins for the EditText
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int marginInDp = 20; // Adjust margin as needed
        float scale = CartViewActivity.this.getResources().getDisplayMetrics().density;
        int marginInPx = (int) (marginInDp * scale + 0.5f); // Convert dp to pixels
        params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);

        input.setLayoutParams(params);
        layout.addView(input);
        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            singleBookQuantity = input.getText().toString();
            try {
                int quantity = Integer.parseInt(singleBookQuantity);

                // Validate the quantity
                if (quantity < 1 || quantity > 100) {
                    Toast.makeText(CartViewActivity.this, "Please enter a valid quantity (1-100)", Toast.LENGTH_SHORT).show();
                    return;
                }

                singleBookQuantityTxt.setText("Qty: " + quantity);
                updateQuantity(quantity);
            } catch (NumberFormatException e) {
                Toast.makeText(CartViewActivity.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }

    private void updateQuantity( int quantity) {
        quantityProgressBar.setVisibility(View.VISIBLE);
        singleBookQuantityTxt.setVisibility(View.GONE);

        String updateUrl = Constant.BASE_URL + "v1/cart/update";

        if (singleBookId == null) {
            Toast.makeText(CartViewActivity.this, "User ID or Item ID is null", Toast.LENGTH_LONG).show();
            quantityProgressBar.setVisibility(View.GONE);
            singleBookQuantityTxt.setVisibility(View.VISIBLE);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId", singleBookId);
            jsonObject.put("quantity", quantity);
        } catch (JSONException e) {
            Toast.makeText(CartViewActivity.this, "Error creating JSON", Toast.LENGTH_SHORT).show();
            quantityProgressBar.setVisibility(View.GONE);
            singleBookQuantityTxt.setVisibility(View.VISIBLE);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, updateUrl, jsonObject,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        Toast.makeText(CartViewActivity.this, "Quantity Updated", Toast.LENGTH_SHORT).show();

                        if (success) {
                            singleBookQuantityTxt.setText(String.valueOf("Qty: " + quantity));
                            singleBookQuantity = String.valueOf(quantity);
                            setUpSingleBookPriceDetails();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(CartViewActivity.this, "Error processing response", Toast.LENGTH_SHORT).show();
                    } finally {
                        quantityProgressBar.setVisibility(View.GONE);
                        singleBookQuantityTxt.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Toast.makeText(CartViewActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    quantityProgressBar.setVisibility(View.GONE);
                    singleBookQuantityTxt.setVisibility(View.VISIBLE);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                if (authToken != null && !authToken.isEmpty()) {
                    headers.put("Authorization", "Bearer " + authToken);
                }
                return headers;
            }
        };
        MySingleton.getInstance(CartViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void getAllCartItems() {
        String paginatedURL = Constant.BASE_URL + "v1/cart";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            cartItemRecycler.setVisibility(View.VISIBLE);
//                        progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("data");
                                String cartId = jsonObject.getString("_id");
                                JSONArray itemsArray = jsonObject.getJSONArray("items");
                                allBooksModelArrayList.clear();

                                // Loop through the items array to add each item to the model
                                for (int i = 0; i < itemsArray.length(); i++) {
                                    JSONObject itemObject = itemsArray.getJSONObject(i);
                                    JSONObject productObject = itemObject.getJSONObject("product");
                                    String type = productObject.getString("type");
                                    if (type.equals("ebook")){
                                        isEBookPresent = true;
                                    }
                                    String quantity = itemObject.getString("quantity");
                                    // Convert the product object to a map to make it dynamic
                                    Map<String, Object> productData = new Gson().fromJson(productObject.toString(), Map.class);
                                    CartItemModel model = new CartItemModel(cartId,productData,quantity); // Pass the map to the model

                                    allBooksModelArrayList.add(model);
                                }
                                if (allBooksModelArrayList.isEmpty()){
                                    pageIndicatorRL.setVisibility(View.GONE);
                                    priceDetailsRL.setVisibility(View.GONE);
                                    bottomButtonRL.setVisibility(View.GONE);
                                    cartItemRecycler.setVisibility(View.GONE);
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    deliveryAddressFullRL.setVisibility(View.GONE);
                                    addNewAddressTxt.setVisibility(View.GONE);
                                    mainRelativeLayout.setBackgroundColor(ContextCompat.getColor(CartViewActivity.this, R.color.white));
                                    noItemRL.setVisibility(View.VISIBLE);
                                }else {
                                    cartItemRecycler.setAdapter(new CartViewItemAdapter(CartViewActivity.this, allBooksModelArrayList));
                                    pageIndicatorRL.setVisibility(View.VISIBLE);
                                    priceDetailsRL.setVisibility(View.VISIBLE);
                                    bottomButtonRL.setVisibility(View.VISIBLE);
                                    cartItemRecycler.setVisibility(View.VISIBLE);
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    mainRelativeLayout.setBackgroundColor(ContextCompat.getColor(CartViewActivity.this, R.color.light_dark_grey));
                                    noItemRL.setVisibility(View.GONE);
                                    setUpPriceDetails();
                                }
                            } else {
                                Toast.makeText(CartViewActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CartViewActivity.this, message, Toast.LENGTH_LONG).show();
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
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    private void getDeliveryAddress(String paginatedURL) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                String selected = "";int selectedPosition = 0;DeliveryAddressItemModel model = null;
                                JSONArray jsonArray = response.getJSONArray("data");
                                if (jsonArray.length() > 0){
                                // Parse books directly here
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                        String billingId = jsonObject2.getString("_id");
                                        String addressType = jsonObject2.getString("addressType");
                                        String firstName = jsonObject2.getString("firstName");
                                        String lastName = jsonObject2.getString("lastName");
                                        String houseNoOrApartmentNo = jsonObject2.getString("apartment");
                                        String streetAddress = jsonObject2.getString("streetAddress");
                                        String townCity = jsonObject2.getString("city");
                                        String state = jsonObject2.getString("state");
                                        String pinCode = jsonObject2.getString("pinCode");
                                        String countryName = jsonObject2.getString("country");
                                        String phone = jsonObject2.getString("phone");
                                        String emailAddress = jsonObject2.getString("email");
                                        selected = jsonObject2.getString("isDefault");
                                        selectedPosition = i;
                                        model = new DeliveryAddressItemModel(billingId, addressType, firstName, lastName, houseNoOrApartmentNo, streetAddress, townCity, state, pinCode, countryName, phone, emailAddress, selected);
                                        deliveryAddressModelArrayList.add(model);
                                    }
                                }
                                if (selected.equals("1")) {
                                    billingIdStr = model.getBillingId();
                                    setDeliveryAddress(selectedPosition);
                                }else {
                                    setDeliveryAddress(selectedPosition);
                                }
                            } else {
                                Toast.makeText(CartViewActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(CartViewActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void setDeliveryAddress(int i) {
//        if (getIntent().getStringExtra("id") != null ){
//            getBillingAddressById(getIntent().getStringExtra("id"));
////            deliveryName.setText(getIntent().getStringExtra("name"));
////            deliveryAddress.setText(getIntent().getStringExtra("address"));
////            deliveryPhone.setText(getIntent().getStringExtra("phone"));
////            deliveryAddressFullRL.setVisibility(View.VISIBLE);
////            addNewAddressTxt.setVisibility(View.GONE);
//        }else {
            if (!deliveryAddressModelArrayList.isEmpty()) {
                deliveryName.setText(deliveryAddressModelArrayList.get(i).getFirstName() + " " + deliveryAddressModelArrayList.get(i).getLastName());
                String addressFullStr = deliveryAddressModelArrayList.get(i).getHouseNoOrApartmentNo() + " "
                        + deliveryAddressModelArrayList.get(i).getStreetAddress() + " "
                        + deliveryAddressModelArrayList.get(i).getTownCity() + " "
                        + deliveryAddressModelArrayList.get(i).getState() + " "
                        + deliveryAddressModelArrayList.get(i).getCountryName() + " "
                        + deliveryAddressModelArrayList.get(i).getPinCode();
                deliveryAddress.setText(addressFullStr);
                deliveryPhone.setText(deliveryAddressModelArrayList.get(i).getPhone());

                billingIdStr = deliveryAddressModelArrayList.get(i).getBillingId();

                if (allBooksModelArrayList.isEmpty() && singleBookId.isEmpty()){
                    billingIdStr = "";
                    deliveryAddressFullRL.setVisibility(View.GONE);
                    addNewAddressTxt.setVisibility(View.GONE);
                }else {
                    deliveryAddressFullRL.setVisibility(View.VISIBLE);
                    addNewAddressTxt.setVisibility(View.GONE);
                }
            } else {
                if (allBooksModelArrayList.isEmpty() && singleBookId.isEmpty()){
                    billingIdStr = "";
                    deliveryAddressFullRL.setVisibility(View.GONE);
                    addNewAddressTxt.setVisibility(View.GONE);
                }else {
                    billingIdStr = "";
                    deliveryAddressFullRL.setVisibility(View.GONE);
                    addNewAddressTxt.setVisibility(View.VISIBLE);
                }
            }
    }
    @SuppressLint("ResourceType")
    public void setUpSingleBookPriceDetails() {
        // Check for null or empty strings before parsing
        int quantity = Integer.parseInt(singleBookQuantity);
        int price = (singleBookPriceStr != null && !singleBookPriceStr.isEmpty()) ? Integer.parseInt(singleBookPriceStr) : 0;
        int sellingPrice = (singleBookSellingPriceStr != null && !singleBookSellingPriceStr.isEmpty()) ? Integer.parseInt(singleBookSellingPriceStr) : 0;
        totalCostPrice = 0;
        totalSellingPrice = 0;
        totalDiscount = 0;
        finalDiscountedAmount = 0;
        shippingCharges = 0;
        // Calculate the total prices
        int origPrice = quantity * price;
        int sellPrice = quantity * sellingPrice;
        totalCostPrice = totalCostPrice + origPrice;
        totalSellingPrice = totalSellingPrice + sellPrice;

        totalDiscount = totalCostPrice - totalSellingPrice;
        finalDiscountedAmount = totalSellingPrice;
        if (isEBookPresent){
            deliveryTxt.setVisibility(View.GONE);
        }else {
            if (totalSellingPrice > 399) {
                deliveryTxt.setText("FREE DELIVERY");
                deliveryTxt.setTextColor(Color.GREEN);
            } else {
                deliveryTxt.setText("₹ " + 50);
                shippingCharges = 50;
                finalDiscountedAmount = totalSellingPrice + shippingCharges;
            }
        }

        priceItemsTxt.setText("Price");
        priceOriginalTxt.setText("₹ " + totalCostPrice);
        totalDiscountTxt.setText("- ₹ " + totalDiscount);
        totalDiscountTxt.setTextColor(Color.GREEN);
        totalAmountTxt1.setText("₹ " + finalDiscountedAmount);
        totalAmountTxt2.setText("₹ " + finalDiscountedAmount);

    }
    public void setUpPriceDetails() {
        totalItems = allBooksModelArrayList.size();
        totalCostPrice = 0;
        totalSellingPrice = 0;
        totalDiscount = 0;
        finalDiscountedAmount = 0;
        shippingCharges = 0;

        for (int i = 0; i < totalItems; i++) {
            // Get the quantity, price, and selling price from the model
            String quantityStr = allBooksModelArrayList.get(i).getQuantity();
            String priceStr = allBooksModelArrayList.get(i).getPrice();
            String sellingPriceStr = allBooksModelArrayList.get(i).getSellingPrice();

            // Safely parse quantity
            int quantity = (quantityStr != null && !quantityStr.isEmpty()) ? Integer.parseInt(quantityStr) : 0;

            // Handle price as Double and then convert it to int
            double price = 0;
            if (priceStr != null && !priceStr.isEmpty()) {
                try {
                    price = Double.parseDouble(priceStr);  // Parse as double if it has decimals
                } catch (NumberFormatException e) {
                    price = 0;  // If parsing fails, set it to 0
                }
            }

            // Handle selling price as Double and then convert it to int
            double sellingPrice = 0;
            if (sellingPriceStr != null && !sellingPriceStr.isEmpty()) {
                try {
                    sellingPrice = Double.parseDouble(sellingPriceStr);  // Parse as double if it has decimals
                } catch (NumberFormatException e) {
                    sellingPrice = 0;  // If parsing fails, set it to 0
                }
            }

            // Calculate the total prices using rounded integers
            int origPrice = (int) (quantity * price);  // Convert the calculated price to integer
            int sellPrice = (int) (quantity * sellingPrice);  // Convert the calculated selling price to integer
            totalCostPrice = totalCostPrice + origPrice;
            totalSellingPrice = totalSellingPrice + sellPrice;
        }

        totalDiscount = totalCostPrice - totalSellingPrice;
        finalDiscountedAmount = totalSellingPrice;
        if (totalSellingPrice >= 299) {
            deliveryTxt.setText("FREE DELIVERY");
            deliveryTxt.setTextColor(Color.GREEN);
        } else {
            deliveryTxt.setText("₹ " + 50);
            shippingCharges = 50;
            finalDiscountedAmount = totalSellingPrice + shippingCharges;
        }

        priceItemsTxt.setText("Price (" + totalItems + " items)");
        priceOriginalTxt.setText("₹ " + totalCostPrice);
        totalDiscountTxt.setText("- ₹ " + totalDiscount);
        totalDiscountTxt.setTextColor(Color.GREEN);
        totalAmountTxt1.setText("₹ " + finalDiscountedAmount);
        totalAmountTxt2.setText("₹ " + finalDiscountedAmount);
    }

    public  void hideEachLayout(){
        pageIndicatorRL.setVisibility(View.GONE);
        priceDetailsRL.setVisibility(View.GONE);
        bottomButtonRL.setVisibility(View.GONE);
        cartItemRecycler.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.GONE);
        deliveryAddressFullRL.setVisibility(View.GONE);
        addNewAddressTxt.setVisibility(View.GONE);
        singleBookRL.setVisibility(View.GONE);
        mainRelativeLayout.setBackgroundColor(ContextCompat.getColor(CartViewActivity.this, R.color.white));
        noItemRL.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeliveryAddress(Constant.BASE_URL + "v1/address");
    }
}