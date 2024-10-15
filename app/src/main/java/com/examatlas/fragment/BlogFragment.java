package com.examatlas.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.BlogAdapter;
import com.examatlas.models.BlogModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlogFragment extends Fragment {

    RecyclerView blogRecycler;
    RelativeLayout noDataLayout;
    ProgressBar blogProgress;
    ArrayList<BlogModel> blogModelArrayList;
    BlogAdapter blogAdapter;
    private final String blogURL = Constant.BASE_URL + "blog/getAllBlogs";
//    private final String blogURL = Constant.BASE_URL2 + "course/getAllCourse";
    String token;
    SessionManager sessionManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blog, container, false);

        noDataLayout = view.findViewById(R.id.noDataLayout);
        blogProgress = view.findViewById(R.id.blogProgress);
        blogRecycler = view.findViewById(R.id.blogRecycler);
//        blogRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        blogRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        getBlogList();
        blogModelArrayList = new ArrayList<>();

        sessionManager = new SessionManager(getContext());

        token = sessionManager.getUserData().get("authToken");

        return view;
    }
    private void getBlogList() {
        blogProgress.setVisibility(View.VISIBLE);
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, blogURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            blogRecycler.setVisibility(View.VISIBLE);
                            blogProgress.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("blogs");
                                blogModelArrayList.clear(); // Clear the list before adding new items

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String blogID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String keyword = jsonObject2.getString("keyword");
                                    String content = jsonObject2.getString("content");
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

                                    BlogModel blogModel = new BlogModel(blogID, title, keyword, content, tags.toString(), createdDate);
                                    blogModelArrayList.add(blogModel);
                                }
                                // If you have already created the adapter, just notify the change
                                if (blogModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    blogProgress.setVisibility(View.GONE);
                                } else {
                                    if (blogAdapter == null) {
                                        blogAdapter = new BlogAdapter(blogModelArrayList, BlogFragment.this);
                                        blogRecycler.setAdapter(blogAdapter);
                                    } else {
                                        blogAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                // Handle the case where status is false
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
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
        MySingletonFragment.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}