package com.examatlas.adapter.extraAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.models.extraModels.BookImageModels;

import java.util.ArrayList;
import java.util.List;

public class BookImageAdapter extends RecyclerView.Adapter<BookImageAdapter.SliderViewHolder> {
    private final List<BookImageModels> imageList; // Use List of BookImageModels

    public BookImageAdapter(ArrayList<BookImageModels> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_image_slider_item_layout, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.bind(imageList.get(position)); // Bind the model
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public SliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(BookImageModels imageModel) {
            Glide.with(imageView.getContext())
//                    .load("https://examatlas-backend.onrender.com/" + imageModel.getFileName())
                    .load(imageModel.getUrl())
                    .error(R.drawable.noimage)
                    .placeholder(R.drawable.noimage)
                    .into(imageView);
            Log.d("BookImageAdapter", "Loading image: " + imageModel.getFileName());
        }
    }
}
