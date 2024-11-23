package com.examatlas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.models.AdminShowAllCategoryModel;

import java.util.ArrayList;
import java.util.List;

public class SignUp5CategoryAdapter extends RecyclerView.Adapter<SignUp5CategoryAdapter.ViewHolder> {
    private ArrayList<AdminShowAllCategoryModel> categoryModelArrayList;
    private Context context;
    private ArrayList<String> categorySelectedArrayList;
    private OnCategoryClickListener onCategoryClickListener;

    public SignUp5CategoryAdapter(ArrayList<AdminShowAllCategoryModel> categoryModelArrayList, Context context, OnCategoryClickListener onCategoryClickListener) {
        this.categoryModelArrayList = categoryModelArrayList;
        this.context = context;
        this.categorySelectedArrayList = new ArrayList<>();
        this.onCategoryClickListener = onCategoryClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_up5_category_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminShowAllCategoryModel category = categoryModelArrayList.get(position);
        holder.itemView.setTag(category);
        holder.title.setText(category.getCategoryName());

        String imageUrl = category.getImageUrl();
        Glide.with(context)
                .load(imageUrl)
                .error(R.drawable.image2)
                .into(holder.description);

        // Set background based on selection status
        if (categorySelectedArrayList.contains(category.getId())) {
            holder.tick.setVisibility(View.VISIBLE);
        } else {
            holder.tick.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return categoryModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView description,tick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titleTxt);
            description = itemView.findViewById(R.id.categoryImageView);
            tick = itemView.findViewById(R.id.tick);

            // Handle click to toggle category selection
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AdminShowAllCategoryModel category = (AdminShowAllCategoryModel) itemView.getTag();
                    String categoryId = category.getId();
                    // Check the number of selected categories
                    if (categorySelectedArrayList.size() >= 3 && !categorySelectedArrayList.contains(categoryId)) {
                        Toast.makeText(context, "You can select only three categories", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Toggle category selection
                    if (categorySelectedArrayList.contains(categoryId)) {
                        categorySelectedArrayList.remove(categoryId);
                        tick.setVisibility(View.GONE);
                    } else {
                        categorySelectedArrayList.add(categoryId);
                        tick.setVisibility(View.VISIBLE);                    }

                    // Notify listener of the selection change
                    if (onCategoryClickListener != null) {
                        onCategoryClickListener.onCategoryClick(categorySelectedArrayList);
                    }

                    // Notify adapter that the item has been clicked
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }

    // Inside SignUp5CategoryAdapter class
    public interface OnCategoryClickListener {
        void onCategoryClick(ArrayList<String> selectedCategoryIds); // Change method to use ArrayList<String>
    }
    public List<String> getSelectedCategoryIds() {
        return categorySelectedArrayList;
    }
}
