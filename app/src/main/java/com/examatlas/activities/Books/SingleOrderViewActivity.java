package com.examatlas.activities.Books;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
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
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.Books.EBooks.PurchasedEBookViewingBookActivity;
import com.examatlas.adapter.books.AllBookShowingAdapter;
import com.examatlas.adapter.books.SingleOrderTrackingItemAdapter;
import com.examatlas.adapter.books.SingleOrderViewBookItemAdapter;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.SingleBookTrackingItemModel;
import com.examatlas.models.Books.SingleOrderViewItemBookModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SingleOrderViewActivity extends AppCompatActivity {
    ImageView backBtn,copyOrderIdBtn,searchIcon,cartBtn;
    TextView cartItemQuantityTxt;
    RecyclerView bookItemRecyclerView,trackingOrderLineRV;
    ArrayList<SingleBookTrackingItemModel> singleBookTrackingItemModelArrayList;
    ArrayList<SingleOrderViewItemBookModel> singleOrderViewItemBookModelArrayList;
    ShimmerFrameLayout bookSuggestionShimmer,wholeShimmerFrameLayout;
    NestedScrollView wholeNestedScrollView;
    RecyclerView bookSuggestionRV;
    ArrayList<AllBooksModel> allBooksModelArrayList;
    private SessionManager sessionManager;
    private String token,orderId = "",shipment_id = "",orderStatus = "";
    RelativeLayout trackingRL,writeReviewRL, viewEBookRL, cancelOrderRL,emailUSRL;
    LinearLayout rsbLinearLayout;
    String finalAmount = "0",costPriceStr = "0",sellingPriceStr = "0",discountStr,shippingChargesStr;
    String addressNameStr, addressRoadStr, addressCityStr, addressStateStr, addressPhoneStr,orderPlacedDate;
    TextView orderIdTxtView,nameTxt,addressLine1Txt,addressLine2Txt,addressLine3Txt,phoneLineTxt,originalAmountTxt,sellingAmountTxt,discountAmountTxt,deliveryChargeAmountTxt,totalAmountTxt;
    Dialog progressDialog;
    String bookId,bookTitle,bookImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order_view);

        sessionManager = new SessionManager(SingleOrderViewActivity.this);
        token = sessionManager.getUserData().get("authToken");

        orderIdTxtView = findViewById(R.id.orderIdTxt);

        if (getIntent().getStringExtra("orderId") != null && getIntent().getStringExtra("shipment_id") != null) {
            orderId = getIntent().getStringExtra("orderId");
            shipment_id = getIntent().getStringExtra("shipment_id");
            orderStatus = getIntent().getStringExtra("orderStatus");
            orderIdTxtView.setText("Order ID - " + orderId);
        }

        backBtn = findViewById(R.id.backBtn);
        copyOrderIdBtn = findViewById(R.id.copyOrderIdBtn);
        searchIcon = findViewById(R.id.searchIcon);
        cartBtn = findViewById(R.id.cartBtn);
        cartItemQuantityTxt = findViewById(R.id.cartItemCountTxt);

        nameTxt = findViewById(R.id.nameTxt);
        addressLine1Txt = findViewById(R.id.addressLine1Txt);
        addressLine2Txt = findViewById(R.id.addressLine2Txt);
        addressLine3Txt = findViewById(R.id.addressLine3Txt);
        phoneLineTxt = findViewById(R.id.phoneLineTxt);
        originalAmountTxt = findViewById(R.id.originalAmountTxt);
        sellingAmountTxt = findViewById(R.id.sellingAmountTxt);
        discountAmountTxt = findViewById(R.id.discountAmountTxt);
        deliveryChargeAmountTxt = findViewById(R.id.deliveryChargeAmountTxt);
        totalAmountTxt = findViewById(R.id.totalAmountTxt);

        wholeNestedScrollView = findViewById(R.id.nestScrollView);
        wholeShimmerFrameLayout = findViewById(R.id.shimmer_container);
        wholeShimmerFrameLayout.startShimmer();
        bookSuggestionShimmer = findViewById(R.id.shimmer_for_suggestion_container);
        bookSuggestionShimmer.startShimmer();
        bookSuggestionRV = findViewById(R.id.booksForUserRecycler);
        bookSuggestionRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        allBooksModelArrayList = new ArrayList<>();

        trackingOrderLineRV = findViewById(R.id.trackOrderLineRV);
        trackingOrderLineRV.setLayoutManager(new LinearLayoutManager(this));

        bookItemRecyclerView = findViewById(R.id.booksItemRecycler);
        bookItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        trackingRL = findViewById(R.id.trackingRL);
        writeReviewRL = findViewById(R.id.writeReviewRL);
        viewEBookRL = findViewById(R.id.viewEBookRL);
        cancelOrderRL = findViewById(R.id.cancelOrderRL);
        emailUSRL = findViewById(R.id.emailUSRL);
        rsbLinearLayout = findViewById(R.id.rsbLinearLayout);

        singleBookTrackingItemModelArrayList = new ArrayList<>();
        singleOrderViewItemBookModelArrayList = new ArrayList<>();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleOrderViewActivity.this, CartViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleOrderViewActivity.this, SearchingBooksActivity.class);
                startActivity(intent);
                finish();            }
        });
        String quantity = sessionManager.getCartQuantity();
        if (!quantity.equals("0")) {
            cartItemQuantityTxt.setVisibility(View.VISIBLE);
            cartItemQuantityTxt.setText(quantity);
        }else {
            cartItemQuantityTxt.setVisibility(View.GONE);
        }
        trackingRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SingleOrderViewActivity.this,TrackingSingleActivity.class));
            }
        });
        writeReviewRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleOrderViewActivity.this, CreatingReviewActivity.class);
                intent.putExtra("bookId", bookId);
                intent.putExtra("bookTitle", bookTitle);
                intent.putExtra("bookImg", bookTitle);
                startActivity(intent);
            }
        });
        copyOrderIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Order ID", orderId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(SingleOrderViewActivity.this, "Order ID - " + orderId + " copied", Toast.LENGTH_SHORT).show();
            }
        });
        if (!orderStatus.equalsIgnoreCase("Confirmed")){
            rsbLinearLayout.setVisibility(View.GONE);
            trackingRL.setVisibility(View.GONE);
        }
        viewEBookRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleOrderViewActivity.this, PurchasedEBookViewingBookActivity.class);
                intent.putExtra("bookId", bookId);
                intent.putExtra("title", bookTitle);
                startActivity(intent);
                finish();
            }
        });
        cancelOrderRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(SingleOrderViewActivity.this)
                        .setTitle("Cancel")
                        .setMessage("Are you sure you want to cancel this order?")
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog = new Dialog(SingleOrderViewActivity.this);
                                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                progressDialog.setContentView(R.layout.progress_bar_drawer);
                                progressDialog.setCancelable(false);
                                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                                progressDialog.show();
                                cancelOrder();
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(SingleOrderViewActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
        emailUSRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = "info@examatlas.com";
                String subject = "Email regarding Book Order";
                String body = "Hello, I need your help!";

                // Create a mailto Uri
                Uri uri = Uri.parse("mailto:" + email)
                        .buildUpon()
                        .appendQueryParameter("subject", subject)
                        .appendQueryParameter("body", body)
                        .build();

                // Create an Intent to send email
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

                // Check if there is an email client available
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No email client installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getSingleOrderHistory();
        getAllBooks();

    }

    private void getTrackingOrderDetails() {
        String trackingURL = Constant.BASE_URL + "v1/order/track_parcel/" + shipment_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, trackingURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel(shipment_id,"Order Confirmed," + orderPlacedDate ,"Your order has been confirmed"));
                                JSONObject data = response.optJSONObject("data");

                                if (data != null) {
                                    // Get the shipment track array
                                    JSONArray shipmentTrackArray = data.optJSONArray("shipment_track");

                                    if (shipmentTrackArray != null && shipmentTrackArray.length() > 0) {
                                        JSONObject shipmentTrack = shipmentTrackArray.optJSONObject(0);
                                        String awbCode = shipmentTrack != null ? shipmentTrack.optString("awb_code", "N/A") : "N/A";
                                        String courierCompanyId = shipmentTrack != null && shipmentTrack.has("courier_company_id") ? shipmentTrack.optString("courier_company_id", "N/A") : "N/A";
                                        String shipmentId = shipmentTrack != null ? shipmentTrack.optString("shipment_id", "N/A") : "N/A";
                                        String orderId = shipmentTrack != null ? shipmentTrack.optString("order_id", "N/A") : "N/A";
                                        String pickupDate = shipmentTrack != null ? shipmentTrack.optString("pickup_date", "Not Available") : "Not Available";
                                        String deliveredDate = shipmentTrack != null ? shipmentTrack.optString("delivered_date", "Not Available") : "Not Available";

                                        // Handle shipment activities (if any)
                                        JSONArray shipmentTrackActivities = data.optJSONArray("shipment_track_activities");
                                        if (shipmentTrackActivities != null) {
                                            for (int i = 0; i < shipmentTrackActivities.length(); i++) {
                                                JSONObject activity = shipmentTrackActivities.optJSONObject(i);
                                                if (activity != null) {
                                                    String activityStatus = activity.optString("status", "Unknown");
                                                    String activityDescription = activity.optString("activity", "No Activity Description");

                                                    singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel(shipment_id,activityStatus,activityDescription));
                                                }
                                            }
                                        } else {
                                            singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel(shipment_id,"Yet to be Shipped",""));
                                            singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel(shipment_id,"Out For Delivery",""));
                                            singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel(shipment_id,"Delivered",""));
                                        }
                                    }
                                }
                                trackingOrderLineRV.setAdapter(new SingleOrderTrackingItemAdapter(SingleOrderViewActivity.this,singleBookTrackingItemModelArrayList));
                            } else {
                                Toast.makeText(SingleOrderViewActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SingleOrderViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SingleOrderViewActivity.this, message, Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(SingleOrderViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void cancelOrder() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId",orderId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String cancelURL = Constant.BASE_URL + "v1/order/cancel";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,cancelURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(SingleOrderViewActivity.this, message, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                onBackPressed();
                            } else {
                                Toast.makeText(SingleOrderViewActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                Toast.makeText(SingleOrderViewActivity.this, message, Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(SingleOrderViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void getSingleOrderHistory() {
        String orderIdURL = Constant.BASE_URL + "v1/order/" + orderId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, orderIdURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
//                                Toast.makeText(MyBookOrderHistory.this, "two", Toast.LENGTH_SHORT).show();
                                JSONObject dataObject = response.getJSONObject("data");

                                JSONArray bookItemsArray = dataObject.getJSONArray("items");
                                for (int j = 0; j < bookItemsArray.length(); j++) {
                                    JSONObject productObj = bookItemsArray.getJSONObject(j).getJSONObject("product");
                                    bookId = productObj.getString("_id");
                                    String type = productObj.getString("type");
                                    if (type.equals("book")){
                                        getTrackingOrderDetails();
                                        cancelOrderRL.setVisibility(View.VISIBLE);
                                        viewEBookRL.setVisibility(View.GONE);
                                        trackingOrderLineRV.setVisibility(View.VISIBLE);
                                    }else {
                                        cancelOrderRL.setVisibility(View.GONE);
                                        viewEBookRL.setVisibility(View.VISIBLE);
                                        trackingRL.setVisibility(View.GONE);
                                        trackingOrderLineRV.setVisibility(View.GONE);
                                    }
                                    bookTitle = productObj.getString("title");
                                    String singleCP = productObj.getString("price");
                                    costPriceStr = String.valueOf(Integer.parseInt(costPriceStr) + Integer.parseInt(singleCP));
                                    String singleSP = productObj.getString("sellingPrice");
                                    sellingPriceStr = String.valueOf(Integer.parseInt(sellingPriceStr) + Integer.parseInt(singleSP));
                                    String author = productObj.getString("author");
                                    String publication = productObj.getString("publication");
                                    JSONArray bookImagesArray = productObj.getJSONArray("images");
                                    bookImg = bookImagesArray.getJSONObject(0).getString("url");
                                    singleOrderViewItemBookModelArrayList.add(new SingleOrderViewItemBookModel(bookId, bookTitle, singleCP, singleSP, bookImg, author, publication));
                                }
                                bookItemRecyclerView.setAdapter(new SingleOrderViewBookItemAdapter(SingleOrderViewActivity.this, singleOrderViewItemBookModelArrayList));

                                finalAmount = dataObject.getString("finalAmount");
                                shippingChargesStr = dataObject.getString("shippingCharges");
                                discountStr = dataObject.getString("discounts");
                                String orderPlacedDate2 = dataObject.getString("createdAt");
                                try {
                                    // Define the format of the incoming timestamp
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                                    Date date = inputFormat.parse(orderPlacedDate2);

                                    // Define output formats for day and month
                                    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.ENGLISH);  // Extract day
                                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);  // Extract full month name

                                    // Extract the date and month
                                    String day = dayFormat.format(date);
                                    String month = monthFormat.format(date);
                                    orderPlacedDate = day + " " + month;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                finalAmount = String.valueOf(Integer.parseInt(finalAmount) + Integer.parseInt(shippingChargesStr));

                                JSONObject addressObject = dataObject.getJSONObject("address");
                                {
                                    String firstName = addressObject.getString("firstName");
                                    String lastName = addressObject.getString("lastName");
                                    String country = addressObject.getString("country");
                                    String streetAddress = addressObject.getString("streetAddress");
                                    String apartment = addressObject.getString("apartment");
                                    String city = addressObject.getString("city");
                                    String state = addressObject.getString("state");
                                    String pinCode = addressObject.getString("pinCode");
                                    String phone = addressObject.getString("phone");
                                    String email = addressObject.getString("email");

                                    addressNameStr = firstName + " " + lastName;
                                    addressRoadStr = apartment + ", " + streetAddress + ", ";
                                    addressCityStr = city + ", " + state + ", ";
                                    addressStateStr = country + " - " + pinCode + ".";
                                    addressPhoneStr = phone;
                                }
                                setAllDetails();
                            } else {
                                Toast.makeText(SingleOrderViewActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SingleOrderViewActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SingleOrderViewActivity.this, message, Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(SingleOrderViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void setAllDetails() {
        nameTxt.setText(addressNameStr);
        addressLine1Txt.setText(addressRoadStr);
        addressLine2Txt.setText(addressCityStr);
        addressLine3Txt.setText(addressStateStr);
        phoneLineTxt.setText(addressPhoneStr);

        // Create a SpannableString for the original price with strikethrough
        SpannableString spannableOriginalPrice = new SpannableString("₹ " + costPriceStr);
        spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);
        originalAmountTxt.setText(spannableOriginalPrice);

        sellingAmountTxt.setText("₹ " + sellingPriceStr);
        discountAmountTxt.setText("₹ -" + discountStr);
        deliveryChargeAmountTxt.setText("₹ " + shippingChargesStr);
        totalAmountTxt.setText("₹ " + finalAmount);

        wholeShimmerFrameLayout.stopShimmer();
        wholeShimmerFrameLayout.setVisibility(View.GONE);
        wholeNestedScrollView.setVisibility(View.VISIBLE);
    }

    private void getAllBooks() {
        String bookURL = Constant.BASE_URL + "v1/books";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, bookURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                allBooksModelArrayList.clear();
                                int totalPage = Integer.parseInt(response.getString("totalPage"));
                                int totalItems = Integer.parseInt(response.getString("totalItems"));

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    // Convert the book object into a Map to make it dynamic
                                    Map<String, Object> bookData = new Gson().fromJson(jsonObject2.toString(), Map.class);

                                    // Extract dimensions (assuming they are present in the 'dimensions' field of the book data)
                                    String length = "";
                                    String width = "";
                                    String height = "";
                                    String weight = "";


                                    if (bookData.containsKey("dimensions")) {
                                        Object dimensionsObj = bookData.get("dimensions");

                                        if (dimensionsObj instanceof Map) {
                                            // If it's already a Map, we can safely cast
                                            Map<String, Object> dimensions = (Map<String, Object>) dimensionsObj;
                                            length = dimensions.containsKey("length") ? dimensions.get("length").toString() : "";
                                            width = dimensions.containsKey("width") ? dimensions.get("width").toString() : "";
                                            height = dimensions.containsKey("height") ? dimensions.get("height").toString() : "";
                                            weight = dimensions.containsKey("weight") ? dimensions.get("weight").toString() : "";
                                        } else if (dimensionsObj instanceof String) {
                                            // If it's a String (likely JSON), parse it into a Map
                                            String dimensionsJson = dimensionsObj.toString();
                                            try {
                                                Map<String, Object> dimensions = new Gson().fromJson(dimensionsJson, Map.class);
                                                length = dimensions.containsKey("length") ? dimensions.get("length").toString() : "";
                                                width = dimensions.containsKey("width") ? dimensions.get("width").toString() : "";
                                                height = dimensions.containsKey("height") ? dimensions.get("height").toString() : "";
                                                weight = dimensions.containsKey("weight") ? dimensions.get("weight").toString() : "";
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                // Handle parsing errors here if necessary
                                            }
                                        }
                                    }

                                    // Pass the data and dimensions to the model constructor
                                    AllBooksModel model = new AllBooksModel(bookData, length, width, height, weight); // Pass map and dimensions

                                    // Add the model to the list
                                    allBooksModelArrayList.add(model);
                                }

                                // Update UI and adapters
                                bookSuggestionRV.setAdapter(new AllBookShowingAdapter(SingleOrderViewActivity.this, allBooksModelArrayList));
                                bookSuggestionShimmer.stopShimmer();
                                bookSuggestionShimmer.setVisibility(View.GONE);
                                bookSuggestionRV.setVisibility(View.VISIBLE);


//                                if (allBooksModelArrayList.isEmpty()) {
//                                    noDataLayout.setVisibility(View.VISIBLE);
//                                    booksRecyclerView.setVisibility(View.GONE);
//                                } else {
//                                    booksRecyclerView.setAdapter(new BookForUserAdapter(getContext(), allBooksModelArrayList));
//                                }
                            } else {
                                Toast.makeText(SingleOrderViewActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SingleOrderViewActivity.this, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(SingleOrderViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }
    public String getOrderPlacedDate(){
        return orderPlacedDate;
    }
}