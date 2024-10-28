package com.examatlas.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.models.AdminShowAllCourseModel;

import java.util.ArrayList;
import java.util.Collections;

public class AdminShowAllCoursesAdapter extends RecyclerView.Adapter<AdminShowAllCoursesAdapter.ViewHolder> {
    Fragment context;
    ArrayList<AdminShowAllCourseModel> adminShowAllCourseModelArrayList;
    ArrayList<AdminShowAllCourseModel> orginalShowAllBlogModelArrayList;
        private String currentQuery = "";
    public AdminShowAllCoursesAdapter(Fragment context, ArrayList<AdminShowAllCourseModel> adminShowAllCourseModelArrayList2) {
        this.context = context;
        this.adminShowAllCourseModelArrayList = adminShowAllCourseModelArrayList2;
        this.orginalShowAllBlogModelArrayList = new ArrayList<>(adminShowAllCourseModelArrayList2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_custom_course_layout_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminShowAllCourseModel currentCourse = adminShowAllCourseModelArrayList.get(position);
        holder.itemView.setTag(currentCourse);

        holder.setHighlightedText(holder.title, currentCourse.getTitle(), currentQuery);
        holder.setHighlightedText(holder.price,"₹ " + currentCourse.getPrice(), currentQuery);

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getContext(), "Work in Progress", Toast.LENGTH_SHORT).show();
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getContext(), "Work in Progress", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return adminShowAllCourseModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,price;
        ImageView courseImage,editBtn,deleteBtn;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.txtPrice);
            courseImage = itemView.findViewById(R.id.courseImage);
            editBtn = itemView.findViewById(R.id.btnEdit);
            deleteBtn = itemView.findViewById(R.id.btnDelete);

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
        adminShowAllCourseModelArrayList.clear();
        if (query.isEmpty()) {
            adminShowAllCourseModelArrayList.addAll(orginalShowAllBlogModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AdminShowAllCourseModel blog : orginalShowAllBlogModelArrayList) {
                if (blog.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        blog.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        blog.getPrice().toLowerCase().contains(lowerCaseQuery)) {
                    adminShowAllCourseModelArrayList.add(blog); // Add matching blog to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }
    public void updateOriginalList(ArrayList<AdminShowAllCourseModel> newList) {
        orginalShowAllBlogModelArrayList.clear();
        orginalShowAllBlogModelArrayList.addAll(newList);
    }
}
