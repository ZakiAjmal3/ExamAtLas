package com.examatlas.adapter.books;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.Books.FilteringBookWithCategoryActivity;
import com.examatlas.models.Books.BookImageModels;
import com.examatlas.models.Books.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final List<CategoryModel> categoryArrayList; // Use List of BookImageModels
    Context context;
    public CategoryAdapter(ArrayList<CategoryModel> categoryArrayList, Context context) {
        this.categoryArrayList = categoryArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_category_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel currentCategory = categoryArrayList.get(position);
        holder.itemView.setTag(currentCategory);
        holder.categoryName.setText(categoryArrayList.get(position).getString("categoryName"));
        String imgURL = categoryArrayList.get(position).getImageUrl();
        Glide.with(context)
                .load(imgURL)
                .error(R.drawable.noimage)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        TextView categoryName;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.categoryIMG);
            categoryName = itemView.findViewById(R.id.categoryName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FilteringBookWithCategoryActivity.class);
                    intent.putExtra("name", categoryArrayList.get(getAdapterPosition()).getString("categoryName"));
                    intent.putExtra("id", categoryArrayList.get(getAdapterPosition()).getString("_id"));
                    context.startActivity(intent);
                }
            });
        }
    }
}
