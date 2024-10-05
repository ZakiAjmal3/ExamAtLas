package com.examatlas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.fragment.CourseFragment;
import com.examatlas.models.CourseModel;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    Fragment context;
    ArrayList<CourseModel> courseModelArrayList;

    public CourseAdapter(Fragment context, ArrayList<CourseModel> courseModelArrayList) {
        this.context = context;
        this.courseModelArrayList = courseModelArrayList;
    }

    @NonNull
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_course_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(courseModelArrayList.get(position));
        holder.title.setText(courseModelArrayList.get(position).getTitle());

        String price = "₹ " + courseModelArrayList.get(position).getPrice();
        holder.price.setText(price);

    }

    @Override
    public int getItemCount() {
        return courseModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,price;
        ImageView courseImage;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.txtPrice);
            courseImage = itemView.findViewById(R.id.courseImage);

        }
    }
}
