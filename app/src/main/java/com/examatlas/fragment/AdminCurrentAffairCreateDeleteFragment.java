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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.AdminShowAllCAAdapter;
import com.examatlas.adapter.AdminTagsForDataALLAdapter;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.models.AdminShowAllCAModel;
import com.examatlas.models.AdminTagsForDataALLModel;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminCurrentAffairCreateDeleteFragment extends Fragment {
    private SearchView searchView;
    RecyclerView showAllCARecyclerView;
    AdminShowAllCAAdapter adminShowAllCAAdapter;
    AdminShowAllCAModel adminShowAllCAModel;
    ArrayList<AdminShowAllCAModel> adminShowAllCAModelArrayList;
    private final String caURL = Constant.BASE_URL + "currentAffair/getAllCA";
    private final String createCAURL = Constant.BASE_URL + "currentAffair/createCA";
    ProgressBar showAllCAProgressBar;
    Button btnCreateCA;
    private Dialog createCADialogBox;
    private EditText titleEditTxt, keywordEditTxt, contentEditTxt, tagsEditTxt;
    private RecyclerView tagsRecyclerView;
    private AdminTagsForDataALLAdapter adminTagsForDataALLAdapter;
    private AdminTagsForDataALLModel adminTagsForDataALLModel;
    private ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList;
    private ImageView uploadImage,btnCross;
    private TextView uploadImageName;
    Button uploadCADetailsBtn;
    RelativeLayout noDataLayout;
    SessionManager sessionManager;
    String authToken;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_create_current_affairs, container, false);

        searchView = view.findViewById(R.id.searchView);

        showAllCARecyclerView = view.findViewById(R.id.showCARecycler);
        showAllCAProgressBar = view.findViewById(R.id.showAllCAProgressBar);

        btnCreateCA = view.findViewById(R.id.btnCreateCA);
        noDataLayout = view.findViewById(R.id.noDataLayout);

        adminShowAllCAModelArrayList = new ArrayList<>();
        showAllCARecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        showAllCAFunction();

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
                if (adminShowAllCAAdapter != null) {
                    adminShowAllCAAdapter.filter(newText);
                }
                return true;
            }
        });

        btnCreateCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateCADialog();
            }
        });

        return view;
    }
    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return (x >= location[0] && x <= (location[0] + view.getWidth()) &&
                y >= location[1] && y <= (location[1] + view.getHeight()));
    }
    private void openKeyboard() {
        searchView.setIconified(false); // Expands the search view
        searchView.requestFocus(); // Requests focus
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT); // Show the keyboard
    }
    public void showAllCAFunction() {
        noDataLayout.setVisibility(View.GONE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, caURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            showAllCARecyclerView.setVisibility(View.VISIBLE);
                            showAllCAProgressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("currentAffairs");
                                adminShowAllCAModelArrayList.clear(); // Clear the list before adding new items

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String caID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String keyword = jsonObject2.getString("keyword");
                                    String content = jsonObject2.getString("content");
                                    String image = jsonObject2.getString("image");
                                    String createdDate = jsonObject2.getString("createdAt");

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

                                    adminShowAllCAModel = new AdminShowAllCAModel(caID, title, keyword, content, tags.toString(), createdDate,image);
                                    adminShowAllCAModelArrayList.add(adminShowAllCAModel);
                                }
                                // Update the original list in the adapter
                                if (adminShowAllCAAdapter != null) {
                                    adminShowAllCAAdapter.updateOriginalList(adminShowAllCAModelArrayList);
                                }
                                // If you have already created the adapter, just notify the change
                                if (adminShowAllCAModelArrayList.isEmpty()) {
                                    showAllCAProgressBar.setVisibility(View.GONE);
                                    noDataLayout.setVisibility(View.VISIBLE);
                                } else {
                                    if (adminShowAllCAAdapter == null) {
                                        adminShowAllCAAdapter = new AdminShowAllCAAdapter(adminShowAllCAModelArrayList, AdminCurrentAffairCreateDeleteFragment.this);
                                        showAllCARecyclerView.setAdapter(adminShowAllCAAdapter);
                                    } else {
                                        adminShowAllCAAdapter.notifyDataSetChanged();
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
    private void openCreateCADialog() {
        createCADialogBox = new Dialog(requireContext());
        createCADialogBox.setContentView(R.layout.admin_create_data_dialog_box);

        TextView headerTxt = createCADialogBox.findViewById(R.id.txtAddData);
        headerTxt.setText("Add Current Affairs");

        titleEditTxt = createCADialogBox.findViewById(R.id.titleEditTxt);
        keywordEditTxt = createCADialogBox.findViewById(R.id.keywordEditText);
        contentEditTxt = createCADialogBox.findViewById(R.id.contentEditText);
        tagsEditTxt = createCADialogBox.findViewById(R.id.tagsEditText);

        tagsRecyclerView = createCADialogBox.findViewById(R.id.tagsRecycler);

        uploadCADetailsBtn = createCADialogBox.findViewById(R.id.btnSubmit);

        btnCross = createCADialogBox.findViewById(R.id.btnCross);
        uploadImage = createCADialogBox.findViewById(R.id.uploadImage);
        uploadImageName = createCADialogBox.findViewById(R.id.txtNoFileChosen);

        adminTagsForDataALLModelArrayList = new ArrayList<>();
        tagsRecyclerView = createCADialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        tagsEditTxt = createCADialogBox.findViewById(R.id.tagsEditText);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openGallery();
            }
        });
        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCADialogBox.dismiss();
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
        uploadCADetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBlogDetails();
            }
        });

        createCADialogBox.show();
        WindowManager.LayoutParams params = createCADialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

// Set the window attributes
        createCADialogBox.getWindow().setAttributes(params);

// Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) createCADialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

// Set top and bottom margins (adjust values as needed)
        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

// Background and animation settings
        createCADialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createCADialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        createCADialogBox.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }
    public void createBlogDetails() {
        String title = titleEditTxt.getText().toString().trim();
        String keyword = keywordEditTxt.getText().toString().trim();
        String content = contentEditTxt.getText().toString().trim();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("keyword", keyword);
            jsonObject.put("content", content);

            // Create a JSONArray for the tags
            JSONArray tagsArray = new JSONArray();
            for (AdminTagsForDataALLModel tag : adminTagsForDataALLModelArrayList) {
                tagsArray.put(tag.getTagName()); // Assuming getTagText() returns the tag's text
            }
            jsonObject.put("tags", tagsArray); // Add the tags array to the JSON object

            jsonObject.put("image", null); // Or whatever your image handling logic is
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, createCAURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                showAllCAFunction();
                                createCADialogBox.dismiss();
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
