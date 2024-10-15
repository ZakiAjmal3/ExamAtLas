package com.examatlas.adapter;

import android.content.Context;
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
import com.examatlas.models.HardBookECommPurchaseModel;

import java.util.ArrayList;
import java.util.Collections;

public class HardBookECommPurchaseAdapter extends RecyclerView.Adapter<HardBookECommPurchaseAdapter.ViewHolder> {
    Context context;
    private ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList;
    private ArrayList<HardBookECommPurchaseModel> originalHardBookECommPurchaseModelArrayList;
    private String currentQuery = "";
    public HardBookECommPurchaseAdapter(Context context, ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList) {
        this.originalHardBookECommPurchaseModelArrayList = new ArrayList<>(hardBookECommPurchaseModelArrayList);
        this.hardBookECommPurchaseModelArrayList = new ArrayList<>(originalHardBookECommPurchaseModelArrayList);
        this.context = context;
    }

    @NonNull
    @Override
    public HardBookECommPurchaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hardcopybook_item_list,parent,false);
        return new HardBookECommPurchaseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HardBookECommPurchaseAdapter.ViewHolder holder, int position) {
        HardBookECommPurchaseModel currentBook = hardBookECommPurchaseModelArrayList.get(hardBookECommPurchaseModelArrayList.size() - 1 - position);
        holder.itemView.setTag(currentBook);

        String purchasingPrice = currentBook.getSellPrice();
        String originalPrice = currentBook.getPrice();
        int discount = Integer.parseInt(purchasingPrice) * 100 / Integer.parseInt(originalPrice);
        discount = 100 - discount;

        // Create a SpannableString for the original price with strikethrough
        SpannableString spannableOriginalPrice = new SpannableString("₹" + originalPrice);
        spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);

        // Create the discount text
        String discountText = "(-" + discount + "%)";

        // Create a new SpannableStringBuilder
        SpannableStringBuilder spannableText = new SpannableStringBuilder();

        // Append the purchasing price
        spannableText.append("₹" + purchasingPrice + " ");

        // Append the original price with strikethrough
        spannableText.append(spannableOriginalPrice);

        // Append the discount text
        spannableText.append(" " + discountText);

        // Set the color for the discount percentage
        int startIndex = spannableText.length() - discountText.length(); // Find start index for discount text
        int endIndex = spannableText.length(); // End index is the end of spannableText
        spannableText.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the spannable text to the holder
        holder.setHighlightedText(holder.title, currentBook.getTitle(), currentQuery);
        holder.setHighlightedText(holder.author, currentBook.getAuthor(), currentQuery);
        holder.setHighlightedPrice(holder.price, spannableText, currentQuery); // Pass spannableText directly

        Glide.with(context)
                .load(R.drawable.book1) // Load the image URL from the model
                .placeholder(R.drawable.book1) // Optional: add a placeholder image
                .error(R.drawable.book1) // Optional: add an error image
                .into(holder.bookImage);
    }

    @Override
    public int getItemCount() {
        return hardBookECommPurchaseModelArrayList.size();
    }

    public void filter(String query) {
        currentQuery = query; // Store current query
        hardBookECommPurchaseModelArrayList.clear();
        if (query.isEmpty()) {
            hardBookECommPurchaseModelArrayList.addAll(originalHardBookECommPurchaseModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (HardBookECommPurchaseModel bookModel : originalHardBookECommPurchaseModelArrayList) {
                if (bookModel.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getContent().toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getTags().toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getPrice().toLowerCase().contains(lowerCaseQuery)) {
                    hardBookECommPurchaseModelArrayList.add(bookModel); // Add matching eBook to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, price;
        ImageView bookImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImage = itemView.findViewById(R.id.imgBook);

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
                textView.setText(priceText); // Set directly as SpannableStringBuilder
                return;
            }

            // Here we are assuming you want to highlight parts of the priceText based on the query
            SpannableString spannableString = SpannableString.valueOf(priceText); // Use the original SpannableStringBuilder
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
