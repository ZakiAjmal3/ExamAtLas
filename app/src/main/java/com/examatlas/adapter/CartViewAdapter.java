package com.examatlas.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.CartViewActivity;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.CartViewModel;
import com.examatlas.models.extraModels.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<CartViewModel> cartViewModelArrayList;
    private final SessionManager sessionManager;
    private final String authToken;
    private final String[] quantityArray = {"1", "2", "3", "more"};

    public CartViewAdapter(Context context, ArrayList<CartViewModel> cartViewModelArrayList) {
        this.context = context;
        this.cartViewModelArrayList = cartViewModelArrayList;
        sessionManager = new SessionManager(context);
        authToken = sessionManager.getUserData().get("authToken");
    }

    @NonNull
    @Override
    public CartViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hardbook_cart_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewAdapter.ViewHolder holder, int position) {
        CartViewModel currentBook = cartViewModelArrayList.get(position);
        holder.title.setText(currentBook.getTitle());
        holder.author.setText(currentBook.getAuthor());

        // Calculate prices and discounts if applicable
        String purchasingPrice = currentBook.getSellPrice(); // Assuming this is the selling price
        String originalPrice = currentBook.getPrice(); // You need to ensure you have the original price
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

        // Set the spannable text to holder
        holder.price.setText(spannableText);

        BookImageAdapter bookImageAdapter = new BookImageAdapter(currentBook.getBookImageArrayList());
        holder.bookImage.setAdapter(bookImageAdapter);

        // Set quantity
        holder.quantityTxt.setText("Qty: " + currentBook.getQuantity());

        holder.quantityTxt.setOnClickListener(view -> showQuantityOptions(currentBook, holder));

        holder.deleteBookBtn.setOnClickListener(view -> new MaterialAlertDialogBuilder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item from the cart?")
                .setPositiveButton("DELETE", (dialogInterface, i) -> deleteBook(currentBook))
                .setNegativeButton("CANCEL", (dialogInterface, i) -> {})
                .show());
    }


    private void showQuantityOptions(CartViewModel currentBook, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Quantity")
                .setItems(quantityArray, (dialog, which) -> {
                    String selectedQuantity = quantityArray[which];

                    if (selectedQuantity.equals("more")) {
                        showCustomQuantityDialog(currentBook, holder);
                    } else {
                        int quantity = Integer.parseInt(selectedQuantity);
                        holder.quantityTxt.setText("Qty: " + selectedQuantity);
                        updateQuantity(currentBook, quantity, holder);
                    }
                });

        builder.create().show();
    }

    private void showCustomQuantityDialog(CartViewModel currentBook, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Quantity");

        // Create a LinearLayout to hold the EditText
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText input = new EditText(context);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        // Set margins for the EditText
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int marginInDp = 20; // Adjust margin as needed
        float scale = context.getResources().getDisplayMetrics().density;
        int marginInPx = (int) (marginInDp * scale + 0.5f); // Convert dp to pixels
        params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);

        input.setLayoutParams(params);
        layout.addView(input);
        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String quantityStr = input.getText().toString();
            try {
                int quantity = Integer.parseInt(quantityStr);

                // Validate the quantity
                if (quantity < 1 || quantity > 100) {
                    Toast.makeText(context, "Please enter a valid quantity (1-100)", Toast.LENGTH_SHORT).show();
                    return;
                }

                holder.quantityTxt.setText("Qty: " + quantity);
                updateQuantity(currentBook, quantity, holder);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }


    private void updateQuantity(CartViewModel currentBook, int quantity, ViewHolder holder) {
        holder.quantityProgressbar.setVisibility(View.VISIBLE);
        holder.quantityTxt.setVisibility(View.GONE);

        String updateUrl = Constant.BASE_URL + "cart/update";
        String userId = sessionManager.getUserData().get("user_id");
        String itemId = currentBook.getItemId();

        if (userId == null || itemId == null) {
            Toast.makeText(context, "User ID or Item ID is null", Toast.LENGTH_LONG).show();
            holder.quantityProgressbar.setVisibility(View.GONE);
            holder.quantityTxt.setVisibility(View.VISIBLE);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("itemId", itemId);
            jsonObject.put("quantity", quantity);
        } catch (JSONException e) {
            Toast.makeText(context, "Error creating JSON", Toast.LENGTH_SHORT).show();
            holder.quantityProgressbar.setVisibility(View.GONE);
            holder.quantityTxt.setVisibility(View.VISIBLE);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, updateUrl, jsonObject,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        if (success) {
                            holder.quantityTxt.setText(String.valueOf("Qty: " + quantity));
                            ((CartViewActivity) context).fetchCartItems();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error processing response", Toast.LENGTH_SHORT).show();
                    } finally {
                        holder.quantityProgressbar.setVisibility(View.GONE);
                        holder.quantityTxt.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    holder.quantityProgressbar.setVisibility(View.GONE);
                    holder.quantityTxt.setVisibility(View.VISIBLE);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                if (authToken != null && !authToken.isEmpty()) {
                    headers.put("Authorization", "Bearer " + authToken);
                }
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void deleteBook(CartViewModel currentBook) {
        String deleteUrl = Constant.BASE_URL + "cart/remove";
        String userId = sessionManager.getUserData().get("user_id");
        String itemId = currentBook.getItemId();

        Log.d("DeleteBookRequest", "UserId: " + userId);
        Log.d("DeleteBookRequest", "ItemId: " + itemId);

        if (userId == null || itemId == null) {
            Log.e("DeleteBookRequest", "UserId or ItemId is null");
            Toast.makeText(context, "User ID or Item ID is null", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("itemId", itemId);
        } catch (JSONException e) {
            Log.e("DeleteBookRequest", "JSON Exception: " + e.getMessage());
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, deleteUrl, jsonObject,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        if (success) {
                            int position = cartViewModelArrayList.indexOf(currentBook);
                            if (position != -1) {
                                cartViewModelArrayList.remove(position);
                                notifyItemRemoved(position);
                            } else {
                                Toast.makeText(context, "Item not found in the cart", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error processing response", Toast.LENGTH_SHORT).show();
                        Log.e("DeleteCartItemsError", "JSON Exception: " + e.getMessage());
                    }
                }, error -> {
            String errorMessage = "Error: " + error.toString();
            if (error.networkResponse != null) {
                try {
                    String responseData = new String(error.networkResponse.data, "UTF-8");
                    errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                    errorMessage += "\nResponse Data: " + responseData;
                } catch (Exception e) {
                    Log.e("DeleteCartItemsError", "Error reading response data: " + e.getMessage());
                }
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            Log.e("DeleteCartItemsError", errorMessage);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                if (authToken != null && !authToken.isEmpty()) {
                    headers.put("Authorization", "Bearer " + authToken);
                }
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return cartViewModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, price, quantityTxt;
        ImageView deleteBookBtn;
        ViewPager2 bookImage;
        ProgressBar quantityProgressbar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImage = itemView.findViewById(R.id.bookImage);
            deleteBookBtn = itemView.findViewById(R.id.deleteBookBtn);
            quantityTxt = itemView.findViewById(R.id.quantityTxtView);
            quantityProgressbar = itemView.findViewById(R.id.quantityProgressbar);
        }
    }
}
