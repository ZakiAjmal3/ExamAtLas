package com.examatlas.adapter.books;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.Books.DeliveryAddressInputActivity;

import com.examatlas.models.Books.DeliveryAddressItemModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeliveryAddressItemAdapter extends RecyclerView.Adapter<DeliveryAddressItemAdapter.ViewHolder> {
    private final Context context;
    Fragment fragment;
    private final ArrayList<DeliveryAddressItemModel> deliveryAddressItemModelArrayList;
    private int selectedPosition = -1;
    SessionManager sessionManager;
    String token;
    private final String[] threeDotsArray = {"Edit", "Delete"};
    ProgressDialog progressDialog;
    String selectedBillingId = "";
    public DeliveryAddressItemAdapter(Context context, ArrayList<DeliveryAddressItemModel> deliveryAddressItemModelArrayList, Fragment fragment,String selectedBillingId) {
        this.context = context;
        this.fragment = fragment;
        this.deliveryAddressItemModelArrayList = deliveryAddressItemModelArrayList;
        this.sessionManager = new SessionManager(context);
        token = sessionManager.getUserData().get("authToken");
        this.selectedBillingId = selectedBillingId;

    }
    @NonNull
    @Override
    public DeliveryAddressItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_address_item_layout, parent, false);
        return new DeliveryAddressItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryAddressItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get the current delivery address model
        DeliveryAddressItemModel currentAddress = deliveryAddressItemModelArrayList.get(position);
        holder.itemView.setTag(currentAddress);

        // Set the name and pin code text
        String namePinCodeStr = currentAddress.getFirstName() + " " + currentAddress.getLastName() + ", " + currentAddress.getPinCode();
        holder.namePinCode.setText(namePinCodeStr);

        // Set the full address
        String addressFullStr = currentAddress.getHouseNoOrApartmentNo() + " " +
                currentAddress.getStreetAddress() + " " +
                currentAddress.getTownCity() + " " +
                currentAddress.getState() + " " +
                currentAddress.getCountryName();
        holder.addressFull.setText(addressFullStr);

        // Set the phone number
        holder.addressPhone.setText(currentAddress.getPhone());

        // Set the radio button checked state based on the selected position
        if (deliveryAddressItemModelArrayList.get(position).getSelected().equals("1")) {
            selectedBillingId = currentAddress.getBillingId();
            selectedPosition = position;
        }
        holder.radioButton.setChecked(position == selectedPosition);
        // Handle the three dots options for edit and delete
        holder.threeDotsBtn.setOnClickListener(view -> showThreeDotsOptions(currentAddress, position));

        // Handle radio button click
        holder.radioButton.setOnClickListener(v -> {
            if (selectedPosition != position) {
                int oldSelectedPosition = selectedPosition;
                selectedPosition = position; // Update the selected position

                // Update the "selected" flag in the model
                for (int i = 0; i < deliveryAddressItemModelArrayList.size(); i++) {
                    if (i == position) {
                        deliveryAddressItemModelArrayList.get(i).setSelected("true"); // Mark the new selected address
                    } else {
                        deliveryAddressItemModelArrayList.get(i).setSelected("false"); // Unmark other addresses
                    }
                }
                selectedBillingId = currentAddress.getBillingId(); // Update the selected address ID
                notifyItemChanged(oldSelectedPosition); // Uncheck the previous radio button
                notifyItemChanged(selectedPosition); // Check the new radio button
            }
        });
    }


    private void showThreeDotsOptions(DeliveryAddressItemModel currentBook, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(null)
                .setItems(threeDotsArray, (dialog, which) -> {
                    String selectedItems = threeDotsArray[which];
                    choseItems(currentBook, selectedItems, position);
                });
        builder.create().show();
    }

    private void choseItems(DeliveryAddressItemModel currentBook, String selectedItems, int position) {
        if (selectedItems.equals("Edit")){
            String id = currentBook.getBillingId();
            String addresstype = currentBook.getAddressType();
            String firstname= currentBook.getFirstName();
            String lastname = currentBook.getLastName();
            String email = currentBook.getEmailAddress();
            String phone = currentBook.getPhone();
            String apartment = currentBook.getHouseNoOrApartmentNo();
            String street = currentBook.getStreetAddress();
            String city = currentBook.getTownCity();
            String state = currentBook.getState();
            String country = currentBook.getCountryName();
            String pincode = currentBook.getPinCode();

            ((DeliveryAddressInputActivity) context).editingAddress(id,addresstype,firstname,lastname,email,phone,apartment,street,city,state,country,pincode);
        }else if (selectedItems.equals("Delete")){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Deleting address...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            deleteAddress(currentBook,position);
        }
    }

    private void deleteAddress(DeliveryAddressItemModel deliveryAddressItemModel,int position) {
        String billingId = deliveryAddressItemModel.getBillingId();
        String paginatedURL = Constant.BASE_URL + "v1/address/delete/" + billingId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                deliveryAddressItemModelArrayList.remove(deliveryAddressItemModel);
                                notifyItemRemoved(position);
                                if (deliveryAddressItemModelArrayList.isEmpty()) {
                                    ((DeliveryAddressInputActivity) context).loadFragment(fragment);
                                }
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("BlogFetchError", errorMessage);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public String getSelectedBillingId() {
        return selectedBillingId;
    }

    @Override
    public int getItemCount() {
        return deliveryAddressItemModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView namePinCode, addressFull, addressPhone;
        RadioButton radioButton;
        ImageView threeDotsBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namePinCode = itemView.findViewById(R.id.nameAndPincodeTxt);
            addressFull = itemView.findViewById(R.id.addressFullTxt);
            addressPhone = itemView.findViewById(R.id.addressPhoneTxt);
            threeDotsBtn = itemView.findViewById(R.id.changeAddressBtn);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}
