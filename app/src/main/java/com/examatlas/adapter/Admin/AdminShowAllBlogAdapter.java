package com.examatlas.adapter.Admin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.adapter.AdminTagsForDataALLAdapter;
import com.examatlas.models.Admin.AdminShowAllCategoryModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MultipartRequest;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.fragment.Admin.AdminCreateBlogsDeleteFragment;
import com.examatlas.models.Admin.AdminShowAllBlogModel;
import com.examatlas.models.AdminTagsForDataALLModel;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminShowAllBlogAdapter extends RecyclerView.Adapter<AdminShowAllBlogAdapter.ViewHolder> {
    private ArrayList<AdminShowAllBlogModel> adminShowAllBlogModelArrayList;
    private ArrayList<AdminShowAllBlogModel> orginalAdminShowAllBlogModelArrayList;
    private Fragment context;
    private String currentQuery = "";
    SessionManager sessionManager;
    String authToken, categoryId;
    File imageFile = null;
    private final String[] threeDotsArray = {"Edit", "Delete"};
    Dialog progressDialog;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AdminShowAllBlogModel currentBlog = adminShowAllBlogModelArrayList.get(position);
        holder.itemView.setTag(currentBlog);

        // Set highlighted text
        holder.setHighlightedText(holder.title, currentBlog.getTitle(), currentQuery);

        String inputDate = currentBlog.getUpdateAt();
        // Remove the 'Z' for the time zone (it represents UTC)
        inputDate = inputDate.replace("Z", "");
        // Define the input format
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // Define the desired output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yy");
        // Format and print the date
        String formattedDate = outputFormat.format(date);
        holder.setHighlightedText(holder.updatedAtTxt, formattedDate, currentQuery);

        holder.setHighlightedText(holder.categoryTxt, currentBlog.getCategoryName(), currentQuery);

        holder.editBlogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showThreeDotsOptions(currentBlog, position);
            }
        });
        String titleStr = currentBlog.getTitle();
        holder.setHighlightedText(holder.title, titleStr, currentQuery);

        holder.setHighlightedText(holder.categoryTxt,currentBlog.getCategoryName(),currentQuery);

        // Post a Runnable to modify the title text based on the number of lines
        holder.title.post(new Runnable() {
            @Override
            public void run() {
                // Get the Layout object from the TextView to measure lines
                Layout layout = holder.title.getLayout();

                // Check the number of lines
                int lineCount = layout.getLineCount();

                // Perform truncation only if the line count exceeds 3
                if (lineCount > 3) {
                    String firstThreeLinesText = "";

                    // Get the start and end positions for the first three lines
                    for (int i = 0; i < 3; i++) {
                        int startPos = layout.getLineStart(i);
                        int endPos = layout.getLineEnd(i);
                        firstThreeLinesText += holder.title.getText().subSequence(startPos, endPos);

                        // Add a space after each line, except for the last one
                        if (i < 2) {
                            firstThreeLinesText += " ";
                        }
                    }

                    // Truncate the last three characters and add "..."
                    if (firstThreeLinesText.length() > 3) {
                        firstThreeLinesText = firstThreeLinesText.substring(0, firstThreeLinesText.length() - 3) + "...";
                    }

                    // Set the text to the title with the first three lines and ellipsis
                    holder.title.setText(firstThreeLinesText);
                    holder.setHighlightedText(holder.title, firstThreeLinesText, currentQuery);

                } else {
                    // If there are 3 or fewer lines, just set the text normally
                    holder.setHighlightedText(holder.title, titleStr, currentQuery);
                }
            }
        });
        // Load the image using Glide
        Glide.with(context)
                .load(currentBlog.getImageURL())
                .error(R.drawable.noimage)
                .into(holder.blogImage);
    }
    private void showThreeDotsOptions(AdminShowAllBlogModel currentCategory, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
        builder.setTitle(null)
                .setItems(threeDotsArray, (dialog, which) -> {
                    String selectedItems = threeDotsArray[which];
                    choseItems(currentCategory, selectedItems, position);
                });
        builder.create().show();
    }
    private void choseItems(AdminShowAllBlogModel currentCategory, String selectedItems, int position) {
        if (selectedItems.equals("Edit")) {
            openEditBlogDialog(position,currentCategory);
        } else if (selectedItems.equals("Delete")) {
            quitDialog(position);
        }
    }
    @Override
    public int getItemCount() {
        return adminShowAllBlogModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, updatedAtTxt,categoryTxt;
        ImageView blogImage,editBlogBtn, deleteBlogBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtBlogTitle);
            categoryTxt = itemView.findViewById(R.id.txtCategory);
            updatedAtTxt = itemView.findViewById(R.id.tagTxt);
            blogImage = itemView.findViewById(R.id.imgBlog);
            editBlogBtn = itemView.findViewById(R.id.editBlogBtn);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context.getActivity(), AdminBlogSingleViewActivity.class);
