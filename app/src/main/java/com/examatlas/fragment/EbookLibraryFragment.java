package com.examatlas.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.examatlas.R;
import com.examatlas.activities.Books.CartViewActivity;
import com.examatlas.activities.Books.EBooks.EBookSeachingActivity;
import com.examatlas.activities.LoginWithEmailActivity;
import com.examatlas.adapter.books.AllBookShowingAdapter;
import com.examatlas.adapter.books.PurchaseEBookAdapter;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.WishListModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EbookLibraryFragment extends Fragment {
    RelativeLayout purchasedEbookRL,bestSellingBookRL;
    ImageView cartBtn;
    TextView cartItemCountTxt;
    private EditText searchView;
    private ImageSlider slider;
    ArrayList<SlideModel> sliderArrayList;
    CardView sliderCardView;
    ShimmerFrameLayout sliderShimmerLayout, purchasedBooksShimmer,bookBestSellerShimmerLayout;
    private RecyclerView purchasedEbooksRecyclerView,bookBestSellingRecyclerView;
    private ArrayList<AllBooksModel> all_E_BooksModelArrayList;
    private ArrayList<AllBooksModel> purchasedEbooksModelArrayList;
    SessionManager sessionManager;
    String token;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ebook_library, container, false);

        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.md_theme_dark_surfaceTint));

        sessionManager = new SessionManager(getContext());
        token = sessionManager.getUserData().get("authToken");

        cartBtn = view.findViewById(R.id.cartBtn);

        slider = view.findViewById(R.id.slider);
        searchView = view.findViewById(R.id.search_icon);
        searchView.setFocusable(false);
        searchView.setClickable(true);

        sliderCardView = view.findViewById(R.id.slider_cardView);
        sliderShimmerLayout = view.findViewById(R.id.shimmer_Slider_container);
        sliderCardView.setVisibility(View.GONE);

        purchasedEbooksRecyclerView = view.findViewById(R.id.booksOfUserRecycler);
        purchasedBooksShimmer = view.findViewById(R.id.shimmer_for_user_container);
        purchasedEbooksRecyclerView.setVisibility(View.GONE);
        purchasedBooksShimmer.setVisibility(View.VISIBLE);
        purchasedBooksShimmer.startShimmer();

        bookBestSellingRecyclerView = view.findViewById(R.id.booksBestSelling);
        bookBestSellerShimmerLayout = view.findViewById(R.id.shimmer_Best_selling_container);
        bookBestSellingRecyclerView.setVisibility(View.GONE);
        bookBestSellerShimmerLayout.setVisibility(View.VISIBLE);
        bookBestSellerShimmerLayout.startShimmer();

        bestSellingBookRL = view.findViewById(R.id.bookForYouRL);
        purchasedEbookRL = view.findViewById(R.id.purchasedEBookRL);
        purchasedEbookRL.setVisibility(View.GONE);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EBookSeachingActivity.class);
                startActivity(intent);
            }
        });
        purchasedEbooksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        bookBestSellingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        all_E_BooksModelArrayList = new ArrayList<>();
        purchasedEbooksModelArrayList = new ArrayList<>();

        cartItemCountTxt = view.findViewById(R.id.cartItemCountTxt);
        String quantity = sessionManager.getCartQuantity();
        if (!quantity.equals("0")) {
            cartItemCountTxt.setVisibility(View.VISIBLE);
            cartItemCountTxt.setText(quantity);
        }else {
            cartItemCountTxt.setVisibility(View.GONE);
        }
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.IsLoggedIn()) {
                    Intent intent = new Intent(getContext(), CartViewActivity.class);
                    startActivity(intent);
                }else {
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Login")
                            .setMessage("You need to login to view cart")
                            .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getContext(), LoginWithEmailActivity.class);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
            }
        });

        getAllPurchasedEBooks();
        getAllEBooks();

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        String quantity = sessionManager.getCartQuantity();
        if (!quantity.equals("0")) {
            cartItemCountTxt.setVisibility(View.VISIBLE);
            cartItemCountTxt.setText(quantity);
        }else {
            cartItemCountTxt.setVisibility(View.GONE);
        }
    }
    private void setupImageSlider() {
        sliderArrayList = new ArrayList<>();
        sliderArrayList.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));
        slider.setImageList(sliderArrayList);
    }
    private void getAllPurchasedEBooks() {
        String url = Constant.BASE_URL + "v1/my-ebook";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                purchasedEbooksModelArrayList.clear();

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    // Convert the book object into a Map to make it dynamic
                                    Map<String, Object> bookData = new Gson().fromJson(jsonObject2.toString(), Map.class);
                                    // Extract dimensions (assuming they are present in the 'dimensions' field of the book data)
                                    String length = "";
                                    String width = "";
                                    String height = "";
                                    String weight = "";

                                    if (bookData.containsKey("dimensions")) {
                                        Map<String, Object> dimensions = (Map<String, Object>) bookData.get("dimensions");
                                        length = dimensions.containsKey("length") ? dimensions.get("length").toString() : "";
                                        width = dimensions.containsKey("width") ? dimensions.get("width").toString() : "";
                                        height = dimensions.containsKey("height") ? dimensions.get("height").toString() : "";
                                        weight = dimensions.containsKey("weight") ? dimensions.get("weight").toString() : "";
                                    }

                                    // Pass the data and dimensions to the model constructor
                                    AllBooksModel model = new AllBooksModel(bookData, length, width, height, weight); // Pass map and dimensions // Pass the map to the model

                                    purchasedEbooksModelArrayList.add(model);
                                }
                                if (purchasedEbooksModelArrayList.isEmpty()){
                                    purchasedEbooksRecyclerView.setVisibility(View.GONE);
                                    purchasedBooksShimmer.stopShimmer();
                                    purchasedBooksShimmer.setVisibility(View.GONE);
                                    purchasedEbookRL.setVisibility(View.GONE);
                                }else {
                                    purchasedEbooksRecyclerView.setAdapter(new PurchaseEBookAdapter(EbookLibraryFragment.this.getContext(), purchasedEbooksModelArrayList));
                                    purchasedBooksShimmer.stopShimmer();
                                    purchasedBooksShimmer.setVisibility(View.GONE);
                                    purchasedEbooksRecyclerView.setVisibility(View.VISIBLE);
                                    purchasedEbookRL.setVisibility(View.VISIBLE);
                                }
                            } else {
//                                Toast.makeText(ge, response.getString("message"), Toast.LENGTH_SHORT).show();
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
//                                Toast.makeText(EBookHomePageActivity.this, message, Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    private void getAllEBooks() {
        String url = Constant.BASE_URL + "v1/books?type=ebook";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                sliderCardView.setVisibility(View.VISIBLE);
                                sliderShimmerLayout.stopShimmer();
                                sliderShimmerLayout.setVisibility(View.GONE);
                                setupImageSlider();

                                JSONArray jsonArray = response.getJSONArray("data");
                                all_E_BooksModelArrayList.clear();

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    // Convert the book object into a Map to make it dynamic
                                    Map<String, Object> bookData = new Gson().fromJson(jsonObject2.toString(), Map.class);
                                    // Extract dimensions (assuming they are present in the 'dimensions' field of the book data)
                                    String length = "";
                                    String width = "";
                                    String height = "";
                                    String weight = "";


                                    if (bookData.containsKey("dimensions")) {
                                        Object dimensionsObj = bookData.get("dimensions");

                                        if (dimensionsObj instanceof Map) {
                                            // If it's already a Map, we can safely cast
                                            Map<String, Object> dimensions = (Map<String, Object>) dimensionsObj;
                                            length = dimensions.containsKey("length") ? dimensions.get("length").toString() : "";
                                            width = dimensions.containsKey("width") ? dimensions.get("width").toString() : "";
                                            height = dimensions.containsKey("height") ? dimensions.get("height").toString() : "";
                                            weight = dimensions.containsKey("weight") ? dimensions.get("weight").toString() : "";
                                        } else if (dimensionsObj instanceof String) {
                                            // If it's a String (likely JSON), parse it into a Map
                                            String dimensionsJson = dimensionsObj.toString();
                                            try {
                                                Map<String, Object> dimensions = new Gson().fromJson(dimensionsJson, Map.class);
                                                length = dimensions.containsKey("length") ? dimensions.get("length").toString() : "";
                                                width = dimensions.containsKey("width") ? dimensions.get("width").toString() : "";
                                                height = dimensions.containsKey("height") ? dimensions.get("height").toString() : "";
                                                weight = dimensions.containsKey("weight") ? dimensions.get("weight").toString() : "";
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                // Handle parsing errors here if necessary
                                            }
                                        }
                                    }

                                    // Pass the data and dimensions to the model constructor
                                    AllBooksModel model = new AllBooksModel(bookData, length, width, height, weight); // Pass map and dimensions

                                    all_E_BooksModelArrayList.add(model);
                                }
                                if (all_E_BooksModelArrayList.isEmpty()){
                                    bookBestSellingRecyclerView.setVisibility(View.GONE);
                                    bookBestSellerShimmerLayout.stopShimmer();
                                    bookBestSellerShimmerLayout.setVisibility(View.GONE);
                                }else {
                                    ArrayList<WishListModel> wishListModelArrayList = new ArrayList<>(sessionManager.getWishListBookIdArrayList());
                                    ArrayList<Boolean> heartToggleStates = new ArrayList<>(Collections.nCopies(all_E_BooksModelArrayList.size(), false));
                                    if (!wishListModelArrayList.isEmpty()) {
                                        for (int i = 0; i < all_E_BooksModelArrayList.size(); i++) {
                                            for (int j = 0; j < wishListModelArrayList.size(); j++) {
                                                if (all_E_BooksModelArrayList.get(i).getString("_id").equals(wishListModelArrayList.get(j).getProductId())) {
                                                    heartToggleStates.set(i, true);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (all_E_BooksModelArrayList.isEmpty()){
                                        bookBestSellingRecyclerView.setVisibility(View.GONE);
                                        bookBestSellerShimmerLayout.stopShimmer();
                                        bookBestSellerShimmerLayout.setVisibility(View.GONE);
                                        bestSellingBookRL.setVisibility(View.GONE);
                                    }else {
                                        bookBestSellingRecyclerView.setAdapter(new AllBookShowingAdapter(EbookLibraryFragment.this.getContext(), all_E_BooksModelArrayList));
                                        bookBestSellerShimmerLayout.stopShimmer();
                                        bookBestSellerShimmerLayout.setVisibility(View.GONE);
                                        bookBestSellingRecyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
//                                Toast.makeText(ge, response.getString("message"), Toast.LENGTH_SHORT).show();
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
//                                Toast.makeText(EBookHomePageActivity.this, message, Toast.LENGTH_LONG).show();
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
                return headers;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
}
