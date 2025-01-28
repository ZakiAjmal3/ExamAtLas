package com.examatlas.adapter;

import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.CurrentAffairsActivity;
import com.examatlas.activities.CurrentAffairsSingleViewActivity;
import com.examatlas.activities.DashboardActivity;
import com.examatlas.fragment.BlogFragment;
import com.examatlas.fragment.HomeFragment;
import com.examatlas.models.CurrentAffairsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CurrentAffairsShowingAllAdapter extends RecyclerView.Adapter<CurrentAffairsShowingAllAdapter.ViewHolder> {
    private ArrayList<CurrentAffairsModel> currentAffairsModelArrayList;
    Fragment context;
    Context context1;

    public CurrentAffairsShowingAllAdapter(ArrayList<CurrentAffairsModel> currentAffairsModelArrayList, Fragment context, Context context1) {
        this.currentAffairsModelArrayList = currentAffairsModelArrayList;
        this.context = context;
        this.context1 = context1;
    }

    @NonNull
    @Override
    public CurrentAffairsShowingAllAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_blog_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentAffairsShowingAllAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(currentAffairsModelArrayList.get(position));
        if (context != null){
            Glide.with(context)
                    .load(currentAffairsModelArrayList.get(position).getCfImageURL())
                    .error(R.drawable.image1)
                    .into(holder.cfImage);
        }else {
            Glide.with(context1)
                    .load(currentAffairsModelArrayList.get(position).getCfImageURL())
                    .error(R.drawable.image1)
                    .into(holder.cfImage);
        }
        String titleStr = currentAffairsModelArrayList.get(position).getCfTitle();
        holder.title.setText(titleStr);
        holder.tags.setText(currentAffairsModelArrayList.get(position).getCfTags());
    }

    @Override
    public int getItemCount() {
        return currentAffairsModelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, tags;
        ImageView cfImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cfImage = itemView.findViewById(R.id.imgBlog);
            title = itemView.findViewById(R.id.txtBlogTitle);
            tags = itemView.findViewById(R.id.tagTxt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof HomeFragment) {
                        Intent intent = new Intent(context.getContext(), CurrentAffairsSingleViewActivity.class);
                        intent.putExtra("CAID",currentAffairsModelArrayList.get(getAdapterPosition()).getCfID());
                        context.startActivity(intent);
                    }else {
                        Intent intent = new Intent(context1, CurrentAffairsSingleViewActivity.class);
                        intent.putExtra("CAID",currentAffairsModelArrayList.get(getAdapterPosition()).getCfID());
                        context1.startActivity(intent);
                    }
                }
            });
        }
    }
}