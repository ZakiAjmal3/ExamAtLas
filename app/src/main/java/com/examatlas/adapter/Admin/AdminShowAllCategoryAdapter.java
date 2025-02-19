package com.examatlas.adapter.Admin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.fragment.Admin.AdminCreateCategoryFragment;
import com.examatlas.models.Admin.AdminShowAllCategoryModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MultipartRequest;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminShowAllCategoryAdapter extends RecyclerView.Adapter<AdminShowAllCategoryAdapter.ViewHolder> {
    private ArrayList<AdminShowAllCategoryModel> categoryModelArrayList;
    private ArrayList<AdminShowAllCategoryModel> originalCategoryModelArrayList;
    private Fragment context;
    private String currentQuery = "";
    SessionManager sessionManager;
    String authToken;
    File imageFile = null;
    private final String[] threeDotsArray = {"Edit", "Delete"};
    Dialog progressDialog;
    public AdminShowAllCategoryAdapter(ArrayList<AdminShowAllCategoryModel> categoryModelArrayList, Fragment context) {
        this.originalCategoryModelArrayList = new ArrayList<>(categoryModelArrayList);
        this.categoryModelArrayList = new ArrayList<>(originalCategoryModelArrayList);
        this.context = context;
        sessionManager = new SessionManager(context.getActivity());
        authToken = sessionManager.getUserData().get("authToken");
    }
    @NonNull
    @Override
    public AdminShowAllCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_category_subcat_item_layout, parent, false);
        return new AdminShowAllCategoryAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AdminShowAllCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AdminShowAllCategoryModel currentCategory = categoryModelArrayList.get(position);
        holder.itemView.setTag(currentCategory);

        holder.categoryTxt.setVisibility(View.GONE);
        holder.setHighlightedText(holder.title, currentCategory.getCategoryName(), currentQuery);

        String imageUrl = categoryModelArrayList.get(position).getImageUrl();
        Glide.with(context.getContext())
                .load(imageUrl)
                .error(R.drawable.image2)
                .into(holder.categoryImage);

        String inputDate = currentCategory.getUpdatedAt();
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

        holder.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showThreeDotsOptions(currentCategory, position);
            }
        });
    }

    private void showThreeDotsOptions(AdminShowAllCategoryModel currentCategory, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
        builder.setTitle(null)
                .setItems(threeDotsArray, (dialog, which) -> {
                    String selectedItems = threeDotsArray[which];
                    choseItems(currentCategory, selectedItems, position);
                });
        builder.create().show();
    }
    private void choseItems(AdminShowAllCategoryModel currentCategory, String selectedItems, int position) {
        if (selectedItems.equals("Edit")) {
            openEditCategoryDialogBox(currentCategory,position);
        } else if (selectedItems.equals("Delete")) {
            new MaterialAlertDialogBuilder(context.getContext())
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to delete this category?")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressDialog = new Dialog(context.getContext());
                            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            progressDialog.setContentView(R.layout.progress_bar_drawer);
                            progressDialog.setCancelable(false);
                            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                            progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                            progressDialog.show();
                            deleteCategory(categoryModelArrayList.get(position),position);
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(context.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
    }
    private void deleteCategory(AdminShowAllCategoryModel adminShowAllCategoryModel, int position) {
        String id = adminShowAllCategoryModel.getId();
        String deleteURL = Constant.BASE_URL + "v1/category/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(context.getContext(), message, Toast.LENGTH_SHORT).show();
                                categoryModelArrayList.remove(adminShowAllCategoryModel);
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
                        progressDialog.dismiss();
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
    ImageView categoryImgView;
    private void openEditCategoryDialogBox(AdminShowAllCategoryModel currentCategory, int position) {
        Dialog dialog = new Dialog(context.getContext());
        dialog.setContentView(R.layout.admin_create_category_dialog_box);

        EditText titleEditTxt = dialog.findViewById(R.id.titleEditTxt);
        titleEditTxt.setText(currentCategory.getCategoryName());
        EditText slugEditText = dialog.findViewById(R.id.slugEditText);
        slugEditText.setText(currentCategory.getSlug());
        categoryImgView = dialog.findViewById(R.id.uploadImage);

        Glide.with(context)
                .load(categoryModelArrayList.get(position).getImageUrl())
                .error(R.drawable.noimage)
                .into(categoryImgView);

        categoryImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AdminCreateCategoryFragment)context).openGallery();
            }
        });

        Button submitBtn = dialog.findViewById(R.id.btnSubmit);
        ImageView crossBtn = dialog.findViewById(R.id.btnCross);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new Dialog(context.getContext());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                imageFile = ((AdminCreateCategoryFragment) context).getImageFile();
                sendUpdateSubjectDetails(position,currentCategory,titleEditTxt.getText().toString().trim(),slugEditText.getText().toString().trim(),dialog);
            }
        });
        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

