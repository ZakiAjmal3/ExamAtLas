package com.examatlas.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.examatlas.R;
import com.examatlas.activities.JoinLiveClassActivity;
import com.examatlas.models.LiveClassesModel;

import java.util.ArrayList;

public class LiveClassesAdapter extends RecyclerView.Adapter<LiveClassesAdapter.ViewHolder> {
    private ArrayList<LiveClassesModel> liveClassesModelArrayList;
    Fragment context;

    public LiveClassesAdapter(ArrayList<LiveClassesModel> liveClassesModelArrayList, Fragment context) {
        this.liveClassesModelArrayList = liveClassesModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public LiveClassesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_live_item_layout, parent, false);
        return new LiveClassesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveClassesAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(liveClassesModelArrayList.get(position));
        holder.title.setText(liveClassesModelArrayList.get(position).getTitle());
        holder.description.setText(liveClassesModelArrayList.get(position).getDescription());
        holder.tags.setText(liveClassesModelArrayList.get(position).getTags());
        holder.teacherName.setText(liveClassesModelArrayList.get(position).getTeacherName());
        Glide.with(context)
                .load(R.drawable.image1) // Load the image URL from the model
                .placeholder(R.drawable.image1) // Optional: add a placeholder image
                .error(R.drawable.image1) // Optional: add an error image
                .into(holder.cfImage);

    }

    @Override
    public int getItemCount() {
        return liveClassesModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, description, tags, teacherName;
        ImageView cfImage;

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
                LiveClassesModel liveClass = liveClassesModelArrayList.get(position);
                Intent intent = new Intent(context.getContext(), JoinLiveClassActivity.class);
                intent.putExtra("meetingID", liveClass.getMeetingID());
                context.startActivity(intent);
            }
        }
    }
}
