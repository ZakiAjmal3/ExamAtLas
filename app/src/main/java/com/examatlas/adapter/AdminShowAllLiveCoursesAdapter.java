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
import com.examatlas.R;
import com.examatlas.activities.AdminLiveCoursesClassesActivity;
import com.examatlas.activities.DashboardActivity;
import com.examatlas.activities.JoinLiveClassActivity;
import com.examatlas.activities.LiveCoursesClassesListActivity;
import com.examatlas.activities.LoginActivity;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.AdminShowAllLiveCoursesModel;
import com.examatlas.models.LiveCoursesClassesListModel;
import com.examatlas.models.LiveCoursesModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
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

    public AdminShowAllLiveCoursesAdapter(ArrayList<AdminShowAllLiveCoursesModel> liveCoursesModelArrayList, Fragment context) {
        this.liveCoursesModelArrayList = liveCoursesModelArrayList;
        this.context = context;
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
        holder.itemView.setTag(liveCoursesModelArrayList.get(position));
        holder.title.setText(liveCoursesModelArrayList.get(position).getTitle());
        holder.tags.setText(liveCoursesModelArrayList.get(position).getTags());
        holder.teacherName.setText(liveCoursesModelArrayList.get(position).getTeacherName());

        BookImageAdapter bookImageAdapter = new BookImageAdapter(currentClasss.getImageArrayList());
        holder.cfImage.setAdapter(bookImageAdapter);

//        // Enable JavaScript (optional, depending on your content)
//        WebSettings webSettings = holder.description.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//
//        String htmlContentTxt = liveCoursesModelArrayList.get(position).getDescription();
//
//        // Inject CSS to control the image size
//        String injectedCss = "<style>"
//                + "p { font-size: 20px; }" // Increase text size only for <p> tags (paragraphs)
//                + "img { width: 100%; height: auto; }" // Adjust image size as needed
//                + "</style>";
//        String fullHtmlContent = injectedCss + htmlContentTxt;
//
//        // Disable scrolling and over-scrolling
//        holder.description.setVerticalScrollBarEnabled(false);  // Disable vertical scroll bar
//        holder.description.setOverScrollMode(WebView.OVER_SCROLL_NEVER); // Disable over-scrolling effect
//
//        // Load the modified HTML content
//        holder.description.loadData(fullHtmlContent, "text/html", "UTF-8");

    }

    @Override
    public int getItemCount() {
        return liveCoursesModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, tags, teacherName;
        ViewPager2 cfImage;
        WebView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cfImage = itemView.findViewById(R.id.imgLiveCourse);
            title = itemView.findViewById(R.id.txtFullName);
//            description = itemView.findViewById(R.id.txtDesc);
            tags = itemView.findViewById(R.id.txtTag);
            teacherName = itemView.findViewById(R.id.txtTeacherName);

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