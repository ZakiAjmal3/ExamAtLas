package com.examatlas.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.examatlas.activities.Books.EBooks.EBookSeachingActivity;
import com.examatlas.activities.Books.SearchingBooksActivity;
import com.examatlas.activities.CurrentAffairsActivity;
import com.examatlas.activities.DashboardActivity;
import com.examatlas.adapter.BlogAdapter;
import com.examatlas.adapter.CurrentAffairsAdapter;
import com.examatlas.adapter.LiveCoursesAdapter;
import com.examatlas.adapter.books.AllBookShowingAdapter;
import com.examatlas.models.BlogModel;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.WishListModel;
import com.examatlas.models.CurrentAffairsModel;
import com.examatlas.models.LiveCoursesModel;
import com.examatlas.models.Books.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.examatlas.utils.MySingletonFragment;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {
    RecyclerView liveClassesRecycler, booksRecycler,e_booksRecycler, blogsRecycler, currentAffairRecycler;
    ShimmerFrameLayout booksShimmeringLayout,e_booksShimmeringLayout,blogShimmeringLayout,currentAffairShimmeringLayout;
    TextView viewAllBlog,viewAllBook,viewAllEBook,viewAllCA;
    ProgressBar homeProgress;
    SessionManager sessionManager;
    ImageSlider slider;
    ArrayList<BlogModel> blogModelArrayList;
    ArrayList<CurrentAffairsModel> currentAffairsModelArrayList;
    ArrayList<LiveCoursesModel> liveCoursesModelArrayList;
    private ArrayList<AllBooksModel> allBooksModelArrayList;
    private ArrayList<AllBooksModel> all_E_BooksModelArrayList;
    BlogAdapter blogAdapter;
    LiveCoursesAdapter liveCoursesAdapter;
    CurrentAffairsAdapter currentAffairAdapter;
    private final String bookURL = Constant.BASE_URL + "v1/books?type=book";
    private final String blogURL = Constant.BASE_URL + "v1/blog?type=blog";
    private final String liveClassURL = Constant.BASE_URL + "liveclass/getAllLiveClass";
    private final String currentAffairsURL = Constant.BASE_URL + "v1/blog?type=current_affairs";
    String token;
    RelativeLayout blogTxtRL,currentAffairsTxtRL;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.seed));

        liveClassesRecycler = view.findViewById(R.id.examRecycler);
        currentAffairRecycler = view.findViewById(R.id.currentAffairRecycler);
        blogsRecycler = view.findViewById(R.id.blogsRecycler);
        booksRecycler = view.findViewById(R.id.booksRecycler);
        e_booksRecycler = view.findViewById(R.id.ebooksRecycler);
        homeProgress = view.findViewById(R.id.homeProgress);

        booksShimmeringLayout = view.findViewById(R.id.shimmer_Book_container);
        e_booksShimmeringLayout = view.findViewById(R.id.shimmer_EBook_container);
        blogShimmeringLayout = view.findViewById(R.id.shimmer_blog_container);
        currentAffairShimmeringLayout = view.findViewById(R.id.shimmer_CA_container);

        viewAllBlog = view.findViewById(R.id.viewAllBlogTxt);
        viewAllEBook = view.findViewById(R.id.viewAllEBooksTxt);
        viewAllBook = view.findViewById(R.id.viewAllBooksTxt);
        viewAllCA = view.findViewById(R.id.viewAllCATxt);

        blogTxtRL = view.findViewById(R.id.blogTxtRL);
        currentAffairsTxtRL = view.findViewById(R.id.currentAffairsTxtRL);

        slider = view.findViewById(R.id.slider);
        ArrayList<SlideModel> sliderArrayList = new ArrayList<>();
        allBooksModelArrayList = new ArrayList<>();
        all_E_BooksModelArrayList = new ArrayList<>();
        blogModelArrayList = new ArrayList<>();
        liveCoursesModelArrayList = new ArrayList<>();
        currentAffairsModelArrayList = new ArrayList<>();

        sessionManager = new SessionManager(getContext());
        token = sessionManager.getUserData().get("authToken");

        sliderArrayList.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));

        slider.setImageList(sliderArrayList);

        liveClassesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        booksRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        e_booksRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        blogsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        currentAffairRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));liveClassesRecycler.setVisibility(View.GONE);
        blogsRecycler.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //        getLiveClasses();
                getAllBooks();
                getBlogList();
                getCurrentAffairs();
                getAllEBook();
            }
        },500);

        viewAllBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the activity hosting the fragment is an instance of DashboardActivity
                if (getActivity() instanceof DashboardActivity) {
                    DashboardActivity activity = (DashboardActivity) getActivity();

                    // Check if the current fragment is an instance of HomeFragment
                    Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.container);

                    if (currentFragment instanceof HomeFragment) {
                        // Begin fragment transaction to replace HomeFragment with BlogFragment
                        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, new BlogFragment());
                        transaction.addToBackStack(null); // Optional, adds fragment to back stack
                        transaction.commit();

                        // Update the bottom navigation to select the 'Blogs' item
                        activity.bottom_navigation.setSelectedItemId(R.id.blogs);
                    }
                }
            }
        });

        viewAllCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CurrentAffairsActivity.class));
            }
        });
        viewAllBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchingBooksActivity.class));
            }
        });
        viewAllEBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EBookSeachingActivity.class));
            }
        });
        return view;
    }

    private void getLiveClasses() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, liveClassURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            liveClassesRecycler.setVisibility(View.VISIBLE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("classes");
                                liveCoursesModelArrayList.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String classID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String subTitle = jsonObject2.getString("subTitle");
                                    String description = jsonObject2.getString("description");
                                    String teacher = jsonObject2.getString("teacher");
                                    String language = jsonObject2.getString("language");
                                    String price = jsonObject2.getString("price");
                                    String categoryId = jsonObject2.getString("categoryId");
                                    String subCategoryId = jsonObject2.getString("subCategoryId");
                                    String subjectId = jsonObject2.getString("subjectId");
                                    String courseContent = jsonObject2.getString("courseContent");
                                    String isActive = jsonObject2.getString("is_active");
                                    String finalPrice = jsonObject2.getString("finalPrice");

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
                                    JSONArray jsonImageArray = jsonObject2.getJSONArray("images");

                                    ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();

                                    for (int j = 0; j < jsonImageArray.length(); j++) {
                                        JSONObject jsonImageObject = jsonImageArray.getJSONObject(j);
                                        BookImageModels bookImageModels = new BookImageModels(
                                                jsonImageObject.getString("url"),
                                                jsonImageObject.getString("filename"));
                                        bookImageArrayList.add(bookImageModels);
                                    }

                                    String startDate = "", endDate = "";
                                    if (jsonObject2.has("startDate")) {
                                        startDate = jsonObject2.getString("startDate");
                                        endDate = jsonObject2.getString("endDate");
                                    }

                                    JSONArray jsonStudentArray = jsonObject2.getJSONArray("students");
                                    ArrayList<BookImageModels> studentsArrayList = new ArrayList<>();
//                                    for (int j = 0; j<jsonImageArray.length();j++){
//                                    }
                                    JSONArray jsonLiveClasses = jsonObject2.getJSONArray("liveClasses");
                                    ArrayList<BookImageModels> liveClassesArrayList = new ArrayList<>();
//                                    for (int j = 0; j<jsonImageArray.length();j++){
//                                    }
                                    LiveCoursesModel liveCoursesModel = new LiveCoursesModel(classID, title,subTitle, description,language,price,teacher,tags.toString(), categoryId, subCategoryId, subjectId,courseContent,isActive,finalPrice, startDate, endDate,bookImageArrayList,null, null, null);

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
    }
    private void getAllEBook() {
        String url = Constant.BASE_URL + "v1/books?type=ebook";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                all_E_BooksModelArrayList.clear();

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    // Convert the book object into a Map to make it dynamic
                                    Map<String, Object> bookData = new Gson().fromJson(jsonObject2.toString(), Map.class);
                                    AllBooksModel model = new AllBooksModel(bookData); // Pass the map to the model

                                    all_E_BooksModelArrayList.add(model);
                                }
                                if (all_E_BooksModelArrayList.isEmpty()){
                                    e_booksRecycler.setVisibility(View.GONE);
                                    e_booksShimmeringLayout.stopShimmer();
                                    e_booksShimmeringLayout.setVisibility(View.GONE);
                                }else {
                                    ArrayList<WishListModel> wishListModelArrayList = new ArrayList<>(sessionManager.getWishListBookIdArrayList());
                                    ArrayList<Boolean> heartToggleStates = new ArrayList<>(Collections.nCopies(allBooksModelArrayList.size(), false));
                                    if (!wishListModelArrayList.isEmpty()) {
                                        for (int i = 0; i < all_E_BooksModelArrayList.size(); i++) {
                                            for (int j = 0; j < wishListModelArrayList.size(); j++) {
                                                if (all_E_BooksModelArrayList.get(i).getString("_id").equals(wishListModelArrayList.get(j).getProductId())) {
                                                    heartToggleStates.set(i, true);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    e_booksRecycler.setAdapter(new AllBookShowingAdapter(HomeFragment.this.getContext(), all_E_BooksModelArrayList));
                                    e_booksShimmeringLayout.stopShimmer();
                                    e_booksShimmeringLayout.setVisibility(View.GONE);
                                    e_booksRecycler.setVisibility(View.VISIBLE);
                                }
                            } else {
//                                Toast.makeText(ge, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
//                                Toast.makeText(EBookHomePageActivity.this, message, Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    private void getAllBooks() {
        String paginatedURL = bookURL;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                allBooksModelArrayList.clear();

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    // Convert the book object into a Map to make it dynamic
                                    Map<String, Object> bookData = new Gson().fromJson(jsonObject2.toString(), Map.class);
                                    AllBooksModel model = new AllBooksModel(bookData); // Pass the map to the model

                                    allBooksModelArrayList.add(model);
                                }

                                // Update UI and adapters
                                booksRecycler.setAdapter(new AllBookShowingAdapter(HomeFragment.this.getContext(), allBooksModelArrayList));
                                booksShimmeringLayout.stopShimmer();
                                booksShimmeringLayout.setVisibility(View.GONE);
                                booksRecycler.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        MySingletonFragment.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    private void getBlogList() {
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, blogURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            blogShimmeringLayout.stopShimmer();
                            blogShimmeringLayout.setVisibility(View.GONE);
                            blogsRecycler.setVisibility(View.VISIBLE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                blogModelArrayList.clear(); // Clear the list before adding new items

                                String totalItems = response.getString("totalItems");
                                String totalPages = response.getString("totalPage");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String blogID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String content = jsonObject2.getString("content");
                                    Log.e("Blog content",content);
                                    JSONObject image = jsonObject2.getJSONObject("image");
                                    String url = image.getString("url");

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

                                    BlogModel blogModel = new BlogModel(blogID,url, title, content, tags.toString(),totalItems,totalPages);
                                    blogModelArrayList.add(blogModel);
                                }
                                if (!blogModelArrayList.isEmpty()) {
                                    // If you have already created the adapter, just notify the change
                                    if (blogAdapter == null) {
                                        blogAdapter = new BlogAdapter(blogModelArrayList, HomeFragment.this);
                                        blogsRecycler.setAdapter(blogAdapter);
                                    } else {
                                        blogAdapter.notifyDataSetChanged();
                                    }
                                }else {
                                    blogsRecycler.setVisibility(View.GONE);
                                    blogShimmeringLayout.setVisibility(View.GONE);
                                    blogShimmeringLayout.stopShimmer();
                                    blogTxtRL.setVisibility(View.GONE);
                                }
                            } else {
                                // Handle the case where status is false
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
    }


    private void getCurrentAffairs() {
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, currentAffairsURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currentAffairShimmeringLayout.stopShimmer();
                            currentAffairShimmeringLayout.setVisibility(View.GONE);
                            currentAffairRecycler.setVisibility(View.VISIBLE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                currentAffairsModelArrayList.clear(); // Clear the list before adding new items

                                String totalItems = response.getString("totalItems");
                                String totalPages = response.getString("totalPage");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String affairID = jsonObject.getString("_id");
                                    String title = jsonObject.getString("title");
                                    String categoryId = jsonObject.getString("categoryId");
                                    String content = jsonObject.getString("content");
                                    JSONObject image = jsonObject.getJSONObject("image");
                                    String url = image.getString("url");


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

                                    CurrentAffairsModel currentAffairModel = new CurrentAffairsModel(affairID,url, title, categoryId, content, tags.toString(),totalItems, totalPages);
                                    currentAffairsModelArrayList.add(currentAffairModel);
                                }
                                if (!currentAffairsModelArrayList.isEmpty()) {
                                    // If you have already created the adapter, just notify the change
                                    if (currentAffairAdapter == null) {
                                        currentAffairAdapter = new CurrentAffairsAdapter(currentAffairsModelArrayList, HomeFragment.this, null);
                                        currentAffairRecycler.setAdapter(currentAffairAdapter);
                                    } else {
                                        currentAffairAdapter.notifyDataSetChanged();
                                    }
                                }else {
                                    currentAffairRecycler.setVisibility(View.GONE);
                                    currentAffairShimmeringLayout.setVisibility(View.GONE);
                                    currentAffairShimmeringLayout.stopShimmer();
                                    currentAffairsTxtRL.setVisibility(View.GONE);
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
