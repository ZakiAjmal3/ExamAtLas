package com.examatlas.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.CartViewActivity;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.HardBookECommPurchaseModel;
import com.examatlas.models.extraModels.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HardBookECommPurchaseAdapter extends RecyclerView.Adapter<HardBookECommPurchaseAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList;
    private final ArrayList<HardBookECommPurchaseModel> originalHardBookECommPurchaseModelArrayList;
    private final ArrayList<Boolean> heartToggleStates; // List to track heart states
    private String currentQuery = "";
    String authToken;
    SessionManager sessionManager;
    ArrayList bookImageUrls;

    public HardBookECommPurchaseAdapter(Context context, ArrayList<HardBookECommPurchaseModel> hardBookECommPurchaseModelArrayList) {
        this.originalHardBookECommPurchaseModelArrayList = new ArrayList<>(hardBookECommPurchaseModelArrayList);
        this.hardBookECommPurchaseModelArrayList = new ArrayList<>(originalHardBookECommPurchaseModelArrayList);
        this.context = context;
        this.heartToggleStates = new ArrayList<>(Collections.nCopies(hardBookECommPurchaseModelArrayList.size(), false));
        sessionManager = new SessionManager(context);
        authToken = sessionManager.getUserData().get("authToken");
        bookImageUrls = new ArrayList<>();
    }

    @NonNull
    @Override
    public HardBookECommPurchaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hardcopybook_item_list, parent, false);
        return new HardBookECommPurchaseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HardBookECommPurchaseAdapter.ViewHolder holder, int position) {
        HardBookECommPurchaseModel currentBook = hardBookECommPurchaseModelArrayList.get(hardBookECommPurchaseModelArrayList.size() - 1 - position);
        holder.itemView.setTag(currentBook);

        // Calculate prices and discount
        String purchasingPrice = currentBook.getSellPrice();
        String originalPrice = currentBook.getPrice();
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

        // Set text to holder
        holder.setHighlightedText(holder.title, currentBook.getTitle(), currentQuery);
        holder.setHighlightedText(holder.author, currentBook.getAuthor(), currentQuery);
        holder.setHighlightedPrice(holder.price, spannableText, currentQuery);

        if (currentBook.getIsInCart().equals("true")){
            holder.addToCartBtn.setVisibility(View.GONE);
            holder.goToCartBtn.setVisibility(View.VISIBLE);
        }

        BookImageAdapter bookImageAdapter = new BookImageAdapter((ArrayList<BookImageModels>) currentBook.getImages());
        holder.viewPager.setAdapter(bookImageAdapter);

        // Set the heart icon based on the state
        holder.toggleHeartIcon.setImageResource(heartToggleStates.get(position) ? R.drawable.ic_heart_red : R.drawable.ic_heart_white);

        // Heart icon toggle logic
        holder.toggleHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isHearted = heartToggleStates.get(position); // Get the current state

                if (!isHearted) {
                    addToWishlist(currentBook);
                    holder.toggleHeartIcon.setImageResource(R.drawable.ic_heart_red);
                    heartToggleStates.set(position, true); // Update the state to true
                } else {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("WishList")
                            .setMessage("Are you sure you want to remove this item from wishlist?")
                            .setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    holder.toggleHeartIcon.setImageResource(R.drawable.ic_heart_white);
                                    removeItemFromWishlist(currentBook);
                                    heartToggleStates.set(position, false); // Update the state to false
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Do nothing, keep the current state
                                }
                            }).show();
                }
            }
        });

        holder.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = sessionManager.getUserData().get("user_id");
                String bookID = currentBook.getId();
                String addToCartUrl = Constant.BASE_URL + "cart/add";

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userId", userId);
                    jsonObject.put("bookId", bookID);
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
                                    String message = response.getString("message");
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    holder.addToCartBtn.setVisibility(View.GONE);
                                    holder.goToCartBtn.setVisibility(View.VISIBLE);
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
            }
        });
        holder.goToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CartViewActivity.class);
                context.startActivity(intent);
            }
        });
    }
    private void addToWishlist(HardBookECommPurchaseModel currentBook) {
        String addBookUrl = Constant.BASE_URL + "wishlist/toggleWishlist";
        String userID = sessionManager.getUserData().get("user_id");
        String bookID = currentBook.getId();
        JSONObject bookDetails = new JSONObject();
        try {
            bookDetails.put("userId", userID);
            bookDetails.put("bookId", bookID);
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error creating JSON object: " + e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addBookUrl, bookDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String message = response.getString("message");
                                JSONObject jsonObject = response.getJSONObject("wishlistItem");
                                currentBook.setItemId(jsonObject.getString("_id"));
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to add item to wishlist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("JSON_ERROR", "Error parsing JSON response: " + e.getMessage());
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
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
    }
    private void removeItemFromWishlist(HardBookECommPurchaseModel currentBook) {
        String removeBookUrl = Constant.BASE_URL + "wishlist/remove/" + currentBook.getItemId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, removeBookUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");

                            if (status) {
                                String message = response.getString("message");
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
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
    }

    @Override
    public int getItemCount() {
        return hardBookECommPurchaseModelArrayList.size();
    }

    public void filter(String query) {
        currentQuery = query;
        hardBookECommPurchaseModelArrayList.clear();
        if (query.isEmpty()) {
            hardBookECommPurchaseModelArrayList.addAll(originalHardBookECommPurchaseModelArrayList);
            heartToggleStates.clear();
            heartToggleStates.addAll(Collections.nCopies(originalHardBookECommPurchaseModelArrayList.size(), false));
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (HardBookECommPurchaseModel bookModel : originalHardBookECommPurchaseModelArrayList) {
                if (bookModel.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getContent().toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getTags().toLowerCase().contains(lowerCaseQuery) ||
                        bookModel.getPrice().toLowerCase().contains(lowerCaseQuery)) {
                    hardBookECommPurchaseModelArrayList.add(bookModel);
                    heartToggleStates.add(false); // Ensure state is added for new entries
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, price;
        ImageView toggleHeartIcon;
        Button addToCartBtn,goToCartBtn;
        ViewPager2 viewPager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            price = itemView.findViewById(R.id.bookPriceInfo);
            viewPager = itemView.findViewById(R.id.imgBook);
            toggleHeartIcon = itemView.findViewById(R.id.heartIconToggle);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
            goToCartBtn = itemView.findViewById(R.id.goToCartBtn);
        }

        public void setHighlightedText(TextView textView, String text, String query) {
            if (query == null || query.isEmpty()) {
                textView.setText(text);
                return;
            }
            SpannableString spannableString = new SpannableString(text);
            int startIndex = text.toLowerCase().indexOf(query.toLowerCase());
            while (startIndex >= 0) {
                int endIndex = startIndex + query.length();
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = text.toLowerCase().indexOf(query.toLowerCase(), endIndex);
            }
            textView.setText(spannableString);
        }

        public void setHighlightedPrice(TextView textView, SpannableStringBuilder priceText, String query) {
            if (query == null || query.isEmpty()) {
                textView.setText(priceText);
                return;
            }

            SpannableString spannableString = SpannableString.valueOf(priceText);
            int startIndex = priceText.toString().toLowerCase().indexOf(query.toLowerCase());
            while (startIndex >= 0) {
                int endIndex = startIndex + query.length();
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = priceText.toString().toLowerCase().indexOf(query.toLowerCase(), endIndex);
            }
            textView.setText(spannableString);
        }
    }
}
