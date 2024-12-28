package com.examatlas.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.examatlas.R;
import com.examatlas.activities.Books.SearchingBooksActivity;
import com.examatlas.adapter.HardBookECommPurchaseAdapter;
import com.examatlas.adapter.books.BookForUserAdapter;
import com.examatlas.adapter.books.CategoryAdapter;
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

public class HardBookECommPurchaseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    ImageView cartBtn,logo;
    private ImageSlider slider;
    ArrayList<SlideModel> sliderArrayList;
    private RecyclerView booksRecyclerView,categoryRecyclerView,bookForUserRecyclerView,bookBestSellerRecyclerView;
    private HardBookECommPurchaseAdapter hardBookECommPurchaseAdapter;
    private ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList;
//    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private String token;
    private RelativeLayout noDataLayout,stickySearchRelativeLayout;
    private EditText searchView;
    private final String bookURL = Constant.BASE_URL + "book/getAllBooks";
//    ImageView cartIcon,wishlistIcon;
    private int currentPage = 1;
    private int totalPages = 1;
    private final int itemsPerPage = 10;
    private boolean isLoading = false;
    ArrayList<CategoryModel> categoryArrayList = new ArrayList<>();
    NestedScrollView nestedScrollView;
    RelativeLayout toolbarRelativeLayout;
    FrameLayout searchViewFrameLayout;

    private float previousY = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_book_ecomm_purchase);

        getWindow().setStatusBarColor(ContextCompat.getColor(HardBookECommPurchaseActivity.this,R.color.md_theme_dark_surfaceTint));

        initializeViews();
//        setupToolbar();
        setupImageSlider();
//        setupSearchView();
        getAllBooks();
//        setClickingListeners();
    }
    private void initializeViews() {
//        toolbar = findViewById(R.id.hardbook_ecomm_purchase_toolbar);
        logo = findViewById(R.id.logo);
        cartBtn = findViewById(R.id.cartBtn);
        slider = findViewById(R.id.slider);
//        progressBar = findViewById(R.id.progressBar);
        nestedScrollView = findViewById(R.id.nestScrollView);
        searchViewFrameLayout = findViewById(R.id.searchViewFrameLayout);
        toolbarRelativeLayout = findViewById(R.id.hardbook_ecomm_purchase_toolbar);
        booksRecyclerView = findViewById(R.id.booksRecycler);
        categoryRecyclerView = findViewById(R.id.categoryRecycler);
        bookForUserRecyclerView = findViewById(R.id.booksForUserRecycler);
        bookBestSellerRecyclerView = findViewById(R.id.booksBestSellerRecycler);
        noDataLayout = findViewById(R.id.noDataLayout);

        searchView = findViewById(R.id.search_icon);
        searchView.setFocusable(false);
        searchView.setClickable(true);
//        cartIcon = findViewById(R.id.cartBtn);
//        wishlistIcon = findViewById(R.id.wishListBtn);
        hardBookECommPurchaseModelArrayList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 is the number of columns
//        booksRecyclerView.setLayoutManager(gridLayoutManager);

        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        bookForUserRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        bookBestSellerRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.HORIZONTAL,false));
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
        categoryRecyclerView.setAdapter(new CategoryAdapter(categoryArrayList,HardBookECommPurchaseActivity.this));

//        booksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                // Get the GridLayoutManager and find the last visible item position
//                int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();
//                int totalItemCount = gridLayoutManager.getItemCount();
//
//                Log.d("ScrollListener", "Last visible item position: " + lastVisibleItemPosition + " Total items: " + totalItemCount);
//
//                // Check if we are at the bottom of the list
//                if (lastVisibleItemPosition + 1 >= totalItemCount && !isLoading) {
//                    // Check if there are more pages to load
//                    if (currentPage < totalPages) {
//                        currentPage++;  // Increment the current page
//                        getAllBooks();   // Fetch the next set of books
//                    }
//                }
//            }
//        });

//        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                // Check if the scroll direction is up or down
//                if (scrollY > previousY) {
//                    // Scroll Down: Hide the toolbar
//                    hideToolbar();
//                } else if (scrollY < previousY) {
//                    // Scroll Up: Show the toolbar
//                    showToolbar();
//                }
//                previousY = scrollY;
//            }
//        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HardBookECommPurchaseActivity.this, SearchingBooksActivity.class);
                startActivity(intent);
            }
        });
    }
