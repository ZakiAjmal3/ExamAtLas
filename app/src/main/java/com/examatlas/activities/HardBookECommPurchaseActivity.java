package com.examatlas.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.examatlas.adapter.HardBookECommPurchaseAdapter;
import com.examatlas.models.HardBookECommPurchaseModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HardBookECommPurchaseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageSlider slider;
    private RecyclerView booksRecyclerView;
    private HardBookECommPurchaseAdapter hardBookECommPurchaseAdapter;
    private ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private String token;
    private RelativeLayout noDataLayout;
    private SearchView searchView;

    private final String ebookURL = Constant.BASE_URL + "book/getAllBooks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_book_ecomm_purchase);

        initializeViews();
        setupToolbar();
        setupImageSlider();
        setupRecyclerView();
        setupSearchView();
        getAllBooks();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.hardbook_ecomm_purchase_toolbar);
        slider = findViewById(R.id.slider);
        progressBar = findViewById(R.id.progressBar);
        booksRecyclerView = findViewById(R.id.booksRecycler);
        noDataLayout = findViewById(R.id.noDataLayout);
        searchView = findViewById(R.id.search_icon);
        hardBookECommPurchaseModelArrayList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
    }

    private void setupImageSlider() {
        ArrayList<SlideModel> sliderArrayList = new ArrayList<>();
        sliderArrayList.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));
        slider.setImageList(sliderArrayList);
    }

    private void setupRecyclerView() {
        booksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSearchView() {
        searchView.setOnClickListener(view -> openKeyboard());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (hardBookECommPurchaseAdapter != null) {
                    hardBookECommPurchaseAdapter.filter(newText);
                }
                return true;
            }
        });

        // Set touch listener for hiding keyboard
        findViewById(R.id.main).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (searchView.isShown() && !isPointInsideView(event.getRawX(), event.getRawY(), searchView)) {
                    searchView.setIconified(true);
                    hideKeyboard();
                }
            }
            return false;
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return (x >= location[0] && x <= (location[0] + view.getWidth()) &&
                y >= location[1] && y <= (location[1] + view.getHeight()));
    }

    private void openKeyboard() {
        searchView.setIconified(false);
        searchView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void getAllBooks() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ebookURL, null,
                this::handleResponse, this::handleError) {
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

    private void handleResponse(JSONObject response) {
        try {
            booksRecyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            boolean status = response.getBoolean("status");

            if (status) {
                JSONArray jsonArray = response.getJSONArray("books");
                hardBookECommPurchaseModelArrayList.clear();
                parseBooks(jsonArray);

                if (hardBookECommPurchaseModelArrayList.isEmpty()) {
                    noDataLayout.setVisibility(View.VISIBLE);
                } else {
                    if (hardBookECommPurchaseAdapter == null) {
                        hardBookECommPurchaseAdapter = new HardBookECommPurchaseAdapter(this, hardBookECommPurchaseModelArrayList);
                        booksRecyclerView.setAdapter(hardBookECommPurchaseAdapter);
                    } else {
                        hardBookECommPurchaseAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
        }
    }

    private void parseBooks(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            HardBookECommPurchaseModel model = new HardBookECommPurchaseModel(
                    jsonObject2.getString("_id"),
                    jsonObject2.getString("title"),
                    jsonObject2.getString("keyword"),
                    jsonObject2.getString("content"),
                    jsonObject2.getString("price"),
                    jsonObject2.getString("sellPrice"),
                    parseTags(jsonObject2.getJSONArray("tags")),
                    jsonObject2.getString("author"),
                    jsonObject2.getString("category"),
                    jsonObject2.getString("createdAt"),
                    jsonObject2.getString("updatedAt")
            );
            hardBookECommPurchaseModelArrayList.add(model);
        }
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

    private void handleError(VolleyError error) {
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
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e("BlogFetchError", errorMessage);
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
