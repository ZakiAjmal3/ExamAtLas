package com.examatlas.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.description.setText(liveCoursesModelArrayList.get(position).getDescription());
        holder.tags.setText(liveCoursesModelArrayList.get(position).getTags());
        holder.teacherName.setText(liveCoursesModelArrayList.get(position).getTeacherName());

        BookImageAdapter bookImageAdapter = new BookImageAdapter(currentClasss.getImageArrayList());
        holder.cfImage.setAdapter(bookImageAdapter);
    }

    @Override
    public int getItemCount() {
        return liveCoursesModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, description, tags, teacherName;
        ViewPager2 cfImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cfImage = itemView.findViewById(R.id.imgLiveCourse);
            title = itemView.findViewById(R.id.txtFullName);
            description = itemView.findViewById(R.id.txtDesc);
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
