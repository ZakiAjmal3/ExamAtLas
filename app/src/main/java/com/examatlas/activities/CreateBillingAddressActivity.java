package com.examatlas.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.CartViewAdapter;
import com.examatlas.models.CartViewModel;
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

public class CreateBillingAddressActivity extends AppCompatActivity {
    RecyclerView bookCartRecyclerView;
    CartViewAdapter cartViewAdapter;
    CartViewModel cartViewModel;
    ArrayList<CartViewModel> cartViewModelArrayList;
    Toolbar toolbar;
    SessionManager sessionManager;
    String cartUrl,authToken,userId;
    RelativeLayout noDataLayout, priceDetailRelativeLayout,deliveryAddressRelativeLayout,bottomStickyButtonLayout;
    ProgressBar progressBar;
    Button goToCheckout,changeAddressBtn;
    TextView fullNameTxt,fullAddressTxt,priceItemsTxt,priceOriginalTxt,totalDiscountTxt,deliveryTxt,totalAmountTxt1,totalAmountTxt2;
    String addressCombineStr = "";
    String firstName,lastName,houseNoOrApartmentNo,streetAddress,townCity,state,pinCode,countryName,phone,emailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_billing_address);

        toolbar = findViewById(R.id.hardbook_ecomm_cart_toolbar);
        bookCartRecyclerView = findViewById(R.id.cartItemRecycler);
        noDataLayout = findViewById(R.id.noDataLayout);
        priceDetailRelativeLayout = findViewById(R.id.priceRelativeLayout);
        deliveryAddressRelativeLayout = findViewById(R.id.deliveryAddressInput);
        bottomStickyButtonLayout = findViewById(R.id.bottomStickyRelativeLayout);
        progressBar = findViewById(R.id.cartProgress);
        goToCheckout = findViewById(R.id.gotoCheckOut);
        changeAddressBtn = findViewById(R.id.changeAddressBtn);

        fullNameTxt = findViewById(R.id.addressNameTxt);
        fullAddressTxt = findViewById(R.id.addressFullTxt);

        priceItemsTxt = findViewById(R.id.priceAndItemstxt);
        priceOriginalTxt = findViewById(R.id.priceTxt);
        totalDiscountTxt = findViewById(R.id.discountTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalAmountTxt1 = findViewById(R.id.totalAmountPriceTxt);
        totalAmountTxt2 = findViewById(R.id.bottomStickyAmountTxt);

        sessionManager = new SessionManager(this);
        cartViewModelArrayList = new ArrayList<>();

        bookCartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartUrl = Constant.BASE_URL + "cart/get/" + sessionManager.getUserData().get("user_id");
        authToken = sessionManager.getUserData().get("authToken");
        userId = sessionManager.getUserData().get("user_id");

        setupToolbar();
        fetchCartItems();
        getBillingAddress();

        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopUpAddAddress();
            }
        });


    }

    private void getBillingAddress() {
        String getAddressURL = Constant.BASE_URL + "billing/billing/user/" + userId;

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, getAddressURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0;i<response.length();i++){
                                JSONObject jsonObject = response.getJSONObject(i);
                                 firstName = jsonObject.getString("firstName");
                                 lastName = jsonObject.getString("lastName");
                                 houseNoOrApartmentNo = jsonObject.getString("apartment");
                                 streetAddress = jsonObject.getString("streetAddress");
                                 townCity = jsonObject.getString("city");
                                 state = jsonObject.getString("state");
                                 pinCode = jsonObject.getString("pinCode");
                                 countryName = jsonObject.getString("country");
                                 phone = jsonObject.getString("phone");
                                 emailAddress = jsonObject.getString("email");

                                addressCombineStr = firstName +" " + lastName + ", " + houseNoOrApartmentNo + ", " + streetAddress + ", " + townCity + ", " + state + ", " + pinCode + ", " + countryName + ", " + phone + ", " + emailAddress;
                                fullNameTxt.setVisibility(View.VISIBLE);
                                fullAddressTxt.setVisibility(View.VISIBLE);
                                fullAddressTxt.setText(addressCombineStr);
                                fullNameTxt.setText(firstName + " " + lastName);
                            }

                        } catch (JSONException e) {

                            Log.e("JSON_ERROR", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.toString();
                if (error.networkResponse != null) {
                    try {
                        String responseData = new String(error.networkResponse.data, "UTF-8");
                        errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                        errorMessage += "\nResponse Data: " + responseData;
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
                Log.e("LoginActivity", errorMessage);
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
        MySingleton.getInstance(CreateBillingAddressActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    EditText firstNameEditText,lastNameEditText,houseNoOrApartmentNoEditText,streetAddressEditText,townCityEditText,stateEditText,pinCodeEditText,countryNameEditText,phoneEditText,emailAddressEditText;
    Button saveAndContinueBtn;
    ImageView crossBtn;
    private void openPopUpAddAddress() {
        Dialog billingAddressInputDialogBox = new Dialog(this);
        billingAddressInputDialogBox.setContentView(R.layout.delivery_address_input_layout);

        firstNameEditText = billingAddressInputDialogBox.findViewById(R.id.firstNameEditText);
        lastNameEditText = billingAddressInputDialogBox.findViewById(R.id.lastNameEditText);
        houseNoOrApartmentNoEditText = billingAddressInputDialogBox.findViewById(R.id.houseNumberEditText);
        streetAddressEditText = billingAddressInputDialogBox.findViewById(R.id.streetAddressEditText);
        townCityEditText = billingAddressInputDialogBox.findViewById(R.id.townCityEditText);
        stateEditText = billingAddressInputDialogBox.findViewById(R.id.stateEditText);
        pinCodeEditText = billingAddressInputDialogBox.findViewById(R.id.pinCodeEditText);
        countryNameEditText = billingAddressInputDialogBox.findViewById(R.id.countryNameEditText);
        phoneEditText = billingAddressInputDialogBox.findViewById(R.id.phoneEditText);
        emailAddressEditText = billingAddressInputDialogBox.findViewById(R.id.emailAddressEditText);

        saveAndContinueBtn = billingAddressInputDialogBox.findViewById(R.id.saveAndContinueBtn);
        crossBtn = billingAddressInputDialogBox.findViewById(R.id.crossBtn);

        if (firstName != null && lastName != null && houseNoOrApartmentNo != null && streetAddress != null && townCity != null && state != null && pinCode != null && countryName != null && phone != null && emailAddress != null){
            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            houseNoOrApartmentNoEditText.setText(houseNoOrApartmentNo);
            streetAddressEditText.setText(streetAddress);
            townCityEditText.setText(townCity);
            stateEditText.setText(state);
            pinCodeEditText.setText(pinCode);
            countryNameEditText.setText(countryName);
            phoneEditText.setText(phone);
            emailAddressEditText.setText(emailAddress);
            saveAndContinueBtn.setText("Update");
        }

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingAddressInputDialogBox.dismiss();
            }
        });

        saveAndContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName,lastName,houseNoOrApartmentNo,streetAddress,townCity,state,pinCode,countryName,phone,emailAddress;

                firstName = firstNameEditText.getText().toString().trim();
                lastName = lastNameEditText.getText().toString().trim();
                houseNoOrApartmentNo = houseNoOrApartmentNoEditText.getText().toString().trim();
                streetAddress = streetAddressEditText.getText().toString().trim();
                townCity = townCityEditText.getText().toString().trim();
                state = stateEditText.getText().toString().trim();
                pinCode = pinCodeEditText.getText().toString().trim();
                countryName = countryNameEditText.getText().toString().trim();
                phone = phoneEditText.getText().toString().trim();
                emailAddress = emailAddressEditText.getText().toString().trim();

                createBillingAddress(firstName,lastName,houseNoOrApartmentNo,streetAddress,townCity,state,pinCode,countryName,phone,emailAddress,billingAddressInputDialogBox);
            }
        });

        billingAddressInputDialogBox.show();
        WindowManager.LayoutParams params = billingAddressInputDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

        // Set the window attributes
        billingAddressInputDialogBox.getWindow().setAttributes(params);

        // Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) billingAddressInputDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

        // Background and animation settings
        billingAddressInputDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        billingAddressInputDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // Prevent dialog from closing when clicking outside
        billingAddressInputDialogBox.setCancelable(false);
        billingAddressInputDialogBox.setCanceledOnTouchOutside(false);

    }

    private void createBillingAddress(String firstName, String lastName, String houseNoOrApartmentNo, String streetAddress, String townCity, String state, String pinCode, String countryName, String phone, String emailAddress,Dialog billingAddressInputDialogBox) {
        String createBillingURL = Constant.BASE_URL + "billing/createBillingDetail";

        JSONObject billingDetailsObject = new JSONObject();
        try {
            billingDetailsObject.put("userId", userId);
            billingDetailsObject.put("firstName", firstName);
            billingDetailsObject.put("lastName", lastName);
            billingDetailsObject.put("apartment", houseNoOrApartmentNo);
            billingDetailsObject.put("streetAddress", streetAddress);
            billingDetailsObject.put("city", townCity);
            billingDetailsObject.put("state", state);
            billingDetailsObject.put("pinCode", pinCode);
            billingDetailsObject.put("country", countryName);
            billingDetailsObject.put("phone", phone);
            billingDetailsObject.put("email", emailAddress);

        }
        catch (JSONException e){
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, createBillingURL, billingDetailsObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(CreateBillingAddressActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (status.equals("true")) {
                                billingAddressInputDialogBox.dismiss();
                                getBillingAddress();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(CreateBillingAddressActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                billingAddressInputDialogBox.dismiss();
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
                Toast.makeText(CreateBillingAddressActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", errorMessage);
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
        MySingleton.getInstance(CreateBillingAddressActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @SuppressLint("ResourceType")
    private void setUpPriceDetails() {

        int totalItems,totalOriginalPrice = 0,totalSellPrice = 0,totalDiscount = 0,totalDelivery = 0;

        totalItems = cartViewModelArrayList.size();

        for (int i = 0; i<cartViewModelArrayList.size(); i++){
            int origPrice = Integer.parseInt(cartViewModelArrayList.get(i).getQuantity()) * Integer.parseInt(cartViewModelArrayList.get(i).getPrice());
            int sellPrice = Integer.parseInt(cartViewModelArrayList.get(i).getQuantity()) * Integer.parseInt(cartViewModelArrayList.get(i).getSellPrice());
            totalOriginalPrice = totalOriginalPrice + origPrice;
            totalSellPrice = totalSellPrice + sellPrice;
        }

        totalDiscount = totalOriginalPrice - totalSellPrice;

        priceItemsTxt.setText("Price (" + totalItems + " items)");
        priceOriginalTxt.setText("₹ " +totalOriginalPrice);
        totalDiscountTxt.setText("- ₹ " +totalDiscount);
        totalDiscountTxt.setTextColor(Color.GREEN);
        totalAmountTxt1.setText("₹ " +totalSellPrice);
        totalAmountTxt2.setText("₹ " +totalSellPrice);

        if (totalSellPrice > 1000){
            deliveryTxt.setText("FREE DELIVERY");
            deliveryTxt.setTextColor(Color.GREEN);
        }else {
            deliveryTxt.setText("₹ " +100);
        }

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }
    public void fetchCartItems() {
        progressBar.setVisibility(View.VISIBLE);
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, cartUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");

                            if (status) {
                                bookCartRecyclerView.setVisibility(View.VISIBLE);
                                priceDetailRelativeLayout.setVisibility(View.VISIBLE);
                                deliveryAddressRelativeLayout.setVisibility(View.VISIBLE);
                                bottomStickyButtonLayout.setVisibility(View.VISIBLE);
                                noDataLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);

                                JSONObject jsonObject = response.getJSONObject("cart");
                                String cartId = jsonObject.getString("_id");
                                JSONArray jsonArray = jsonObject.getJSONArray("items");
                                cartViewModelArrayList.clear(); // Clear the list before adding new items
                                ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String bookIdData = jsonObject2.getString("bookId");
                                    if (bookIdData == null || bookIdData.equals("null"))
                                        continue;
                                    String itemId = jsonObject2.getString("_id");
                                    String quantity = jsonObject2.getString("quantity");

                                    JSONObject jsonObject3 = jsonObject2.getJSONObject("bookId");
                                    String bookId = jsonObject3.getString("_id");
                                    String type  = jsonObject3.getString("type");
                                    String title = jsonObject3.getString("title");
                                    String keyword = jsonObject3.getString("keyword");
                                    String stock = jsonObject3.getString("stock");
                                    String price = jsonObject3.getString("price");
                                    String sellPrice = jsonObject3.getString("sellPrice");
                                    String content = jsonObject3.getString("content");
                                    String author = jsonObject3.getString("author");
                                    String categoryId = jsonObject3.getString("categoryId");
                                    String subCategoryId = jsonObject3.getString("subCategoryId");
                                    String subjectId = jsonObject3.getString("subjectId");
                                    String createdDate = jsonObject3.getString("createdAt");
                                    String updatedAt = jsonObject3.getString("updatedAt");

                                    JSONArray jsonArray3 = jsonObject3.getJSONArray("images");

                                    for (int j = 0; j < jsonArray3.length(); j++) {
                                        JSONObject jsonObject4 = jsonArray3.getJSONObject(j);
                                        BookImageModels bookImageModels = new BookImageModels(
                                                jsonObject4.getString("url"),
                                                jsonObject4.getString("filename"),
                                                jsonObject4.getString("contentType"),
                                                jsonObject4.getString("size"), // Assuming size is an integer
                                                jsonObject4.getString("uploadDate"),
                                                jsonObject4.getString("_id")
                                        );
                                        bookImageArrayList.add(bookImageModels);
                                    }

                                    // Use StringBuilder for tags
                                    StringBuilder tags = new StringBuilder();
                                    JSONArray jsonArray1 = jsonObject3.getJSONArray("tags");
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        String singleTag = jsonArray1.getString(j);
                                        tags.append(singleTag).append(", ");
                                    }
                                    // Remove trailing comma and space if any
                                    if (tags.length() > 0) {
                                        tags.setLength(tags.length() - 2);
                                    }

                                    cartViewModel = new CartViewModel(cartId, itemId, bookId,type, title, keyword,stock, price, sellPrice, content, author, categoryId,subCategoryId,subjectId, tags.toString(),bookImageArrayList, createdDate, updatedAt, quantity);
                                    cartViewModelArrayList.add(cartViewModel);
                                }
                                // If you have already created the adapter, just notify the change
                                if (cartViewModelArrayList.isEmpty()) {
                                    Toast.makeText(CreateBillingAddressActivity.this, "654", Toast.LENGTH_LONG).show();

                                    noDataLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (cartViewAdapter == null) {

                                        bookCartRecyclerView.setVisibility(View.VISIBLE);
                                        priceDetailRelativeLayout.setVisibility(View.VISIBLE);
                                        deliveryAddressRelativeLayout.setVisibility(View.VISIBLE);
                                        bottomStickyButtonLayout.setVisibility(View.VISIBLE);
                                        noDataLayout.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        cartViewAdapter = new CartViewAdapter(CreateBillingAddressActivity.this, cartViewModelArrayList);
                                        bookCartRecyclerView.setAdapter(cartViewAdapter);
                                    } else {
                                        cartViewAdapter.notifyDataSetChanged();
                                    }
                                }
                                setUpPriceDetails();
                            } else {
                                bookCartRecyclerView.setVisibility(View.GONE);
                                priceDetailRelativeLayout.setVisibility(View.GONE);
                                deliveryAddressRelativeLayout.setVisibility(View.GONE);
                                bottomStickyButtonLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                noDataLayout.setVisibility(View.VISIBLE);
                                // Handle the case where status is false
                                String message = response.getString("message");
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
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
                Toast.makeText(CreateBillingAddressActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(CreateBillingAddressActivity.this).addToRequestQueue(jsonObjectRequest);
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