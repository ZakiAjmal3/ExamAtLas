package com.examatlas.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.examatlas.adapter.AdminShowAllSubjectAdapter;
import com.examatlas.models.AdminShowAllSubjectModel;
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

public class AdminCreateSubjectFragment extends Fragment {
    Button createBtn,searchBtn;
    SearchView searchView;
    RecyclerView showSubjectRecycler;
    RelativeLayout noDataLayout;
    ProgressBar progressBar;
    AdminShowAllSubjectAdapter subjectAdapter;
    AdminShowAllSubjectModel subjectModel;
    ArrayList<AdminShowAllSubjectModel> subjectModelArrayList = new ArrayList<>();
    private final String subjectURL = Constant.BASE_URL + "subject/getSubject";
    private final String createSubjectURL = Constant.BASE_URL + "subject/addSubject";
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
        View view = inflater.inflate(R.layout.admin_fragment_create_subject, container, false);

        createBtn = view.findViewById(R.id.btnCreate);
        searchBtn = view.findViewById(R.id.btnSearch);
        searchView = view.findViewById(R.id.searchView);
        showSubjectRecycler = view.findViewById(R.id.showAllSubjectRecycler);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        progressBar = view.findViewById(R.id.showAllSubjectProgressBar);

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        showSubjectRecycler.setLayoutManager(linearLayoutManager);
        subjectModelArrayList.clear();
        getAllSubject();

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
                    getAllSubject();  // Load next page
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
                if (subjectAdapter != null) {
                    subjectAdapter.filter(newText);
                }
                return true;
            }
        });

        return view;
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

    private Dialog createSubjectDialogBox;
    private EditText titleEditTxt;
    private Button submitBtn;
    private ImageView crossBtn;
    private void openDialogBoxCreateSubject() {

        createSubjectDialogBox = new Dialog(requireContext());
        createSubjectDialogBox.setContentView(R.layout.admin_create_subject_dialog_box);

        titleEditTxt = createSubjectDialogBox.findViewById(R.id.titleEditTxt);
        submitBtn = createSubjectDialogBox.findViewById(R.id.btnSubmit);
        crossBtn = createSubjectDialogBox.findViewById(R.id.btnCross);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSubjectDialogBox.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSubjectDetails(titleEditTxt.getText().toString().trim());
            }
        });
        createSubjectDialogBox.show();
        WindowManager.LayoutParams params = createSubjectDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

// Set the window attributes
        createSubjectDialogBox.getWindow().setAttributes(params);

// Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) createSubjectDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

// Set top and bottom margins (adjust values as needed)
        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

// Background and animation settings
        createSubjectDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createSubjectDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        createSubjectDialogBox.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }

    public void sendSubjectDetails(String title) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, createSubjectURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                getAllSubject();
                                createSubjectDialogBox.dismiss();
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

    public void getAllSubject() {

        String subjectURLPage = subjectURL + "?search=" + searchQuery + "&page=" + currentPage + "&per_page=" + itemsPerPage;
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
                                    String title = jsonObject2.getString("title");
                                    String isActive = jsonObject2.getString("is_active");

                                    subjectModel = new AdminShowAllSubjectModel(id,title,isActive);
                                    subjectModelArrayList.add(subjectModel);
                                }
//                                updateUI();
                                if (subjectModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    showSubjectRecycler.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (subjectAdapter == null) {
                                        subjectAdapter = new AdminShowAllSubjectAdapter( subjectModelArrayList,AdminCreateSubjectFragment.this);
                                        showSubjectRecycler.setAdapter(subjectAdapter);
                                    } else {
                                        subjectAdapter.notifyDataSetChanged();
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

//    private void loadAllSubject() {
//        if (currentPage < totalPages){
//            currentPage++;
//            getAllSubject();
//            Log.e("checking" ,currentPage + " " + totalPages);
//        }
//    }

    private void updateUI() {
        if (subjectModelArrayList.isEmpty()) {
            noDataLayout.setVisibility(View.VISIBLE);
            showSubjectRecycler.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            noDataLayout.setVisibility(View.GONE);
            showSubjectRecycler.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if (subjectAdapter == null) {
                subjectAdapter = new AdminShowAllSubjectAdapter(subjectModelArrayList, AdminCreateSubjectFragment.this);
                showSubjectRecycler.setAdapter(subjectAdapter);
            } else {
                subjectAdapter.notifyDataSetChanged();
            }
        }
    }
}
