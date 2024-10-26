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
import com.examatlas.adapter.AdminShowAllEBookAdapter;
import com.examatlas.adapter.AdminTagsForDataALLAdapter;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.models.AdminShowAllEBooksModel;
import com.examatlas.models.AdminTagsForDataALLModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminEBooksCreateDeleteFragment extends Fragment {
    Button createBtn,searchBtn;
    SearchView searchView;
    RecyclerView showEbookRecycler;
    RelativeLayout noDataLayout;
    ProgressBar progressBar;
    AdminShowAllEBookAdapter ebookAdapter;
    ArrayList<AdminShowAllEBooksModel> ebookModelArrayList = new ArrayList<>();
    private final String ebookURL = Constant.BASE_URL + "book/getAllBooks";
    private final String createEBookURL = Constant.BASE_URL + "book/createBook";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_create_ebook, container, false);

        createBtn = view.findViewById(R.id.btnCreate);
        searchBtn = view.findViewById(R.id.btnSearch);

        searchView = view.findViewById(R.id.searchView);
        showEbookRecycler = view.findViewById(R.id.showEBookRecycler);
        noDataLayout = view.findViewById(R.id.noDataLayout);

        progressBar = view.findViewById(R.id.showAllEbookProgressBar);

        showEbookRecycler.setLayoutManager(new GridLayoutManager(getContext(),2));

        noDataLayout.setVisibility(View.GONE);

        showAllEbooksFunction();

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
                    // If the searchView is shown and the touch is outside of it
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
                if (ebookAdapter != null) {
                    ebookAdapter.filter(newText);
                }
                return true;
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEBookDialog();
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
    public void showAllEbooksFunction() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ebookURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            showEbookRecycler.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("books");
                                ebookModelArrayList.clear(); // Clear the list before adding new items

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String ebookID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String keyword = jsonObject2.getString("keyword");
                                    String price = jsonObject2.getString("price");
                                    String content = jsonObject2.getString("content");
                                    String author = jsonObject2.getString("author");
                                    String category = jsonObject2.getString("category");
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

                                    AdminShowAllEBooksModel ebookModel = new AdminShowAllEBooksModel(ebookID, title, keyword, content,category,author, price ,tags.toString(),createdDate);
                                    ebookModelArrayList.add(ebookModel);
                                }
                                // Update the original list in the adapter
                                if (ebookAdapter != null) {
                                    ebookAdapter.updateOriginalList(ebookModelArrayList);
                                }
                                // If you have already created the adapter, just notify the change
                                if (ebookModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (ebookAdapter == null) {
                                        ebookAdapter = new AdminShowAllEBookAdapter(ebookModelArrayList, AdminEBooksCreateDeleteFragment.this);
                                        showEbookRecycler.setAdapter(ebookAdapter);
                                    } else {
                                        ebookAdapter.notifyDataSetChanged();
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
                return headers;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    private Dialog createEBookDialogBox;
    private EditText titleEditTxt,keywordEditTxt,contentEditTxt,categoryEditTxt,authorEditTxt, priceEditTxt,tagsEditTxt;
    Button submitEBookDetailsBtn;
    private RecyclerView tagsRecyclerView;
    private AdminTagsForDataALLAdapter adminTagsForDataALLAdapter;
    private AdminTagsForDataALLModel adminTagsForDataALLModel;
    private ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList;
    private ImageView btnCross;
    private void createEBookDialog() {
        // Create and show the dialog
        createEBookDialogBox = new Dialog(requireContext());
        createEBookDialogBox.setContentView(R.layout.admin_create_ebook_dialog_box);

        titleEditTxt = createEBookDialogBox.findViewById(R.id.titleEditTxt);
        keywordEditTxt = createEBookDialogBox.findViewById(R.id.keywordEditText);
        contentEditTxt = createEBookDialogBox.findViewById(R.id.contentEditText);
        tagsEditTxt = createEBookDialogBox.findViewById(R.id.tagsEditText);
        authorEditTxt = createEBookDialogBox.findViewById(R.id.authorEditText);
        priceEditTxt = createEBookDialogBox.findViewById(R.id.priceEditText);
        categoryEditTxt = createEBookDialogBox.findViewById(R.id.categoryEditText);

        tagsRecyclerView = createEBookDialogBox.findViewById(R.id.tagsRecycler);

        submitEBookDetailsBtn = createEBookDialogBox.findViewById(R.id.btnSubmit);
        btnCross = createEBookDialogBox.findViewById(R.id.btnCross);

        adminTagsForDataALLModelArrayList = new ArrayList<>();
        tagsRecyclerView = createEBookDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEBookDialogBox.dismiss();
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
        submitEBookDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEBookDetails();
            }
        });
        createEBookDialogBox.show();
        WindowManager.LayoutParams params = createEBookDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

// Set the window attributes
        createEBookDialogBox.getWindow().setAttributes(params);

// Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) createEBookDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

// Set top and bottom margins (adjust values as needed)
        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

// Background and animation settings
        createEBookDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createEBookDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        createEBookDialogBox.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }

    private void createEBookDetails() {
        String title = titleEditTxt.getText().toString().trim();
        String keyword = keywordEditTxt.getText().toString().trim();
        String content = contentEditTxt.getText().toString().trim();
        String category = categoryEditTxt.getText().toString().trim();
        String author = authorEditTxt.getText().toString().trim();
        String price = priceEditTxt.getText().toString().trim();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("keyword", keyword);
            jsonObject.put("content", content);
            jsonObject.put("category", category);
            jsonObject.put("author", author);
            jsonObject.put("price", price);

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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, createEBookURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                showAllEbooksFunction();
                                createEBookDialogBox.dismiss();
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
                return headers;
            }
        };

        MySingletonFragment.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
