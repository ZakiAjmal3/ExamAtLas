package com.examatlas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.fragment.BlogFragment;
import com.examatlas.models.BlogModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {
    private ArrayList<BlogModel> blogModelArrayList;
    Fragment context;

    public BlogAdapter(ArrayList<BlogModel> blogModelArrayList, Fragment context) {
        this.blogModelArrayList = blogModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BlogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_blog_layout_with_wide_width,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(blogModelArrayList.get(position));
        holder.title.setText(blogModelArrayList.get(position).getTitle());
        holder.content.setText(blogModelArrayList.get(position).getContent());
        holder.tags.setText(blogModelArrayList.get(position).getTags());

    }

    @Override
    public int getItemCount() {
        return blogModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content,tags;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtBlogTitle);
            content = itemView.findViewById(R.id.content);
            tags = itemView.findViewById(R.id.tagTxt);

        }
    }
}
