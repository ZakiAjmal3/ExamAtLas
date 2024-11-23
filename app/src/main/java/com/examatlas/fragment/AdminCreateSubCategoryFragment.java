package com.examatlas.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.AdminShowAllCategoryAdapter;
import com.examatlas.adapter.AdminShowAllSubCategoryAdapter;
import com.examatlas.adapter.AdminShowAllSubjectAdapter;
import com.examatlas.adapter.AdminTagsForDataALLAdapter;
import com.examatlas.models.AdminShowAllCategoryModel;
import com.examatlas.models.AdminShowAllSubCategoryModel;
import com.examatlas.models.AdminTagsForDataALLModel;
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

public class AdminCreateSubCategoryFragment extends Fragment {
    Button createBtn, searchBtn;
    SearchView searchView;
    RecyclerView showSubCategoryRecycler;
    RelativeLayout noDataLayout;
    ProgressBar progressBar,nextItemLoadingProgressBar;
    AdminShowAllSubCategoryAdapter subCategoryAdapter;
    AdminShowAllSubCategoryModel subCategoryModel;
    ArrayList<AdminShowAllSubCategoryModel> subCategoryModelArrayList;
    ArrayList<AdminShowAllSubCategoryModel> subCategoryModelArrayList2;
    private final String subCategoryURL = Constant.BASE_URL + "category/getSubCategory";
    private final String subCreateCategoryURL = Constant.BASE_URL + "category/createSubCategory";
    private final String categoryURL = Constant.BASE_URL + "category/getCategory";
    AdminShowAllCategoryModel categoryModel;
    AdminShowAllCategoryAdapter categoryAdapter;
    ArrayList<AdminShowAllCategoryModel> categoryModelArrayList = new ArrayList<>();
    SessionManager sessionManager;
    String authToken;
    private String searchQuery = "";
    private int totalRows = 1,totalPages = 1, currentPage = 1;
    private final int itemsPerPage = 10;
    private boolean isLoading = false;
    ArrayList<String> categoryNameList;
    String categoryId = null;
    private NestedScrollView nestedSV;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_fragment_create_subcategory, container, false);

        createBtn = view.findViewById(R.id.btnCreate);
        searchBtn = view.findViewById(R.id.btnSearch);
        searchView = view.findViewById(R.id.searchView);
        showSubCategoryRecycler = view.findViewById(R.id.showAllSubCategoryRecycler);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        progressBar = view.findViewById(R.id.showAllSubCategoryProgressBar);
        nextItemLoadingProgressBar = view.findViewById(R.id.nextItemLoadingProgressBar);
        nestedSV = view.findViewById(R.id.nestScrollView);

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        subCategoryModelArrayList = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        showSubCategoryRecycler.setLayoutManager(manager);

        getAllCategory();
        getAllSubCategory();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogBoxCreateSubject();
            }
        });
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    currentPage++;
                    // on below line we are making our progress bar visible.
                    if (currentPage <= totalPages) {
                        nextItemLoadingProgressBar.setVisibility(View.VISIBLE);
                        // on below line we are again calling
                        // a method to load data in our array list.
                        getAllSubCategory();
                    }
                }
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKeyboard();
            }
        });

        // Set up touch listener for the parent layout
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (searchView.isShown() && !isPointInsideView(event.getRawX(), event.getRawY(), searchView)) {
                        searchView.setIconified(true); // Collapse the search view
                        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // Hide the keyboard
                    }
                }
                return false; // Allow other events to be handled
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not needed for this implementation
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Call the filter method when the search text changes
                if (subCategoryAdapter != null) {
                    subCategoryAdapter.filter(newText);
                }
                return true;
            }
        });

        return view;
    }

    private Dialog createSubCategoryDialogBox;
    Spinner categorySpinner;
    private EditText titleEditTxt, slugEditTxt;
    private Button submitBtn;
    private ImageView crossBtn;
    @SuppressLint("ClickableViewAccessibility")
    private void openDialogBoxCreateSubject() {

        createSubCategoryDialogBox = new Dialog(requireContext());
        createSubCategoryDialogBox.setContentView(R.layout.admin_create_subcategory_dialog_box);

        titleEditTxt = createSubCategoryDialogBox.findViewById(R.id.titleEditTxt);
        categorySpinner = createSubCategoryDialogBox.findViewById(R.id.categorySpinner);
        slugEditTxt = createSubCategoryDialogBox.findViewById(R.id.slugEditText);

        submitBtn = createSubCategoryDialogBox.findViewById(R.id.btnSubmit);
        crossBtn = createSubCategoryDialogBox.findViewById(R.id.btnCross);

        categoryNameList = new ArrayList<>();
        String[] categoryNameList = new String[categoryModelArrayList.size() + 1]; // +1 for "Select Category"
        categoryNameList[0] = "Select Category";
        for (int i = 0; i < categoryModelArrayList.size(); i++) {
            categoryNameList[i + 1] = categoryModelArrayList.get(i).getCategoryName();
        }
        setupCategorySpinner(categorySpinner,titleEditTxt, slugEditTxt,categoryNameList,null);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSubCategoryDialogBox.dismiss();
            }
        });
        createSubCategoryDialogBox.findViewById(android.R.id.content).setOnTouchListener((v, event) -> {
            if (categoryId == null) {
                // Show toast if no category is selected
                Toast.makeText(getContext(), "Please select a category first", Toast.LENGTH_SHORT).show();
            }
            return false; // Return false to allow the touch event to propagate
        });
        titleEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category", Toast.LENGTH_SHORT).show();
                }
            }
        });
        slugEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category", Toast.LENGTH_SHORT).show();
                }
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSubCategoryDetails(categoryId,titleEditTxt.getText().toString().trim(), slugEditTxt.getText().toString().trim(), createSubCategoryDialogBox);
            }
        });

        createSubCategoryDialogBox.show();
        WindowManager.LayoutParams params = createSubCategoryDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

