package com.examatlas.adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
import com.examatlas.fragment.AdminBlogCreateDeleteFragment;
import com.examatlas.models.AdminShowAllBlogModel;
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

public class AdminShowAllBlogAdapter extends RecyclerView.Adapter<AdminShowAllBlogAdapter.ViewHolder> {
    private ArrayList<AdminShowAllBlogModel> adminShowAllBlogModelArrayList;
    private ArrayList<AdminShowAllBlogModel> orginalAdminShowAllBlogModelArrayList;
    private Fragment context;
    private String currentQuery = "";
    SessionManager sessionManager;
    String authToken;

    public AdminShowAllBlogAdapter(ArrayList<AdminShowAllBlogModel> adminShowAllBlogModelArrayList, Fragment context) {
        this.adminShowAllBlogModelArrayList = adminShowAllBlogModelArrayList;
        this.context = context;
        this.orginalAdminShowAllBlogModelArrayList = new ArrayList<>(adminShowAllBlogModelArrayList);
        sessionManager = new SessionManager(context.getContext());
        authToken = sessionManager.getUserData().get("authToken");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_custom_blog_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminShowAllBlogModel currentBlog = adminShowAllBlogModelArrayList.get(position);
        holder.itemView.setTag(currentBlog);

        // Set highlighted text
        holder.setHighlightedText(holder.title, currentBlog.getTitle(), currentQuery);
        holder.setHighlightedText(holder.keyword, currentBlog.getKeyword(), currentQuery);
        holder.setHighlightedText(holder.tags, currentBlog.getTags(), currentQuery);

        holder.editBlogBtn.setOnClickListener(view -> openEditBlogDialog(currentBlog));
        holder.deleteBlogBtn.setOnClickListener(view -> quitDialog(position));

        // Enable JavaScript (optional, depending on your content)
        WebSettings webSettings = holder.content.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String htmlContentTxt = adminShowAllBlogModelArrayList.get(position).getContent();

        // Inject CSS to control the image size
        String injectedCss = "<style>"
                + "p { font-size: 20px; }" // Increase text size only for <p> tags (paragraphs)
                + "img { width: 100%; height: auto; }" // Adjust image size as needed
                + "</style>";
        String fullHtmlContent = injectedCss + htmlContentTxt;

        // Disable scrolling and over-scrolling
        holder.content.setVerticalScrollBarEnabled(false);  // Disable vertical scroll bar
        holder.content.setOverScrollMode(WebView.OVER_SCROLL_NEVER); // Disable over-scrolling effect

        // Load the modified HTML content
        holder.content.loadData(fullHtmlContent, "text/html", "UTF-8");
    }

