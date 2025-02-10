package com.examatlas.adapter.books;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
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
import com.examatlas.activities.Books.EBooks.PurchasedEBookViewingBookActivity;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.BookImageModels;
import com.examatlas.utils.SessionManager;

import java.util.ArrayList;

public class PurchaseEBookAdapter extends RecyclerView.Adapter<PurchaseEBookAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<AllBooksModel> allBooksModelArrayList;
    SessionManager sessionManager;
    public PurchaseEBookAdapter(Context context, ArrayList<AllBooksModel> allBooksModelArrayList) {
        this.allBooksModelArrayList = new ArrayList<>(allBooksModelArrayList);
        this.context = context;
// Check if context is valid before initializing SessionManager
        if (context != null) {
            sessionManager = new SessionManager(context.getApplicationContext());
        }
    }
    @NonNull
    @Override
    public PurchaseEBookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hardcopybook_item_list2, parent, false);
        return new PurchaseEBookAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PurchaseEBookAdapter.ViewHolder holder, int position) {
        AllBooksModel currentBook = allBooksModelArrayList.get(position);
        holder.itemView.setTag(currentBook);

        holder.price.setVisibility(View.GONE);
        holder.deliveryTxt.setVisibility(View.GONE);

        holder.title.setText(currentBook.getString("title"));
        // Set the title to one line and add ellipsis if it exceeds
        holder.title.setEllipsize(TextUtils.TruncateAt.END);
        holder.title.setMaxLines(1);

        // Set the book image
        ArrayList<BookImageModels> bookImageModelsArrayList = currentBook.getImages();
        if (!bookImageModelsArrayList.isEmpty()) {
            String imageUrl = currentBook.getImages().get(0).getUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .error(R.drawable.noimage)
                    .placeholder(R.drawable.noimage)
                    .into(holder.bookImg);
        } else {
            Glide.with(context)
                    .load(R.drawable.noimage)
                    .into(holder.bookImg);
        }
    }


    @Override
    public int getItemCount() {
        return allBooksModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price,deliveryTxt;
        ImageView bookImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            price = itemView.findViewById(R.id.bookPriceInfo);
            deliveryTxt = itemView.findViewById(R.id.deliveryTypeTxt);
            bookImg = itemView.findViewById(R.id.imgBook);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = allBooksModelArrayList.get(getAdapterPosition()).getString("title");
                    Intent intent = new Intent(context, PurchasedEBookViewingBookActivity.class);
                    intent.putExtra("bookId", allBooksModelArrayList.get(getAdapterPosition()).getString("_id"));
                    intent.putExtra("title", title);
                    Log.e("Title", title);
                    context.startActivity(intent);
                }
            });
        }
    }
}
