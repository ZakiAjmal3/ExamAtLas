package com.examatlas.adapter.books;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.models.Books.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class FilteringCategoryAdapter extends RecyclerView.Adapter<FilteringCategoryAdapter.ViewHolder> {
    private final List<CategoryModel> categoryArrayList; // Use List of BookImageModels
    private final Context context;
    private int selectedPosition = -1;

    public FilteringCategoryAdapter(ArrayList<CategoryModel> categoryArrayList, Context context) {
        this.categoryArrayList = categoryArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_category_filtering_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Set category name
        holder.categoryName.setText(categoryArrayList.get(position).getCategoryName());

        // Load image using Glide
        Glide.with(context)
                .load(R.drawable.book_img2)  // Use your image URL here
                .error(R.drawable.book_img2)  // Fallback image
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView categoryName;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.categoryIMG);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}
