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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.BookImageModels;

import java.util.ArrayList;

public class HomeFragmentBookAdapter extends RecyclerView.Adapter<HomeFragmentBookAdapter.ViewHolder> {
    private final Fragment context;
    private final ArrayList<AllBooksModel> allBooksModelArrayList;

    public HomeFragmentBookAdapter(Fragment context, ArrayList<AllBooksModel> allBooksModelArrayList) {
        this.allBooksModelArrayList = new ArrayList<>(allBooksModelArrayList);
        this.context = context;
    }

    @NonNull
    @Override
    public HomeFragmentBookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_books_item_home_fragment_layout, parent, false);
        return new HomeFragmentBookAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFragmentBookAdapter.ViewHolder holder, int position) {
        AllBooksModel currentBook = allBooksModelArrayList.get(position);
        holder.itemView.setTag(currentBook);

        // Set the title
        holder.title.setText(currentBook.getString("title"));

        // Get prices as strings
        String purchasingPrice = currentBook.getString("sellingPrice");
        String originalPrice = currentBook.getString("price");

        // Check if the prices are valid (non-empty and numeric)
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
        TextView title, price;
        //        ViewPager2 viewPager;
        ImageView bookImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtBookTitle);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImg = itemView.findViewById(R.id.imgBook);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getContext(), SingleBookDetailsActivity.class);
                    intent.putExtra("bookID", allBooksModelArrayList.get(getAdapterPosition()).getString("_id"));
                    context.startActivity(intent);
                }
            });
        }
    }
}
