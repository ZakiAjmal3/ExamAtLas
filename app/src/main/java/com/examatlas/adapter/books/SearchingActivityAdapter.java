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
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.activities.LoginWithEmailActivity;
import com.examatlas.activities.OtpActivity;
import com.examatlas.adapter.extraAdapter.BookImageAdapter;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.BookImageModels;
import com.examatlas.models.Books.WishListModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SearchingActivityAdapter extends RecyclerView.Adapter<SearchingActivityAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<AllBooksModel> allBooksModelArrayList;
    private final ArrayList<AllBooksModel> originalAllBooksModelArrayList;
    private final ArrayList<WishListModel> wishListModelArrayList;
    private ArrayList<Boolean> heartToggleStates;
    private String currentQuery = "";
    private final SessionManager sessionManager;
    private final String authToken;

    public SearchingActivityAdapter(Context context, ArrayList<AllBooksModel> allBooksModelArrayList, ArrayList<Boolean> heartToggleStates) {
        this.originalAllBooksModelArrayList = new ArrayList<>(allBooksModelArrayList);
        this.allBooksModelArrayList = new ArrayList<>(originalAllBooksModelArrayList);
        this.context = context;
        sessionManager = new SessionManager(context);
        authToken = sessionManager.getUserData().get("authToken");
        this.heartToggleStates = heartToggleStates;
        this.wishListModelArrayList = new ArrayList<>(sessionManager.getWishListBookIdArrayList());

    }

    @NonNull
    @Override
    public SearchingActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_searching_activity_item_single_layout, parent, false);
        return new SearchingActivityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchingActivityAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AllBooksModel currentBook = allBooksModelArrayList.get(position);
        holder.itemView.setTag(currentBook);

        // Set the highlighted title (search term highlighting)
        holder.setHighlightedText(holder.title, currentBook.getString("title"), currentQuery);
        holder.title.setEllipsize(TextUtils.TruncateAt.END);
        holder.title.setMaxLines(2);

        // Get prices as strings
        String purchasingPrice = String.valueOf( (int) Double.parseDouble(currentBook.getString("sellingPrice")));
        String originalPrice = String.valueOf( (int) Double.parseDouble(currentBook.getString("price")));

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

                holder.setHighlightedPrice(holder.price, spannableText, currentQuery);
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
                (ArrayList<BookImageModels>) currentBook.getImages(),
                holder.bookImg,
                holder.indicatorLayout
        );
        holder.bookImg.setAdapter(bookImageAdapter);

        // Fallback image loading (if required, e.g., for placeholder handling):
        // Glide.with(context)
        //        .load(R.drawable.book1)
        //        .error(R.drawable.book1)
        //        .placeholder(R.drawable.book1)
        //        .into(holder.bookImg);
        if (!heartToggleStates.isEmpty()) {
            holder.heartWishListIcon.setImageResource(heartToggleStates.get(position) ? R.drawable.ic_heart_red : R.drawable.ic_heart_grey);
        }
        holder.heartWishListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isHeartSelected = heartToggleStates.get(position);

                if (sessionManager.IsLoggedIn()) {
                    // Handle the case when the user is logged in
                    if (isHeartSelected) {
                        // Remove from wishlist
                        heartToggleStates.set(position, false);
                        holder.heartWishListIcon.setImageResource(R.drawable.ic_heart_grey);
                        removeFromWishList(currentBook);
                    } else {
                        // Add to wishlist
                        heartToggleStates.set(position, true);
                        holder.heartWishListIcon.setImageResource(R.drawable.ic_heart_red);
                        addToWishList(currentBook);
                    }
                } else {
                    // Handle the case when the user is not logged in
                    if (isHeartSelected) {
                        // Remove from session wishlist (local storage)
                        heartToggleStates.set(position, false);
                        holder.heartWishListIcon.setImageResource(R.drawable.ic_heart_grey);
                        sessionManager.removeWishListBookIdArrayList(currentBook.getString("_id"));
                        Toast.makeText(context, "Item removed from wishlist", Toast.LENGTH_SHORT).show();

                    } else {
                        // Add to session wishlist (local storage)
                        heartToggleStates.set(position, true);
                        holder.heartWishListIcon.setImageResource(R.drawable.ic_heart_red);
                        sessionManager.setAddedItemWishList(new WishListModel(
                                null, null, currentBook.getString("_id"), null, null,
                                currentBook.getString("title"), currentBook.getString("author"),
                                currentBook.getString("price"), currentBook.getString("sellingPrice"),
                                currentBook.getImages()
                        ));
//                        Toast.makeText(context, currentBook.getString("_id"), Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Item added to wishlist", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    private void removeFromWishList(AllBooksModel currentBook) {
        String addToWishListURL = Constant.BASE_URL + "v1/wishlist/delete/" + currentBook.getString("_id");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addToWishListURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            sessionManager.removeWishListBookIdArrayList(currentBook.getString("_id"));
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

    private void addToWishList(AllBooksModel currentBook) {
        String addToWishListURL = Constant.BASE_URL + "v1/wishlist";

        JSONArray productIdsArray = new JSONArray();
        productIdsArray.put(currentBook.getString("_id"));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productIds",productIdsArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addToWishListURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            String message = "Item added to wishlist.";
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = response.getJSONArray("data");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            String productId = jsonObject1.getString("productId");
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("wishlistItem");
                            String wishlistId = jsonObject2.getString("_id");
                            String userId = jsonObject2.getString("userId");
                            WishListModel wishListModel = new WishListModel(wishlistId,userId,productId,null,null,currentBook.getString("title"),currentBook.getString("author"),currentBook.getString("price"),currentBook.getString("sellingPrice"),currentBook.getImages());
                            sessionManager.setAddedItemWishList(wishListModel);
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
        return allBooksModelArrayList.size();
    }
//    public void filter(String query) {
//        currentQuery = query;
//        allBooksModelArrayList.clear();
//        if (query.isEmpty()) {
//            allBooksModelArrayList.addAll(originalAllBooksModelArrayList);
//        } else {
//            String lowerCaseQuery = query.toLowerCase();
//            for (AllBooksModel bookModel : originalAllBooksModelArrayList) {
//                if (bookModel.getString("title").toLowerCase().contains(lowerCaseQuery) ||
//                        bookModel.getString("description").toLowerCase().contains(lowerCaseQuery) ||
//                        bookModel.getString("tags").toLowerCase().contains(lowerCaseQuery) ||
//                        bookModel.getString("price").toLowerCase().contains(lowerCaseQuery)) {
//                    allBooksModelArrayList.add(bookModel);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView heartWishListIcon;
        ViewPager2 bookImg;
        LinearLayout indicatorLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImg = itemView.findViewById(R.id.bookImg);
            indicatorLayout = itemView.findViewById(R.id.indicatorLayout);
            heartWishListIcon = itemView.findViewById(R.id.heartIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SingleBookDetailsActivity.class);
                    intent.putExtra("bookId", allBooksModelArrayList.get(getAbsoluteAdapterPosition()).getString("_id"));
                    context.startActivity(intent);
                }
            });
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
