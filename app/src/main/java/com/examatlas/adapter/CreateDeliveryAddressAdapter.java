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
import com.examatlas.models.CreateDeliveryAddressModel;

import java.util.ArrayList;

public class CreateDeliveryAddressAdapter extends RecyclerView.Adapter<CreateDeliveryAddressAdapter.ViewHolder> {

    Context context;
    ArrayList<CreateDeliveryAddressModel> createDeliveryAddressModelArrayList;
    public int selectedPosition = -1; // Variable to track the selected position

    public CreateDeliveryAddressAdapter(Context context, ArrayList<CreateDeliveryAddressModel> createDeliveryAddressModelArrayList) {
        this.context = context;
        this.createDeliveryAddressModelArrayList = createDeliveryAddressModelArrayList;
    }

    @NonNull
    @Override
    public CreateDeliveryAddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_address_item_layout, parent, false);
        return new CreateDeliveryAddressAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateDeliveryAddressAdapter.ViewHolder holder, int position) {
        String fullNameSTR = createDeliveryAddressModelArrayList.get(position).getFirstName() + " " + createDeliveryAddressModelArrayList.get(position).getLastName();
        String fullAddressSTR = createDeliveryAddressModelArrayList.get(position).getHouseNoOrApartmentNo() + ", " +
                createDeliveryAddressModelArrayList.get(position).getStreetAddress() + ", " +
                createDeliveryAddressModelArrayList.get(position).getTownCity() + ", " +
                createDeliveryAddressModelArrayList.get(position).getState() + ", " +
                createDeliveryAddressModelArrayList.get(position).getPinCode() + ", " +
                createDeliveryAddressModelArrayList.get(position).getCountryName() + ", " +
                createDeliveryAddressModelArrayList.get(position).getPhone() + ", " +
                createDeliveryAddressModelArrayList.get(position).getEmailAddress() + ".";

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
        return createDeliveryAddressModelArrayList.size();
    }
    public CreateDeliveryAddressModel getSelectedAddress() {
        if (selectedPosition != -1) {
            return createDeliveryAddressModelArrayList.get(selectedPosition);
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
