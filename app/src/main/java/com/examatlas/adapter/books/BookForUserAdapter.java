package com.examatlas.adapter.books;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.BookImageModels;

import java.util.ArrayList;

public class BookForUserAdapter extends RecyclerView.Adapter<BookForUserAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<AllBooksModel> allBooksModelArrayList;

    public BookForUserAdapter(Context context, ArrayList<AllBooksModel> allBooksModelArrayList) {
        this.allBooksModelArrayList = new ArrayList<>(allBooksModelArrayList);
        this.context = context;
    }

    @NonNull
    @Override
    public BookForUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hardcopybook_item_list2, parent, false);
        return new BookForUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookForUserAdapter.ViewHolder holder, int position) {
        AllBooksModel currentBook = allBooksModelArrayList.get(position);
        holder.itemView.setTag(currentBook);

        holder.title.setText(allBooksModelArrayList.get(position).getString("title"));

        // Calculate prices and discount
        String purchasingPrice = currentBook.getString("sellingPrice");
        String originalPrice = currentBook.getString("price");
        int discount = Integer.parseInt(purchasingPrice) * 100 / Integer.parseInt(originalPrice);
        discount = 100 - discount;

        // Create a SpannableString for the original price with strikethrough
        SpannableString spannableOriginalPrice = new SpannableString("₹" + originalPrice);
        spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);

        // Create the discount text
        String discountText = "(-" + discount + "%)";
        SpannableStringBuilder spannableText = new SpannableStringBuilder();
        spannableText.append("₹" + purchasingPrice + " ");
        spannableText.append(spannableOriginalPrice);
        spannableText.append(" " + discountText);

        // Set the color for the discount percentage
        int startIndex = spannableText.length() - discountText.length();
        spannableText.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.price.setText(spannableText);

//        BookImageAdapter bookImageAdapter = new BookImageAdapter((ArrayList<BookImageModels>) currentBook.getImages());
//        holder.viewPager.setAdapter(bookImageAdapter);
        ArrayList<BookImageModels> bookImageModelsArrayList = currentBook.getImages();
        if (!bookImageModelsArrayList.isEmpty()){
            String imageUrl = currentBook.getImages().get(0).getUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .error(R.drawable.noimage)
                    .placeholder(R.drawable.noimage)
                    .into(holder.bookImg);
        }else {
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
        TextView title, price;
//        ViewPager2 viewPager;
        ImageView bookImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImg = itemView.findViewById(R.id.imgBook);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleBookDetailsActivity.class);
                    intent.putExtra("bookID", allBooksModelArrayList.get(getAdapterPosition()).getString("_id"));
                    context.startActivity(intent);
                }
            });
        }
    }
}
