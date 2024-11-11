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
import com.examatlas.adapter.LiveCoursesAdapter;
import com.examatlas.models.BlogModel;
import com.examatlas.models.CurrentAffairsModel;
import com.examatlas.models.LiveCoursesModel;
import com.examatlas.models.extraModels.BookImageModels;
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
    ArrayList<LiveCoursesModel> liveCoursesModelArrayList;
    BlogAdapter blogAdapter;
    LiveCoursesAdapter liveCoursesAdapter;
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
        liveCoursesModelArrayList = new ArrayList<>();
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
                        try {
                            liveClassesRecycler.setVisibility(View.VISIBLE);
                            liveClassesProgress.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("classes");
                                liveCoursesModelArrayList.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String classID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String description = jsonObject2.getString("description");
                                    String teacherName = jsonObject2.getString("teacher");

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

                                    String categoryId = jsonObject2.getString("categoryId");
                                    String subCategoryId = jsonObject2.getString("subCategoryId");
                                    String subjectId = jsonObject2.getString("subjectId");

                                    ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();

                                    // Check if "images" is a JSONArray or JSONObject
                                    if (jsonObject2.has("images")) {
                                        Object imagesObj = jsonObject2.get("images");
                                        if (imagesObj instanceof JSONArray) {
                                            // If images is a JSONArray
                                            JSONArray jsonImageArray = (JSONArray) imagesObj;
                                            for (int j = 0; j < jsonImageArray.length(); j++) {
                                                JSONObject jsonImageObject = jsonImageArray.getJSONObject(j);
                                                BookImageModels bookImageModels = new BookImageModels(
                                                        jsonImageObject.getString("url"),
                                                        jsonImageObject.getString("filename")
                                                );
                                                bookImageArrayList.add(bookImageModels);
                                            }
                                        } else if (imagesObj instanceof JSONObject) {
                                            // If images is a JSONObject
                                            JSONObject jsonImageObject = (JSONObject) imagesObj;
                                            BookImageModels bookImageModels = new BookImageModels(
                                                    jsonImageObject.getString("url"),
                                                    jsonImageObject.getString("filename")
                                            );
                                            bookImageArrayList.add(bookImageModels);
                                        }
                                    }

                                    String startDate = jsonObject2.getString("startDate");
                                    String endDate = jsonObject2.getString("endDate");

                                    JSONArray jsonStudentArray = jsonObject2.getJSONArray("students");
                                    ArrayList<BookImageModels> studentsArrayList = new ArrayList<>();
//                                    for (int j = 0; j<jsonImageArray.length();j++){
//                                    }
                                    JSONArray jsonLiveClasses = jsonObject2.getJSONArray("liveClasses");
                                    ArrayList<BookImageModels> liveClassesArrayList = new ArrayList<>();
//                                    for (int j = 0; j<jsonImageArray.length();j++){
//                                    }
                                    LiveCoursesModel liveCoursesModel = new LiveCoursesModel(classID, title, description, teacherName, tags.toString(),categoryId,subCategoryId,subjectId,startDate,endDate,bookImageArrayList,studentsArrayList,liveClassesArrayList);
                                    liveCoursesModelArrayList.add(liveCoursesModel);
                                }
                                if (liveCoursesAdapter == null) {
                                    liveCoursesAdapter = new LiveCoursesAdapter(liveCoursesModelArrayList, HomeFragment.this);
                                    liveClassesRecycler.setAdapter(liveCoursesAdapter);
                                } else {
                                    liveCoursesAdapter.notifyDataSetChanged();
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("Live Classes Error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("Live Classes Error", error.getMessage());
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
                                JSONArray jsonArray = response.getJSONArray("data");
                                blogModelArrayList.clear(); // Clear the list before adding new items

                                JSONObject jsonObject = response.getJSONObject("pagination");
                                String totalRows = jsonObject.getString("totalRows");
                                String totalPages = jsonObject.getString("totalPages");
                                String currentPage = jsonObject.getString("currentPage");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String blogID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String keyword = jsonObject2.getString("keyword");
                                    String content = jsonObject2.getString("content");

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

                                    BlogModel blogModel = new BlogModel(blogID, title, keyword, content, tags.toString(),totalRows,totalPages,currentPage);
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
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();}
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
//                                    String image = jsonObject.getString("image");
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

                                    CurrentAffairsModel currentAffairModel = new CurrentAffairsModel(affairID, title, keyword, content, tags.toString(),totalRows, totalPages,currentPage);
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
