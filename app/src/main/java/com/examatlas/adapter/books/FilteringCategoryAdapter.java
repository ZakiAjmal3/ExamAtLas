package com.examatlas.adapter.books;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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
import com.examatlas.activities.Books.FilteringBookWithCategoryActivity;
import com.examatlas.models.Books.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class FilteringCategoryAdapter extends RecyclerView.Adapter<FilteringCategoryAdapter.ViewHolder> {
    private final List<CategoryModel> categoryArrayList;
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

//        // Load image using Glide
//        Glide.with(context)
//                .load(R.drawable.book_img2)  // Assuming CategoryModel has getImageUrl() method
//                .error(R.drawable.book_img2)  // Fallback image
//                .into(holder.imageView);

//        // Set background color for the selected item
//        if (selectedPosition == position) {
//            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//        } else {
//            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_dark_grey));
//        }
//
//        // Apply rounded corners for the previous and next items
//        applyRoundedCorners(holder, position);
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

//    private void applyRoundedCorners(ViewHolder holder, int position) {
//        // Only modify the adjacent items if the current item is selected
//        if (selectedPosition == position) {
//            // Apply rounded corners to the previous item (bottom-right)
//            if (position > 0) {
//                View previousItemView = holder.itemView.getRootView().findViewWithTag("item_" + (position - 1));
//                if (previousItemView != null) {
//                    GradientDrawable prevDrawable = new GradientDrawable();
//                    prevDrawable.setColor(ContextCompat.getColor(context, R.color.light_dark_grey));
//                    prevDrawable.setCornerRadii(new float[]{0, 0, 0, 0, 20, 20, 0, 0}); // Bottom right rounded
//                    previousItemView.setBackground(prevDrawable);
//                }
//            }
//
//            // Apply rounded corners to the next item (top-right)
//            if (position < categoryArrayList.size() - 1) {
//                View nextItemView = holder.itemView.getRootView().findViewWithTag("item_" + (position + 1));
//                if (nextItemView != null) {
//                    GradientDrawable nextDrawable = new GradientDrawable();
//                    nextDrawable.setColor(ContextCompat.getColor(context, R.color.light_dark_grey));
//                    nextDrawable.setCornerRadii(new float[]{0, 0, 20, 20, 0, 0, 0, 0}); // Top right rounded
//                    nextItemView.setBackground(nextDrawable);
//                }
//            }
//        }
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        private final ImageView imageView;
        private final TextView categoryName;

        public ViewHolder(View itemView) {
            super(itemView);
//            imageView = itemView.findViewById(R.id.categoryIMG);
            categoryName = itemView.findViewById(R.id.categoryName);

//            // Assign a tag to each item view for easier reference
//            itemView.setTag("item_" + getAdapterPosition());
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int previousSelectedPosition = selectedPosition;
//                    selectedPosition = getAdapterPosition();
//
//                    // Only notify if the selected item has changed
//                    if (previousSelectedPosition != selectedPosition) {
//                        notifyItemChanged(previousSelectedPosition);
//                        notifyItemChanged(selectedPosition);
//                    }
//                }
//            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((FilteringBookWithCategoryActivity) context).setCategoryName(categoryArrayList.get(getAdapterPosition()).getCategoryName());
                }
            });
        }
    }
}
