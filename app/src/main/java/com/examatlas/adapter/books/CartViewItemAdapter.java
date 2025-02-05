package com.examatlas.adapter.books;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.Books.CartViewActivity;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.activities.LoginWithEmailActivity;
import com.examatlas.models.Books.CartItemModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartViewItemAdapter extends RecyclerView.Adapter<CartViewItemAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<CartItemModel> allBooksModelArrayList;
    private final SessionManager sessionManager;
    private final String authToken;
    private final String[] quantityArray = {"1", "2", "3", "more"};
    Dialog progressDialog;
    public CartViewItemAdapter(Context context, ArrayList<CartItemModel> allBooksModelArrayList) {
        this.allBooksModelArrayList = new ArrayList<>(allBooksModelArrayList);
        this.context = context;
        sessionManager = new SessionManager(context);
        authToken = sessionManager.getUserData().get("authToken");
    }

    @NonNull
    @Override
    public CartViewItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_item_cart_view_recycler_item_layout, parent, false);
        return new CartViewItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewItemAdapter.ViewHolder holder, int position) {
        CartItemModel currentBook = allBooksModelArrayList.get(position);
        holder.itemView.setTag(currentBook);

        holder.title.setText(currentBook.getTitle());
        // Set the title to one line and add ellipsis if it exceeds
        holder.title.setEllipsize(TextUtils.TruncateAt.END);
        holder.title.setMaxLines(2);

        // Get prices as strings
        String purchasingPrice = currentBook.getSellingPrice();
        String originalPrice = currentBook.getPrice();

        // Initialize prices
        double purchasingPriceDouble = 0;
        double originalPriceDouble = 0;

        try {
            // Parse the prices only if they are non-empty and valid numbers
            if (!purchasingPrice.isEmpty()) {
                purchasingPriceDouble = Double.parseDouble(purchasingPrice);
            }
            if (!originalPrice.isEmpty()) {
                originalPriceDouble = Double.parseDouble(originalPrice);
            }

            // Calculate discount only if both prices are valid
            if (originalPriceDouble > 0 && purchasingPriceDouble > 0) {
                // Safely calculate the discount using double values
                double discount = (1 - (purchasingPriceDouble / originalPriceDouble)) * 100;

                // Create a SpannableString for the original price with strikethrough
                SpannableString spannableOriginalPrice = new SpannableString("₹" + originalPrice);
                spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);

                // Create the discount text
                String discountText = "(-" + Math.round(discount) + "%)";
                SpannableStringBuilder spannableText = new SpannableStringBuilder();
                spannableText.append("₹" + purchasingPrice + " ");
                spannableText.append(spannableOriginalPrice);
                spannableText.append(" " + discountText);

                // Set the color for the discount percentage
                int startIndex = spannableText.length() - discountText.length();
                spannableText.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.price.setText(spannableText);
            } else {
                // Fallback: if prices are invalid, show default values or error message
                holder.price.setText("Invalid Price");
            }

        } catch (NumberFormatException e) {
            // Handle any parsing exceptions
            e.printStackTrace(); // Optional: log the error
            holder.price.setText("Invalid Price");
        }

        // Set the book image
        String imgUrl = currentBook.getImageUrl();
        if (!imgUrl.isEmpty()) {
            Glide.with(context)
                    .load(imgUrl)
                    .error(R.drawable.noimage)
                    .placeholder(R.drawable.noimage)
                    .into(holder.bookImg);
        } else {
            Glide.with(context)
                    .load(R.drawable.noimage)
                    .into(holder.bookImg);
        }

        // Set quantity
        holder.quantityTxt.setText("Qty: " + currentBook.getQuantity());
        holder.quantityTxt.setOnClickListener(view -> showQuantityOptions(currentBook, holder));

        holder.removeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new Dialog(context);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                deleteBook(currentBook);
            }
        });

        holder.buyNowRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CartViewActivity.class);
                intent.putExtra("bookId", currentBook.getProductId());
                intent.putExtra("buyNow", true);
                context.startActivity(intent);
            }
        });
    }


    private void showQuantityOptions(CartItemModel currentBook, ViewHolder holder) {
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

    private void showCustomQuantityDialog(CartItemModel currentBook, ViewHolder holder) {
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


    private void updateQuantity(CartItemModel currentBook, int quantity, ViewHolder holder) {
        holder.quantityProgress.setVisibility(View.VISIBLE);
        holder.quantityTxt.setVisibility(View.GONE);

        String updateUrl = Constant.BASE_URL + "v1/cart/update";
        String productId = currentBook.getProductId();

        if (productId == null) {
            Toast.makeText(context, "User ID or Item ID is null", Toast.LENGTH_LONG).show();
            holder.quantityProgress.setVisibility(View.GONE);
            holder.quantityTxt.setVisibility(View.VISIBLE);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId", productId);
            jsonObject.put("quantity", quantity);
        } catch (JSONException e) {
            Toast.makeText(context, "Error creating JSON", Toast.LENGTH_SHORT).show();
            holder.quantityProgress.setVisibility(View.GONE);
            holder.quantityTxt.setVisibility(View.VISIBLE);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, updateUrl, jsonObject,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        Toast.makeText(context, "Quantity Updated", Toast.LENGTH_SHORT).show();

                        if (success) {
                            holder.quantityTxt.setText(String.valueOf("Qty: " + quantity));
                            if (context instanceof CartViewActivity) {
                                ((CartViewActivity) context).getAllCartItems();
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error processing response", Toast.LENGTH_SHORT).show();
                    } finally {
                        holder.quantityProgress.setVisibility(View.GONE);
                        holder.quantityTxt.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    holder.quantityProgress.setVisibility(View.GONE);
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

    private void deleteBook(CartItemModel currentBook) {
        String productId = currentBook.getProductId();
        String deleteUrl = Constant.BASE_URL + "v1/cart/remove/" + productId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, deleteUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                sessionManager.setCartItemQuantity();
                                Toast.makeText(context, "Item removed from the cart", Toast.LENGTH_SHORT).show();
                                allBooksModelArrayList.remove(currentBook);
                                if (allBooksModelArrayList.isEmpty()){
                                    ((CartViewActivity) context).hideEachLayout();
                                }else {
                                    if (context instanceof CartViewActivity) {
                                        ((CartViewActivity) context).getAllCartItems();
                                    }
                                }
                                notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        String responseData = new String(error.networkResponse.data, "UTF-8");
                        errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                        errorMessage += "\nResponse Data: " + responseData;
                        Log.d("DeleteBookError", "Response Data: " + responseData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("DeleteBookError", errorMessage);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");  // Add Accept header
                if (!TextUtils.isEmpty(authToken)) {
                    headers.put("Authorization", "Bearer " + authToken);
                }
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return allBooksModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,author, price,quantityTxt;
        //        ViewPager2 viewPager;
        ImageView bookImg;
        ProgressBar quantityProgress;
        RelativeLayout removeRL,buyNowRl;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            quantityTxt = itemView.findViewById(R.id.quantityTxtView);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImg = itemView.findViewById(R.id.bookImage);
            quantityProgress = itemView.findViewById(R.id.quantityProgressbar);
            removeRL = itemView.findViewById(R.id.removeRL);
            buyNowRl = itemView.findViewById(R.id.buyNowRl);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, SingleBookDetailsActivity.class);
//                    intent.putExtra("bookID", allBooksModelArrayList.get(getAdapterPosition()).getString("_id"));
//                    context.startActivity(intent);
//                }
//            });
        }
    }
}
