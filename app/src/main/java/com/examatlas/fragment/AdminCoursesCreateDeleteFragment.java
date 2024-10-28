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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.AdminShowAllCoursesAdapter;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.models.AdminShowAllCourseModel;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminCoursesCreateDeleteFragment extends Fragment {
    SearchView searchView;
    RecyclerView showAllCoursesRecycler;
    RelativeLayout noDataLayout;
    ProgressBar courseProgress;
    ArrayList<AdminShowAllCourseModel> adminShowAllCourseModelArrayList;
    AdminShowAllCoursesAdapter adminShowAllCoursesAdapter;
    Button btnCreateCourses;
    private final String courseURL = Constant.BASE_URL + "course/getAllCourse";
    private final String createCoursesURL = Constant.BASE_URL + "course/createcourse";
    private Dialog createCoursesDialogBox;
    private EditText titleEditTxt, priceEditTxt;
    Button submitCourseDetailsBtn;
    ImageView crossBtn;
    SessionManager sessionManager;
    String authToken;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_create_courses, container, false);

        searchView = view.findViewById(R.id.searchView);

        btnCreateCourses = view.findViewById(R.id.btnCreate);
        showAllCoursesRecycler = view.findViewById(R.id.showCoursesRecycler);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        courseProgress = view.findViewById(R.id.showAllCoursesProgressBar);
        adminShowAllCourseModelArrayList = new ArrayList<>();

        showAllCoursesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        showAllCoursesFunction();

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
                if (adminShowAllCoursesAdapter != null) {
                    adminShowAllCoursesAdapter.filter(newText);
                }
                return true;
            }
        });
        btnCreateCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateCoursesDialog();
            }
        });
        return view;
    }

    private void showAllCoursesFunction() {
        noDataLayout.setVisibility(View.GONE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, courseURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            showAllCoursesRecycler.setVisibility(View.VISIBLE);
                            courseProgress.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("course");
                                adminShowAllCourseModelArrayList.clear(); // Clear the list before adding new items

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String courseID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String price = jsonObject2.getString("price");
                                    String image = jsonObject2.getString("image");
                                    String createdDate = jsonObject2.getString("createdAt");

                                    AdminShowAllCourseModel courseModel = new AdminShowAllCourseModel(courseID, title, price, image, createdDate);
                                    adminShowAllCourseModelArrayList.add(courseModel);
                                }

                                // Update the original list in the adapter
                                if (adminShowAllCoursesAdapter != null) {
                                    adminShowAllCoursesAdapter.updateOriginalList(adminShowAllCourseModelArrayList);
                                }

                                // Check if the list is empty
                                if (adminShowAllCourseModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    courseProgress.setVisibility(View.GONE);
                                } else {
                                    if (adminShowAllCoursesAdapter == null) {
                                        adminShowAllCoursesAdapter = new AdminShowAllCoursesAdapter(AdminCoursesCreateDeleteFragment.this, adminShowAllCourseModelArrayList);
                                        showAllCoursesRecycler.setAdapter(adminShowAllCoursesAdapter);
                                    } else {
                                        adminShowAllCoursesAdapter.notifyDataSetChanged();
                                    }
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
                // Handle error
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

    private void openCreateCoursesDialog(){
        createCoursesDialogBox = new Dialog(requireContext());
        createCoursesDialogBox.setContentView(R.layout.admin_create_course_dialog);

        titleEditTxt = createCoursesDialogBox.findViewById(R.id.titleEditTxt);
        priceEditTxt = createCoursesDialogBox.findViewById(R.id.priceEditTxt);
        crossBtn = createCoursesDialogBox.findViewById(R.id.btnCross);

        submitCourseDetailsBtn = createCoursesDialogBox.findViewById(R.id.btnSubmit);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCoursesDialogBox.dismiss();
            }
        });

        submitCourseDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCourseDetailsFunction(titleEditTxt.getText().toString().trim(),priceEditTxt.getText().toString().trim());
            }
        });
        createCoursesDialogBox.show();
        WindowManager.LayoutParams params = createCoursesDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        createCoursesDialogBox.getWindow().setAttributes(params);
        createCoursesDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createCoursesDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        createCoursesDialogBox.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    private void submitCourseDetailsFunction(String title, String price) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("price", price);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, createCoursesURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                                // Refresh the course list
                                adminShowAllCourseModelArrayList.clear();
                                showAllCoursesFunction(); // Fetch the updated list

                                // Dismiss the dialog
                                createCoursesDialogBox.dismiss();
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

}
