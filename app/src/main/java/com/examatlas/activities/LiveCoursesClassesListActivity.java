package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.LiveCourseClassesListAdapter;
import com.examatlas.models.LiveCoursesClassesListModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LiveCoursesClassesListActivity extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar progressBar;
    RelativeLayout noDataLayout;
    RecyclerView classesListRecyclerView;
    LiveCourseClassesListAdapter liveCourseClassesListAdapter;
    ArrayList<LiveCoursesClassesListModel> liveCoursesClassesListModelArrayList;
    String getLiveClassesListURL;
    SessionManager sessionManager;
    String authToken;
    String courseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_courses_classes_list);

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");
        Intent intent = getIntent();
        courseId = intent.getStringExtra("course_id");

        toolbar = findViewById(R.id.liveCoursesClassesListToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        progressBar = findViewById(R.id.liveCoursesClassesListProgressBar);
        noDataLayout = findViewById(R.id.noDataLayout);
        classesListRecyclerView = findViewById(R.id.liveCoursesClassesListRecyclerView);
        classesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        liveCoursesClassesListModelArrayList = new ArrayList<>();

        getLiveClassesListURL = Constant.BASE_URL + "liveclass/getAllScheduledCourseByCourseId/" + courseId;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        getLiveClassesList(); // Always refresh data when the activity is resumed
//    }

    private void getLiveClassesList() {
        progressBar.setVisibility(View.VISIBLE);
        noDataLayout.setVisibility(View.GONE);
        classesListRecyclerView.setVisibility(View.GONE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getLiveClassesListURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API Response", response.toString());
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                liveCoursesClassesListModelArrayList.clear();
                                JSONArray jsonArray = response.getJSONArray("courses");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String classID = jsonObject.getString("_id");
                                    String courseID = jsonObject.getString("courseId");
                                    String title = jsonObject.getString("title");
                                    String meetingID = jsonObject.getString("meetingId");
                                    String time = jsonObject.getString("time");
                                    String date = jsonObject.getString("date");
                                    String addedBy = jsonObject.getString("addedBy");
                                    String scheduledTime = jsonObject.getString("scheduleTime");
                                    String classStatus = jsonObject.getString("status");
                                    String startedAt,endedAt;
                                    if (classStatus.equalsIgnoreCase("completed")) {
                                        startedAt = jsonObject.getString("startedAt");
                                        endedAt = jsonObject.getString("endedAt");
                                    }else {
                                        startedAt = null;
                                        endedAt = null;
                                    }
                                    LiveCoursesClassesListModel liveCoursesClassesListModel = new LiveCoursesClassesListModel(classID, courseID, title, meetingID, time, date, addedBy, scheduledTime, classStatus, startedAt,endedAt, null);
                                    liveCoursesClassesListModelArrayList.add(liveCoursesClassesListModel);
                                }

                                if (!liveCoursesClassesListModelArrayList.isEmpty()) {
                                    progressBar.setVisibility(View.GONE);
                                    classesListRecyclerView.setVisibility(View.VISIBLE);
                                    liveCourseClassesListAdapter = new LiveCourseClassesListAdapter(LiveCoursesClassesListActivity.this, liveCoursesClassesListModelArrayList);
                                    classesListRecyclerView.setAdapter(liveCourseClassesListAdapter);
                                    liveCourseClassesListAdapter.notifyDataSetChanged();
                                } else {
                                    showNoDataLayout();
                                }
                            } else {
                                showNoDataLayout();
                            }
                        } catch (JSONException e) {
                            showErrorToast(String.valueOf(e));
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
                        Toast.makeText(LiveCoursesClassesListActivity.this, message, Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void showNoDataLayout() {
        classesListRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.VISIBLE);
    }

    private void showErrorToast(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show(); // Fallback message
        }
    }
    public String getCourseId() {
        return courseId;
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
