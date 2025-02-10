package com.examatlas.adapter.books;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.Books.CreatingReviewActivity;
import com.examatlas.activities.Books.CustomRatingBar;
import com.examatlas.activities.Books.SingleOrderViewActivity;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.MyOrdersItemModel;
import com.examatlas.utils.SessionManager;

import java.util.ArrayList;

public class MyOrdersItemsAdapter extends RecyclerView.Adapter<MyOrdersItemsAdapter.ViewHolder> {
    Context context;
    ArrayList<MyOrdersItemModel> myOrdersItemModelArrayList;
    ArrayList<MyOrdersItemModel> orginalMyOrdersItemModelArrayList;
    private String currentQuery = "";
    SessionManager sessionManager;
    String userId;
    public MyOrdersItemsAdapter(Context context, ArrayList<MyOrdersItemModel> myOrdersItemModelArrayList) {
        this.context = context;
        this.orginalMyOrdersItemModelArrayList = new ArrayList<>(myOrdersItemModelArrayList);
        this.myOrdersItemModelArrayList = new ArrayList<>(orginalMyOrdersItemModelArrayList);
        sessionManager = new SessionManager(context);
        userId = sessionManager.getUserData().get("user_id");
    }

    @NonNull
    @Override
    public MyOrdersItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_books_order_item_layout, parent, false);
        return new MyOrdersItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrdersItemsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.writeReviewTxt.setVisibility(View.GONE);
        holder.editYourReviewTxt.setVisibility(View.GONE);

        String orderStatus = myOrdersItemModelArrayList.get(position).getOrderSuccess();
        if (orderStatus.equalsIgnoreCase("Pending")){
            holder.successTxt.setTextColor(context.getResources().getColor(R.color.red_orange));
        }
        else if (orderStatus.equalsIgnoreCase("Confirmed") || orderStatus.equalsIgnoreCase("Paid")|| orderStatus.equalsIgnoreCase("Shipped") || orderStatus.equalsIgnoreCase("Delivered")) {
            holder.successTxt.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if (orderStatus.equalsIgnoreCase("Failed") || orderStatus.equalsIgnoreCase("Cancelled")) {
            holder.successTxt.setTextColor(context.getResources().getColor(R.color.red));
        }
        if (orderStatus.equalsIgnoreCase("Delivered")){
            if (myOrdersItemModelArrayList.get(position).getReviewerUserId().equals(userId)){
                holder.writeReviewTxt.setVisibility(View.GONE);
                holder.editYourReviewTxt.setVisibility(View.VISIBLE);
            }else {
                holder.writeReviewTxt.setVisibility(View.VISIBLE);
                holder.editYourReviewTxt.setVisibility(View.GONE);
            }
        }
//        holder.successTxt.setText(orderStatus);
        holder.setHighlightedText(holder.successTxt, orderStatus, currentQuery);

//        holder.title.setText(myOrdersItemModelArrayList.get(position).getBookTitle());
        holder.setHighlightedText(holder.title, myOrdersItemModelArrayList.get(position).getBookTitle(), currentQuery);
        holder.title.setEllipsize(TextUtils.TruncateAt.END);
        holder.title.setMaxLines(2);
        String bookImgURL = myOrdersItemModelArrayList.get(position).getBookImgURL();
        Glide.with(context)
                .load(bookImgURL)
                .error(R.drawable.noimage)
                .into(holder.bookImg);

        holder.writeReviewTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreatingReviewActivity.class);
                    intent.putExtra("bookId", myOrdersItemModelArrayList.get(position).getProduct_id());
                    intent.putExtra("bookTitle", myOrdersItemModelArrayList.get(position).getBookTitle());
                    intent.putExtra("bookImg", myOrdersItemModelArrayList.get(position).getBookImgURL());
                context.startActivity(intent);
            }
        });
        String rating = myOrdersItemModelArrayList.get(position).getReviewRating();
        if (!rating.isEmpty())
            holder.ratingBar.setRating(Float.valueOf(rating));
        holder.editYourReviewTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,CreatingReviewActivity.class);{
                    intent.putExtra("bookId",myOrdersItemModelArrayList.get(position).getProduct_id());
                    intent.putExtra("bookTitle",myOrdersItemModelArrayList.get(position).getBookTitle());
                    intent.putExtra("bookImg",myOrdersItemModelArrayList.get(position).getBookImgURL());
                    intent.putExtra("reviewId",myOrdersItemModelArrayList.get(position).getReviewId());
                    intent.putExtra("rating",myOrdersItemModelArrayList.get(position).getReviewRating());
                    intent.putExtra("ratingHeadline",myOrdersItemModelArrayList.get(position).getReviewHeadline());
                    intent.putExtra("ratingTxt",myOrdersItemModelArrayList.get(position).getReviewTxt());
                    context.startActivity(intent);
                }
            }
        });
    }
    public void filter(String query) {
        currentQuery = query;
        myOrdersItemModelArrayList.clear();
        if (query.isEmpty()) {
            myOrdersItemModelArrayList.addAll(orginalMyOrdersItemModelArrayList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (MyOrdersItemModel orderModel : orginalMyOrdersItemModelArrayList) {
                if (orderModel.getOrderSuccess().toLowerCase().contains(lowerCaseQuery) ||
                        orderModel.getBookTitle().toLowerCase().contains(lowerCaseQuery))
                {
                    myOrdersItemModelArrayList.add(orderModel);
                }
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return myOrdersItemModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView successTxt, title,writeReviewTxt,editYourReviewTxt;
        ImageView bookImg;
        CustomRatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            successTxt = itemView.findViewById(R.id.txtSuccess);
            title = itemView.findViewById(R.id.txtBookTitle);
            bookImg = itemView.findViewById(R.id.imgBook);
            writeReviewTxt = itemView.findViewById(R.id.writeReviewTxt);
            editYourReviewTxt = itemView.findViewById(R.id.editYourReviewTxt);
            ratingBar = itemView.findViewById(R.id.ratingBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleOrderViewActivity.class);
                    intent.putExtra("orderId", myOrdersItemModelArrayList.get(getAdapterPosition()).getOrderId());
                    intent.putExtra("shipment_id", myOrdersItemModelArrayList.get(getAdapterPosition()).getShipment_id());
                    intent.putExtra("orderStatus", myOrdersItemModelArrayList.get(getAdapterPosition()).getOrderSuccess());
                    context.startActivity(intent);
                }
            });
        }
        public void setHighlightedText(TextView textView, String text, String query) {
            if (query == null || query.isEmpty()) {
                textView.setText(text);
                return;
            }
            Log.d("Highlight", "Text: " + text + " | Query: " + query);  // Debugging log
            String lowerCaseText = text.toLowerCase();
            String lowerCaseQuery = query.toLowerCase();
            SpannableString spannableString = new SpannableString(text);
            int startIndex = lowerCaseText.indexOf(lowerCaseQuery);

            while (startIndex >= 0) {
                int endIndex = startIndex + lowerCaseQuery.length();
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = lowerCaseText.indexOf(lowerCaseQuery, endIndex);
            }

            textView.setText(spannableString);
        }
    }
}
