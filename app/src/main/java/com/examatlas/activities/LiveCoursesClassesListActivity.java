package com.examatlas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.examatlas.adapter.LiveCourseClassesListAdapter;
import com.examatlas.adapter.LiveCoursesAdapter;
import com.examatlas.fragment.LiveCoursesFragment;
import com.examatlas.models.LiveCoursesClassesListModel;
import com.examatlas.models.LiveCoursesModel;
import com.examatlas.models.extraModels.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.MySingletonFragment;
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
    LiveCoursesClassesListModel liveCoursesClassesListModel;
    ArrayList<LiveCoursesClassesListModel> liveCoursesClassesListModelArrayList;
    String getLiveClassesListURL;
    SessionManager sessionManager;
    String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_courses_classes_list);

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");
        Intent intent = getIntent();
        String courseId = intent.getStringExtra("course_id");

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

        getLiveClassesListURL  = Constant.BASE_URL + "liveclass/getAllScheduledCourseByCourseId/" + courseId;
        getLiveClassesList();
    }

    private void getLiveClassesList() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getLiveClassesListURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API Response", response.toString()); // Log the entire response
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
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
                                    String startedAt = jsonObject.getString("startedAt");
                                    liveCoursesClassesListModel = new LiveCoursesClassesListModel(classID, courseID, title, meetingID, time, date, addedBy, scheduledTime, classStatus, startedAt, null);
                                    liveCoursesClassesListModelArrayList.add(liveCoursesClassesListModel);
                                }
                                if (liveCoursesClassesListModelArrayList != null) {
                                    progressBar.setVisibility(View.GONE);
                                    classesListRecyclerView.setVisibility(View.VISIBLE);
                                    liveCourseClassesListAdapter = new LiveCourseClassesListAdapter(LiveCoursesClassesListActivity.this, liveCoursesClassesListModelArrayList);
                                    classesListRecyclerView.setAdapter(liveCourseClassesListAdapter);
                                    liveCourseClassesListAdapter.notifyDataSetChanged();
                                } else {
                                    classesListRecyclerView.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    noDataLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LiveCoursesClassesListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LiveCoursesClassesListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onResume() {
        super.onResume();
        getLiveClassesList();
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