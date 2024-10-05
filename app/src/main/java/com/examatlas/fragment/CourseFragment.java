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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.CourseAdapter;
import com.examatlas.models.CourseModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingletonFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseFragment extends Fragment {
    RecyclerView courseRecycler;
    RelativeLayout noDataLayout;
    ProgressBar courseProgress;
    ArrayList<CourseModel> courseModelArrayList;
    CourseAdapter courseAdapter;
    private final String courseURL = Constant.BASE_URL + "course/getAllCourse";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        courseRecycler = view.findViewById(R.id.courseRecycler);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        courseProgress = view.findViewById(R.id.courseProgress);
        courseModelArrayList = new ArrayList<>();

        courseRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        getCourseList();

        return view;
    }

    private void getCourseList() {
        courseProgress.setVisibility(View.VISIBLE);
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, courseURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            courseRecycler.setVisibility(View.VISIBLE);
                            courseProgress.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("course");
                                courseModelArrayList.clear(); // Clear the list before adding new items

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String courseID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String price = jsonObject2.getString("price");
                                    String image = jsonObject2.getString("image");
                                    String createdDate = jsonObject2.getString("createdAt");

                                    CourseModel courseModel = new CourseModel(courseID, title, price, image, createdDate);
                                    courseModelArrayList.add(courseModel);
                                }
                                // If you have already created the adapter, just notify the change
                                if (courseModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    courseProgress.setVisibility(View.GONE);
                                } else {
                                    if (courseAdapter == null) {
                                        courseAdapter = new CourseAdapter(CourseFragment.this, courseModelArrayList);
                                        courseRecycler.setAdapter(courseAdapter);
                                    } else {
                                        courseAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                // Handle the case where status is false
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (
                                JSONException e) {
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
                return headers;
            }
        };
        MySingletonFragment.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
