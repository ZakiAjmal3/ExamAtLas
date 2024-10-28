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
import com.examatlas.adapter.CreateDeliveryAddressAdapter;
import com.examatlas.adapter.extraAdapter.BookOrderSummaryItemsDetailsRecyclerViewAdapter;
import com.examatlas.models.CartViewModel;
import com.examatlas.models.CreateDeliveryAddressModel;
import com.examatlas.models.extraModels.BookImageModels;
import com.examatlas.models.extraModels.BookOrderSummaryItemsDetailsRecyclerViewModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateDeliveryAddressActivity extends AppCompatActivity {
    RecyclerView bookCartRecyclerView,deliveryAddressRecyclerView;
    CreateDeliveryAddressModel createDeliveryAddressModel;
    CreateDeliveryAddressAdapter createDeliveryAddressAdapter;
    ArrayList<CreateDeliveryAddressModel> createDeliveryAddressModelArrayList;
    CartViewAdapter cartViewAdapter;
    CartViewModel cartViewModel;
    ArrayList<CartViewModel> cartViewModelArrayList;
    Toolbar toolbar;
    SessionManager sessionManager;
    String cartUrl,authToken,userId;
    RelativeLayout noDataLayout, priceDetailRelativeLayout,deliveryAddressRelativeLayout,bottomStickyButtonLayout;
    ProgressBar progressBar;
    Button goToCheckout,changeAddressBtn,addNewAddress;
    TextView priceItemsTxt,priceOriginalTxt,totalDiscountTxt,deliveryTxt,totalAmountTxt1,totalAmountTxt2;
    String addressCombineStr = "";
    String billingId,firstName,lastName,houseNoOrApartmentNo,streetAddress,townCity,state,pinCode,countryName,phone,emailAddress;
    int totalSellPrice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_delivery_address);

        toolbar = findViewById(R.id.hardbook_ecomm_cart_toolbar);
        bookCartRecyclerView = findViewById(R.id.cartItemRecycler);
        deliveryAddressRecyclerView = findViewById(R.id.deliveryAddressRecyclerView);
        noDataLayout = findViewById(R.id.noDataLayout);
        priceDetailRelativeLayout = findViewById(R.id.priceRelativeLayout);
        deliveryAddressRelativeLayout = findViewById(R.id.deliveryAddressInput);
        bottomStickyButtonLayout = findViewById(R.id.bottomStickyRelativeLayout);
        progressBar = findViewById(R.id.cartProgress);
        goToCheckout = findViewById(R.id.gotoCheckOut);
        changeAddressBtn = findViewById(R.id.changeAddressBtn);
        addNewAddress = findViewById(R.id.addNewAddress);

        priceItemsTxt = findViewById(R.id.priceAndItemstxt);
        priceOriginalTxt = findViewById(R.id.priceTxt);
        totalDiscountTxt = findViewById(R.id.discountTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalAmountTxt1 = findViewById(R.id.totalAmountPriceTxt);
        totalAmountTxt2 = findViewById(R.id.bottomStickyAmountTxt);

        sessionManager = new SessionManager(this);
        cartViewModelArrayList = new ArrayList<>();
        createDeliveryAddressModelArrayList = new ArrayList<>();

        bookCartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deliveryAddressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartUrl = Constant.BASE_URL + "cart/get/" + sessionManager.getUserData().get("user_id");
        authToken = sessionManager.getUserData().get("authToken");
        userId = sessionManager.getUserData().get("user_id");

        setupToolbar();
        fetchCartItems();
        getBillingAddress();

        changeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateDeliveryAddressAdapter adapter = (CreateDeliveryAddressAdapter) deliveryAddressRecyclerView.getAdapter();
                CreateDeliveryAddressModel selectedAddress = adapter.getSelectedAddress();
                if (adapter != null) {
                    if (selectedAddress != null) {
                        openPopUpAddAddress(selectedAddress);
                    } else {
                        Toast.makeText(CreateDeliveryAddressActivity.this, "Please select an address to edit.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopUpAddAddress(null);
            }
        });
        goToCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateDeliveryAddressAdapter adapter = (CreateDeliveryAddressAdapter) deliveryAddressRecyclerView.getAdapter();
                CreateDeliveryAddressModel selectedAddress = adapter.getSelectedAddress();
                if (selectedAddress == null) {
                    Toast.makeText(CreateDeliveryAddressActivity.this, "Please select an address to continue.", Toast.LENGTH_SHORT).show();
                }else {
                    openOrderSummaryDialog(selectedAddress);
                }
            }
        });

    }
    RecyclerView itemDetailsRecyclerView;
    BookOrderSummaryItemsDetailsRecyclerViewModel bookOrderSummaryItemsDetailsRecyclerViewModel;
    BookOrderSummaryItemsDetailsRecyclerViewAdapter bookOrderSummaryItemsDetailsRecyclerViewAdapter;
    ArrayList<BookOrderSummaryItemsDetailsRecyclerViewModel> bookOrderSummaryItemsDetailsRecyclerViewModelArrayList;
    TextView totalAmountTxt;
    Button proceedToPaymentBtn;
    ImageView orderSummaryCrossBtn;
    private void openOrderSummaryDialog(CreateDeliveryAddressModel selectedAddress) {
        Dialog orderSummaryDialog = new Dialog(this);
        orderSummaryDialog.setContentView(R.layout.book_order_summary_before_payment_dialog_box);

        itemDetailsRecyclerView = orderSummaryDialog.findViewById(R.id.orderSummaryRecyclerView);
        itemDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        totalAmountTxt = orderSummaryDialog.findViewById(R.id.totalAmountPriceTxt);
        proceedToPaymentBtn = orderSummaryDialog.findViewById(R.id.proceedToPayment);
        orderSummaryCrossBtn = orderSummaryDialog.findViewById(R.id.crossBtn);

        bookOrderSummaryItemsDetailsRecyclerViewModelArrayList = new ArrayList<>();

        for (int i = 0; i<cartViewModelArrayList.size(); i++){
            bookOrderSummaryItemsDetailsRecyclerViewModel = new BookOrderSummaryItemsDetailsRecyclerViewModel(cartViewModelArrayList.get(i).getTitle(),cartViewModelArrayList.get(i).getSellPrice(),cartViewModelArrayList.get(i).getQuantity());
            bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.add(bookOrderSummaryItemsDetailsRecyclerViewModel);
        }
        bookOrderSummaryItemsDetailsRecyclerViewAdapter = new BookOrderSummaryItemsDetailsRecyclerViewAdapter(this,bookOrderSummaryItemsDetailsRecyclerViewModelArrayList);
        itemDetailsRecyclerView.setAdapter(bookOrderSummaryItemsDetailsRecyclerViewAdapter);

        totalAmountTxt.setText("₹" + totalSellPrice);

        orderSummaryCrossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderSummaryDialog.dismiss();
            }
        });
        proceedToPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkOutURL = Constant.BASE_URL + "payment/checkout";

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userId", userId);
                    jsonObject.put("amount", totalSellPrice);
                    jsonObject.put("billingDetailId", selectedAddress.getBillingId());

                    JSONArray cartItemsArray = new JSONArray();
                    for (CartViewModel cartItem : cartViewModelArrayList) {
                        JSONObject itemObject = new JSONObject();
                        try {
                            itemObject.put("cartId", cartItem.getCartId()); // Assuming you have a method to get cartId
                            itemObject.put("itemId", cartItem.getItemId());
                            itemObject.put("title", cartItem.getTitle());
                            itemObject.put("sellPrice", cartItem.getSellPrice());
                            itemObject.put("quantity", cartItem.getQuantity());
                            itemObject.put("bookId", cartItem.getBookId());
                            itemObject.put("type", cartItem.getType());
                            itemObject.put("keyword", cartItem.getKeyword());
                            itemObject.put("stock", cartItem.getStock());
                            itemObject.put("price", cartItem.getPrice());
                            itemObject.put("content", cartItem.getContent());
                            itemObject.put("author", cartItem.getAuthor());
                            itemObject.put("categoryId", cartItem.getCategoryId());
                            itemObject.put("subCategoryId", cartItem.getSubCategoryId());
                            itemObject.put("subjectId", cartItem.getSubjectId());
                            itemObject.put("createdDate", cartItem.getCreatedAt());
                            itemObject.put("updatedAt", cartItem.getUpdatedAt());

                            // Assuming bookImageArrayList is a property in CartViewModel
                            JSONArray bookImagesArray = new JSONArray();
                            for (BookImageModels image : cartItem.getBookImageArrayList()) {
                                JSONObject imageObject = new JSONObject();
                                imageObject.put("url", image.getUrl());
                                imageObject.put("filename", image.getFileName());
                                imageObject.put("contentType", image.getContentType());
                                imageObject.put("size", image.getSize());
                                imageObject.put("uploadDate", image.getUploadDate());
                                imageObject.put("_id", image.getId()); // Assuming you have an ID for the image

                                bookImagesArray.put(imageObject);
                            }
                            itemObject.put("images", bookImagesArray);

                        } catch (JSONException e) {
                            e.printStackTrace(); // Handle JSON exception
                        }

                        cartItemsArray.put(itemObject);
                    }
                    jsonObject.put("cartItems", cartItemsArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, checkOutURL, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("responseData",response.toString());
                                try {
                                    String status = response.getString("success");
                                    if (status.equals("true")){
                                        JSONObject orderJsonObject = response.getJSONObject("order");
                                        String orderId = orderJsonObject.getString("id");
                                        String amount = orderJsonObject.getString("amount");
                                        String currency = orderJsonObject.getString("currency");
                                        JSONObject orderDetailsJsobObject = orderJsonObject.getJSONObject("orderDetails");
                                        String userId = orderDetailsJsobObject.getString("userId");
                                        String totalAmount = orderDetailsJsobObject.getString("totalAmount");
                                        String paymentMethod = orderDetailsJsobObject.getString("paymentMethod");
                                        String billingDetailsId = orderDetailsJsobObject.getString("billingDetailId");
                                        String razorpay_order_id = orderDetailsJsobObject.getString("razorpay_order_id");
                                        String orderDetailsId = orderDetailsJsobObject.getString("_id");

                                        Intent intent = new Intent(CreateDeliveryAddressActivity.this,BookOrderPaymentActivity.class);
                                        intent.putExtra("orderId",orderId);
                                        intent.putExtra("amount",amount);
                                        intent.putExtra("currency",currency);
                                        intent.putExtra("userId",userId);
                                        intent.putExtra("totalAmount",totalAmount);
                                        intent.putExtra("paymentMethod",paymentMethod);
                                        intent.putExtra("billingDetailId",billingDetailsId);
                                        intent.putExtra("razorpay_order_id",razorpay_order_id);
                                        intent.putExtra("orderDetailsId",orderDetailsId);
                                        startActivity(intent);
                                    }
                                } catch (JSONException e) {
                                    Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.toString();
                        Toast.makeText(CreateDeliveryAddressActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("onErrorResponse", errorMessage);
                        if (error.networkResponse != null) {
                            try {
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
                                Toast.makeText(CreateDeliveryAddressActivity.this, message, Toast.LENGTH_LONG).show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            }
        });
        orderSummaryDialog.show();
        WindowManager.LayoutParams params = orderSummaryDialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

        // Set the window attributes
        orderSummaryDialog.getWindow().setAttributes(params);

        // Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) orderSummaryDialog.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

        // Background and animation settings
        orderSummaryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        orderSummaryDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

    }

    private void getBillingAddress() {
        String getAddressURL = Constant.BASE_URL + "billing/billing/user/" + userId;

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, getAddressURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            createDeliveryAddressModelArrayList.clear();
                            for (int i = 0;i<response.length();i++){
                                JSONObject jsonObject = response.getJSONObject(i);
                                billingId = jsonObject.getString("_id");
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

                                createDeliveryAddressModel = new CreateDeliveryAddressModel(billingId,firstName,lastName,houseNoOrApartmentNo,streetAddress,townCity,state,pinCode,countryName,phone,emailAddress);
                                createDeliveryAddressModelArrayList.add(createDeliveryAddressModel);

                                addressCombineStr = firstName +" " + lastName + ", " + houseNoOrApartmentNo + ", " + streetAddress + ", " + townCity + ", " + state + ", " + pinCode + ", " + countryName + ", " + phone + ", " + emailAddress;
                            }
                            createDeliveryAddressAdapter = new CreateDeliveryAddressAdapter(CreateDeliveryAddressActivity.this, createDeliveryAddressModelArrayList);
                            deliveryAddressRecyclerView.setAdapter(createDeliveryAddressAdapter);
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
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(CreateDeliveryAddressActivity.this, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(CreateDeliveryAddressActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    EditText firstNameEditText,lastNameEditText,houseNoOrApartmentNoEditText,streetAddressEditText,townCityEditText,stateEditText,pinCodeEditText,countryNameEditText,phoneEditText,emailAddressEditText;
    Button saveAndContinueBtn;
    ImageView crossBtn;
    private void openPopUpAddAddress(CreateDeliveryAddressModel selectedAddress) {
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

        if (selectedAddress != null && selectedAddress.getFirstName() != null && selectedAddress.getLastName() != null && selectedAddress.getHouseNoOrApartmentNo() != null && selectedAddress.getStreetAddress() != null && selectedAddress.getTownCity() != null && selectedAddress.getState() != null && selectedAddress.getPinCode() != null && selectedAddress.getCountryName() != null && selectedAddress.getPhone() != null && selectedAddress.getEmailAddress() != null){
            firstNameEditText.setText(selectedAddress.getFirstName());
            lastNameEditText.setText(selectedAddress.getLastName());
            houseNoOrApartmentNoEditText.setText(selectedAddress.getHouseNoOrApartmentNo());
            streetAddressEditText.setText(selectedAddress.getStreetAddress());
            townCityEditText.setText(selectedAddress.getTownCity());
            stateEditText.setText(selectedAddress.getState());
            pinCodeEditText.setText(selectedAddress.getPinCode());
            countryNameEditText.setText(selectedAddress.getCountryName());
            phoneEditText.setText(selectedAddress.getPhone());
            emailAddressEditText.setText(selectedAddress.getEmailAddress());
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
                String firstName, lastName, houseNoOrApartmentNo, streetAddress, townCity, state, pinCode, countryName, phone, emailAddress;

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

                if (saveAndContinueBtn.getText().toString().equals("Update")) {
                    updateBillingAddress(firstName, lastName, houseNoOrApartmentNo, streetAddress, townCity, state, pinCode, countryName, phone, emailAddress, billingAddressInputDialogBox,selectedAddress.getBillingId());
                } else {
                    createBillingAddress(firstName, lastName, houseNoOrApartmentNo, streetAddress, townCity, state, pinCode, countryName, phone, emailAddress, billingAddressInputDialogBox);
                }
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

    private void updateBillingAddress(String firstName, String lastName, String houseNoOrApartmentNo, String streetAddress, String townCity, String state, String pinCode, String countryName, String phone, String emailAddress, Dialog billingAddressInputDialogBox, String billingId) {

        String createBillingURL = Constant.BASE_URL + "billing/billing/updatebilling/" + userId + "/" + billingId;

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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, createBillingURL, billingDetailsObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(CreateDeliveryAddressActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (status.equals("true")) {
                                billingAddressInputDialogBox.dismiss();
                                getBillingAddress();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(CreateDeliveryAddressActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                billingAddressInputDialogBox.dismiss();
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(CreateDeliveryAddressActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(CreateDeliveryAddressActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(CreateDeliveryAddressActivity.this).addToRequestQueue(jsonObjectRequest);
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
                            Toast.makeText(CreateDeliveryAddressActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (status.equals("true")) {
                                billingAddressInputDialogBox.dismiss();
                                getBillingAddress();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(CreateDeliveryAddressActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                billingAddressInputDialogBox.dismiss();
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(CreateDeliveryAddressActivity.this, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(CreateDeliveryAddressActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @SuppressLint("ResourceType")
    private void setUpPriceDetails() {

        int totalItems,totalOriginalPrice = 0,totalDiscount = 0,totalDelivery = 0;

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
                                    Toast.makeText(CreateDeliveryAddressActivity.this, "654", Toast.LENGTH_LONG).show();

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
                                        cartViewAdapter = new CartViewAdapter(CreateDeliveryAddressActivity.this, cartViewModelArrayList);
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
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(CreateDeliveryAddressActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
        MySingleton.getInstance(CreateDeliveryAddressActivity.this).addToRequestQueue(jsonObjectRequest);
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