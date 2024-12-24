package com.examatlas.activities.Books;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.HardBookECommPurchaseActivity;
import com.examatlas.adapter.HardBookECommPurchaseAdapter;
import com.examatlas.adapter.books.BookForUserAdapter;
import com.examatlas.adapter.books.CategoryAdapter;
import com.examatlas.adapter.books.FilteringCategoryAdapter;
import com.examatlas.models.Books.CategoryModel;
import com.examatlas.models.HardBookECommPurchaseModel;
import com.examatlas.models.extraModels.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilteringBookWithCategoryActivity extends AppCompatActivity {
    RecyclerView categoryRecycler, booksRecycler;
    ArrayList<CategoryModel> categoryArrayList = new ArrayList<>();
    private HardBookECommPurchaseAdapter hardBookECommPurchaseAdapter;
    private ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList;
    private final String bookURL = Constant.BASE_URL + "book/getAllBooks";
    private int currentPage = 1;
    private int totalPages = 1;
    private final int itemsPerPage = 10;
    private SessionManager sessionManager;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering_book_with_category);

        categoryRecycler = findViewById(R.id.categoryRecycler);
        booksRecycler = findViewById(R.id.booksRecycler);

        booksRecycler.setLayoutManager(new GridLayoutManager(this,2));
        hardBookECommPurchaseModelArrayList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");

        categoryRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        categoryArrayList.add(new CategoryModel(null,"Science"));
        categoryArrayList.add(new CategoryModel(null,"Maths"));
        categoryArrayList.add(new CategoryModel(null,"Computer"));
        categoryArrayList.add(new CategoryModel(null,"Hindi"));
        categoryArrayList.add(new CategoryModel(null,"History"));
        categoryArrayList.add(new CategoryModel(null,"Geography"));
        categoryArrayList.add(new CategoryModel(null,"Science"));
        categoryArrayList.add(new CategoryModel(null,"Maths"));
        categoryArrayList.add(new CategoryModel(null,"Computer"));
        categoryArrayList.add(new CategoryModel(null,"Hindi"));
        categoryArrayList.add(new CategoryModel(null,"History"));
        categoryArrayList.add(new CategoryModel(null,"Geography"));
        categoryArrayList.add(new CategoryModel(null,"Science"));
        categoryArrayList.add(new CategoryModel(null,"Maths"));
        categoryArrayList.add(new CategoryModel(null,"Computer"));
        categoryArrayList.add(new CategoryModel(null,"Hindi"));
        categoryArrayList.add(new CategoryModel(null,"History"));
        categoryArrayList.add(new CategoryModel(null,"Geography"));
        categoryArrayList.add(new CategoryModel(null,"Science"));
        categoryArrayList.add(new CategoryModel(null,"Maths"));
        categoryArrayList.add(new CategoryModel(null,"Computer"));
        categoryArrayList.add(new CategoryModel(null,"Hindi"));
        categoryArrayList.add(new CategoryModel(null,"History"));
        categoryArrayList.add(new CategoryModel(null,"Geography"));
        categoryArrayList.add(new CategoryModel(null,"Science"));
        categoryArrayList.add(new CategoryModel(null,"Maths"));
        categoryArrayList.add(new CategoryModel(null,"Computer"));
        categoryArrayList.add(new CategoryModel(null,"Hindi"));
        categoryArrayList.add(new CategoryModel(null,"History"));
        categoryArrayList.add(new CategoryModel(null,"Geography"));
        categoryArrayList.add(new CategoryModel(null,"Science"));
        categoryArrayList.add(new CategoryModel(null,"Maths"));
        categoryArrayList.add(new CategoryModel(null,"Computer"));
        categoryArrayList.add(new CategoryModel(null,"Hindi"));
        categoryArrayList.add(new CategoryModel(null,"History"));
        categoryArrayList.add(new CategoryModel(null,"Geography"));
        categoryRecycler.setAdapter(new FilteringCategoryAdapter(categoryArrayList, FilteringBookWithCategoryActivity.this));

        getAllBooks();
    }
    private void getAllBooks() {
        String paginatedURL = bookURL + "?type=book&page=" + currentPage + "&per_page=" + itemsPerPage;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            booksRecycler.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("books");
                                hardBookECommPurchaseModelArrayList.clear();

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();
                                    JSONArray jsonArray1 = jsonObject2.getJSONArray("images");

                                    JSONObject jsonObject = response.getJSONObject("pagination");

                                    int totalRows = Integer.parseInt(jsonObject.getString("totalRows"));
                                    totalPages = Integer.parseInt(jsonObject.getString("totalPages"));
                                    currentPage = Integer.parseInt(jsonObject.getString("currentPage"));
                                    int pageSize = Integer.parseInt(jsonObject.getString("pageSize"));

                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                                        BookImageModels bookImageModels = new BookImageModels(
                                                jsonObject3.getString("url"),
                                                jsonObject3.getString("filename")
                                        );
                                        bookImageArrayList.add(bookImageModels);
                                    }
                                    HardBookECommPurchaseModel model = new HardBookECommPurchaseModel(
                                            jsonObject2.getString("_id"),
                                            jsonObject2.getString("type"),
                                            jsonObject2.getString("title"),
                                            jsonObject2.getString("keyword"),
                                            jsonObject2.getString("stock"),
                                            jsonObject2.getString("price"),
                                            jsonObject2.getString("sellPrice"),
                                            jsonObject2.getString("content"),
                                            jsonObject2.getString("author"),
                                            jsonObject2.getString("categoryId"),
                                            jsonObject2.getString("subCategoryId"),
                                            jsonObject2.getString("subjectId"),
                                            parseTags(jsonObject2.getJSONArray("tags")), // Ensure this method is implemented correctly
                                            jsonObject2.getString("bookUrl"),
                                            bookImageArrayList,
                                            jsonObject2.getString("createdAt"),
                                            jsonObject2.getString("updatedAt"),
                                            jsonObject2.getString("isInCart"),
                                            jsonObject2.getString("isInWishList"),
                                            totalRows,totalPages,currentPage,pageSize
                                    );
                                    hardBookECommPurchaseModelArrayList.add(model);
                                }
                                hardBookECommPurchaseAdapter = new HardBookECommPurchaseAdapter(FilteringBookWithCategoryActivity.this, hardBookECommPurchaseModelArrayList);
                                booksRecycler.setAdapter(new BookForUserAdapter(FilteringBookWithCategoryActivity.this,hardBookECommPurchaseModelArrayList));
                            } else {
                                Toast.makeText(FilteringBookWithCategoryActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(FilteringBookWithCategoryActivity.this, message, Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    private String parseTags(JSONArray tagsArray) throws JSONException {
        StringBuilder tags = new StringBuilder();
        for (int j = 0; j < tagsArray.length(); j++) {
            tags.append(tagsArray.getString(j)).append(", ");
        }
        if (tags.length() > 0) {
            tags.setLength(tags.length() - 2); // Remove trailing comma and space
        }
        return tags.toString();
    }
}