package com.examatlas.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.AdminDashboardActivity;
import com.examatlas.activities.MainActivity;
import com.examatlas.fragment.AdminCreateSubjectFragment;
import com.examatlas.models.AdminShowAllSubjectModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminShowAllSubjectAdapter extends RecyclerView.Adapter<AdminShowAllSubjectAdapter.ViewHolder> {
    private ArrayList<AdminShowAllSubjectModel> subjectModelArrayList;
    private ArrayList<AdminShowAllSubjectModel> originalSubjectModelArrayList;
    private Fragment context;
    private String currentQuery = "";
    SessionManager sessionManager;
    String authToken;
    public AdminShowAllSubjectAdapter(ArrayList<AdminShowAllSubjectModel> subjectModelArrayList, Fragment context) {
        this.originalSubjectModelArrayList = new ArrayList<>(subjectModelArrayList);
        this.subjectModelArrayList = new ArrayList<>(originalSubjectModelArrayList);
        this.context = context;
        sessionManager = new SessionManager(context.getContext());
        authToken = sessionManager.getUserData().get("authToken");
    }
    @NonNull
    @Override
    public AdminShowAllSubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_create_subject_item_layout, parent, false);
        return new AdminShowAllSubjectAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminShowAllSubjectAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AdminShowAllSubjectModel currentSubject = subjectModelArrayList.get(position);
        holder.itemView.setTag(currentSubject);
        // Access the last item first

        holder.setHighlightedText(holder.title, currentSubject.getTitle(), currentQuery);

        holder.editSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditSubjectDialogBox(subjectModelArrayList.get(position));
            }
        });

        holder.deleteSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(context.getContext())
                        .setTitle("Delete Subject")
                        .setMessage("Are you sure you want to delete this subject?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteSubject(subjectModelArrayList.get(position));
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

    private void deleteSubject(AdminShowAllSubjectModel adminShowAllSubjectModel) {
        String id = adminShowAllSubjectModel.getId();
        String deleteURL = Constant.BASE_URL + "subject/deleteSubject/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(context.getContext(), message, Toast.LENGTH_SHORT).show();
                                subjectModelArrayList.remove(adminShowAllSubjectModel);
                                notifyDataSetChanged();
                                ((AdminCreateSubjectFragment) context).getAllSubject();
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

    private void openEditSubjectDialogBox(AdminShowAllSubjectModel adminShowAllSubjectModel) {
        Dialog dialog = new Dialog(context.getContext());
        dialog.setContentView(R.layout.admin_create_subject_dialog_box);

        EditText titleEditTxt = dialog.findViewById(R.id.titleEditTxt);
        titleEditTxt.setText(adminShowAllSubjectModel.getTitle());

        Button submitBtn = dialog.findViewById(R.id.btnSubmit);
        ImageView crossBtn = dialog.findViewById(R.id.btnCross);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUpdateSubjectDetails(adminShowAllSubjectModel,titleEditTxt.getText().toString().trim(),dialog);
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

    private void sendUpdateSubjectDetails(AdminShowAllSubjectModel adminShowAllSubjectModel, String title, Dialog dialog) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("id", adminShowAllSubjectModel.getId());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        String subjectURL = Constant.BASE_URL + "subject/addSubject";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, subjectURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(context.getContext(), message, Toast.LENGTH_SHORT).show();
                                // Update the title in the list if successful
                                updateSubjectInList(adminShowAllSubjectModel, title);
                                dialog.dismiss();
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
    private void updateSubjectInList(AdminShowAllSubjectModel adminShowAllSubjectModel, String newTitle) {
        // Find the subject in the list
        for (int i = 0; i < subjectModelArrayList.size(); i++) {
            if (subjectModelArrayList.get(i).getId().equals(adminShowAllSubjectModel.getId())) {
                // Update the title of the subject in the list
                subjectModelArrayList.get(i).setTitle(newTitle);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return subjectModelArrayList.size();
    }

    public void filter(String query) {
        currentQuery = query; // Store current query
        subjectModelArrayList.clear();
        if (query.isEmpty()) {
            subjectModelArrayList.addAll(originalSubjectModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AdminShowAllSubjectModel subjectModel : originalSubjectModelArrayList) {
                if (subjectModel.getTitle().toLowerCase().contains(lowerCaseQuery)){
                    subjectModelArrayList.add(subjectModel); // Add matching eBook to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView editSubjectBtn, deleteSubjectBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titleTxt);
            editSubjectBtn = itemView.findViewById(R.id.editTitleBtn);
            deleteSubjectBtn = itemView.findViewById(R.id.deleteSubjectBtn);
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

    public void updateOriginalList(ArrayList<AdminShowAllSubjectModel> newList) {
        originalSubjectModelArrayList.clear();
        originalSubjectModelArrayList.addAll(newList);
    }
}
