package com.examatlas.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.CurrentAffairsAdapter;
import com.examatlas.adapter.CurrentAffairsShowingAllAdapter;
import com.examatlas.fragment.HomeFragment;
import com.examatlas.models.CurrentAffairsModel;
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

public class CurrentAffairsActivity extends AppCompatActivity {
    ImageView backBtn;
    RecyclerView currentAffairRecycler;
    ArrayList<CurrentAffairsModel> currentAffairsModelArrayList;
    CurrentAffairsShowingAllAdapter currentAffairAdapter;
    SessionManager sessionManager;
    String authToken,currentAffairsURL = Constant.BASE_URL + "currentAffair/getAllCA";
    ShimmerFrameLayout shimmerFrameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_affairs);

        backBtn = findViewById(R.id.backBtn);
        currentAffairRecycler = findViewById(R.id.currentAffairsRecycler);
        currentAffairRecycler.setVisibility(View.GONE);
        shimmerFrameLayout = findViewById(R.id.shimmer_blog_container);
        shimmerFrameLayout.startShimmer();
        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");
        currentAffairsModelArrayList = new ArrayList<>();
        currentAffairRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCurrentAffairs();
            }
        },1000);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    private void getCurrentAffairs() {
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, currentAffairsURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currentAffairRecycler.setVisibility(View.VISIBLE);
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                currentAffairsModelArrayList.clear(); // Clear the list before adding new items

                                JSONObject jsonObject2 = response.getJSONObject("pagination");
                                String totalRows = jsonObject2.getString("totalRows");
                                String totalPages = jsonObject2.getString("totalPages");
                                String currentPage = jsonObject2.getString("currentPage");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String affairID = jsonObject.getString("_id");
                                    String title = jsonObject.getString("title");
                                    String keyword = jsonObject.getString("keyword");
                                    String content = jsonObject.getString("content");
                                    JSONObject image = jsonObject.getJSONObject("image");
                                    String url = image.getString("url");
//                                    String createdDate = jsonObject.getString("createdAt");


                                    // Use StringBuilder for tags
                                    StringBuilder tags = new StringBuilder();
                                    JSONArray tagsArray = jsonObject.getJSONArray("tags");
                                    for (int j = 0; j < tagsArray.length(); j++) {
                                        String singleTag = tagsArray.getString(j);
                                        tags.append(singleTag).append(", ");
                                    }
                                    // Remove trailing comma and space if any
                                    if (tags.length() > 0) {
                                        tags.setLength(tags.length() - 2); // Adjust to remove the last comma and space
                                    }

                                    CurrentAffairsModel currentAffairModel = new CurrentAffairsModel(affairID,url, title, keyword, content, tags.toString(),totalRows, totalPages,currentPage);
                                    currentAffairsModelArrayList.add(currentAffairModel);
                                }
                                // If you have already created the adapter, just notify the change
                                currentAffairAdapter = new CurrentAffairsShowingAllAdapter(currentAffairsModelArrayList, null,CurrentAffairsActivity.this);
                                currentAffairRecycler.setAdapter(currentAffairAdapter);
                            } else {
                                // Handle the case where status is false
                                String message = response.getString("message");
                                Toast.makeText(CurrentAffairsActivity.this, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CurrentAffairsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("CurrentAffairs", errorMessage);
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
        // Use the MySingleton instance to add the request to the queue
        MySingleton.getInstance(CurrentAffairsActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}