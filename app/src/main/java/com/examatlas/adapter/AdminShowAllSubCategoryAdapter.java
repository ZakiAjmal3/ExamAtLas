package com.examatlas.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.examatlas.fragment.AdminCreateSubCategoryFragment;
import com.examatlas.models.AdminShowAllSubCategoryModel;
import com.examatlas.models.AdminTagsForDataALLModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminShowAllSubCategoryAdapter extends RecyclerView.Adapter<AdminShowAllSubCategoryAdapter.ViewHolder> {

    private ArrayList<AdminShowAllSubCategoryModel> subCategoryModelArrayList;
    private ArrayList<AdminShowAllSubCategoryModel> originalSubCategoryModelArrayList;
    private Fragment context;
    private String currentQuery = "";
    SessionManager sessionManager;
    String authToken;
    public AdminShowAllSubCategoryAdapter(ArrayList<AdminShowAllSubCategoryModel> subCategoryModelArrayList, Fragment context) {
        this.originalSubCategoryModelArrayList = new ArrayList<>(subCategoryModelArrayList);
        this.subCategoryModelArrayList = new ArrayList<>(originalSubCategoryModelArrayList);
        this.context = context;
        sessionManager = new SessionManager(context.getContext());
        authToken = sessionManager.getUserData().get("authToken");
    }
    @NonNull
    @Override
    public AdminShowAllSubCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_create_subcategory_item_layout, parent, false);
        return new AdminShowAllSubCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminShowAllSubCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AdminShowAllSubCategoryModel currentCategory = subCategoryModelArrayList.get(position);
        holder.itemView.setTag(currentCategory);

        holder.setHighlightedText(holder.categoryName, currentCategory.getCategoryName() + ":", currentQuery);
        holder.setHighlightedText(holder.subCategoryName, currentCategory.getSubCategoryName(), currentQuery);

        holder.editSubCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditCategoryDialogBox(currentCategory,position);
            }
        });
        holder.deleteSubCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(context.getContext())
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete this category?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteSubCategory(subCategoryModelArrayList.get(position));
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
    }

    private void deleteSubCategory(AdminShowAllSubCategoryModel adminShowAllCategoryModel) {
        String id = adminShowAllCategoryModel.getSubCategoryId();
        String deleteURL = Constant.BASE_URL + "category/deleteSubCategory/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(context.getContext(), message, Toast.LENGTH_SHORT).show();
                                subCategoryModelArrayList.remove(adminShowAllCategoryModel);
                                ((AdminCreateSubCategoryFragment) context).getAllSubCategory();
                                notifyDataSetChanged();
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
    private void openEditCategoryDialogBox(AdminShowAllSubCategoryModel currentCategory, int position) {
        Dialog dialog = new Dialog(context.getContext());
        dialog.setContentView(R.layout.admin_create_subcategory_dialog_box);

        Spinner categorySpinner = dialog.findViewById(R.id.categorySpinner);

        EditText titleEditTxt = dialog.findViewById(R.id.titleEditTxt);
        titleEditTxt.setText(currentCategory.getSubCategoryName());
        EditText slugEditTxt = dialog.findViewById(R.id.slugEditText);
        slugEditTxt.setText(currentCategory.getSubCategoryName());

        Button submitBtn = dialog.findViewById(R.id.btnSubmit);
        ImageView crossBtn = dialog.findViewById(R.id.btnCross);

        String[] categoryNameList = new String[subCategoryModelArrayList.size() + 1]; // +1 for "Select Category"
        categoryNameList[0] = "Select Category";
        for (int i = 0; i < subCategoryModelArrayList.size(); i++) {
            categoryNameList[i + 1] = subCategoryModelArrayList.get(i).getCategoryName();
        }
        ((AdminCreateSubCategoryFragment) context).setupCategorySpinner(categorySpinner,titleEditTxt,slugEditTxt,categoryNameList,currentCategory);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUpdateSubjectDetails(currentCategory,titleEditTxt.getText().toString().trim(),slugEditTxt.getText().toString().trim(),dialog,position);
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

    private void sendUpdateSubjectDetails(AdminShowAllSubCategoryModel currentCategory, String name, String slug, Dialog dialog, int position) {
        String updateURL = Constant.BASE_URL + "category/createSubCategory";
        // Create JSON object to send in the request
        JSONObject categoryObject = new JSONObject();
        try {
            categoryObject.put("id", currentCategory.getSubCategoryId());
            categoryObject.put("categoryId", currentCategory.getCategoryId());
            categoryObject.put("subCategoryName", name);
            categoryObject.put("slug", slug);

        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error creating JSON object: " + e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, updateURL, categoryObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(context.getContext(),message, Toast.LENGTH_SHORT).show();
                                updateSubjectInList(currentCategory, name,slug);
                                ((AdminCreateSubCategoryFragment) context).getAllSubCategory();
                                notifyItemChanged(position);
                                dialog.dismiss();
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

    private void updateSubjectInList(AdminShowAllSubCategoryModel currentCategory, String name,String slug) {
        // Find the subject in the list
        for (int i = 0; i < subCategoryModelArrayList.size(); i++) {
            if (subCategoryModelArrayList.get(i).getSubCategoryId().equals(currentCategory.getSubCategoryId())) {
                // Update the title of the subject in the list
                subCategoryModelArrayList.get(i).setSubCategoryName(name);
//                subCategoryModelArrayList.get(i).de(description);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return subCategoryModelArrayList.size();
    }

    public void filter(String query) {
        currentQuery = query; // Store current query
        subCategoryModelArrayList.clear();
        if (query.isEmpty()) {
            subCategoryModelArrayList.addAll(originalSubCategoryModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AdminShowAllSubCategoryModel categoryModel : originalSubCategoryModelArrayList) {
                if (categoryModel.getCategoryName().toLowerCase().contains(lowerCaseQuery) || categoryModel.getSubCategoryName().toLowerCase().contains(lowerCaseQuery)) {
                    subCategoryModelArrayList.add(categoryModel); // Add matching eBook to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName,subCategoryName;
        ImageView editSubCategoryBtn,deleteSubCategoryBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.categoryTxt);
            subCategoryName = itemView.findViewById(R.id.titleTxt);
            editSubCategoryBtn = itemView.findViewById(R.id.editTitleBtn);
            deleteSubCategoryBtn = itemView.findViewById(R.id.deleteSubjectBtn);
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

    public void updateOriginalList(ArrayList<AdminShowAllSubCategoryModel> newList) {
        originalSubCategoryModelArrayList.clear();
        originalSubCategoryModelArrayList.addAll(newList);
    }
}