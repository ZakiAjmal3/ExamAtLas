package com.examatlas.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.BlogSingleViewActivity;
import com.examatlas.activities.CurrentAffairsActivity;
import com.examatlas.fragment.BlogFragment;
import com.examatlas.models.BlogModel;

import java.util.ArrayList;


public class BlogAdapterForShowingAllBlogs extends RecyclerView.Adapter<BlogAdapterForShowingAllBlogs.ViewHolder> {
    private ArrayList<BlogModel> blogModelArrayList;
    Fragment context;

    public BlogAdapterForShowingAllBlogs(ArrayList<BlogModel> blogModelArrayList, Fragment context) {
        this.blogModelArrayList = blogModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BlogAdapterForShowingAllBlogs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_blog_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onBindViewHolder(@NonNull BlogAdapterForShowingAllBlogs.ViewHolder holder, int position) {
        holder.itemView.setTag(blogModelArrayList.get(position));
        holder.tags.setText(blogModelArrayList.get(position).getTags());
        String titleStr = blogModelArrayList.get(position).getTitle();

// Set the title text first to measure the number of lines
        holder.title.setText(titleStr);

        Glide.with(context)
                .load(blogModelArrayList.get(position).getImageURL())
                .error(R.drawable.image1)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return blogModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,tags;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtBlogTitle);
            imageView = itemView.findViewById(R.id.imgBlog);
            tags = itemView.findViewById(R.id.tagTxt);

            if (context instanceof BlogFragment){

                // Set the WebView's width to 150dp
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.width = RecyclerView.LayoutParams.MATCH_PARENT;
                itemView.setLayoutParams(params);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    }
                    Intent intent = new Intent(context.getActivity(), BlogSingleViewActivity.class);
                    intent.putExtra("blogID",blogModelArrayList.get(getAdapterPosition()).getBlogID());
                    context.startActivity(intent);
                }
            });
        }
    }
}