//                    intent.putExtra("blogID",adminShowAllBlogModelArrayList.get(getAdapterPosition()).getBlogID());
//                    context.startActivity(intent);
//                }
//            });
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
                        blog.getContent().toLowerCase().contains(lowerCaseQuery)) {
                    adminShowAllBlogModelArrayList.add(blog); // Add matching blog to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }
    ImageView blogImgView;
    private void openEditBlogDialog(int position,AdminShowAllBlogModel blogModel) {
        Dialog editBlogDialogBox = new Dialog(context.requireContext());
        editBlogDialogBox.setContentView(R.layout.admin_create_blog_dialog_box);

        Spinner categorySpinner;
        ArrayList<AdminShowAllCategoryModel> categoryModelArrayList;

        categorySpinner = editBlogDialogBox.findViewById(R.id.categorySpinner);

        ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList = new ArrayList<>();
        AdminTagsForDataALLAdapter adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        RecyclerView tagsRecyclerView = editBlogDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setVisibility(View.VISIBLE);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(context.requireContext(), 2));
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        TextView headerTxt = editBlogDialogBox.findViewById(R.id.txtAddData);
        headerTxt.setText("Edit Blog");
        TextView blogImgTxt = editBlogDialogBox.findViewById(R.id.txtNoFileChosen);
        EditText titleEditTxt = editBlogDialogBox.findViewById(R.id.titleEditTxt);
        EditText keywordEditTxt = editBlogDialogBox.findViewById(R.id.keywordEditText);
        EditText contentEditTxt = editBlogDialogBox.findViewById(R.id.contentEditText);
        EditText tagsEditTxt = editBlogDialogBox.findViewById(R.id.tagsEditText);
        EditText slugEditTxt = editBlogDialogBox.findViewById(R.id.slugEditText);
        blogImgView = editBlogDialogBox.findViewById(R.id.uploadImage);

        String blogImgURL = blogModel.getImageURL();
        String blogImgURLSplit = blogImgURL.substring(blogImgURL.lastIndexOf("/"));
        blogImgTxt.setText(blogImgURLSplit);
        titleEditTxt.setText(blogModel.getTitle());
        contentEditTxt.setText(blogModel.getContent());
        keywordEditTxt.setText(blogModel.getKeyword());
        slugEditTxt.setText(blogModel.getSlug());
        Glide.with(context)
                .load(blogModel.getImageURL())
                .error(R.drawable.noimage)
                .into(blogImgView);

        blogImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AdminCreateBlogsDeleteFragment) context).openGallery();
            }
        });

        String[] tagsArray = blogModel.getTags().split(",");
        for (String tag : tagsArray) {
            adminTagsForDataALLModelArrayList.add(new AdminTagsForDataALLModel(tag.trim()));
        }
        adminTagsForDataALLAdapter.notifyDataSetChanged();

        ((AdminCreateBlogsDeleteFragment) context).setupCategorySpinner(categorySpinner,titleEditTxt,keywordEditTxt,slugEditTxt,contentEditTxt,tagsEditTxt,blogModel);

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
            imageFile = ((AdminCreateBlogsDeleteFragment) context).getImageFile();
            sendingBlogDetails(blogModel.getBlogID(),
                    titleEditTxt.getText().toString().trim(),
                    keywordEditTxt.getText().toString().trim(),
                    slugEditTxt.getText().toString().trim(),
                    contentEditTxt.getText().toString().trim(),
                    adminTagsForDataALLModelArrayList,position);
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
    public void setCategoryImage(Uri uri, Bitmap imageFile){
        if (blogImgView != null) {
            if (uri != null) {
                Glide.with(context)
                        .load(uri)
                        .into(blogImgView);
            }
            if (imageFile != null) {
                Glide.with(context)
                        .load(imageFile)
                        .into(blogImgView);
            }
        }
    }
    private void sendingBlogDetails(String blogId, String title, String keyword, String slug, String content,
                                             ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList, int position) {
        String updateURL = Constant.BASE_URL + "v1/blog";
        categoryId = ((AdminCreateBlogsDeleteFragment) context).getCategoryName();

        // Prepare form data
        Map<String, String> params = new HashMap<>();
        params.put("id", blogId);
        params.put("title", title);
        params.put("slug", slug);
        params.put("keyword", keyword);
        params.put("content", content);
        params.put("categoryId", categoryId);
        params.put("type", "blog");

        // Convert tags to a JSONArray and add to the params
        JSONArray tagsArray = new JSONArray();
        for (AdminTagsForDataALLModel tag : adminTagsForDataALLModelArrayList) {
            tagsArray.put(tag.getTagName()); // Assuming `getTag()` returns the tag string
        }
        params.put("tags", tagsArray.toString());

        // Create a Map for files
        Map<String, File> files = new HashMap<>();
        if (imageFile != null && imageFile.exists()) {
            files.put("image", imageFile); // Add the image file if exists
        }

        // Create and send the multipart request
        MultipartRequest multipartRequest = new MultipartRequest(updateURL, params, files,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            boolean status = responseObject.getBoolean("success");
                            if (status) {
                                Toast.makeText(context.getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();

                                JSONObject dataObj = responseObject.getJSONObject("data");
                                String title = dataObj.getString("title");
                                String slug = dataObj.getString("slug");
                                String content = dataObj.getString("content");
                                String keyword = dataObj.getString("keyword");
                                String categoryId = dataObj.getString("categoryId");
                                String imageUrl = dataObj.getJSONObject("image").getString("url");
                                StringBuilder tags = new StringBuilder();
                                JSONArray jsonArray1 = dataObj.getJSONArray("tags");
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    String singleTag = jsonArray1.getString(j);
                                    tags.append(singleTag).append(", ");
                                }
//                                     Remove trailing comma and space if any
                                if (tags.length() > 0) {
                                    tags.setLength(tags.length() - 2);
                                }
                                adminShowAllBlogModelArrayList.get(position).setTitle(title);
                                adminShowAllBlogModelArrayList.get(position).setSlug(slug);
                                adminShowAllBlogModelArrayList.get(position).setContent(content);
                                adminShowAllBlogModelArrayList.get(position).setKeyword(keyword);
                                adminShowAllBlogModelArrayList.get(position).setCategoryId(categoryId);
                                adminShowAllBlogModelArrayList.get(position).setTags(tags.toString());
                                adminShowAllBlogModelArrayList.get(position).setImageURL(imageUrl);

                                notifyItemChanged(position);  // Update the UI
                            } else {
                                Toast.makeText(context.getContext(), "Failed to update blog", Toast.LENGTH_SHORT).show();
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
                }, authToken);

        // Add the request to the queue
        MySingletonFragment.getInstance(context).addToRequestQueue(multipartRequest);
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
        progressDialog = new Dialog(context.getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_bar_drawer);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressDialog.show();
        String deleteURL = Constant.BASE_URL + "v1/blog/delete/" + adminShowAllBlogModelArrayList.get(position).getBlogID();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(context.getContext(), "Blog Deleted Successfully", Toast.LENGTH_SHORT).show();
                                adminShowAllBlogModelArrayList.remove(position);
                                notifyItemRemoved(position);
                                progressDialog.dismiss();
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
                progressDialog.dismiss();
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
