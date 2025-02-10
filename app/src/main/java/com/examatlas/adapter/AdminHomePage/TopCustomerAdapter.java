package com.examatlas.adapter.AdminHomePage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.models.AdminHomePage.TopCustomersModel;
import com.examatlas.utils.SessionManager;

import java.util.ArrayList;

public class TopCustomerAdapter extends RecyclerView.Adapter<TopCustomerAdapter.ViewHolder>{
    private final Context context;
    private final ArrayList<TopCustomersModel> topCustomersModelArrayList;
    SessionManager sessionManager;
    public TopCustomerAdapter(Context context, ArrayList<TopCustomersModel> topCustomersModelArrayList) {
        this.topCustomersModelArrayList = new ArrayList<>(topCustomersModelArrayList);
        this.context = context;
// Check if context is valid before initializing SessionManager
        if (context != null) {
            sessionManager = new SessionManager(context.getApplicationContext());
        }
    }

    @NonNull
    @Override
    public TopCustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_home_top_customer_item_layout, parent, false);
        return new TopCustomerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopCustomerAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(topCustomersModelArrayList.size());
        Character firstChar = topCustomersModelArrayList.get(position).getCustomerName().charAt(0);
        holder.profile1CharTxt.setText(firstChar.toString().toUpperCase());
        holder.userNameTxt.setText(topCustomersModelArrayList.get(position).getCustomerName());
        holder.userEmailTxt.setText(topCustomersModelArrayList.get(position).getCustomerEmail());
        holder.noOfOrdersTxt.setText("Orders: "+topCustomersModelArrayList.get(position).getNumberOfOrders());

    }

    @Override
    public int getItemCount() {
        return topCustomersModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView profile1CharTxt, userNameTxt, userEmailTxt, noOfOrdersTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile1CharTxt = itemView.findViewById(R.id.profileTxt);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            userEmailTxt = itemView.findViewById(R.id.userEmailTxt);
            noOfOrdersTxt = itemView.findViewById(R.id.noOfOrdersTxt);
        }
    }
}

