package com.examatlas.adapter.books;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.BookImageModels;

import java.util.ArrayList;

public class SearchingActivityAdapter extends RecyclerView.Adapter<SearchingActivityAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<AllBooksModel> allBooksModelArrayList;
    private final ArrayList<AllBooksModel> originalAllBooksModelArrayList;

    private String currentQuery = "";

    public SearchingActivityAdapter(Context context, ArrayList<AllBooksModel> allBooksModelArrayList) {
        this.originalAllBooksModelArrayList = new ArrayList<>(allBooksModelArrayList);
        this.allBooksModelArrayList = new ArrayList<>(originalAllBooksModelArrayList);
        this.context = context;
    }

    @NonNull
    @Override
    public SearchingActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_searching_activity_item_single_layout, parent, false);
        return new SearchingActivityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchingActivityAdapter.ViewHolder holder, int position) {
        AllBooksModel currentBook = allBooksModelArrayList.get(position);
        holder.itemView.setTag(currentBook);

        // Set the highlighted title (search term highlighting)
        holder.setHighlightedText(holder.title, currentBook.getString("title"), currentQuery);
        holder.title.setEllipsize(TextUtils.TruncateAt.END);
        holder.title.setMaxLines(2);

        // Get prices as strings
        String purchasingPrice = currentBook.getString("sellingPrice");
        String originalPrice = currentBook.getString("price");

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

                holder.setHighlightedPrice(holder.price, spannableText, currentQuery);
            } else {
                // Fallback: if prices are invalid, show default values or error message
                holder.price.setText("Invalid Price");
            }

        } catch (NumberFormatException e) {
            // Handle any parsing exceptions
            e.printStackTrace(); // Optional: log the error
            holder.price.setText("Invalid Price");
        }

        // Set the book images (assuming you have an adapter handling images for each book)
        BookImageAdapter bookImageAdapter = new BookImageAdapter(
                (ArrayList<BookImageModels>) currentBook.getImages(),
                holder.bookImg,
                holder.indicatorLayout
        );
        holder.bookImg.setAdapter(bookImageAdapter);

        // Fallback image loading (if required, e.g., for placeholder handling):
        // Glide.with(context)
        //        .load(R.drawable.book1)
        //        .error(R.drawable.book1)
        //        .placeholder(R.drawable.book1)
        //        .into(holder.bookImg);
    }


    @Override
    public int getItemCount() {
        return allBooksModelArrayList.size();
    }
    public void filter(String query) {
        currentQuery = query;
        allBooksModelArrayList.clear();
        if (query.isEmpty()) {
            allBooksModelArrayList.addAll(originalAllBooksModelArrayList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AllBooksModel bookModel : originalAllBooksModelArrayList) {
                if (bookModel.getString("title").toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getString("description").toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getString("tags").toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getString("price").toLowerCase().contains(lowerCaseQuery)) {
                    allBooksModelArrayList.add(bookModel);
                }
            }
        }
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        //        ViewPager2 viewPager;
        ViewPager2 bookImg;
        LinearLayout indicatorLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImg = itemView.findViewById(R.id.bookImg);
            indicatorLayout = itemView.findViewById(R.id.indicatorLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleBookDetailsActivity.class);
                    intent.putExtra("bookID", allBooksModelArrayList.get(getAbsoluteAdapterPosition()).getString("_id"));
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
