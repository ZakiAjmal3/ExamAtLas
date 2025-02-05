package com.examatlas.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.Books.DeliveryAddressInputActivity;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.activities.OtpActivity;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeliveryAddressInput2Fragment extends Fragment {
    TextInputLayout firstNameLayout,lastNameLayout,phoneLayout,emailLayout,countryLayout,pincodeLayout,stateLayout,cityLayout,houseNumberLayout,streetAddressLayout;
    EditText firstNameEditText, lastNameEditText, phoneEditText, emailAddressEditText,countryEditText, pincodeEditText, stateEditText, cityEditText, houseNumberEditText, streetAddressEditText, landmarkEditText;
    TextView homeTxt, workTxt;
    ImageView homeImg, workImg;
    LinearLayout homeLL, workLL;
    Boolean isHomeSelected = false, isWorkSelected = false;
    String selectedAddressType = "";
    Button useMyLocationBtn,saveAddressBtn;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    String addressURL = Constant.BASE_URL + "v1/address";
    SessionManager sessionManager;
    String token,addressId = "";
    Dialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_address_input_layout_2, container, false);

        sessionManager = new SessionManager(getContext());
        token = sessionManager.getUserData().get("authToken");

        firstNameLayout = view.findViewById(R.id.firstNameLayout);
        lastNameLayout = view.findViewById(R.id.lastNameLayout);
        phoneLayout = view.findViewById(R.id.phoneLayout);
        emailLayout = view.findViewById(R.id.emailAddressLayout);
        countryLayout = view.findViewById(R.id.countryLayout);
        pincodeLayout = view.findViewById(R.id.pincodeLayout);
        stateLayout = view.findViewById(R.id.stateLayout);
        cityLayout = view.findViewById(R.id.cityLayout);
        houseNumberLayout = view.findViewById(R.id.houseNumberLayout);
        streetAddressLayout = view.findViewById(R.id.streetAddressLayout);

        firstNameEditText = view.findViewById(R.id.firstNameLayoutEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailAddressEditText = view.findViewById(R.id.emailAddressEditText);
        countryEditText = view.findViewById(R.id.countryEditText);
        pincodeEditText = view.findViewById(R.id.pincodeEditText);
        stateEditText = view.findViewById(R.id.stateEditText);
        cityEditText = view.findViewById(R.id.CityEditText);
        houseNumberEditText = view.findViewById(R.id.houseNumberEditText);
        streetAddressEditText = view.findViewById(R.id.streetAddressEditText);

        homeLL = view.findViewById(R.id.homeLL);
        workLL = view.findViewById(R.id.workLL);
        homeTxt = view.findViewById(R.id.homeTxt);
        workTxt = view.findViewById(R.id.workTxt);
        homeImg = view.findViewById(R.id.homeImg);
        workImg = view.findViewById(R.id.workImg);

        saveAddressBtn = view.findViewById(R.id.saveAddress);
        useMyLocationBtn = view.findViewById(R.id.useMyLocationBtn);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        homeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHomeSelected){
                    homeTxt.setTextColor(getResources().getColor(R.color.seed));
                    Glide.with(getContext()).load(R.drawable.home2_color).into(homeImg);
                    homeLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_selected);
                    isHomeSelected = true;
                    selectedAddressType = "Home";
                    workTxt.setTextColor(getResources().getColor(R.color.dark_grey));
                    Glide.with(getContext()).load(R.drawable.ic_work).into(workImg);
                    workLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isWorkSelected = false;
                }else {
                    homeTxt.setTextColor(getResources().getColor(R.color.dark_grey));
                    Glide.with(getContext()).load(R.drawable.home2).into(homeImg);
                    homeLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isHomeSelected = false;
                    selectedAddressType = "";
                    workTxt.setTextColor(getResources().getColor(R.color.dark_grey));
                    Glide.with(getContext()).load(R.drawable.ic_work).into(workImg);
                    workLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isWorkSelected = false;
                }
            }
        });
        workLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isWorkSelected){
                    workTxt.setTextColor(getResources().getColor(R.color.seed));
                    Glide.with(getContext()).load(R.drawable.ic_work_color).into(workImg);
                    workLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_selected);
                    isWorkSelected = true;
                    selectedAddressType = "Work";
                    homeTxt.setTextColor(getResources().getColor(R.color.dark_grey));
                    Glide.with(getContext()).load(R.drawable.home2).into(homeImg);
                    homeLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isHomeSelected = false;
                }else {
                    workTxt.setTextColor(getResources().getColor(R.color.dark_grey));
                    Glide.with(getContext()).load(R.drawable.ic_work).into(workImg);
                    workLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isWorkSelected = false;
                    selectedAddressType = "";
                    homeTxt.setTextColor(getResources().getColor(R.color.dark_grey));
                    Glide.with(getContext()).load(R.drawable.home2).into(homeImg);
                    homeLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                    isHomeSelected = false;
                }
            }
        });

        useMyLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for location permissions
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, fetch the location
                    getLocation();
                } else {
                    // Request location permissions
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        });

        saveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAllFields();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null){
            addressId = bundle.getString("id");
            firstNameEditText.setText(bundle.getString("firstname"));
            lastNameEditText.setText(bundle.getString("lastname"));
            emailAddressEditText.setText(bundle.getString("email"));
            phoneEditText.setText(bundle.getString("phone"));
            houseNumberEditText.setText(bundle.getString("apartment"));
            streetAddressEditText.setText(bundle.getString("street"));
            cityEditText.setText(bundle.getString("city"));
            stateEditText.setText(bundle.getString("state"));
            countryEditText.setText(bundle.getString("country"));
            pincodeEditText.setText(bundle.getString("pincode"));
            selectedAddressType = bundle.getString("addressType");
            if (selectedAddressType.equalsIgnoreCase("Home")){
                homeTxt.setTextColor(getResources().getColor(R.color.seed));
                Glide.with(getContext()).load(R.drawable.home2_color).into(homeImg);
                homeLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_selected);
                isHomeSelected = true;
                selectedAddressType = "Home";
                workTxt.setTextColor(getResources().getColor(R.color.dark_grey));
                Glide.with(getContext()).load(R.drawable.ic_work).into(workImg);
                workLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                isWorkSelected = false;
            }else {
                workTxt.setTextColor(getResources().getColor(R.color.seed));
                Glide.with(getContext()).load(R.drawable.ic_work_color).into(workImg);
                workLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_selected);
                isWorkSelected = true;
                selectedAddressType = "Work";
                homeTxt.setTextColor(getResources().getColor(R.color.dark_grey));
                Glide.with(getContext()).load(R.drawable.home2).into(homeImg);
                homeLL.setBackgroundResource(R.drawable.rounded_corner_for_rate_product_plain);
                isHomeSelected = false;
            }
        }

        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    firstNameLayout.setError(null);
                }else {
                    firstNameLayout.setError("Please provide necessary details");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    lastNameLayout.setError(null);
                }else {
                    lastNameLayout.setError("Please provide necessary details");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        emailAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    emailLayout.setError(null);
                }else {
                    emailLayout.setError("Please provide necessary details");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    phoneLayout.setError(null);
                }else {
                    phoneLayout.setError("Please provide necessary details");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        streetAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    streetAddressLayout.setError(null);
                }else {
                    streetAddressLayout.setError("Please provide necessary details");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        houseNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    houseNumberLayout.setError(null);
                }else {
                    houseNumberLayout.setError("Please provide necessary details");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    cityLayout.setError(null);
                }else {
                    cityLayout.setError("Please provide necessary details");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        stateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    stateLayout.setError(null);
                }else {
                    stateLayout.setError("Please provide necessary details");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        countryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    countryLayout.setError(null);
                }else {
                    countryLayout.setError("Please provide necessary details");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.equals("")){
                    countryLayout.setError("Please provide necessary details");
                }
            }
        });
        pincodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals("")){
                    pincodeLayout.setError(null);
                }else {
                    pincodeLayout.setError("Please provide necessary details");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return view;
    }

    public void checkAllFields() {
        if (firstNameEditText.getText().toString().isEmpty()){
            firstNameLayout.setError("Please provide necessary details");
        }else {
            firstNameLayout.setError(null);
        }
        if (lastNameEditText.getText().toString().isEmpty()){
            lastNameLayout.setError("Please provide necessary details");
        }else {
            lastNameLayout.setError(null);
        }
        if (phoneEditText.getText().toString().isEmpty()){
            phoneLayout.setError("Please provide necessary details");
        }else {
            phoneLayout.setError(null);
        }
        if (emailAddressEditText.getText().toString().isEmpty()){
            emailLayout.setError("Please provide necessary details");
        }else {
            emailLayout.setError(null);
        }
        if (pincodeEditText.getText().toString().isEmpty()){
            pincodeLayout.setError("Please provide necessary details");
        }else {
            pincodeLayout.setError(null);
        }
        if (stateEditText.getText().toString().isEmpty()){
            stateLayout.setError("Please provide necessary details");
        }else {
            stateLayout.setError(null);
        }
        if (cityEditText.getText().toString().isEmpty()){
            cityLayout.setError("Please provide necessary details");
        }else {
            cityLayout.setError(null);
        }
        if (countryEditText.getText().toString().isEmpty()){
            countryLayout.setError("Please provide necessary details");
        }else {
            countryLayout.setError(null);
        }
        if (houseNumberEditText.getText().toString().isEmpty()){
            houseNumberLayout.setError("Please provide necessary details");
        }else {
            houseNumberLayout.setError(null);
        }
        if (streetAddressEditText.getText().toString().isEmpty()){
            streetAddressLayout.setError("Please provide necessary details");
        }else {
            streetAddressLayout.setError(null);
        }
        if (selectedAddressType.isEmpty()){
            Toast.makeText(getContext(), "Please select address type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addressId.isEmpty()){
            sendAddress();
        }
        else {
            updateAddress(addressId);
        }
    }

    public void updateAddress(String id) {
        String addressUpdateURL = addressURL + "/update/" + id;
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Updating new Address...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("addressType", selectedAddressType);
            jsonObject.put("firstName", firstNameEditText.getText().toString().trim());
            jsonObject.put("lastName", lastNameEditText.getText().toString().trim());
            jsonObject.put("country", countryEditText.getText().toString().trim());
            jsonObject.put("streetAddress", streetAddressEditText.getText().toString().trim());
            jsonObject.put("apartment", houseNumberEditText.getText().toString().trim());
            jsonObject.put("city", cityEditText.getText().toString().trim());
            jsonObject.put("state", stateEditText.getText().toString().trim());
            jsonObject.put("pinCode", pincodeEditText.getText().toString().trim());
            jsonObject.put("phone", phoneEditText.getText().toString().trim());
            jsonObject.put("email", emailAddressEditText.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addressUpdateURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            Log.e("Create User response",response.toString());
                            String status = response.getString("success");
                            String message = response.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            if (status.equals("true")){
                                DeliveryAddressInputActivity activity = (DeliveryAddressInputActivity) getActivity();
                                activity.loadFragment(new DeliveryAddressInput1Fragment());
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();

                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        progressDialog.dismiss();

//                        Toast.makeText(OtpActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
//                        Toast.makeText(OtpActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        progressDialog.dismiss();

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
    public void sendAddress() {
        progressDialog = new Dialog(DeliveryAddressInput2Fragment.this.getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_bar_drawer);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("addressType", selectedAddressType);
            jsonObject.put("firstName", firstNameEditText.getText().toString().trim());
            jsonObject.put("lastName", lastNameEditText.getText().toString().trim());
            jsonObject.put("country", countryEditText.getText().toString().trim());
            jsonObject.put("streetAddress", streetAddressEditText.getText().toString().trim());
            jsonObject.put("apartment", houseNumberEditText.getText().toString().trim());
            jsonObject.put("city", cityEditText.getText().toString().trim());
            jsonObject.put("state", stateEditText.getText().toString().trim());
            jsonObject.put("pinCode", pincodeEditText.getText().toString().trim());
            jsonObject.put("phone", phoneEditText.getText().toString().trim());
            jsonObject.put("email", emailAddressEditText.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addressURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            Log.e("Create User response",response.toString());
                            String status = response.getString("success");
                            String message = response.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            if (status.equals("true")){
                                DeliveryAddressInputActivity activity = (DeliveryAddressInputActivity) getActivity();
                                activity.loadFragment(new DeliveryAddressInput1Fragment());
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();

                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        progressDialog.dismiss();

//                        Toast.makeText(OtpActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
//                        Toast.makeText(OtpActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        progressDialog.dismiss();

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
    // Method to fetch location
    private void getLocation() {
        // Check for permissions before accessing location
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the last known location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                // Use the latitude and longitude
                                // Convert location (latitude, longitude) to address
                                getAddressFromLocation(latitude, longitude);
                            }
                        }
                    });
        }
    }
    // Convert latitude and longitude to address using Geocoder
    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String pinCode = address.getPostalCode(); // Get full address
                String city = address.getLocality(); // Get city
                String state = address.getAdminArea();
                String country = address.getCountryName(); // Get country

                pincodeEditText.setText(pinCode);
                cityEditText.setText(city);
                stateEditText.setText(state);
                countryEditText.setText(country);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch the location
                getLocation();
            } else {
                // Permission denied
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