    @Override
    public int getItemCount() {
        return adminShowAllBlogModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, keyword, tags;
        ImageView editBlogBtn, deleteBlogBtn;
        WebView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtBlogTitle);
            keyword = itemView.findViewById(R.id.txtBlogKeyword);
            content = itemView.findViewById(R.id.content);
            tags = itemView.findViewById(R.id.tagTxt);
            editBlogBtn = itemView.findViewById(R.id.editBlogBtn);
            deleteBlogBtn = itemView.findViewById(R.id.deleteBlogBtn);
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
                spannableString.setSpan(new android.text.style.BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = text.toLowerCase().indexOf(query.toLowerCase(), endIndex);
            }
            textView.setText(spannableString);
        }
    }

    public void filter(String query) {
        currentQuery = query; // Store current query
        adminShowAllBlogModelArrayList.clear();
        if (query.isEmpty()) {
            adminShowAllBlogModelArrayList.addAll(orginalAdminShowAllBlogModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AdminShowAllBlogModel blog : orginalAdminShowAllBlogModelArrayList) {
                if (blog.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        blog.getKeyword().toLowerCase().contains(lowerCaseQuery) ||
                        blog.getContent().toLowerCase().contains(lowerCaseQuery)) {
                    adminShowAllBlogModelArrayList.add(blog); // Add matching blog to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }

    private void openEditBlogDialog(AdminShowAllBlogModel blogModel) {
        Dialog editBlogDialogBox = new Dialog(context.requireContext());
        editBlogDialogBox.setContentView(R.layout.admin_create_data_dialog_box);

        ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList = new ArrayList<>();
        AdminTagsForDataALLAdapter adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        RecyclerView tagsRecyclerView = editBlogDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setVisibility(View.VISIBLE);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(context.requireContext(), 2));
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        TextView headerTxt = editBlogDialogBox.findViewById(R.id.txtAddData);
        headerTxt.setText("Edit Blog");
        EditText titleEditTxt = editBlogDialogBox.findViewById(R.id.titleEditTxt);
        EditText keywordEditTxt = editBlogDialogBox.findViewById(R.id.keywordEditText);
        EditText contentEditTxt = editBlogDialogBox.findViewById(R.id.contentEditText);
        EditText tagsEditTxt = editBlogDialogBox.findViewById(R.id.tagsEditText);

        titleEditTxt.setText(blogModel.getTitle());
        keywordEditTxt.setText(blogModel.getKeyword());
        contentEditTxt.setText(blogModel.getContent());

        String[] tagsArray = blogModel.getTags().split(",");
        for (String tag : tagsArray) {
            adminTagsForDataALLModelArrayList.add(new AdminTagsForDataALLModel(tag.trim()));
        }
        adminTagsForDataALLAdapter.notifyDataSetChanged();

        Button uploadBlogDetailsBtn = editBlogDialogBox.findViewById(R.id.btnSubmit);
        uploadBlogDetailsBtn.setClickable(true);

        tagsEditTxt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String tagText = tagsEditTxt.getText().toString().trim();
                if (!tagText.isEmpty()) {
                    adminTagsForDataALLModelArrayList.add(new AdminTagsForDataALLModel(tagText));
                    adminTagsForDataALLAdapter.notifyItemInserted(adminTagsForDataALLModelArrayList.size() - 1);
                    tagsEditTxt.setText("");
                    tagsRecyclerView.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        });

        uploadBlogDetailsBtn.setOnClickListener(view -> {
            uploadBlogDetailsBtn.setClickable(false);
            sendingBlogDetails(blogModel.getBlogID(),
                    titleEditTxt.getText().toString().trim(),
                    keywordEditTxt.getText().toString().trim(),
                    contentEditTxt.getText().toString().trim(),
                    adminTagsForDataALLModelArrayList);
            editBlogDialogBox.dismiss();
        });

        ImageView btnCross = editBlogDialogBox.findViewById(R.id.btnCross);
        btnCross.setOnClickListener(view -> editBlogDialogBox.dismiss());

        editBlogDialogBox.show();
        WindowManager.LayoutParams params = editBlogDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

        // Set the window attributes
        editBlogDialogBox.getWindow().setAttributes(params);

        // Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) editBlogDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

        // Background and animation settings
        editBlogDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editBlogDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void sendingBlogDetails(String blogId, String title, String keyword, String content, ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList) {
        String updateURL = Constant.BASE_URL + "blog/updateBlog/" + blogId;

        // Create JSON object to send in the request
        JSONObject blogDetails = new JSONObject();
        try {
            blogDetails.put("title", title);
            blogDetails.put("keyword", keyword);
            blogDetails.put("content", content);

            // Convert tags to a JSONArray
            JSONArray tagsArray = new JSONArray();
            for (AdminTagsForDataALLModel tag : adminTagsForDataALLModelArrayList) {
                tagsArray.put(tag.getTagName()); // Assuming `getTag()` returns the tag string
            }
            blogDetails.put("tags", tagsArray);

        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error creating JSON object: " + e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, updateURL, blogDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(context.getContext(), "Blog Updated Successfully", Toast.LENGTH_SHORT).show();
                                ((AdminBlogCreateDeleteFragment) context).showAllBlogFunction();
                            } else {
                                Toast.makeText(context.getContext(), "Failed to update blog", Toast.LENGTH_SHORT).show();
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
                Log.e("BlogUpdateError", errorMessage);
            }
        }){
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


    private void quitDialog(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context.requireContext());
        builder.setTitle("Delete Blog")
                .setMessage("Are you sure you want to delete this blog?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> deleteBlog(position))
                .show();
    }

    private void deleteBlog(int position) {
        String deleteURL = Constant.BASE_URL + "blog/deleteBlog/" + adminShowAllBlogModelArrayList.get(position).getBlogID();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(context.getContext(), "Blog Deleted Successfully", Toast.LENGTH_SHORT).show();
                                adminShowAllBlogModelArrayList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, adminShowAllBlogModelArrayList.size());
                                ((AdminBlogCreateDeleteFragment) context).showAllBlogFunction();
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
        MySingletonFragment.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    public void updateOriginalList(ArrayList<AdminShowAllBlogModel> newList) {
        orginalAdminShowAllBlogModelArrayList.clear();
        orginalAdminShowAllBlogModelArrayList.addAll(newList);
    }
}
