package com.examatlas.fragment;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.examatlas.adapter.AdminShowAllCategoryAdapter;
import com.examatlas.adapter.AdminTagsForDataALLAdapter;
import com.examatlas.models.AdminShowAllCategoryModel;
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

public class AdminCreateCategoryFragment extends Fragment {
    Button createBtn,searchBtn;
    SearchView searchView;
    RecyclerView showSubjectRecycler;
    RelativeLayout noDataLayout;
    ProgressBar progressBar;
    AdminShowAllCategoryAdapter categoryAdapter;
    AdminShowAllCategoryModel categoryModel;
    ArrayList<AdminShowAllCategoryModel> categoryModelArrayList = new ArrayList<>();
    private final String categoryURL = Constant.BASE_URL + "category/getCategory";
    private final String createCategoryURL = Constant.BASE_URL + "category/createCategory";
    SessionManager sessionManager;
    String authToken;
    private String searchQuery = "";
    private int totalPages = 1,currentPage = 1;
    private final int itemsPerPage = 10;
    private boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_fragment_create_category, container, false);

        createBtn = view.findViewById(R.id.btnCreate);
        searchBtn = view.findViewById(R.id.btnSearch);
        searchView = view.findViewById(R.id.searchView);
        showSubjectRecycler = view.findViewById(R.id.showAllSubjectRecycler);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        progressBar = view.findViewById(R.id.showAllCategoryProgressBar);

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        showSubjectRecycler.setLayoutManager(linearLayoutManager);
        categoryModelArrayList.clear();
        getAllCategory();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogBoxCreateSubject();
            }
        });
        showSubjectRecycler.setItemAnimator(null);
        showSubjectRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if we are at the bottom of the RecyclerView
                int totalItemCount = showSubjectRecycler.getLayoutManager().getItemCount();
                int lastVisibleItem = ((LinearLayoutManager) showSubjectRecycler.getLayoutManager()).findLastVisibleItemPosition();

                if (totalItemCount <= (lastVisibleItem + 2) && !isLoading && currentPage < totalPages) {
                    isLoading = true;
                    getAllCategory();  // Load next page
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
                if (categoryAdapter != null) {
                    categoryAdapter.filter(newText);
                }
                return true;
            }
        });

        return view;
    }

    private Dialog createCategoryDialogBox;
    private EditText titleEditTxt,descriptionEditTxt,tagsEditTxt;
    private Button submitBtn;
    private ImageView crossBtn;
    private RecyclerView tagsRecyclerView;
    private AdminTagsForDataALLAdapter adminTagsForDataALLAdapter;
    private AdminTagsForDataALLModel adminTagsForDataALLModel;
    private ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList;
    private void openDialogBoxCreateSubject() {

        createCategoryDialogBox = new Dialog(requireContext());
        createCategoryDialogBox.setContentView(R.layout.admin_create_category_dialog_box);

        titleEditTxt = createCategoryDialogBox.findViewById(R.id.titleEditTxt);
        descriptionEditTxt = createCategoryDialogBox.findViewById(R.id.descriptionEditText);
        submitBtn = createCategoryDialogBox.findViewById(R.id.btnSubmit);
        crossBtn = createCategoryDialogBox.findViewById(R.id.btnCross);

        tagsRecyclerView = createCategoryDialogBox.findViewById(R.id.tagsRecycler);
        adminTagsForDataALLModelArrayList = new ArrayList<>();
        tagsRecyclerView = createCategoryDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        tagsEditTxt = createCategoryDialogBox.findViewById(R.id.tagsEditText);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCategoryDialogBox.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCategoryDetails(titleEditTxt.getText().toString().trim(),descriptionEditTxt.getText().toString().trim(),adminTagsForDataALLModelArrayList,createCategoryDialogBox);
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

        createCategoryDialogBox.show();
        WindowManager.LayoutParams params = createCategoryDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

// Set the window attributes
        createCategoryDialogBox.getWindow().setAttributes(params);

// Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) createCategoryDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

// Set top and bottom margins (adjust values as needed)
        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

// Background and animation settings
        createCategoryDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createCategoryDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        createCategoryDialogBox.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }

    private void sendCategoryDetails(String title, String description, ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList, Dialog createCategoryDialogBox) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("categoryName", title);
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, createCategoryURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                getAllCategory();
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

    public void getAllCategory() {
        String subjectURLPage = categoryURL  + "?search=" + searchQuery + "&page=" + currentPage + "&per_page=" + itemsPerPage;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subjectURLPage, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            showSubjectRecycler.setVisibility(View.VISIBLE);
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
                                    String description = jsonObject2.getString("description");
                                    String is_active = jsonObject2.getString("is_active");

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

                                    categoryModel = new AdminShowAllCategoryModel(id,categoryName,description,is_active,tags.toString());
                                    categoryModelArrayList.add(categoryModel);
                                }
//                                updateUI();
                                if (categoryModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    showSubjectRecycler.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (categoryAdapter == null) {
                                        categoryAdapter = new AdminShowAllCategoryAdapter( categoryModelArrayList,AdminCreateCategoryFragment.this);
                                        showSubjectRecycler.setAdapter(categoryAdapter);
                                    } else {
                                        categoryAdapter.notifyDataSetChanged();
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
