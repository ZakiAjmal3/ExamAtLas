package com.examatlas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.models.CurrentAffairsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CurrentAffairsAdapter extends RecyclerView.Adapter<CurrentAffairsAdapter.ViewHolder> {
    private ArrayList<CurrentAffairsModel> currentAffairsModelArrayList;
    Fragment context;

    public CurrentAffairsAdapter(ArrayList<CurrentAffairsModel> currentAffairsModelArrayList, Fragment context) {
        this.currentAffairsModelArrayList = currentAffairsModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CurrentAffairsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_current_affair_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentAffairsAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(currentAffairsModelArrayList.get(position));
        holder.title.setText(currentAffairsModelArrayList.get(position).getCfTitle());
        holder.content.setText(currentAffairsModelArrayList.get(position).getCfContent());
        holder.tags.setText(currentAffairsModelArrayList.get(position).getCfTags());

//        if (currentAffairsModelArrayList.get(position).getCfImage() != null) {
            Glide.with(context)
//                    .load(currentAffairsModelArrayList.get(position).getCfImage())
                    .load(R.drawable.image1)
                    .placeholder(R.drawable.image1)
                    .error(R.drawable.image1)
                    .into(holder.cfImage);
//        } else {
//            Glide.with(context)
//                    .load(R.drawable.noimage)
//                    .placeholder(R.drawable.noimage)
//                    .error(R.drawable.noimage)
//                    .into(holder.cfImage);
//        }
    }

    @Override
    public int getItemCount() {
        return currentAffairsModelArrayList.size();
    }

//    private String formatDate(String dateStr) {
//        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
//        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
//        Date date;
//        try {
//            date = inputFormat.parse(dateStr);
//            return outputFormat.format(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return dateStr; // Return original if parsing fails
//        }
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, tags;
        ImageView cfImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cfImage = itemView.findViewById(R.id.imgAffair);
            title = itemView.findViewById(R.id.txtTitle);
            content = itemView.findViewById(R.id.txtContent);
            tags = itemView.findViewById(R.id.tagTxt);
        }
    }
}