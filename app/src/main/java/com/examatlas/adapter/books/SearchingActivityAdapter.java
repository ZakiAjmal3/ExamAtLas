package com.examatlas.adapter.books;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
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
import com.examatlas.models.HardBookECommPurchaseModel;

import java.util.ArrayList;
import java.util.Collections;

public class SearchingActivityAdapter extends RecyclerView.Adapter<SearchingActivityAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList;
    private final ArrayList<HardBookECommPurchaseModel> originalHardBookECommPurchaseModelArrayList;

    private String currentQuery = "";

    public SearchingActivityAdapter(Context context, ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList) {
        this.originalHardBookECommPurchaseModelArrayList = new ArrayList<>(hardBookECommPurchaseModelArrayList);
        this.hardBookECommPurchaseModelArrayList = new ArrayList<>(originalHardBookECommPurchaseModelArrayList);        this.context = context;
    }

    @NonNull
    @Override
    public SearchingActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_searching_activity_item_single_layout, parent, false);
        return new SearchingActivityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchingActivityAdapter.ViewHolder holder, int position) {
        HardBookECommPurchaseModel currentBook = hardBookECommPurchaseModelArrayList.get(hardBookECommPurchaseModelArrayList.size() - 1 - position);
        holder.itemView.setTag(currentBook);

        holder.setHighlightedText(holder.title, currentBook.getTitle(), currentQuery);

        // Calculate prices and discount
        String purchasingPrice = currentBook.getSellPrice();
        String originalPrice = currentBook.getPrice();
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

        holder.setHighlightedPrice(holder.price, spannableText, currentQuery);

//        BookImageAdapter bookImageAdapter = new BookImageAdapter((ArrayList<BookImageModels>) currentBook.getImages());
//        holder.viewPager.setAdapter(bookImageAdapter);
        Glide.with(context)
                .load(R.drawable.book1)
                .error(R.drawable.book1)
                .placeholder(R.drawable.book1)
                .into(holder.bookImg);

    }

    @Override
    public int getItemCount() {
        return hardBookECommPurchaseModelArrayList.size();
    }
    public void filter(String query) {
        currentQuery = query;
        hardBookECommPurchaseModelArrayList.clear();
        if (query.isEmpty()) {
            hardBookECommPurchaseModelArrayList.addAll(originalHardBookECommPurchaseModelArrayList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (HardBookECommPurchaseModel bookModel : originalHardBookECommPurchaseModelArrayList) {
                if (bookModel.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getContent().toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getTags().toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getPrice().toLowerCase().contains(lowerCaseQuery)) {
                    hardBookECommPurchaseModelArrayList.add(bookModel);
                }
            }
        }
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        //        ViewPager2 viewPager;
        ImageView bookImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImg = itemView.findViewById(R.id.bookImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleBookDetailsActivity.class);
                    intent.putExtra("bookID",hardBookECommPurchaseModelArrayList.get(getAbsoluteAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
        public void setHighlightedText(TextView textView, String text, String query) {
            if (query == null || query.isEmpty()) {
                textView.setText(text);
                return;
            }
            SpannableString spannableString = new SpannableString(text);
            int startIndex = text.toLowerCase().indexOf(query.toLowerCase());
            while (startIndex >= 0) {
                int endIndex = startIndex + query.length();
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = text.toLowerCase().indexOf(query.toLowerCase(), endIndex);
            }
            textView.setText(spannableString);
        }

        public void setHighlightedPrice(TextView textView, SpannableStringBuilder priceText, String query) {
            if (query == null || query.isEmpty()) {
                textView.setText(priceText);
                return;
            }

            SpannableString spannableString = SpannableString.valueOf(priceText);
            int startIndex = priceText.toString().toLowerCase().indexOf(query.toLowerCase());
            while (startIndex >= 0) {
                int endIndex = startIndex + query.length();
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = priceText.toString().toLowerCase().indexOf(query.toLowerCase(), endIndex);
            }
            textView.setText(spannableString);
        }
    }
}
