package com.examatlas.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.adapter.extraAdapter.BookOrderSummaryItemsDetailsRecyclerViewAdapter;
import com.examatlas.models.BookOrderHistoryModel;
import com.examatlas.models.extraModels.BookOrderSummaryItemsDetailsRecyclerViewModel;

import java.util.ArrayList;

public class BookOrderHistoryAdapter extends RecyclerView.Adapter<BookOrderHistoryAdapter.ViewHolder> {
    private ArrayList<BookOrderHistoryModel> orderHistoryModelsArrayList;
    Context context;

    public BookOrderHistoryAdapter(ArrayList<BookOrderHistoryModel> orderHistoryModelsArrayList, Context context) {
        this.orderHistoryModelsArrayList = orderHistoryModelsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookOrderHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_order_history_recycler_view_item_layout, parent, false);
        return new BookOrderHistoryAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull BookOrderHistoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.itemView.setTag(position);

        String orderIdStr = "Order ID: " + orderHistoryModelsArrayList.get(position).getOrder_id();
        holder.orderIdTxt.setText(orderIdStr);

        holder.copyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToCopy = orderHistoryModelsArrayList.get(position).getOrder_id();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Order ID", textToCopy);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, "Copied to clipboard: " + textToCopy, Toast.LENGTH_LONG).show();
            }
        });

        String amountStr = "₹ " + orderHistoryModelsArrayList.get(position).getTotalAmount();
        holder.totalAmountTxt.setTextColor(ContextCompat.getColor(context, R.color.green));
        holder.totalAmountTxt.setText(amountStr);

        String statusStr = orderHistoryModelsArrayList.get(position).getStatus();
        holder.statusTxt.setText(statusStr);

        if (statusStr.equalsIgnoreCase("Paid")) {
            holder.statusTxt.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (statusStr.equalsIgnoreCase("Pending")) {
            holder.statusTxt.setTextColor(ContextCompat.getColor(context, R.color.mat_yellow));
        } else {
            holder.statusTxt.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.methodTxt.setTextColor(ContextCompat.getColor(context, R.color.green));
        holder.methodTxt.setText(orderHistoryModelsArrayList.get(position).getPaymentMethod());

        holder.shippingToAddressTxt.setText(orderHistoryModelsArrayList.get(position).getShippingAddressFull());
        holder.shippingNameTxt.setText(orderHistoryModelsArrayList.get(position).getShippingAddressUserName());

        holder.bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.clear();
        for (int i = 0; i < orderHistoryModelsArrayList.get(position).getOrderItemsArrayList().size(); i++){
            String itemName = orderHistoryModelsArrayList.get(position).getOrderItemsArrayList().get(i).getTitle();
            String itemPrice = orderHistoryModelsArrayList.get(position).getOrderItemsArrayList().get(i).getSellPrice();
            String itemQuantity = orderHistoryModelsArrayList.get(position).getOrderItemsArrayList().get(i).getQuantity();
            holder.bookOrderSummaryItemsDetailsRecyclerViewModel = new BookOrderSummaryItemsDetailsRecyclerViewModel(itemName,itemPrice,itemQuantity);
            holder.bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.add(holder.bookOrderSummaryItemsDetailsRecyclerViewModel);
        }
        holder.bookOrderSummaryItemsDetailsRecyclerViewAdapter = new BookOrderSummaryItemsDetailsRecyclerViewAdapter(context,holder.bookOrderSummaryItemsDetailsRecyclerViewModelArrayList);
        holder.orderItemRecyclerView.setAdapter(holder.bookOrderSummaryItemsDetailsRecyclerViewAdapter);
    }

    @Override
    public int getItemCount() {
        return orderHistoryModelsArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTxt, totalAmountTxt, statusTxt, methodTxt, shippingToAddressTxt, shippingNameTxt;
        RecyclerView orderItemRecyclerView;
        ImageView copyImg;
        BookOrderSummaryItemsDetailsRecyclerViewModel bookOrderSummaryItemsDetailsRecyclerViewModel;
        BookOrderSummaryItemsDetailsRecyclerViewAdapter bookOrderSummaryItemsDetailsRecyclerViewAdapter;
        ArrayList<BookOrderSummaryItemsDetailsRecyclerViewModel> bookOrderSummaryItemsDetailsRecyclerViewModelArrayList;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderIdTxt = itemView.findViewById(R.id.orderIdTxt);
            totalAmountTxt = itemView.findViewById(R.id.priceTxt);
            statusTxt = itemView.findViewById(R.id.paidTxt);
            methodTxt = itemView.findViewById(R.id.methodTxt);
            shippingToAddressTxt = itemView.findViewById(R.id.shippingToTxtDisplay);
            shippingNameTxt = itemView.findViewById(R.id.nameTxt);
            copyImg = itemView.findViewById(R.id.copyImg);

            orderItemRecyclerView = itemView.findViewById(R.id.bookItemsSummaryRecyclerView);
            orderItemRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            bookOrderSummaryItemsDetailsRecyclerViewModelArrayList = new ArrayList<>();

        }
    }
}
