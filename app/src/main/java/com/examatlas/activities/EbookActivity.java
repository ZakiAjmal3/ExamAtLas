package com.examatlas.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.SessionManager;

import java.util.ArrayList;

public class EbookActivity extends AppCompatActivity {
    ImageView toolbarBackBtn;
    RecyclerView ebookRecyclerview;
//    EbookAdapter ebookAdapter;
//    ArrayList<EbookModel> ebookModelArrayList;
    ArrayList<AllBooksModel> ebookModelArrayList;
    ProgressBar progressBar;
    private final String bookURL = Constant.BASE_URL + "book/getAllBooks";
    RelativeLayout noDataLayout;
    String token;
    SessionManager sessionManager;
    private int currentPage = 1;
    private int totalPages = 1;
    private final int itemsPerPage = 10; // Total number of pages, update this based on response

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook);

        toolbarBackBtn = findViewById(R.id.imgBack);
        ebookRecyclerview = findViewById(R.id.eBookRecycler);
        progressBar = findViewById(R.id.ebookProgress);
        noDataLayout = findViewById(R.id.noDataLayout);

        ebookModelArrayList = new ArrayList<>();

        ebookRecyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

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

//        getEbooks();

    }

//    private void getEbooks() {
//        String paginatedURL = bookURL + "?type=book&page=" + currentPage + "&per_page=" + itemsPerPage;
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            ebookRecyclerview.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.GONE);
//                            boolean status = response.getBoolean("status");
//
//                            if (status) {
//                                JSONArray jsonArray = response.getJSONArray("books");
//                                ebookModelArrayList.clear();
//
//                                // Parse books directly here
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
//                                    ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();
//                                    JSONArray jsonArray1 = jsonObject2.getJSONArray("images");
//
//                                    JSONObject jsonObject = response.getJSONObject("pagination");
//
//                                    int totalRows = Integer.parseInt(jsonObject.getString("totalRows"));
//                                    totalPages = Integer.parseInt(jsonObject.getString("totalPages"));
//                                    currentPage = Integer.parseInt(jsonObject.getString("currentPage"));
//                                    int pageSize = Integer.parseInt(jsonObject.getString("pageSize"));
//
//                                    for (int j = 0; j < jsonArray1.length(); j++) {
//                                        JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
//                                        BookImageModels bookImageModels = new BookImageModels(
//                                                jsonObject3.getString("url"),
//                                                jsonObject3.getString("filename")
//                                        );
//                                        bookImageArrayList.add(bookImageModels);
//                                    }
//                                    AllBooksModel model = new AllBooksModel(
//                                            jsonObject2.getString("_id"),
//                                            jsonObject2.getString("type"),
//                                            jsonObject2.getString("title"),
//                                            jsonObject2.getString("keyword"),
//                                            jsonObject2.getString("stock"),
//                                            jsonObject2.getString("price"),
//                                            jsonObject2.getString("sellPrice"),
//                                            jsonObject2.getString("content"),
//                                            jsonObject2.getString("author"),
//                                            jsonObject2.getString("categoryId"),
//                                            jsonObject2.getString("subCategoryId"),
//                                            jsonObject2.getString("subjectId"),
//                                            parseTags(jsonObject2.getJSONArray("tags")), // Ensure this method is implemented correctly
//                                            jsonObject2.getString("bookUrl"),
//                                            bookImageArrayList,
//                                            jsonObject2.getString("createdAt"),
//                                            jsonObject2.getString("updatedAt"),
//                                            jsonObject2.getString("isInCart"),
//                                            jsonObject2.getString("isInWishList"),
//                                            totalRows,totalPages,currentPage,pageSize
//                                    );
//                                    ebookModelArrayList.add(model);
//                                }
//                                updateUI();
//                                if (ebookModelArrayList.isEmpty()) {
//                                    noDataLayout.setVisibility(View.VISIBLE);
//                                    ebookRecyclerview.setVisibility(View.GONE);
//                                    progressBar.setVisibility(View.GONE);
//                                } else {
//                                    if (ebookAdapter == null) {
//                                        ebookAdapter = new HardBookECommPurchaseAdapter(EbookActivity.this, ebookModelArrayList);
//                                        ebookRecyclerview.setAdapter(ebookAdapter);
//                                    } else {
//                                        ebookAdapter.notifyDataSetChanged();
//                                    }
//                                }
//                            } else {
//                                Toast.makeText(EbookActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        String errorMessage = "Error: " + error.toString();
//                        if (error.networkResponse != null) {
//                            try {
//                                // Parse the error response
//                                String jsonError = new String(error.networkResponse.data);
//                                JSONObject jsonObject = new JSONObject(jsonError);
//                                String message = jsonObject.optString("message", "Unknown error");
//                                // Now you can use the message
//                                Toast.makeText(EbookActivity.this, message, Toast.LENGTH_LONG).show();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        Log.e("BlogFetchError", errorMessage);
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "Bearer " + token);
//                return headers;
//            }
//        };
//        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
//    }
//    private void updateUI() {
//        if (ebookModelArrayList.isEmpty()) {
//            noDataLayout.setVisibility(View.VISIBLE);
//            ebookRecyclerview.setVisibility(View.GONE);
//            progressBar.setVisibility(View.GONE);
//        } else {
//            noDataLayout.setVisibility(View.GONE);
//            ebookRecyclerview.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
//            if (ebookAdapter == null) {
//                ebookAdapter = new HardBookECommPurchaseAdapter(this, ebookModelArrayList);
//                ebookRecyclerview.setAdapter(ebookAdapter);
//            } else {
//                ebookAdapter.notifyDataSetChanged();
//            }
//        }
//    }
//    private String parseTags(JSONArray tagsArray) throws JSONException {
//        StringBuilder tags = new StringBuilder();
//        for (int j = 0; j < tagsArray.length(); j++) {
//            tags.append(tagsArray.getString(j)).append(", ");
//        }
//        if (tags.length() > 0) {
//            tags.setLength(tags.length() - 2); // Remove trailing comma and space
//        }
//        return tags.toString();
//    }
//    // Call this method to set the scroll listener on your RecyclerView
//    private void setupScrollListener() {
//        ebookRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == ebookModelArrayList.size() - 1) {
//                    // Load more items when reaching the end
//                    getEbooks();
//                }
//            }
//        });
//    }
}