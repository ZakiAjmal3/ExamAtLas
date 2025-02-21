package com.examatlas.fragment.Admin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.models.Admin.AdminOrdersSectionModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminOrdersFragment extends Fragment {
    SessionManager sessionManager;
    String authToken;
    String ordersURL = Constant.BASE_URL + "v1/order/allOrder";
    int currentPage = 1,pageSize = 10,totalPages = 0;
    ArrayList<AdminOrdersSectionModel> allOrderArrayList;
    RelativeLayout mainRL,noDataLayout;
    TableLayout tableLayout;
    private TextView prevTv, nextTv;
    private TextView oneTv, twoTv, threeTv, fourTv, fiveTv;
    Spinner totalItemSelectSpinner;
    private final String[] bookTypeArraylist = {"10", "20","30", "50", "100"};
    Dialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_fragment_orders, container, false);
        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        mainRL = view.findViewById(R.id.mainRL);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        tableLayout = view.findViewById(R.id.ordersTableLayout);

        // Initialize pagination views
        prevTv = view.findViewById(R.id.prevTv);
        nextTv = view.findViewById(R.id.nextTv);
        oneTv = view.findViewById(R.id.oneTv);
        twoTv = view.findViewById(R.id.twoTv);
        threeTv = view.findViewById(R.id.threeTv);
        fourTv = view.findViewById(R.id.fourTv);
        fiveTv = view.findViewById(R.id.fiveTv);

        totalItemSelectSpinner = view.findViewById(R.id.totalItemSelectSpinner);

        // Set listeners on page number views
        setPaginationListeners();

        progressDialog = new Dialog(getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_bar_drawer);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressDialog.show();

        // Initial data loading for the first page
        getAllData();

        // Set listener for Prev button
        prevTv.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                getAllData();
                updatePagination();
                progressDialog = new Dialog(getContext());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
            }
        });

        // Set listener for Next button
        nextTv.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                getAllData();
                updatePagination();
                progressDialog = new Dialog(getContext());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
            }
        });
        allOrderArrayList = new ArrayList<>();
        setUpBookTypeSpinnerMain();
