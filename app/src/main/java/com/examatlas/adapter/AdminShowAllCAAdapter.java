package com.examatlas.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
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
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.AdminCurrentAffairsSingleViewActivity;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.fragment.AdminCreateCurrentAffairFragment;
import com.examatlas.models.AdminShowAllCAModel;
import com.examatlas.models.AdminTagsForDataALLModel;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminShowAllCAAdapter extends RecyclerView.Adapter<AdminShowAllCAAdapter.ViewHolder> {
    private ArrayList<AdminShowAllCAModel> adminShowAllCAModelArrayList;
    private ArrayList<AdminShowAllCAModel> orginalAdminShowAllCAModelArrayList;
    private Fragment context;
    private String currentQuery = "";
    SessionManager sessionManager;
    String authToken;
    public AdminShowAllCAAdapter(ArrayList<AdminShowAllCAModel> adminShowAllCAModelArrayList, Fragment context) {
        this.adminShowAllCAModelArrayList = adminShowAllCAModelArrayList;
        this.context = context;
        this.orginalAdminShowAllCAModelArrayList = new ArrayList<>(adminShowAllCAModelArrayList);
        sessionManager = new SessionManager(context.getContext());
        authToken = sessionManager.getUserData().get("authToken");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_custom_current_affair_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminShowAllCAModel currentCA = adminShowAllCAModelArrayList.get(position);
        holder.itemView.setTag(currentCA);

        // Set highlighted text for the tags
        holder.setHighlightedText(holder.tags, currentCA.getTags(), currentQuery);

        holder.editCABtn.setOnClickListener(view -> openEditCADialog(currentCA));
        holder.deleteCABtn.setOnClickListener(view -> quitDialog(position));

        // Load the image using Glide
        Glide.with(context)
                .load(currentCA.getImageURL())
                .error(R.drawable.noimage)
                .into(holder.caImage);

        String titleStr = currentCA.getTitle();
        holder.setHighlightedText(holder.title, titleStr, currentQuery);

        // Post a Runnable to modify the title text based on the number of lines
        holder.title.post(new Runnable() {
            @Override
            public void run() {
                // Get the Layout object from the TextView to measure lines
                Layout layout = holder.title.getLayout();

                // Check the number of lines
                int lineCount = layout.getLineCount();

                // Only perform truncation if the line count is more than 3
                if (lineCount > 3) {
                    String firstThreeLinesText = "";

                    // Get the start and end positions for the first three lines
                    for (int i = 0; i < 3; i++) {
                        int startPos = layout.getLineStart(i);
                        int endPos = layout.getLineEnd(i);
                        firstThreeLinesText += holder.title.getText().subSequence(startPos, endPos);

                        // Add a space after each line, except for the last line
                        if (i < 2) {
                            firstThreeLinesText += " ";
                        }
                    }

                    // Truncate the last three characters and add "..."
                    if (firstThreeLinesText.length() > 3) {
                        firstThreeLinesText = firstThreeLinesText.substring(0, firstThreeLinesText.length() - 3) + "...";
                    }

                    // Set the text to the title with the first three lines and ellipsis
                    holder.setHighlightedText(holder.title, firstThreeLinesText, currentQuery);

                } else {
                    // If there are three or fewer lines, just set the text normally
                    holder.setHighlightedText(holder.title, titleStr, currentQuery);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return adminShowAllCAModelArrayList.size();
    }

    public void filter(String query) {
        currentQuery = query; // Store current query
        adminShowAllCAModelArrayList.clear();
        if (query.isEmpty()) {
            adminShowAllCAModelArrayList.addAll(orginalAdminShowAllCAModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AdminShowAllCAModel CA : orginalAdminShowAllCAModelArrayList) {
                if (CA.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        CA.getKeyword().toLowerCase().contains(lowerCaseQuery) ||
                        CA.getContent().toLowerCase().contains(lowerCaseQuery)) {
                    adminShowAllCAModelArrayList.add(CA); // Add matching blog to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }
    private void openEditCADialog(AdminShowAllCAModel caModel) {
        Dialog editBlogDialogBox = new Dialog(context.requireContext());
        editBlogDialogBox.setContentView(R.layout.admin_create_current_affairs_dialog_box);

        ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList = new ArrayList<>();
        AdminTagsForDataALLAdapter adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        RecyclerView tagsRecyclerView = editBlogDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setVisibility(View.VISIBLE);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(context.requireContext(), 2));
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        TextView headerTxt = editBlogDialogBox.findViewById(R.id.txtAddData);
        EditText titleEditTxt = editBlogDialogBox.findViewById(R.id.titleEditTxt);
        EditText keywordEditTxt = editBlogDialogBox.findViewById(R.id.keywordEditText);
        EditText contentEditTxt = editBlogDialogBox.findViewById(R.id.contentEditText);
        EditText tagsEditTxt = editBlogDialogBox.findViewById(R.id.tagsEditText);

        headerTxt.setText("Edit Current Affairs");
        titleEditTxt.setText(caModel.getTitle());
        keywordEditTxt.setText(caModel.getKeyword());
        contentEditTxt.setText(caModel.getContent());

        String[] tagsArray = caModel.getTags().split(",");
        for (String tag : tagsArray) {
            adminTagsForDataALLModelArrayList.add(new AdminTagsForDataALLModel(tag.trim()));
        }
        adminTagsForDataALLAdapter.notifyDataSetChanged();

        Button uploadBlogDetailsBtn = editBlogDialogBox.findViewById(R.id.btnSubmit);

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
            sendingCADetails(caModel.getCaID(),
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

    private void sendingCADetails(String caID, String title, String keyword, String content, ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList) {
        String updateURL = Constant.BASE_URL + "currentAffair/updateCA/" + caID;

        // Create JSON object to send in the request
        JSONObject caDetails = new JSONObject();
        try {
            caDetails.put("title", title);
            caDetails.put("keyword", keyword);
            caDetails.put("content", content);

            // Convert tags to a JSONArray
            JSONArray tagsArray = new JSONArray();
            for (AdminTagsForDataALLModel tag : adminTagsForDataALLModelArrayList) {
                tagsArray.put(tag.getTagName()); // Assuming `getTag()` returns the tag string
            }
            caDetails.put("tags", tagsArray);

        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error creating JSON object: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, updateURL, caDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(context.getContext(), "Current Affairs Updated Successfully", Toast.LENGTH_SHORT).show();
                                ((AdminCreateCurrentAffairFragment) context).showAllCAFunction();
                            } else {
                                Toast.makeText(context.getContext(), "Failed to update Current Affairs", Toast.LENGTH_SHORT).show();
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


    private void quitDialog(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context.requireContext());
        builder.setTitle("Delete Current Affairs")
                .setMessage("Are you sure you want to delete this current affairs?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> deleteCurrentAffairs(position))
                .show();
    }

    private void deleteCurrentAffairs(int position) {
        String deleteURL = Constant.BASE_URL + "currentAffair/deleteById/" + adminShowAllCAModelArrayList.get(position).getCaID();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(context.getContext(), "Current Affairs Deleted Successfully", Toast.LENGTH_SHORT).show();
                                adminShowAllCAModelArrayList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, adminShowAllCAModelArrayList.size());
                                ((AdminCreateCurrentAffairFragment) context).showAllCAFunction();
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, tags;
        ImageView caImage,editCABtn, deleteCABtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            caImage = itemView.findViewById(R.id.imgAffair);
            title = itemView.findViewById(R.id.txtTitle);
            tags = itemView.findViewById(R.id.tagTxt);
            editCABtn = itemView.findViewById(R.id.editCABtn);
            deleteCABtn = itemView.findViewById(R.id.deleteCABtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getActivity(), AdminCurrentAffairsSingleViewActivity.class);
                    intent.putExtra("CAID",adminShowAllCAModelArrayList.get(getAbsoluteAdapterPosition()).getCaID());
                    context.startActivity(intent);
                }
            });
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
    public void updateOriginalList(ArrayList<AdminShowAllCAModel> newList) {
        orginalAdminShowAllCAModelArrayList.clear();
        orginalAdminShowAllCAModelArrayList.addAll(newList);
    }
}