// Original height of the toolbar
    int originalHeight;

    // Hide the toolbar and set its height to wrap content
    private void hideToolbar() {
        // Store the original height of the toolbar
        originalHeight = toolbarRelativeLayout.getHeight();

        // Set height to wrap content by changing the LayoutParams
        ViewGroup.LayoutParams params = toolbarRelativeLayout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        toolbarRelativeLayout.setLayoutParams(params);

        // Animate the toolbar upwards to hide it
        ObjectAnimator animator = ObjectAnimator.ofFloat(toolbarRelativeLayout, "translationY", 0f, -toolbarRelativeLayout.getHeight() / 2.4f);
        animator.setDuration(100);
        animator.start();
    }

    // Show the toolbar and restore its original height
    private void showToolbar() {
        // Restore the original height of the toolbar
        ViewGroup.LayoutParams params = toolbarRelativeLayout.getLayoutParams();
        params.height = originalHeight;  // Restore the original height
        toolbarRelativeLayout.setLayoutParams(params);

        // Animate the toolbar back into view
        ObjectAnimator animator = ObjectAnimator.ofFloat(toolbarRelativeLayout, "translationY", -toolbarRelativeLayout.getHeight() / 2.4f, 0f);
        animator.setDuration(100);
        animator.start();
    }
//    private void setClickingListeners() {
//        wishlistIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HardBookECommPurchaseActivity.this, WishlistActivity.class);
//                startActivity(intent);
//            }
//        });
//        cartIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HardBookECommPurchaseActivity.this, CartViewActivity.class);
//                startActivity(intent);
//            }
//        });
//    }

//    private void setupToolbar() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
//    }

    private void setupImageSlider() {
        sliderArrayList = new ArrayList<>();
        sliderArrayList.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));
        slider.setImageList(sliderArrayList);
    }
//    @SuppressLint("ClickableViewAccessibility")
//    private void setupSearchView() {
//        searchView.setOnClickListener(view -> openKeyboard());
//        searchView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (hardBookECommPurchaseAdapter != null) {
//                    hardBookECommPurchaseAdapter.filter(String.valueOf(editable));
//                }
//            }
//        });
//        searchView.addTextChangedListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//            }
//        });
//    }

//    private void openKeyboard() {
////        searchView.setIconified(false);
//        searchView.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
//        }
//    }
    private void getAllBooks() {
        String paginatedURL = bookURL + "?type=book&page=" + currentPage + "&per_page=" + itemsPerPage;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            booksRecyclerView.setVisibility(View.VISIBLE);
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
                                bookForUserRecyclerView.setAdapter(new BookForUserAdapter(HardBookECommPurchaseActivity.this,hardBookECommPurchaseModelArrayList));
                                bookBestSellerRecyclerView.setAdapter(new BookForUserAdapter(HardBookECommPurchaseActivity.this,hardBookECommPurchaseModelArrayList));
//                                updateUI();
                                if (hardBookECommPurchaseModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    booksRecyclerView.setVisibility(View.GONE);
//                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (hardBookECommPurchaseAdapter == null) {
                                        hardBookECommPurchaseAdapter = new HardBookECommPurchaseAdapter(HardBookECommPurchaseActivity.this, hardBookECommPurchaseModelArrayList);
                                        booksRecyclerView.setAdapter(new BookForUserAdapter(HardBookECommPurchaseActivity.this,hardBookECommPurchaseModelArrayList));
                                    } else {
                                        hardBookECommPurchaseAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                Toast.makeText(HardBookECommPurchaseActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(HardBookECommPurchaseActivity.this, message, Toast.LENGTH_LONG).show();
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
    private void updateUI() {
        if (hardBookECommPurchaseModelArrayList.isEmpty()) {
            noDataLayout.setVisibility(View.VISIBLE);
            booksRecyclerView.setVisibility(View.GONE);
//            progressBar.setVisibility(View.GONE);
        } else {
            noDataLayout.setVisibility(View.GONE);
            booksRecyclerView.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
            if (hardBookECommPurchaseAdapter == null) {
                hardBookECommPurchaseAdapter = new HardBookECommPurchaseAdapter(this, hardBookECommPurchaseModelArrayList);
                booksRecyclerView.setAdapter(hardBookECommPurchaseAdapter);
            } else {
                hardBookECommPurchaseAdapter.notifyDataSetChanged();
            }
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

