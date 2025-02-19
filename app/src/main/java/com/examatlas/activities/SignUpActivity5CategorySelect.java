package com.examatlas.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.SignUp5CategoryAdapter;
import com.examatlas.models.Books.CategoryModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity5CategorySelect extends AppCompatActivity implements SignUp5CategoryAdapter.OnCategoryClickListener {
    private final String categoryURL = Constant.BASE_URL + "v1/category";
    RecyclerView categoryRecyclerView;
    ArrayList<CategoryModel> categoryModelArrayList;
    SignUp5CategoryAdapter categoryAdapter;
    Button nextBtn;
    SessionManager sessionManager;
    String authToken;
    ArrayList<String> selectedCategoryIds;
    ProgressBar progressBar;
    String userId;
    private final String serverUrl = Constant.BASE_URL + "v1/auth/createUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity5_category_select);

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        nextBtn = findViewById(R.id.userNextBtn);

        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");
        userId = sessionManager.getUserData().get("user_id");
        categoryModelArrayList = new ArrayList<>();
        selectedCategoryIds = new ArrayList<>();

        getAllCategory();

        // Set up "Next" button click listener
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCategoryIds.isEmpty()) {
                    Toast.makeText(SignUpActivity5CategorySelect.this, "Please select at least one category.", Toast.LENGTH_SHORT).show();
                }else if (selectedCategoryIds.size() > 3 ) {
                    Toast.makeText(SignUpActivity5CategorySelect.this, "You can select only three category", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with the selected categories
                    Toast.makeText(SignUpActivity5CategorySelect.this, "Selected categories: " + selectedCategoryIds.toString(), Toast.LENGTH_SHORT).show();
                    // You can pass these IDs to the next activity or process them further.
                    updateCategory();
//                    startActivity(new Intent(SignUpActivity5CategorySelect.this,SignUpActivity6SubCategorySelect.class));
                }
            }
        });
    }

    private void updateCategory() {
        List<String> categorySelectedArrayList = categoryAdapter.getSelectedCategoryIds();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", userId);
            JSONArray categoryArray = new JSONArray(categorySelectedArrayList);
            jsonObject.put("organisation", categoryArray);
            jsonObject.put("step", 2);
            jsonObject.put("isActive", true);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("LoginPayload", jsonObject.toString());
        ProgressDialog progressDialog = new ProgressDialog(SignUpActivity5CategorySelect.this);
        progressDialog.setMessage("Sending Details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, serverUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(SignUpActivity5CategorySelect.this, message, Toast.LENGTH_SHORT).show();
                            Log.e("CategoryResponse",response.toString());
                            if (status.equals("true")) {
                                JSONObject userDataJson = response.getJSONObject("data");
                                String firstName = userDataJson.getString("firstname");
                                String lastName = userDataJson.getString("lastname");
                                String email = userDataJson.getString("email");
                                String user_id = userDataJson.getString("_id");
                                String role = userDataJson.getString("role");
                                String isActive = userDataJson.getString("isActive");
                                String createdAt = userDataJson.getString("createdAt");
                                String updatedAt = userDataJson.getString("updatedAt");
                                int step = userDataJson.getInt("step");
                                JSONObject addressObj = userDataJson.getJSONObject("address");
                                String state = addressObj.getString("state");
                                String city = addressObj.getString("city");

                                // Step 8: Process the "organisation" array from the response
                                JSONArray organisationArray = userDataJson.getJSONArray("organisation");
                                for (int i = 0; i < organisationArray.length(); i++) {
                                    // If you want to store or process organisation IDs
                                    String organisationId = organisationArray.getString(i);
                                    Log.d("Organisation", "Organisation ID: " + organisationId);
                                }
                                sessionManager.saveWithOutToken(user_id,firstName,lastName,email,state,city,role,isActive,String.valueOf(step),createdAt,updatedAt,null);
                                if (Objects.equals(getIntent().getStringExtra("task"), "signUp")) {
                                    Intent intent = new Intent(SignUpActivity5CategorySelect.this, LoginWithEmailActivity.class);
                                    sessionManager.logout();
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Intent intent = new Intent(SignUpActivity5CategorySelect.this, DashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SignUpActivity5CategorySelect.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        Toast.makeText(SignUpActivity5CategorySelect.this, error.toString(), Toast.LENGTH_SHORT).show();
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(SignUpActivity5CategorySelect.this, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(SignUpActivity5CategorySelect.this).addToRequestQueue(jsonObjectRequest);
    }

    public void getAllCategory() {
        String subjectURLPage = categoryURL;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subjectURLPage, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            categoryRecyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                categoryModelArrayList.clear();

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    // Convert the book object into a Map to make it dynamic
                                    Map<String, Object> categoryData = new Gson().fromJson(jsonObject2.toString(), Map.class);

                                    // Extract image URL from the "imageUrl" field, handle missing image gracefully
                                    JSONObject imgObj = jsonObject2.optJSONObject("imageUrl");
                                    String imageURL = null;
                                    if (imgObj != null) {
                                        imageURL = imgObj.optString("url", ""); // Default to empty string if URL is not present
                                    }
                                    // Handle missing image gracefully
                                    if (imageURL == null || imageURL.isEmpty()) {
                                        imageURL = "default_image_url"; // Placeholder for missing images
                                    }

                                    CategoryModel model = new CategoryModel(categoryData,imageURL); // Pass the map to the model
                                    // Add the model to the list
                                    categoryModelArrayList.add(model);
                                }
                                if (categoryModelArrayList.isEmpty()) {
                                    categoryRecyclerView.setVisibility(View.GONE);
                                } else {
                                    if (categoryAdapter == null) {
                                        categoryAdapter = new SignUp5CategoryAdapter(categoryModelArrayList, SignUpActivity5CategorySelect.this, SignUpActivity5CategorySelect.this);
                                        categoryRecyclerView.setAdapter(categoryAdapter);
                                    } else {
                                        categoryAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                Toast.makeText(SignUpActivity5CategorySelect.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SignUpActivity5CategorySelect.this, message, Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(SignUpActivity5CategorySelect.this).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    public void onCategoryClick(ArrayList<String> selectedCategoryIds) {
        this.selectedCategoryIds = selectedCategoryIds;
    }
}
