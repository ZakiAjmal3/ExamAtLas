package com.examatlas.adapter.books;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.models.Books.SingleOrderViewItemBookModel;

import java.util.ArrayList;

public class SingleOrderViewBookItemAdapter extends RecyclerView.Adapter<SingleOrderViewBookItemAdapter.ViewHolder> {
    Context context;
    ArrayList<SingleOrderViewItemBookModel> singleOrderViewItemBookModelArrayList;
    public SingleOrderViewBookItemAdapter(Context context, ArrayList<SingleOrderViewItemBookModel> SingleOrderViewItemBookModelArrayList) {
        this.context = context;
        this.singleOrderViewItemBookModelArrayList = SingleOrderViewItemBookModelArrayList;
    }

    @NonNull
    @Override
    public SingleOrderViewBookItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_order_view_book_item_layout, parent, false);
        return new SingleOrderViewBookItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleOrderViewBookItemAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(singleOrderViewItemBookModelArrayList.get(position));

        holder.title.setText(singleOrderViewItemBookModelArrayList.get(position).getBookTitle());
        holder.title.setEllipsize(TextUtils.TruncateAt.END);
        holder.title.setMaxLines(2);

        holder.author.setText(singleOrderViewItemBookModelArrayList.get(position).getBookAuthor());
        holder.description.setText(singleOrderViewItemBookModelArrayList.get(position).getBookPublication());
        holder.description.setEllipsize(TextUtils.TruncateAt.END);
        holder.description.setMaxLines(2);
        // Get prices as strings
        String purchasingPrice = singleOrderViewItemBookModelArrayList.get(position).getBookSellingPrice();
        String originalPrice = singleOrderViewItemBookModelArrayList.get(position).getBookPrice();

        // Initialize prices
        int purchasingPriceInt = 0;
        int originalPriceInt = 0;

        try {
            // Parse the prices only if they are non-empty and valid numbers
            if (!purchasingPrice.isEmpty()) {
                purchasingPriceInt = Integer.parseInt(purchasingPrice);
            }
            if (!originalPrice.isEmpty()) {
                originalPriceInt = Integer.parseInt(originalPrice);
            }

            // Calculate discount only if both prices are valid
            if (originalPriceInt > 0 && purchasingPriceInt > 0) {
                int discount = purchasingPriceInt * 100 / originalPriceInt;
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
            } else {
                // Fallback: if prices are invalid, show default values or error message
                holder.price.setText("Invalid Price");
            }

        } catch (NumberFormatException e) {
            // Handle any parsing exceptions
            e.printStackTrace(); // Optional: log the error
            holder.price.setText("Invalid Price");
        }
        Glide.with(context)
                .load(singleOrderViewItemBookModelArrayList.get(position).getBookImage())
                .error(R.drawable.noimage)
                .into(holder.bookImg);
    }

    @Override
    public int getItemCount() {
        return singleOrderViewItemBookModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,author,description,price;
        ImageView bookImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            description = itemView.findViewById(R.id.bookDescription);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImg = itemView.findViewById(R.id.imgBook);
        }
    }
}
