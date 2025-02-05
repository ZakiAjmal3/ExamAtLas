package com.examatlas.adapter.books;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.Books.CartViewActivity;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.activities.Books.WishListActivity;
import com.examatlas.activities.LoginWithEmailActivity;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.WishListModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {
    Context context;
    ArrayList<WishListModel> wishListModelArrayList;
    Dialog progressDialog;
    private final SessionManager sessionManager;
    private final String authToken;
    public WishListAdapter(Context context, ArrayList<WishListModel> wishListModelArrayList) {
        this.context = context;
        this.wishListModelArrayList = wishListModelArrayList;
        sessionManager = new SessionManager(context);
        authToken = sessionManager.getUserData().get("authToken");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_book_item_layout, parent, false);
        return new com.examatlas.adapter.books.WishListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WishListModel wishListModel = wishListModelArrayList.get(position);
        holder.title.setText(wishListModel.getBookTitle());
        holder.title.setEllipsize(TextUtils.TruncateAt.END);
        holder.title.setMaxLines(2);
        holder.author.setText(wishListModel.getBookAuthor());
        // Get prices as strings
        String purchasingPrice = wishListModel.getBookSellingPrice();
        String originalPrice = wishListModel.getBookPrice();

        // Initialize prices
        int purchasingPriceInt = 0;
        int originalPriceInt = 0;

        try {
            // Parse the prices only if they are non-empty and valid numbers
            if (!purchasingPrice.isEmpty()) {
                purchasingPriceInt = Integer.parseInt(purchasingPrice);
            }
            if (!originalPrice.isEmpty()) {
                originalPriceInt = Integer.parseInt(originalPrice);
            }

            // Calculate discount only if both prices are valid
            if (originalPriceInt > 0 && purchasingPriceInt > 0) {
                int discount = purchasingPriceInt * 100 / originalPriceInt;
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
            } else {
                // Fallback: if prices are invalid, show default values or error message
                holder.price.setText("Invalid Price");
            }

        } catch (NumberFormatException e) {
            // Handle any parsing exceptions
            e.printStackTrace(); // Optional: log the error
            holder.price.setText("Invalid Price");
        }

        // Set the book images (assuming you have an adapter handling images for each book)
        BookImageAdapter bookImageAdapter = new BookImageAdapter(
                wishListModel.getImageUrlArraylist(),
                holder.bookImage,
                holder.indicatorLayout
        );
        holder.bookImage.setAdapter(bookImageAdapter);
        holder.toggleHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.IsLoggedIn()) {
                    sessionManager.removeWishListBookIdArrayList(wishListModelArrayList.get(position).getProductId());
                    removeFromWishList(wishListModel);
                }else {
                    holder.toggleHeartIcon.setImageResource(R.drawable.ic_heart_grey);
                    sessionManager.removeWishListBookIdArrayList(wishListModelArrayList.get(position).getProductId());
                    Toast.makeText(context, "Item removed from wishlist", Toast.LENGTH_SHORT).show();
                    wishListModelArrayList.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        holder.addToCartBtn.setOnClickListener(new View.OnClickListener() {
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
                if (sessionManager.IsLoggedIn()) {
                    addToCart(wishListModel, holder.addToCartBtn, holder.goToCartBtn);
                }else {
                    progressDialog.dismiss();
                    context.startActivity(new Intent(context, LoginWithEmailActivity.class));
                }
            }
        });
        holder.goToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context,CartViewActivity.class));
            }
        });
    }

    private void addToCart(WishListModel wishListModel,Button addToCartBtn, Button goToCartBtn) {
        if (sessionManager.IsLoggedIn()) {

            String addToCartUrl = Constant.BASE_URL + "v1/cart";

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("productId", wishListModel.getProductId());
                jsonObject.put("quantity", 1);
                jsonObject.put("type", "book");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addToCartUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("success");
                                if (status.equals("true")) {
                                    sessionManager.setCartItemQuantity();
                                    Toast.makeText(context, "Item Added to Cart", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    addToCartBtn.setVisibility(View.GONE);
                                    goToCartBtn.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String errorMessage = "Error: " + error.toString();
                    if (error.networkResponse != null) {
                        try {
                            String responseData = new String(error.networkResponse.data, "UTF-8");
                            errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                            errorMessage += "\nResponse Data: " + responseData;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("LoginActivity", errorMessage);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + authToken);
                    return headers;
                }
            };
            MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        } else {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Login")
                    .setMessage("You need to login to add items to cart")
                    .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(context, LoginWithEmailActivity.class);
                            context.startActivity(intent);
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
    }

    private void removeFromWishList(WishListModel currentBook) {
        String addToWishListURL = Constant.BASE_URL + "v1/wishlist/delete/" + currentBook.getProductId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addToWishListURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            wishListModelArrayList.remove(currentBook);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            ((WishListActivity) context).getWishListItem();
                        } catch (JSONException e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
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
        return wishListModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, price;
        ImageView toggleHeartIcon;
        Button addToCartBtn,goToCartBtn;
        ViewPager2 bookImage;
        LinearLayout indicatorLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImage = itemView.findViewById(R.id.imgBook);
            toggleHeartIcon = itemView.findViewById(R.id.heartIconToggle);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
            goToCartBtn = itemView.findViewById(R.id.goToCartBtn);
            indicatorLayout = itemView.findViewById(R.id.indicatorLayout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sessionManager.IsLoggedIn()){
                        context.startActivity(new Intent(context, SingleBookDetailsActivity.class));
                    }else {
                        context.startActivity(new Intent(context, LoginWithEmailActivity.class));
                    }
                }
            });
        }
    }
}
