package com.examatlas.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.examatlas.R;
import com.examatlas.activities.JoinLiveClassActivity;
import com.examatlas.activities.LiveCoursesClassesListActivity;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.LiveCoursesModel;

import java.util.ArrayList;

public class LiveCoursesAdapter extends RecyclerView.Adapter<LiveCoursesAdapter.ViewHolder> {
    private ArrayList<LiveCoursesModel> liveCoursesModelArrayList;
    Fragment context;

    public LiveCoursesAdapter(ArrayList<LiveCoursesModel> liveCoursesModelArrayList, Fragment context) {
        this.liveCoursesModelArrayList = liveCoursesModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public LiveCoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_live_item_layout, parent, false);
        return new LiveCoursesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveCoursesAdapter.ViewHolder holder, int position) {
        LiveCoursesModel currentClasss = liveCoursesModelArrayList.get(position);
        holder.itemView.setTag(currentClasss);
        holder.itemView.setTag(liveCoursesModelArrayList.get(position));
        holder.title.setText(liveCoursesModelArrayList.get(position).getTitle());
        holder.tags.setText(liveCoursesModelArrayList.get(position).getTags());
        holder.teacherName.setText(liveCoursesModelArrayList.get(position).getTeacherName());

        BookImageAdapter bookImageAdapter = new BookImageAdapter(currentClasss.getImageArrayList(),null,null);
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
                Intent intent = new Intent(context.getContext(), LiveCoursesClassesListActivity.class);
                intent.putExtra("course_id", liveCoursesModelArrayList.get(position).getCourseID());
                context.startActivity(intent);
            }
        }
    }
}
