package com.examatlas.adapter.AdminHomePage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.models.AdminHomePage.TopCategoryModel;
import com.examatlas.utils.SessionManager;

import java.util.ArrayList;

public class TopCategoryAdapter extends RecyclerView.Adapter<TopCategoryAdapter.ViewHolder>{
    private final Context context;
    private final ArrayList<TopCategoryModel> topCategoryModelArrayList;
    SessionManager sessionManager;
    public TopCategoryAdapter(Context context, ArrayList<TopCategoryModel> topCategoryModelArrayList) {
        this.topCategoryModelArrayList = new ArrayList<>(topCategoryModelArrayList);
        this.context = context;
// Check if context is valid before initializing SessionManager
        if (context != null) {
            sessionManager = new SessionManager(context.getApplicationContext());
        }
    }

    @NonNull
    @Override
    public TopCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_home_top_categories_item_layout, parent, false);
        return new TopCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopCategoryAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(topCategoryModelArrayList.size());

        holder.title.setText(topCategoryModelArrayList.get(position).getCategoryName());
        holder.numCount.setText(topCategoryModelArrayList.get(position).getCategoryNumber());

    }

    @Override
    public int getItemCount() {
        return topCategoryModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, numCount;
        //        ViewPager2 viewPager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            numCount = itemView.findViewById(R.id.numberCountTxt);
        }
    }
}
