package com.examatlas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.models.EbookModel;

import java.util.ArrayList;

public class EbookAdapter extends RecyclerView.Adapter<EbookAdapter.ViewHolder> {
    private ArrayList<EbookModel> ebookModelArrayList;
    Context context;

    public EbookAdapter(ArrayList<EbookModel> ebookModelArrayList, Context context) {
        this.ebookModelArrayList = ebookModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public EbookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_ebook_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EbookAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(ebookModelArrayList.get(position));
        holder.title.setText(ebookModelArrayList.get(position).getTitle());
        holder.content.setText(ebookModelArrayList.get(position).getContent());
        holder.tags.setText(ebookModelArrayList.get(position).getTags());
        holder.author.setText("By: " + ebookModelArrayList.get(position).getAuthor());

        Glide.with(context)
                .load(R.drawable.image1) // Load the image URL from the model
                .placeholder(R.drawable.image1) // Optional: add a placeholder image
                .error(R.drawable.image1) // Optional: add an error image
                .into(holder.ebookImage);

    }

    @Override
    public int getItemCount() {
        return ebookModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, tags, author,price;
        ImageView ebookImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtBookTitle);
            content = itemView.findViewById(R.id.txtContent);
            tags = itemView.findViewById(R.id.txtTags);
            author = itemView.findViewById(R.id.txtAuthor);
            ebookImage = itemView.findViewById(R.id.imgBook);
        }
    }
}
