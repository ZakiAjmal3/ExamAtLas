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
import com.examatlas.adapter.AdminShowAllSubCategoryAdapter;
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
    ProgressBar progressBar;
    AdminShowAllSubCategoryAdapter subCategoryAdapter;
    AdminShowAllSubCategoryModel subCategoryModel;
    ArrayList<AdminShowAllSubCategoryModel> subCategoryModelArrayList = new ArrayList<>();
    private final String subCategoryURL = Constant.BASE_URL + "/category/getSubCategory";
    private final String subCreateCategoryURL = Constant.BASE_URL + "category/createSubCategory";
    SessionManager sessionManager;
    String authToken;
    private String searchQuery = "";
    private int totalPages = 1, currentPage = 1;
    private final int itemsPerPage = 10;
    private boolean isLoading = false;
    ArrayList<String> categoryNameList;
    String categoryId = null;
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

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        showSubCategoryRecycler.setLayoutManager(linearLayoutManager);
        subCategoryModelArrayList.clear();
        getAllSubCategory();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogBoxCreateSubject();
            }
        });
        showSubCategoryRecycler.setItemAnimator(null);
        showSubCategoryRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if we are at the bottom of the RecyclerView
                int totalItemCount = showSubCategoryRecycler.getLayoutManager().getItemCount();
                int lastVisibleItem = ((LinearLayoutManager) showSubCategoryRecycler.getLayoutManager()).findLastVisibleItemPosition();

                if (totalItemCount <= (lastVisibleItem + 2) && !isLoading && currentPage < totalPages) {
                    isLoading = true;
                    getAllSubCategory();  // Load next page
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
    private EditText titleEditTxt,descriptionEditTxt,tagsEditTxt;
    private Button submitBtn;
    private ImageView crossBtn;
    private RecyclerView tagsRecyclerView;
    private AdminTagsForDataALLAdapter adminTagsForDataALLAdapter;
    private AdminTagsForDataALLModel adminTagsForDataALLModel;
    private ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList;
    @SuppressLint("ClickableViewAccessibility")
    private void openDialogBoxCreateSubject() {

        createSubCategoryDialogBox = new Dialog(requireContext());
        createSubCategoryDialogBox.setContentView(R.layout.admin_create_subcategory_dialog_box);

        titleEditTxt = createSubCategoryDialogBox.findViewById(R.id.titleEditTxt);
        categorySpinner = createSubCategoryDialogBox.findViewById(R.id.categorySpinner);
        descriptionEditTxt = createSubCategoryDialogBox.findViewById(R.id.descriptionEditText);
        submitBtn = createSubCategoryDialogBox.findViewById(R.id.btnSubmit);
        crossBtn = createSubCategoryDialogBox.findViewById(R.id.btnCross);

        tagsRecyclerView = createSubCategoryDialogBox.findViewById(R.id.tagsRecycler);
        adminTagsForDataALLModelArrayList = new ArrayList<>();
        tagsRecyclerView = createSubCategoryDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        tagsEditTxt = createSubCategoryDialogBox.findViewById(R.id.tagsEditText);
        categoryNameList = new ArrayList<>();
        String[] categoryNameList = new String[subCategoryModelArrayList.size() + 1]; // +1 for "Select Category"
        categoryNameList[0] = "Select Category";
        for (int i = 0; i < subCategoryModelArrayList.size(); i++) {
            categoryNameList[i + 1] = subCategoryModelArrayList.get(i).getSubCategoryName();
        }
        setupCategorySpinner(categorySpinner,titleEditTxt,descriptionEditTxt,tagsEditTxt,null);

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
        descriptionEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tagsEditTxt.setOnClickListener(new View.OnClickListener() {
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
                sendSubCategoryDetails(categoryId,titleEditTxt.getText().toString().trim(),descriptionEditTxt.getText().toString().trim(),adminTagsForDataALLModelArrayList, createSubCategoryDialogBox);
            }
        });

        tagsEditTxt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String tagText = tagsEditTxt.getText().toString().trim();
                if (!tagText.isEmpty()) {
                    adminTagsForDataALLModelArrayList.add(new AdminTagsForDataALLModel(tagText));
                    adminTagsForDataALLAdapter.notifyItemInserted(adminTagsForDataALLModelArrayList.size() - 1);
                    tagsEditTxt.setText(""); // Clear the EditText
                    tagsRecyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
                }
                return true; // Indicate that we've handled the event
            }
            return false; // Pass the event on
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

    public void setupCategorySpinner(Spinner categorySpinners, EditText titleEditTxt, EditText descriptionEditTxt, EditText tagsEditTxt, AdminShowAllSubCategoryModel currentCategory) {
        // Assuming `subCategoryModelArrayList` contains the categories data
        ArrayList<String> categoryNameList = new ArrayList<>();
        categoryNameList.add("Select Category"); // First item is "Select Category"

        // Populate category names from your subCategoryModelArrayList
        for (AdminShowAllSubCategoryModel category : subCategoryModelArrayList) {
            categoryNameList.add(category.getCategoryName());
        }

        // Set the adapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinners.setAdapter(adapter);
        if (currentCategory != null) {
            for (int i = 0; i < categoryNameList.size(); i++) {
                if (categoryNameList.get(i).equals(currentCategory.getCategoryName())) {
                    categorySpinners.setSelection(i);
                    categorySpinners.setEnabled(false);
                }
            }
        }

        // Set the OnItemSelectedListener to handle category selection
        categorySpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) { // Ensure that a category is selected (not "Select Category")
                    categoryId = subCategoryModelArrayList.get(position - 1).getCategoryId(); // Get the corresponding categoryId
                    titleEditTxt.setEnabled(true); // Enable the EditText fields
                    descriptionEditTxt.setEnabled(true);
                    tagsEditTxt.setEnabled(true);
                } else {
                    categoryId = null; // Reset categoryId if "Select Category" is selected
                    titleEditTxt.setEnabled(false); // Disable the EditText fields
                    descriptionEditTxt.setEnabled(false);
                    tagsEditTxt.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                categoryId = null; // Reset categoryId if nothing is selected
                titleEditTxt.setEnabled(false); // Disable the EditText fields
                descriptionEditTxt.setEnabled(false);
                tagsEditTxt.setEnabled(false);
            }
        });
    }
    private void sendSubCategoryDetails(String categoryId,String title, String description, ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList, Dialog createCategoryDialogBox) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("categoryId", categoryId);
            jsonObject.put("subCategoryName", title);
            jsonObject.put("description", description);

            // Create a JSONArray for the tags
            JSONArray tagsArray = new JSONArray();
            for (AdminTagsForDataALLModel tag : adminTagsForDataALLModelArrayList) {
                tagsArray.put(tag.getTagName()); // Assuming getTagText() returns the tag's text
            }
            jsonObject.put("tags", tagsArray); // Add the tags array to the JSON object
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
        subCategoryAdapter = null;
        String subjectURLPage = subCategoryURL  + "?per_page=" + itemsPerPage;
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
                            subCategoryModelArrayList.clear();

                            if (status) {
                                createBtn.setEnabled(true);
                                JSONObject jsonObject = response.getJSONObject("pagination");

                                int totalRows = Integer.parseInt(jsonObject.getString("totalRows"));
                                totalPages = Integer.parseInt(jsonObject.getString("totalPages"));
                                currentPage = Integer.parseInt(jsonObject.getString("currentPage"));

                                JSONArray jsonArray = response.getJSONArray("data");
                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String subCategoryId = jsonObject2.getString("_id");

                                    // Check if categoryId is null
                                    JSONObject jsonObject3 = jsonObject2.optJSONObject("categoryId");
                                    String categoryId = "";
                                    String categoryName = "";
                                    String categoryDescription = "";
                                    String categoryTags = "";

                                    // If categoryId is not null, parse its data
                                    if (jsonObject3 != null) {
                                        categoryId = jsonObject3.getString("_id");
                                        categoryName = jsonObject3.getString("categoryName");
                                        categoryDescription = jsonObject3.getString("description");

                                        // Use StringBuilder for tags, but only if jsonObject3 is not null
                                        StringBuilder categoryTagsBuilder = new StringBuilder();
                                        if (jsonObject3.has("tags")) {
                                            JSONArray jsonArray1 = jsonObject3.getJSONArray("tags");
                                            for (int j = 0; j < jsonArray1.length(); j++) {
                                                String singleTag = jsonArray1.getString(j);
                                                categoryTagsBuilder.append(singleTag).append(", ");
                                            }
                                            if (categoryTagsBuilder.length() > 0) {
                                                categoryTagsBuilder.setLength(categoryTagsBuilder.length() - 2); // Remove trailing comma
                                            }
                                            categoryTags = categoryTagsBuilder.toString();
                                        }
                                    }

                                    String subCategoryName = jsonObject2.getString("subCategoryName");
                                    String subCategoryDescription = jsonObject2.getString("description");

                                    // Use StringBuilder for subcategory tags
                                    StringBuilder subCategoryTagsBuilder = new StringBuilder();
                                    JSONArray jsonArray2 = jsonObject2.getJSONArray("tags");
                                    for (int j = 0; j < jsonArray2.length(); j++) {
                                        String singleTag = jsonArray2.getString(j);
                                        subCategoryTagsBuilder.append(singleTag).append(", ");
                                    }
                                    if (subCategoryTagsBuilder.length() > 0) {
                                        subCategoryTagsBuilder.setLength(subCategoryTagsBuilder.length() - 2); // Remove trailing comma
                                    }
                                    String subCategoryTags = subCategoryTagsBuilder.toString();

                                    subCategoryModel = new AdminShowAllSubCategoryModel(
                                            categoryId, categoryName, categoryDescription, categoryTags,
                                            subCategoryId, subCategoryName, subCategoryDescription, subCategoryTags);

                                    subCategoryModelArrayList.add(subCategoryModel);
                                }

                                // Update UI after fetching the data
                                if (subCategoryModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    showSubCategoryRecycler.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (subCategoryAdapter == null) {
                                        subCategoryAdapter = new AdminShowAllSubCategoryAdapter(subCategoryModelArrayList, AdminCreateSubCategoryFragment.this);
                                        showSubCategoryRecycler.setAdapter(subCategoryAdapter);
                                    } else {
                                        subCategoryAdapter.notifyDataSetChanged();
                                    }
                                }
                                isLoading = false;
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
}
