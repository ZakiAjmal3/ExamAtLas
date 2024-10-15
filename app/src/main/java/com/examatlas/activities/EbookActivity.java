package com.examatlas.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.examatlas.adapter.EbookAdapter;
import com.examatlas.models.EbookModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EbookActivity extends AppCompatActivity {
    ImageView toolbarBackBtn;
    RecyclerView ebookRecyclerview;
    EbookAdapter ebookAdapter;
    ArrayList<EbookModel> ebookModelArrayList;
    ProgressBar progressBar;
    private final String ebookURL = Constant.BASE_URL + "book/getAllBooks";
    RelativeLayout noDataLayout;
    String token;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook);

        toolbarBackBtn = findViewById(R.id.imgBack);
        ebookRecyclerview = findViewById(R.id.eBookRecycler);
        progressBar = findViewById(R.id.ebookProgress);
        noDataLayout = findViewById(R.id.noDataLayout);

        ebookModelArrayList = new ArrayList<>();

        ebookRecyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));

        ebookRecyclerview.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sessionManager = new SessionManager(EbookActivity.this);

        token = sessionManager.getUserData().get("authToken");

        getEbooks();

    }

    private void getEbooks() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ebookURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ebookRecyclerview.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("books");
                                ebookModelArrayList.clear(); // Clear the list before adding new items

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String ebookID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String keyword = jsonObject2.getString("keyword");
                                    String price = jsonObject2.getString("price");
                                    String content = jsonObject2.getString("content");
                                    String author = jsonObject2.getString("author");
                                    String category = jsonObject2.getString("category");
                                    String createdDate = jsonObject2.getString("createdAt");

                                    // Use StringBuilder for tags
                                    StringBuilder tags = new StringBuilder();
                                    JSONArray jsonArray1 = jsonObject2.getJSONArray("tags");
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        String singleTag = jsonArray1.getString(j);
                                        tags.append(singleTag).append(", ");
                                    }
                                    // Remove trailing comma and space if any
                                    if (tags.length() > 0) {
                                        tags.setLength(tags.length() - 2);
                                    }

                                    EbookModel ebookModel = new EbookModel(ebookID, title, keyword, price, content, author,category,tags.toString(),createdDate);
                                    ebookModelArrayList.add(ebookModel);
                                }
                                // If you have already created the adapter, just notify the change
                                if (ebookModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (ebookAdapter == null) {
                                        ebookAdapter = new EbookAdapter(ebookModelArrayList, EbookActivity.this);
                                        ebookRecyclerview.setAdapter(ebookAdapter);
                                    } else {
                                        ebookAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                // Handle the case where status is false
                                String message = response.getString("message");
                                Toast.makeText(EbookActivity.this, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EbookActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(EbookActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}