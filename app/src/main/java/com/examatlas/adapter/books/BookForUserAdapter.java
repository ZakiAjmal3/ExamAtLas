package com.examatlas.adapter.books;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.activities.CartViewActivity;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.HardBookECommPurchaseModel;
import com.examatlas.models.extraModels.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BookForUserAdapter extends RecyclerView.Adapter<BookForUserAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList;

    public BookForUserAdapter(Context context, ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList) {
        this.hardBookECommPurchaseModelArrayList = new ArrayList<>(hardBookECommPurchaseModelArrayList);
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
        HardBookECommPurchaseModel currentBook = hardBookECommPurchaseModelArrayList.get(position);
        holder.itemView.setTag(currentBook);

        holder.title.setText(hardBookECommPurchaseModelArrayList.get(position).getTitle());

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

        holder.price.setText(spannableText);

//        BookImageAdapter bookImageAdapter = new BookImageAdapter((ArrayList<BookImageModels>) currentBook.getImages());
//        holder.viewPager.setAdapter(bookImageAdapter);
        Glide.with(context)
                .load(R.drawable.book1)
                .error(R.drawable.book1)
                .placeholder(R.drawable.book1)
                .into(holder.viewPager);

    }

    @Override
    public int getItemCount() {
        return hardBookECommPurchaseModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
//        ViewPager2 viewPager;
        ImageView viewPager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            price = itemView.findViewById(R.id.bookPriceInfo);
            viewPager = itemView.findViewById(R.id.imgBook);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleBookDetailsActivity.class);
                    intent.putExtra("bookID",hardBookECommPurchaseModelArrayList.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
