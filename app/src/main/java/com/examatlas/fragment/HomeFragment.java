package com.examatlas.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.examatlas.R;
import com.examatlas.adapter.BlogAdapter;
import com.examatlas.adapter.CurrentAffairsAdapter;
import com.examatlas.adapter.LiveClassesAdapter;
import com.examatlas.models.BlogModel;
import com.examatlas.models.CurrentAffairsModel;
import com.examatlas.models.LiveClassesModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.examatlas.utils.MySingletonFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {
    RecyclerView liveClassesRecycler, testimonialRecycler, blogsRecycler, currentAffairRecycler;
    ProgressBar homeProgress,blogProgressBar,currentAffairProgress,liveClassesProgress;
    SessionManager sessionManager;
    ImageSlider slider;
    ArrayList<BlogModel> blogModelArrayList;
    ArrayList<CurrentAffairsModel> currentAffairsModelArrayList;
    ArrayList<LiveClassesModel> liveClassesModelArrayList;
    BlogAdapter blogAdapter;
    LiveClassesAdapter liveClassesAdapter;
    CurrentAffairsAdapter currentAffairAdapter;
    private final String blogURL = Constant.BASE_URL + "blog/getAllBlogs";
    private final String liveClassURL = Constant.BASE_URL + "liveclass/getAllLiveClass";
    private final String currentAffairsURL = Constant.BASE_URL + "currentAffair/getAllCA";
    String token;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        liveClassesRecycler = view.findViewById(R.id.examRecycler);
//        testimonialRecycler = view.findViewById(R.id.testimonialRecycler);
        currentAffairRecycler = view.findViewById(R.id.currentAffairRecycler);
        blogsRecycler = view.findViewById(R.id.blogsRecycler);
        homeProgress = view.findViewById(R.id.homeProgress);
        slider = view.findViewById(R.id.slider);
        blogProgressBar = view.findViewById(R.id.blogProgress);
        currentAffairProgress = view.findViewById(R.id.currentAffairProgress);
        liveClassesProgress = view.findViewById(R.id.liveClassesProgress);
        ArrayList<SlideModel> sliderArrayList = new ArrayList<>();
        blogModelArrayList = new ArrayList<>();
        liveClassesModelArrayList = new ArrayList<>();
        currentAffairsModelArrayList = new ArrayList<>();
        sessionManager = new SessionManager(getContext());

        sliderArrayList.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));

        slider.setImageList(sliderArrayList);

        liveClassesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        blogsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        testimonialRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        currentAffairRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        getSliderImage();
        liveClassesRecycler.setVisibility(View.GONE);
        liveClassesProgress.setVisibility(View.VISIBLE);
        blogsRecycler.setVisibility(View.GONE);
        blogProgressBar.setVisibility(View.VISIBLE);
        currentAffairRecycler.setVisibility(View.GONE);
        currentAffairProgress.setVisibility(View.VISIBLE);
        getLiveClasses();

