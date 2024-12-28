package com.examatlas.activities.Books;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.examatlas.adapter.books.SearchingActivityAdapter;
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

public class SearchingBooksActivity extends AppCompatActivity {
    ImageView backBtn;
    EditText searchEditText;
    RecyclerView allBooksRecyclerView;
    SearchingActivityAdapter hardBookECommPurchaseAdapter;
    ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList;
    SessionManager sessionManager;
    String token;
    LinearLayout englishLL,hindiLL,rs_100_200_LinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_books);

        getWindow().setStatusBarColor(ContextCompat.getColor(SearchingBooksActivity.this,R.color.md_theme_dark_surfaceTint));

        rs_100_200_LinearLayout = findViewById(R.id.RS_100_200_LL);
        englishLL = findViewById(R.id.englishLL);
        hindiLL = findViewById(R.id.hindiLL);

        Animation english = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left_anim_for_hindi_english_search);
        englishLL.startAnimation(english);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation hindi = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left_anim_for_hindi_english_search);
                hindiLL.startAnimation(hindi);
                hindiLL.setVisibility(View.VISIBLE);
            }
        },350);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation rs = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left_anim_for_hindi_english_search);
                rs_100_200_LinearLayout.startAnimation(rs);
                rs_100_200_LinearLayout.setVisibility(View.VISIBLE);

            }
        },700);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        searchEditText = findViewById(R.id.search_icon);
        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");
        allBooksRecyclerView = findViewById(R.id.allBookRecycler);
        allBooksRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        hardBookECommPurchaseModelArrayList = new ArrayList<>();

        openKeyboard();
        getAllBooks();
    }

    private void openKeyboard() {
//        searchView.setIconified(false);
        searchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    private void getAllBooks() {
        String bookURL = Constant.BASE_URL + "book/getAllBooks";
        String paginatedURL = bookURL + "?type=book&page=" + 1 + "&per_page=" + 10;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            allBooksRecyclerView.setVisibility(View.VISIBLE);
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
                                            0,0,0,0
                                    );
                                    hardBookECommPurchaseModelArrayList.add(model);
                                }
                                hardBookECommPurchaseAdapter = new SearchingActivityAdapter(SearchingBooksActivity.this, hardBookECommPurchaseModelArrayList);
                                allBooksRecyclerView.setAdapter(hardBookECommPurchaseAdapter);
                            } else {
                                Toast.makeText(SearchingBooksActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SearchingBooksActivity.this, message, Toast.LENGTH_LONG).show();
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