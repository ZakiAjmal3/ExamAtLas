package com.examatlas.adapter.books;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.BookImageModels;

import java.io.File;
import java.util.ArrayList;

public class ReviewImagesListAdapter extends RecyclerView.Adapter<ReviewImagesListAdapter.ViewHolder> {
    ArrayList<File> imageFiles;
    Context context;
    public ReviewImagesListAdapter(Context context, ArrayList<File> imageFiles) {
        this.context = context;
        this.imageFiles = imageFiles;
    }

    @NonNull
    @Override
    public ReviewImagesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_images_item_list_layout, parent, false);
        return new ReviewImagesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewImagesListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemView.setTag(imageFiles.get(position));

        if (position < imageFiles.size()) {
            File imageFile = imageFiles.get(position);
            Glide.with(context)
                    .load(imageFile) // Load the image from the file
                    .into(holder.productImg);  // Bind the image to the ImageView
        }
        holder.crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageFiles.size() == 1){
                    Toast.makeText(context, "Please Add More Images in order to remove this image", Toast.LENGTH_LONG).show();
                }else {
                    imageFiles.remove(position);
                    notifyItemRemoved(position);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return imageFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImg;
        CardView crossBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImg = itemView.findViewById(R.id.imgView);
            crossBtn = itemView.findViewById(R.id.crossBtn);
        }
    }
}
