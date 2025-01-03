package com.examatlas.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.fragment.AdminEBooksCreateDeleteFragment;
import com.examatlas.models.AdminShowAllEBooksModel;
import com.examatlas.models.AdminTagsForDataALLModel;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AdminShowAllEBookAdapter extends RecyclerView.Adapter<AdminShowAllEBookAdapter.ViewHolder> {
    private ArrayList<AdminShowAllEBooksModel> ebookModelArrayList;
    private ArrayList<AdminShowAllEBooksModel> originalEbookModelArrayList;
    private Fragment context;
    private String currentQuery = "";
    SessionManager sessionManager;
    String authToken;

    public AdminShowAllEBookAdapter(ArrayList<AdminShowAllEBooksModel> ebookModelArrayList, Fragment context) {
        this.originalEbookModelArrayList = new ArrayList<>(ebookModelArrayList);
        this.ebookModelArrayList = new ArrayList<>(originalEbookModelArrayList);
        this.context = context;
        sessionManager = new SessionManager(context.getContext());
        authToken = sessionManager.getUserData().get("authToken");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_custom_ebook_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Access the last item first
            AdminShowAllEBooksModel currentEbook = ebookModelArrayList.get(position);
            holder.itemView.setTag(currentEbook);

        holder.setHighlightedText(holder.title, currentEbook.getTitle(), currentQuery);
        holder.setHighlightedText(holder.content, currentEbook.getContent(), currentQuery);
        holder.setHighlightedText(holder.tags, currentEbook.getTags(), currentQuery);
        holder.setHighlightedText(holder.author, "By: " + currentEbook.getAuthor(), currentQuery);

        holder.editEbookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditDialog(currentEbook);
            }
        });
        holder.deleteEbookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitDialog(position);
            }
        });

    }

    private void quitDialog(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context.requireContext());
        builder.setTitle("Delete E-Book")
                .setMessage("Are you sure you want to delete this e-book?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> deleteEBook(position))
                .show();
    }
    private void deleteEBook(int position) {
        String deleteURL = Constant.BASE_URL + "book/deleteBook/" + ebookModelArrayList.get(position).getId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(context.getContext(), "E-Book Deleted Successfully", Toast.LENGTH_SHORT).show();
                                ebookModelArrayList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, ebookModelArrayList.size());
                                ((AdminEBooksCreateDeleteFragment) context).showAllEbooksFunction();
                            } else {
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
                Toast.makeText(context.getContext(), errorMessage, Toast.LENGTH_LONG).show();
//                Log.e("BlogFetchError", errorMessage);
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
        MySingletonFragment.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    private void openEditDialog(AdminShowAllEBooksModel currentEbook) {
        EditText titleEditTxt, keywordEditTxt, contentEditTxt, categoryEditTxt, authorEditTxt, priceEditTxt, tagsEditTxt;
        Button submitEBookDetailsBtn;
        RecyclerView tagsRecyclerView;
        ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList = new ArrayList<>();
        ImageView btnCross;
        Dialog createEBookDialogBox = new Dialog(context.getContext());
        createEBookDialogBox.setContentView(R.layout.admin_create_ebook_dialog_box);

        titleEditTxt = createEBookDialogBox.findViewById(R.id.titleEditTxt);
        titleEditTxt.setText(currentEbook.getTitle());
        keywordEditTxt = createEBookDialogBox.findViewById(R.id.keywordEditText);
        keywordEditTxt.setText(currentEbook.getKeyword());
        contentEditTxt = createEBookDialogBox.findViewById(R.id.contentEditText);
        contentEditTxt.setText(currentEbook.getContent());
        tagsEditTxt = createEBookDialogBox.findViewById(R.id.tagsEditText);

        // Get the tags from the currentEbook and split them
        String tagsAll = currentEbook.getTags();
        if (tagsAll != null && !tagsAll.isEmpty()) {
            String[] tagsArray = tagsAll.split(","); // Split the string into an array

            // Trim whitespace and add to the ArrayList
            for (String tag : tagsArray) {
                adminTagsForDataALLModelArrayList.add(new AdminTagsForDataALLModel(tag.trim())); // Create model and trim whitespace
            }
        }

        authorEditTxt = createEBookDialogBox.findViewById(R.id.authorEditText);
        authorEditTxt.setText(currentEbook.getAuthor());
        priceEditTxt = createEBookDialogBox.findViewById(R.id.priceEditText);
        priceEditTxt.setText(currentEbook.getPrice());
        categoryEditTxt = createEBookDialogBox.findViewById(R.id.categoryEditText);
        categoryEditTxt.setText(currentEbook.getCategory());

        tagsRecyclerView = createEBookDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(context.getContext(), 2));

        // Initialize the adapter here
        AdminTagsForDataALLAdapter adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);
        tagsRecyclerView.setVisibility(View.VISIBLE);

        submitEBookDetailsBtn = createEBookDialogBox.findViewById(R.id.btnSubmit);
        btnCross = createEBookDialogBox.findViewById(R.id.btnCross);

        btnCross.setOnClickListener(view -> createEBookDialogBox.dismiss());

        tagsEditTxt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String tagText = tagsEditTxt.getText().toString().trim();
                if (!tagText.isEmpty()) {
                    adminTagsForDataALLModelArrayList.add(new AdminTagsForDataALLModel(tagText));
                    adminTagsForDataALLAdapter.notifyItemInserted(adminTagsForDataALLModelArrayList.size() - 1);
                    tagsEditTxt.setText(""); // Clear the EditText
                }
                return true; // Indicate that we've handled the event
            }
            return false; // Pass the event on
        });

        submitEBookDetailsBtn.setOnClickListener(view -> {
             submitEBookDetails(currentEbook.getId(),
                    titleEditTxt.getText().toString().trim(),
                    keywordEditTxt.getText().toString().trim(),
                    contentEditTxt.getText().toString().trim(),
                    authorEditTxt.getText().toString().trim(),
                    priceEditTxt.getText().toString().trim(),
                    categoryEditTxt.getText().toString().trim(),
                     adminTagsForDataALLModelArrayList);

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



    private void submitEBookDetails(String id, String title, String keyword, String content, String author, String price, String category, ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList) {
        String updateURL = Constant.BASE_URL + "book/updateBook/" + id;

        JSONObject ebookDetails = new JSONObject();
        try {
            ebookDetails.put("title", title);
            ebookDetails.put("keyword", keyword);
            ebookDetails.put("content", content);
            ebookDetails.put("author", author);
            ebookDetails.put("category", category);
            ebookDetails.put("price", price);
            // Convert tags to a JSONArray
            JSONArray tagsArray = new JSONArray();
            for (AdminTagsForDataALLModel tag : adminTagsForDataALLModelArrayList) {
                tagsArray.put(tag.getTagName()); // Assuming `getTag()` returns the tag string
            }
            ebookDetails.put("tags", tagsArray);

        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error creating JSON object: " + e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, updateURL, ebookDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(context.getContext(), "E-Book Updated Successfully", Toast.LENGTH_SHORT).show();
                                ((AdminEBooksCreateDeleteFragment) context).showAllEbooksFunction();
                            } else {
                                Toast.makeText(context.getContext(), "Failed to update E-Book", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON response: " + e.getMessage());
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
                Toast.makeText(context.getContext(), errorMessage, Toast.LENGTH_LONG).show();
//                Log.e("CurrentAffairsUpdateError", errorMessage);
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

        MySingletonFragment.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return ebookModelArrayList.size();
    }

    public void filter(String query) {
        currentQuery = query; // Store current query
        ebookModelArrayList.clear();
        if (query.isEmpty()) {
            ebookModelArrayList.addAll(originalEbookModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AdminShowAllEBooksModel eBooksModel : originalEbookModelArrayList) {
                if (eBooksModel.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        eBooksModel.getContent().toLowerCase().contains(lowerCaseQuery) ||
                        eBooksModel.getTags().toLowerCase().contains(lowerCaseQuery) ||
                        eBooksModel.getPrice().toLowerCase().contains(lowerCaseQuery)) {
                    ebookModelArrayList.add(eBooksModel); // Add matching eBook to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, tags, author;
        ImageView editEbookBtn,deleteEbookBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtBookTitle);
            content = itemView.findViewById(R.id.txtContent);
            tags = itemView.findViewById(R.id.txtTags);
            author = itemView.findViewById(R.id.txtAuthor);
            editEbookBtn = itemView.findViewById(R.id.editEbookBtn);
            deleteEbookBtn = itemView.findViewById(R.id.deleteEbookBtn);
        }

        public void setHighlightedText(TextView textView, String text, String query) {
            if (query == null || query.isEmpty()) {
                textView.setText(text);
                return;
            }
            SpannableString spannableString = new SpannableString(text);
            int startIndex = text.toLowerCase().indexOf(query.toLowerCase());
            while (startIndex >= 0) {
                int endIndex = startIndex + query.length();
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = text.toLowerCase().indexOf(query.toLowerCase(), endIndex);
            }
            textView.setText(spannableString);
        }
    }

    public void updateOriginalList(ArrayList<AdminShowAllEBooksModel> newList) {
        originalEbookModelArrayList.clear();
        originalEbookModelArrayList.addAll(newList);
    }
}