// Set the window attributes
        createSubCategoryDialogBox.getWindow().setAttributes(params);

// Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) createSubCategoryDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

// Set top and bottom margins (adjust values as needed)
        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

// Background and animation settings
        createSubCategoryDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createSubCategoryDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        createSubCategoryDialogBox.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }

    public void setupCategorySpinner(Spinner categorySpinners, EditText titleEditTxt, EditText slugEditTxt, String[] categoryNameList , AdminShowAllSubCategoryModel currentCategory) {
        // Set the adapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinners.setAdapter(adapter);
        if (currentCategory != null) {
            for (int i = 0; i < categoryNameList.length; i++) {
                if (categoryNameList[i].equals(currentCategory.getCategoryName())) {
                    categorySpinners.setSelection(i);
                    categorySpinners.setEnabled(false);
                }
            }
        }

        // Set the OnItemSelectedListener to handle category selection
        categorySpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0 && subCategoryModelArrayList != null && position < subCategoryModelArrayList.size()) {
                    // Ensure that a category is selected and the position is within bounds
                    categoryId = categoryModelArrayList.get(position - 1).getId(); // Get the corresponding categoryId
                    titleEditTxt.setEnabled(true); // Enable the EditText fields
                    slugEditTxt.setEnabled(true);
                } else {
                    categoryId = null; // Reset categoryId if "Select Category" or invalid selection is selected
                    titleEditTxt.setEnabled(false); // Disable the EditText fields
                    slugEditTxt.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                categoryId = null; // Reset categoryId if nothing is selected
                titleEditTxt.setEnabled(false); // Disable the EditText fields
                slugEditTxt.setEnabled(false);
            }
        });

    }
    private void sendSubCategoryDetails(String categoryId,String title, String slug, Dialog createCategoryDialogBox) {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("id",null);
            jsonObject.put("categoryId", categoryId);
            jsonObject.put("subCategoryName", title);
            jsonObject.put("slug", slug);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, subCreateCategoryURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                getAllSubCategory();
                                createCategoryDialogBox.dismiss();
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

        MySingletonFragment.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    public void getAllSubCategory() {
//        subCategoryAdapter = null;
        String subjectURLPage = subCategoryURL + "?page=" + currentPage + "&per_page=" + itemsPerPage;;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subjectURLPage, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("SubCategoryResponse", response.toString());
                            showSubCategoryRecycler.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                subCategoryModelArrayList2 = new ArrayList<>();
                                createBtn.setEnabled(true);
                                JSONObject jsonObject = response.getJSONObject("pagination");

                                totalRows = Integer.parseInt(jsonObject.getString("totalRows"));
                                totalPages = Integer.parseInt(jsonObject.getString("totalPages"));
                                currentPage = Integer.parseInt(jsonObject.getString("currentPage"));

                                JSONArray jsonArray = response.getJSONArray("data");
//                                int initialSize = subCategoryModelArrayList.size(); // Get the initial size of the ArrayList

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String subCategoryId = jsonObject2.getString("_id");

                                    // Check if categoryId is null
                                    JSONObject jsonObject3 = jsonObject2.optJSONObject("categoryId");
                                    String categoryId = "";
                                    String categoryName = "";
                                    // If categoryId is not null, parse its data
                                    if (jsonObject3 != null) {
                                        categoryId = jsonObject3.getString("_id");
                                        categoryName = jsonObject3.getString("categoryName");
                                    }

                                    String subCategoryName = jsonObject2.getString("subCategoryName");
                                    String icActive = jsonObject2.getString("is_active");

                                    subCategoryModel = new AdminShowAllSubCategoryModel(categoryId,categoryName,subCategoryId,subCategoryName,icActive,totalRows,totalPages,currentPage);
                                    subCategoryModelArrayList.add(subCategoryModel);
                                }
//                                updateUI();
                                // Update UI after fetching the data
                                if (subCategoryModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    showSubCategoryRecycler.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    subCategoryAdapter = new AdminShowAllSubCategoryAdapter(subCategoryModelArrayList, AdminCreateSubCategoryFragment.this);
                                    showSubCategoryRecycler.setAdapter(subCategoryAdapter);
                                }
                                isLoading = false;
                                nextItemLoadingProgressBar.setVisibility(View.GONE);
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
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
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
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    public void getAllCategory() {
        String subjectURLPage = categoryURL  + "?search=" + searchQuery + "&page=" + currentPage + "&per_page=" + itemsPerPage;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subjectURLPage, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            createBtn.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("pagination");

                                int totalRows = Integer.parseInt(jsonObject.getString("totalRows"));
                                totalPages = Integer.parseInt(jsonObject.getString("totalPages"));
                                currentPage = Integer.parseInt(jsonObject.getString("currentPage"));

                                JSONArray jsonArray = response.getJSONArray("data");
                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String id = jsonObject2.getString("_id");
                                    String categoryName = jsonObject2.getString("categoryName");
                                    String description = jsonObject2.getString("slug");
                                    String is_active = jsonObject2.getString("is_active");
                                    JSONObject imageObj = jsonObject2.getJSONObject("image");
                                    String imageUrl = imageObj.getString("url");

                                    categoryModel = new AdminShowAllCategoryModel(id, categoryName, description, is_active, imageUrl);
                                    categoryModelArrayList.add(categoryModel);
                                }
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
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void openKeyboard() {
        searchView.setIconified(false); // Expands the search view
        searchView.requestFocus(); // Requests focus
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT); // Show the keyboard
    }

    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return (x >= location[0] && x <= (location[0] + view.getWidth()) &&
                y >= location[1] && y <= (location[1] + view.getHeight()));
    }
    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        int previousLength = subCategoryModelArrayList.size(); // Initial size of the list
        subCategoryModelArrayList.addAll(subCategoryModelArrayList2); // Add new items to the existing list
        int newAddedLength = subCategoryModelArrayList2.size(); // Length of the newly added items

        subCategoryModelArrayList2.clear(); // Clear temporary list

        Log.d("UpdateUI", "Previous Length: " + previousLength);
        Log.d("UpdateUI", "New Length: " + subCategoryModelArrayList.size());
        Log.d("UpdateUI", "New Items Added: " + newAddedLength);

        if (subCategoryModelArrayList.isEmpty()) {
            noDataLayout.setVisibility(View.VISIBLE);
            showSubCategoryRecycler.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            noDataLayout.setVisibility(View.GONE);
            showSubCategoryRecycler.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            if (subCategoryAdapter == null) {
                // Initialize the adapter if it is null
                subCategoryAdapter = new AdminShowAllSubCategoryAdapter(subCategoryModelArrayList, AdminCreateSubCategoryFragment.this);
                showSubCategoryRecycler.setAdapter(subCategoryAdapter);
                Log.d("UpdateUI", "Adapter was null, now set.");
            } else {
                // Notify the adapter about the newly added items
                if (newAddedLength > 0) {
                    // Notify the adapter about the range of new items inserted
                    subCategoryAdapter.notifyItemRangeInserted(previousLength, newAddedLength);
                    Log.d("UpdateUI", "Notified adapter about new items.");
                } else {
                    Log.d("UpdateUI", "No new items to notify.");
                }
            }
        }

        // If you want to scroll to the newly loaded items, you can scroll to the last item
        if (newAddedLength > 0) {
            showSubCategoryRecycler.scrollToPosition(subCategoryModelArrayList.size() - 1);
        }
    }
}