// Set the window attributes
        dialog.getWindow().setAttributes(params);

// Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) dialog.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

// Set top and bottom margins (adjust values as needed)
        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

// Background and animation settings
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }

    public void setCategoryImage(Uri uri, Bitmap imageFile){
        if (categoryImgView != null) {
            if (uri != null) {
                Glide.with(context)
                        .load(uri)
                        .into(categoryImgView);
            }
            if (imageFile != null) {
                Glide.with(context)
                        .load(imageFile)
                        .into(categoryImgView);
            }
        }
    }
    private void sendUpdateSubjectDetails(int position, AdminShowAllCategoryModel currentCategory, String name, String slug, Dialog dialog) {

        String createCategoryURL = Constant.BASE_URL + "v1/category";
        // Prepare form data
        Map<String, String> params = new HashMap<>();
        params.put("categoryName", name);   // Send the category name
        params.put("slug", slug);     // Send the slug
        params.put("id", currentCategory.getId());     // Send the slug

        // Create a Map for files (if imageFile exists)
        Map<String, File> files = new HashMap<>();

        // If an image file is selected, add it to the files map
        if (imageFile != null && imageFile.exists()) {
            files.put("image", imageFile);
        }
//        Log.e("image",imageFile.toString());
        // Create and send the multipart request
        MultipartRequest multipartRequest = new MultipartRequest(createCategoryURL, params, files,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            boolean status = responseObject.getBoolean("success");
                            if (status) {
                                String message = responseObject.getString("message");
                                Toast.makeText(context.getContext(), message, Toast.LENGTH_SHORT).show();
                                JSONObject dataObj = responseObject.getJSONObject("data");
                                String name = dataObj.getString("categoryName");
                                String slug = dataObj.getString("slug");
                                String imageURL = dataObj.getJSONObject("image").getString("url");

                                updateCategoryInList(currentCategory, name, slug,imageURL);

                                dialog.dismiss();
                                progressDialog.dismiss();
                            } else {
                                // Handle error message if status is false
                                String errorMessage = responseObject.getString("message");
                                Toast.makeText(context.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(context.getContext(), "An error occurred while processing the response.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error response
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                String responseData = new String(error.networkResponse.data, "UTF-8");
                                errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                                errorMessage += "\nResponse Data: " + responseData;
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(context.getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("CategoryFetchError", errorMessage);
                    }
                }, authToken);

        // Add the request to the queue
        MySingletonFragment.getInstance(context).addToRequestQueue(multipartRequest);
    }

    private void updateCategoryInList(AdminShowAllCategoryModel currentCategory, String name, String description, String imageURL) {
        // Find the subject in the list
        for (int i = 0; i < categoryModelArrayList.size(); i++) {
            if (categoryModelArrayList.get(i).getId().equals(currentCategory.getId())) {
                // Update the title of the subject in the list
                categoryModelArrayList.get(i).setCategoryName(name);
                categoryModelArrayList.get(i).setSlug(description);
                if (imageURL != null) {
                    categoryModelArrayList.get(i).setImageUrl(imageURL);
                }
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryModelArrayList.size();
    }

    public void filter(String query) {
        currentQuery = query; // Store current query
        categoryModelArrayList.clear();
        if (query.isEmpty()) {
            categoryModelArrayList.addAll(originalCategoryModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AdminShowAllCategoryModel categoryModel : originalCategoryModelArrayList) {
                if (categoryModel.getCategoryName().toLowerCase().contains(lowerCaseQuery) || categoryModel.getSlug().toLowerCase().contains(lowerCaseQuery)) {
                    categoryModelArrayList.add(categoryModel); // Add matching eBook to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,categoryTxt,updatedAtTxt;
        ImageView threeDots;
        ImageView categoryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titleTxt);
            categoryTxt = itemView.findViewById(R.id.txtSubCategory);
            updatedAtTxt = itemView.findViewById(R.id.updatedAtTxt);
            categoryImage = itemView.findViewById(R.id.imgBlog);
            threeDots = itemView.findViewById(R.id.threeDots);
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

    public void updateOriginalList(ArrayList<AdminShowAllCategoryModel> newList) {
        originalCategoryModelArrayList.clear();
        originalCategoryModelArrayList.addAll(newList);
    }
}
