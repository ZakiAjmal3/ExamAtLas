package com.examatlas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.R;
import com.examatlas.models.DeliveryAddressModel;

import java.util.ArrayList;

public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.ViewHolder> {

    Context context;
    ArrayList<DeliveryAddressModel> deliveryAddressModelArrayList;
    public int selectedPosition = -1; // Variable to track the selected position

    public DeliveryAddressAdapter(Context context, ArrayList<DeliveryAddressModel> deliveryAddressModelArrayList) {
        this.context = context;
        this.deliveryAddressModelArrayList = deliveryAddressModelArrayList;
    }

    @NonNull
    @Override
    public DeliveryAddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_address_item_layout, parent, false);
        return new DeliveryAddressAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryAddressAdapter.ViewHolder holder, int position) {
        String fullNameSTR = deliveryAddressModelArrayList.get(position).getFirstName() + " " + deliveryAddressModelArrayList.get(position).getLastName();
        String fullAddressSTR = deliveryAddressModelArrayList.get(position).getHouseNoOrApartmentNo() + ", " +
                deliveryAddressModelArrayList.get(position).getStreetAddress() + ", " +
                deliveryAddressModelArrayList.get(position).getTownCity() + ", " +
                deliveryAddressModelArrayList.get(position).getState() + ", " +
                deliveryAddressModelArrayList.get(position).getPinCode() + ", " +
                deliveryAddressModelArrayList.get(position).getCountryName() + ", " +
                deliveryAddressModelArrayList.get(position).getPhone() + ", " +
                deliveryAddressModelArrayList.get(position).getEmailAddress() + ".";

        holder.fullName.setText(fullNameSTR);
        holder.fullAddress.setText(fullAddressSTR);

        // Set the RadioButton state based on the selected position
        holder.radioButton.setChecked(position == selectedPosition);

        // Set OnClickListener for the RadioButton
        holder.radioButton.setOnClickListener(v -> {
            // Update selected position
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged(); // Refresh the RecyclerView
        });
    }

    @Override
    public int getItemCount() {
        return deliveryAddressModelArrayList.size();
    }
    public DeliveryAddressModel getSelectedAddress() {
        if (selectedPosition != -1) {
            return deliveryAddressModelArrayList.get(selectedPosition);
        }
        return null; // No address selected
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, fullAddress;
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.addressNameTxt);
            fullAddress = itemView.findViewById(R.id.addressFullTxt);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}