//        getAllData();
        return view;
    }
    private void setUpBookTypeSpinnerMain() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, bookTypeArraylist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        totalItemSelectSpinner.setAdapter(adapter);

        totalItemSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String bookType = bookTypeArraylist[i];
                pageSize = Integer.parseInt(bookType);
                progressDialog = new Dialog(getContext());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                currentPage = 1;
                getAllData();
                updatePagination();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void setPaginationListeners() {
        oneTv.setOnClickListener(v -> onPageClick(Integer.parseInt(oneTv.getText().toString())));
        twoTv.setOnClickListener(v -> onPageClick(Integer.parseInt(twoTv.getText().toString())));
        threeTv.setOnClickListener(v -> onPageClick(Integer.parseInt(threeTv.getText().toString())));
        fourTv.setOnClickListener(v -> onPageClick(Integer.parseInt(fourTv.getText().toString())));
        fiveTv.setOnClickListener(v -> onPageClick(Integer.parseInt(fiveTv.getText().toString())));
    }

    private void onPageClick(int page) {
        progressDialog = new Dialog(getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_bar_drawer);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressDialog.show();
        if (page >= 1 && page <= totalPages) {
            currentPage = page;
            getAllData();
            updatePagination();
        }
    }
    private void updatePagination() {
        // Set visibility based on totalPages
        oneTv.setVisibility(View.VISIBLE);
        twoTv.setVisibility(View.VISIBLE);
        threeTv.setVisibility(View.VISIBLE);
        fourTv.setVisibility(View.VISIBLE);
        fiveTv.setVisibility(View.VISIBLE);

        // Dynamically update the page numbers for the buttons
        int pageNumber1 = currentPage;
        int pageNumber2 = currentPage + 1;
        int pageNumber3 = currentPage + 2;
        int pageNumber4 = currentPage + 3;
        int pageNumber5 = currentPage + 4;

        // Make sure no page number exceeds totalPages
        if (pageNumber1 > totalPages) pageNumber1 = totalPages;
        if (pageNumber2 > totalPages) pageNumber2 = totalPages;
        if (pageNumber3 > totalPages) pageNumber3 = totalPages;
        if (pageNumber4 > totalPages) pageNumber4 = totalPages;
        if (pageNumber5 > totalPages) pageNumber5 = totalPages;

        // Set the text for pagination buttons, ensure they don't exceed totalPages
        oneTv.setText(pageNumber1 <= totalPages ? String.valueOf(pageNumber1) : "");
        twoTv.setText(pageNumber2 <= totalPages ? String.valueOf(pageNumber2) : "");
        threeTv.setText(pageNumber3 <= totalPages ? String.valueOf(pageNumber3) : "");
        fourTv.setText(pageNumber4 <= totalPages ? String.valueOf(pageNumber4) : "");
        fiveTv.setText(pageNumber5 <= totalPages ? String.valueOf(pageNumber5) : "");

        // Handle case when the current page is the last page (totalPages)
        if (currentPage == totalPages) {
            fiveTv.setText(String.valueOf(totalPages)); // Make sure no number exceeds totalPages
        }

        // Enable/Disable Prev/Next buttons
        prevTv.setEnabled(currentPage > 1);
        nextTv.setEnabled(currentPage < totalPages);

        // Handle page limit for first and last pages
        if (currentPage == 1 || currentPage == 2) {
            oneTv.setText("1");
            twoTv.setText("2");
            threeTv.setText("3");
            fourTv.setText("4");
            fiveTv.setText("5");
        } else if (currentPage == totalPages) {
            // When the user reaches the last page
            int lastPage = totalPages;
            oneTv.setText(String.valueOf(lastPage - 4));
            twoTv.setText(String.valueOf(lastPage - 3));
            threeTv.setText(String.valueOf(lastPage - 2));
            fourTv.setText(String.valueOf(lastPage - 1));
            fiveTv.setText(String.valueOf(lastPage));
        } else if (currentPage + 4 >= totalPages) {
            // When the current page is close to the last page
            int lastPage = totalPages;
            oneTv.setText(String.valueOf(lastPage - 4));
            twoTv.setText(String.valueOf(lastPage - 3));
            threeTv.setText(String.valueOf(lastPage - 2));
            fourTv.setText(String.valueOf(lastPage - 1));
            fiveTv.setText(String.valueOf(lastPage));
        }

        // Update pagination button background
        updatePaginationButtonBackground(oneTv);
        updatePaginationButtonBackground(twoTv);
        updatePaginationButtonBackground(threeTv);
        updatePaginationButtonBackground(fourTv);
        updatePaginationButtonBackground(fiveTv);
    }
    private void updatePaginationButtonBackground(TextView textView) {
        textView.setBackgroundResource(textView.getText().toString().equals(String.valueOf(currentPage))
                ? R.drawable.rounded_corner_for_rate_product_selected
                : R.drawable.rounded_corner_for_rate_product_plain);
    }
    private void getAllData() {
        String paginatedUrl = ordersURL + "?pageNumber=" + currentPage + "&pageSize=" + pageSize;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            allOrderArrayList.clear();
                            totalPages = response.getInt("totalPage");
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i<dataArray.length();i++){
                                JSONObject rOrderObj = dataArray.getJSONObject(i);
                                JSONArray productArray = rOrderObj.getJSONArray("items");
                                if (productArray == null){
                                    productArray = rOrderObj.getJSONArray("item");
                                    Log.e("book Empty", productArray.toString());
                                }
                                String bookTitle = "" ,bookPrice = "" ,bookImgURL = "";
                                if (productArray.length() > 0) {
                                    Log.e("book", productArray.toString());
                                    JSONObject bookItemObj = productArray.getJSONObject(0);
                                    JSONObject productObj = bookItemObj.getJSONObject("product");
                                    bookTitle = productObj.getString("title");
                                    bookPrice = productObj.getString("price");
                                    JSONArray bookImageArray = productObj.getJSONArray("images");
                                    if (bookImageArray.length() > 0) {
                                        bookImgURL = bookImageArray.getJSONObject(0).getString("url");
                                    }
                                }
                                String orderStatus = rOrderObj.getString("status");
                                String orderId = rOrderObj.getString("orderId");
                                String date = rOrderObj.getString("createdAt");
                                String totalPrice = rOrderObj.getString("totalAmount");

                                JSONObject updateByObj = rOrderObj.getJSONObject("shippingAddress");
                                String customerName = updateByObj.getString("firstName") + " "
                                        + updateByObj.getString("lastName");
                                String customerEmail = updateByObj.getString("email");

                                allOrderArrayList.add(new AdminOrdersSectionModel(orderId,customerName,customerEmail,date,orderStatus,totalPrice,bookTitle,bookImgURL,bookPrice,orderStatus));
                            }
                            if (!allOrderArrayList.isEmpty()){
                                mainRL.setVisibility(View.VISIBLE);
                                tableLayout.setVisibility(View.VISIBLE);
                                noDataLayout.setVisibility(View.GONE);
                                setTableData();
                            }else {
                                mainRL.setVisibility(View.GONE);
                                noDataLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException | ParseException e) {
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
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    @SuppressLint("ResourceAsColor")
    private void setTableData() throws ParseException {
        // Store the heading row separately
        View headingRow = tableLayout.getChildAt(0);
        // Remove all child views except the heading row
        tableLayout.removeAllViews();
        // Add the heading row back to the tableLayout
        tableLayout.addView(headingRow);
        for(int i = 0; i<allOrderArrayList.size();i++){
            TableRow tableRow = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.admin_orders_section_table_item_layout, null);

            ((TextView) tableRow.findViewById(R.id.orderIdTxt)).setText("# " +allOrderArrayList.get(i).getOrderId());

            String customerName = allOrderArrayList.get(i).getCustomerName();
            Character customerFirstLetter = customerName.charAt(0);
            ((TextView) tableRow.findViewById(R.id.profileTxt)).setText(customerFirstLetter.toString().toUpperCase());
            ((TextView) tableRow.findViewById(R.id.customerNameTxt)).setText(customerName);
            ((TextView) tableRow.findViewById(R.id.customerEmailTxt)).setText(allOrderArrayList.get(i).getCustomerEmail());

            String inputDate = allOrderArrayList.get(i).getDate();
            // Remove the 'Z' for the time zone (it represents UTC)
            inputDate = inputDate.replace("Z", "");
            // Define the input format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date date = inputFormat.parse(inputDate);
            // Define the desired output format
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yy");
            // Format and print the date
            String formattedDate = outputFormat.format(date);
            ((TextView) tableRow.findViewById(R.id.dateTxt)).setText(formattedDate);

            String paymentStatus = allOrderArrayList.get(i).getPayment();
            TextView paymentTxt = tableRow.findViewById(R.id.paymentTxt);
            TextView orderStatusTxt = tableRow.findViewById(R.id.orderStatusTxt);

            if (paymentStatus.equalsIgnoreCase("Confirmed")) {
                paymentTxt.setText("Paid");
                paymentTxt.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                orderStatusTxt.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            } else {
                paymentTxt.setText("Pending");
                paymentTxt.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_orange));
                orderStatusTxt.setTextColor(ContextCompat.getColor(getContext(), R.color.red_orange));
            }
            ((TextView) tableRow.findViewById(R.id.totalPriceTxt)).setText("₹ " + allOrderArrayList.get(i).getTotalPrice());

            String bookTitle = allOrderArrayList.get(i).getProductsName();
            String bookPrice = allOrderArrayList.get(i).getProductsPrice();
            String bookImg = allOrderArrayList.get(i).getProductImg();
            TextView bookTitleTxt,bookPriceTxt;
            ImageView bookImgView;
            bookTitleTxt = tableRow.findViewById(R.id.bookTitleTxt);
            bookPriceTxt = tableRow.findViewById(R.id.bookPriceTxt);
            bookImgView = tableRow.findViewById(R.id.productImg);
            bookTitleTxt.setEllipsize(TextUtils.TruncateAt.END);  // Always set ellipsize
            bookTitleTxt.setMaxWidth(500);  // Constrain width to allow ellipsize
            bookTitleTxt.setSingleLine(true);
            bookTitleTxt.setText(bookTitle);
            bookPriceTxt.setText("₹ " + bookPrice);
            Glide.with(getContext()).load(bookImg).into(bookImgView);

            orderStatusTxt.setText(allOrderArrayList.get(i).getOrderStatus());

            tableLayout.addView(tableRow);
        }
        checkPaginationTxt();
        progressDialog.dismiss();
    }
    private void checkPaginationTxt() {
        if (totalPages == 1){
            oneTv.setVisibility(View.VISIBLE);
            twoTv.setVisibility(View.GONE);
            threeTv.setVisibility(View.GONE);
            fourTv.setVisibility(View.GONE);
            fiveTv.setVisibility(View.GONE);
        } else if (totalPages == 2) {
            oneTv.setVisibility(View.VISIBLE);
            twoTv.setVisibility(View.VISIBLE);
            threeTv.setVisibility(View.GONE);
            fourTv.setVisibility(View.GONE);
            fiveTv.setVisibility(View.GONE);
        }else if (totalPages == 3) {
            oneTv.setVisibility(View.VISIBLE);
            twoTv.setVisibility(View.VISIBLE);
            threeTv.setVisibility(View.VISIBLE);
            fourTv.setVisibility(View.GONE);
            fiveTv.setVisibility(View.GONE);
        }else if (totalPages == 4) {
            oneTv.setVisibility(View.VISIBLE);
            twoTv.setVisibility(View.VISIBLE);
            threeTv.setVisibility(View.VISIBLE);
            fourTv.setVisibility(View.VISIBLE);
            fiveTv.setVisibility(View.GONE);
        }else {
            oneTv.setVisibility(View.VISIBLE);
            twoTv.setVisibility(View.VISIBLE);
            threeTv.setVisibility(View.VISIBLE);
            fourTv.setVisibility(View.VISIBLE);
            fiveTv.setVisibility(View.VISIBLE);
        }
    }
}