//        txtSeeAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                ((DashboardActivity) getActivity()).loadCourse();
//            }
//        });
        token = sessionManager.getUserData().get("authToken");

        return view;
    }

    public void loadFragment(Fragment fragment) {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

//    private void getSliderImage() {
//        homeProgress.setVisibility(View.VISIBLE);
//        ServiceApi api = ApiClient.getClient().create(ServiceApi.class);
//        Call<BannerRequest> call = api.getBannerImages();
//        call.enqueue(new Callback<BannerRequest>() {
//            @Override
//            public void onResponse(Call<BannerRequest> call, Response<BannerRequest> response) {
//                homeProgress.setVisibility(View.GONE);
//                if (response.body() != null) {
//                    if (response.body().getStatus()) {
//                        bannerList.addAll(response.body().getBannerimages());
//                        SliderAdapter adapter = new SliderAdapter(HomeFragment.this, bannerList);
//                        slider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
//                        slider.setSliderAdapter(adapter);
//                        slider.setScrollTimeInSec(3);
//                        slider.setAutoCycle(true);
//                        slider.startAutoCycle();
//                    }
//                } else {
//                    Log.e("BODY", "Body is null");
//                }
//                getExamsList();
//            }
//
//            @Override
//            public void onFailure(Call<BannerRequest> call, Throwable t) {
//                Log.e("EXCEPTION", t.getLocalizedMessage());
//                homeProgress.setVisibility(View.GONE);
//                getExamsList();
//            }
//        });
//    }

    private void getLiveClasses() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, liveClassURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LiveClassesResponse", response.toString());
                        try {
                            liveClassesRecycler.setVisibility(View.VISIBLE);
                            liveClassesProgress.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("classes");
                                liveClassesModelArrayList.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String classID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String meetingID = "ax7m-hd4z-0zcs";
                                    String description = jsonObject2.getString("description");
                                    String teacherName = jsonObject2.getString("teacher");

                                    // Handle keyword extraction
                                    String keyWord;
                                    if (jsonObject2.has("keyword") && !jsonObject2.isNull("keyword")) {
                                        keyWord = jsonObject2.getString("keyword");
                                    } else {
                                        keyWord = "No Value"; // Default value
                                    }

                                    // Use StringBuilder for tags
                                    StringBuilder tags = new StringBuilder();
                                    JSONArray jsonArray1 = jsonObject2.getJSONArray("tags");
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        String singleTag = jsonArray1.getString(j);
                                        tags.append(singleTag).append(", ");
                                    }
                                    if (tags.length() > 0) {
                                        tags.setLength(tags.length() - 2);
                                    }

                                    LiveClassesModel liveClassesModel = new LiveClassesModel(classID, title, meetingID, description, teacherName, keyWord, tags.toString());
                                    liveClassesModelArrayList.add(liveClassesModel);
                                }

                                Log.d("LiveClassesListSize", "Size: " + liveClassesModelArrayList.size());

                                if (liveClassesAdapter == null) {
                                    liveClassesAdapter = new LiveClassesAdapter(liveClassesModelArrayList, HomeFragment.this);
                                    liveClassesRecycler.setAdapter(liveClassesAdapter);
                                } else {
                                    liveClassesAdapter.notifyDataSetChanged();
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
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
        getBlogList();
    }

    private void getBlogList() {
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, blogURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            blogsRecycler.setVisibility(View.VISIBLE);
                            blogProgressBar.setVisibility(View.GONE);
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
                                if (blogAdapter == null) {
                                    blogAdapter = new BlogAdapter(blogModelArrayList, HomeFragment.this);
                                    blogsRecycler.setAdapter(blogAdapter);
                                } else {
                                    blogAdapter.notifyDataSetChanged();
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
//                String errorMessage = "Error: " + error.toString();
//                if (error.networkResponse != null) {
//                    try {
//                        String responseData = new String(error.networkResponse.data, "UTF-8");
//                        errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
//                        errorMessage += "\nResponse Data: " + responseData;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();}
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
        getCurrentAffairs();
    }


    private void getCurrentAffairs() {
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, currentAffairsURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currentAffairRecycler.setVisibility(View.VISIBLE);
                            currentAffairProgress.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("currentAffairs");
                                currentAffairsModelArrayList.clear(); // Clear the list before adding new items

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String affairID = jsonObject.getString("_id");
                                    String title = jsonObject.getString("title");
                                    String keyword = jsonObject.getString("keyword");
                                    String content = jsonObject.getString("content");
                                    String image = jsonObject.getString("image");
                                    String createdDate = jsonObject.getString("createdAt");


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

                                    CurrentAffairsModel currentAffairModel = new CurrentAffairsModel(affairID, title, keyword, content, tags.toString(),image, createdDate);
                                    currentAffairsModelArrayList.add(currentAffairModel);
                                }

                                // If you have already created the adapter, just notify the change
                                if (currentAffairAdapter == null) {
                                    currentAffairAdapter = new CurrentAffairsAdapter(currentAffairsModelArrayList, HomeFragment.this);
                                    currentAffairRecycler.setAdapter(currentAffairAdapter);
                                } else {
                                    currentAffairAdapter.notifyDataSetChanged();
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
//                Log.e("CurrentAffairsFetchError", errorMessage);
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

        // Use the MySingleton instance to add the request to the queue
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
