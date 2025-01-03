package com.examatlas.adapter.extraAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.models.extraModels.BookOrderSummaryItemsDetailsRecyclerViewModel;

import java.util.ArrayList;

public class BookOrderSummaryItemsDetailsRecyclerViewAdapter extends RecyclerView.Adapter<BookOrderSummaryItemsDetailsRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<BookOrderSummaryItemsDetailsRecyclerViewModel> bookOrderSummaryItemsDetailsRecyclerViewModelArrayList;

    public BookOrderSummaryItemsDetailsRecyclerViewAdapter(Context context, ArrayList<BookOrderSummaryItemsDetailsRecyclerViewModel> bookOrderSummaryItemsDetailsRecyclerViewModelArrayList) {
        this.context = context;
        this.bookOrderSummaryItemsDetailsRecyclerViewModelArrayList = bookOrderSummaryItemsDetailsRecyclerViewModelArrayList;
    }

    @NonNull
    @Override
    public BookOrderSummaryItemsDetailsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_order_summary_items_details_recyclerview_layout, parent, false);
        return new BookOrderSummaryItemsDetailsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookOrderSummaryItemsDetailsRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(position);

        String itemTitleAndQuantity = bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.get(position).getItemName() + " x " + bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.get(position).getItemQuantity();
        holder.itemTitle.setText(itemTitleAndQuantity);

        int itemPrice = Integer.parseInt(bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.get(position).getItemQuantity()) * Integer.parseInt(bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.get(position).getItemPrice());
        holder.itemPrice.setText("₹" + itemPrice + ".00");
    }

    @Override
    public int getItemCount() {
        return bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle,itemPrice;;

        public ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemPrice = itemView.findViewById(R.id.itemPrice);
        }
    }
}
