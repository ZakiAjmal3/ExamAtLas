package com.examatlas.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.AdminLiveCoursesClassesActivity;
import com.examatlas.activities.DashboardActivity;
import com.examatlas.activities.JoinLiveClassActivity;
import com.examatlas.activities.LiveCoursesClassesListActivity;
import com.examatlas.activities.LoginActivity;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.fragment.AdminBlogCreateDeleteFragment;
import com.examatlas.models.AdminShowAllLiveCoursesModel;
import com.examatlas.models.LiveCoursesClassesListModel;
import com.examatlas.models.LiveCoursesModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminShowAllLiveCoursesAdapter extends RecyclerView.Adapter<AdminShowAllLiveCoursesAdapter.ViewHolder> {
    private ArrayList<AdminShowAllLiveCoursesModel> liveCoursesModelArrayList;
    Fragment context;
    SessionManager sessionManager;
    String authToken;
    public AdminShowAllLiveCoursesAdapter(ArrayList<AdminShowAllLiveCoursesModel> liveCoursesModelArrayList, Fragment context) {
        this.liveCoursesModelArrayList = liveCoursesModelArrayList;
        this.context = context;
        sessionManager = new SessionManager(context.getContext());
        authToken = sessionManager.getUserData().get("authToken");
    }

    @NonNull
    @Override
    public AdminShowAllLiveCoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_custom_live_item_layout, parent, false);
        return new AdminShowAllLiveCoursesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminShowAllLiveCoursesAdapter.ViewHolder holder, int position) {
        AdminShowAllLiveCoursesModel currentClasss = liveCoursesModelArrayList.get(position);
        holder.itemView.setTag(currentClasss);
        holder.title.setText(liveCoursesModelArrayList.get(position).getTitle());
        holder.tags.setText(liveCoursesModelArrayList.get(position).getTags());

        String imgURL = liveCoursesModelArrayList.get(position).getImageURL();
        Glide.with(context)
                .load(imgURL)
                .error(R.drawable.image1)
                .into(holder.cfImage);
        holder.deleteCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLiveCourse(currentClasss);
            }
        });
    }

    private void deleteLiveCourse(AdminShowAllLiveCoursesModel currentClasss) {
        String deleteURL = Constant.BASE_URL + "liveclass/deleteClass/" + currentClasss.getCourseID();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");
                    if (status) {
                        Toast.makeText(context.getContext(), "Blog Deleted Successfully", Toast.LENGTH_SHORT).show();
                        liveCoursesModelArrayList.remove(currentClasss);
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
    @Override
    public int getItemCount() {
        return liveCoursesModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, tags;
        ImageView cfImage,deleteCourseBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cfImage = itemView.findViewById(R.id.imgLiveCourse);
            title = itemView.findViewById(R.id.txtFullName);
//            description = itemView.findViewById(R.id.txtDesc);
            tags = itemView.findViewById(R.id.txtTag);
            deleteCourseBtn = itemView.findViewById(R.id.deleteCourseBtn);
            itemView.setOnClickListener((View.OnClickListener) this);
        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context.getContext(), AdminLiveCoursesClassesActivity.class);
                intent.putExtra("course_id", liveCoursesModelArrayList.get(position).getCourseID());
                context.startActivity(intent);
            }
        }
    }
}