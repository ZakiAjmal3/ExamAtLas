package com.examatlas.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.Books.CartViewActivity;
import com.examatlas.activities.Books.DeliveryAddressInputActivity;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.adapter.books.DeliveryAddressItemAdapter;
import com.examatlas.models.Books.DeliveryAddressItemModel;
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

public class DeliveryAddressInput1Fragment extends Fragment {
    TextView addAddressTxt,deliverHereBtn;
    ShimmerFrameLayout shimmer_address_container;
    RecyclerView deliveryAddressRecyclerView;
    ArrayList<DeliveryAddressItemModel> deliveryAddressModelArrayList = new ArrayList<>();
    DeliveryAddressItemAdapter deliveryAddressItemAdapter;
    SessionManager sessionManager;
    String token;
    int selectedPosition = -1;
    RelativeLayout noAddressRL;
    String selectedBillingId = "";
    Dialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_address_input_layout_1, container, false);

        sessionManager = new SessionManager(getContext());
        token = sessionManager.getUserData().get("authToken");

        noAddressRL = view.findViewById(R.id.noAddressRL);

        if (getArguments() != null) {
            selectedBillingId = getArguments().getString("id");
        }
        addAddressTxt = view.findViewById(R.id.addAddressTxt);
        deliverHereBtn = view.findViewById(R.id.deliverHereBtn);
        shimmer_address_container = view.findViewById(R.id.shimmer_address_container);
        shimmer_address_container.startShimmer();
        deliveryAddressRecyclerView = view.findViewById(R.id.addressRecyclerView);
        deliveryAddressRecyclerView.setVisibility(View.VISIBLE);
        deliveryAddressRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DeliveryAddressInput1Fragment fragment = new DeliveryAddressInput1Fragment();
        deliveryAddressItemAdapter = new DeliveryAddressItemAdapter(getContext(),deliveryAddressModelArrayList,fragment, selectedBillingId);

        addAddressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    DeliveryAddressInputActivity activity = (DeliveryAddressInputActivity) getActivity();
                    activity.loadFragment(new DeliveryAddressInput2Fragment());
                }
            }
        });
        getDeliveryAddress();

        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedBillingId = deliveryAddressItemAdapter.getSelectedBillingId();
                if (!selectedBillingId.equals("")){
                    progressDialog = new Dialog(getContext());
                    progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    progressDialog.setContentView(R.layout.progress_bar_drawer);
                    progressDialog.setCancelable(false);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                    progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                    progressDialog.show();
                    setThisAddressAsSelected();
                }else {
                    Toast.makeText(getContext(), "Please Add Address", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void setThisAddressAsSelected() {
        String paginatedURL = Constant.BASE_URL + "v1/address/select/" + selectedBillingId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                progressDialog.dismiss();
                                getActivity().finish();
                            } else {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void getDeliveryAddress() {
        String paginatedURL = Constant.BASE_URL + "v1/address";;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                deliveryAddressModelArrayList.clear();

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
                                    String selected = jsonObject2.getString("isDefault");

//                                    if (selected.equals("true")){
//                                        selectedBillingId = billingId;
//                                    }else {
                                        selectedBillingId = jsonArray.getJSONObject(0).getString("_id");
//                                    }
                                    DeliveryAddressItemModel model = new DeliveryAddressItemModel(billingId, addressType, firstName, lastName, houseNoOrApartmentNo, streetAddress, townCity, state, pinCode, countryName, phone, emailAddress,selected);
                                    deliveryAddressModelArrayList.add(model);

                                }
                                if (deliveryAddressModelArrayList.isEmpty()){
                                    noAddressRL.setVisibility(View.VISIBLE);
                                    shimmer_address_container.stopShimmer();
                                    shimmer_address_container.setVisibility(View.GONE);
                                    deliveryAddressRecyclerView.setVisibility(View.GONE);
                                }else {
                                    noAddressRL.setVisibility(View.GONE);
                                    shimmer_address_container.stopShimmer();
                                    shimmer_address_container.setVisibility(View.GONE);
                                    deliveryAddressRecyclerView.setVisibility(View.VISIBLE);
                                    deliveryAddressRecyclerView.setAdapter(deliveryAddressItemAdapter);
                                }
                            } else {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    public void onResume() {
        super.onResume();
        getDeliveryAddress();
    }
}
