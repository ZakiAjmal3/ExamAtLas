package com.examatlas.fragment.Admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.adapter.AdminHomePage.TopCategoryAdapter;
import com.examatlas.adapter.AdminHomePage.TopCustomerAdapter;
import com.examatlas.models.Admin.AdminOrdersSectionModel;
import com.examatlas.models.AdminHomePage.StockReportModel;
import com.examatlas.models.AdminHomePage.TopCategoryModel;
import com.examatlas.models.AdminHomePage.TopCustomersModel;
import com.examatlas.models.AdminHomePage.TopSellingProductModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminHomeFragment extends Fragment {
    SessionManager sessionManager;
    String authToken;
    String dashboardURL = Constant.BASE_URL + "v1/dashboard/admin";
    private BarChart barChart;
    ArrayList<BarEntry> barGraphArrayList = new ArrayList<>();
    private TableLayout recentOrderTableLayout,topSellingTableLayout,stockReportTableLayout;
    TextView totalProductsTxt, totalOrdersTxt, totalStudentsTxt, totalRevenueTxt;
    RecyclerView topCategoryRecyclerView, topCustomersRecyclerView;
    ArrayList<AdminOrdersSectionModel> recentOrdersModelArrayList;
    ArrayList<TopCustomersModel> topCustomersModelArrayList;
    ArrayList<TopCategoryModel> topCategoryModelArrayList;
    ArrayList<TopSellingProductModel> topSellingProductModelArrayList;
    ArrayList<StockReportModel> stockReportModelArrayList;
    RelativeLayout mainRelativeLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_fragment_home, container, false);

        barChart = view.findViewById(R.id.barChart);

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        mainRelativeLayout = view.findViewById(R.id.mainRL);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_for_user_container);
        shimmerFrameLayout.startShimmer();

        recentOrderTableLayout = view.findViewById(R.id.recentOrderTableLayout);
        topSellingTableLayout = view.findViewById(R.id.topSellingProductsTableLayout);
        stockReportTableLayout = view.findViewById(R.id.stockReportTableLayout);

        totalProductsTxt = view.findViewById(R.id.txtDisplayTProduct);
        totalOrdersTxt = view.findViewById(R.id.txtDisplayTOrders);
        totalStudentsTxt = view.findViewById(R.id.txtDisplayTStudent);
        totalRevenueTxt = view.findViewById(R.id.txtDisplayTRevenue);

        topCategoryRecyclerView = view.findViewById(R.id.topCategoriesRecyclerView);
        topCustomersRecyclerView = view.findViewById(R.id.topCustomerRecyclerView);

        topCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topCustomersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recentOrdersModelArrayList = new ArrayList<>();
        topCustomersModelArrayList = new ArrayList<>();
        topCategoryModelArrayList = new ArrayList<>();
        topSellingProductModelArrayList = new ArrayList<>();
        stockReportModelArrayList = new ArrayList<>();

        getAllData();

        return view;
    }

    private void getAllData() {
        recentOrdersModelArrayList.clear();
        topCustomersModelArrayList.clear();
        topCategoryModelArrayList.clear();
        topSellingProductModelArrayList.clear();
        stockReportModelArrayList.clear();
        barGraphArrayList.clear();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, dashboardURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONObject dataObj = response.getJSONObject("data");
                                String totalProduct = dataObj.getString("totalProduct");
                                String totalOrder = dataObj.getString("totalOrder");
                                String totalStudent = dataObj.getString("totalStudent");
                                String overAllRevenue = dataObj.getString("overAllRevenue");
                                totalProductsTxt.setText(totalProduct);
                                totalOrdersTxt.setText(totalOrder);
                                totalStudentsTxt.setText(totalStudent);
                                totalRevenueTxt.setText("₹ "+overAllRevenue);

                                // Recent Orders Data fetching/setting

                                JSONArray rOrderJArray = dataObj.getJSONArray("recentOrders");
                                for (int i = 0; i<rOrderJArray.length();i++){
                                    JSONObject rorderObj = rOrderJArray.getJSONObject(i);
                                    JSONObject orderObj = rorderObj.getJSONObject("orderDetails");
                                    JSONArray productArray = orderObj.getJSONArray("items");
                                    if (productArray == null){
                                        productArray = orderObj.getJSONArray("items");
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
                                    String orderStatus = orderObj.getString("status");
                                    String orderId = orderObj.getString("orderId");
                                    String date = orderObj.getString("createdAt");
                                    String totalPrice = orderObj.getString("totalAmount");
                                    JSONObject updateByObj = orderObj.getJSONObject("customerData");
                                    String customerName = updateByObj.getString("firstName") + " "
                                            + updateByObj.getString("lastName");
                                    String customerEmail = updateByObj.getString("email");
                                    recentOrdersModelArrayList.add(new AdminOrdersSectionModel(orderId,customerName,customerEmail,date,orderStatus,totalPrice,bookTitle,bookImgURL,bookPrice,orderStatus));
                                }

                                //Top Customer Data fetching/setting

                                JSONArray topCustoArrayJ = dataObj.getJSONArray("topCustomers");
                                for (int i = 0; i<topCustoArrayJ.length();i++){
                                    JSONObject customerObj = topCustoArrayJ.getJSONObject(i);
                                    String customerName = customerObj.getString("customerName");
                                    String customerEmail = customerObj.getString("customerEmail");
                                    String totalOrders = customerObj.getString("totalOrders");

                                    topCustomersModelArrayList.add(new TopCustomersModel(customerName,customerEmail,totalOrders));
                                }

                                //Top Categories fetching/setting

                                JSONArray topCategory = dataObj.getJSONArray("topCategories");
                                for (int i = 0; i<topCategory.length();i++){
                                    JSONObject categoryobj = topCategory.getJSONObject(i);
                                    String totalOrders = categoryobj.getString("totalOrders");
                                    String categoryName = categoryobj.getString("categoryName");
                                    topCategoryModelArrayList.add(new TopCategoryModel(categoryName,totalOrders));
                                }

                                //Top Selling Products

                                JSONArray topSellingArray = dataObj.getJSONArray("topSellingProducts");
                                for (int i = 0; i<topSellingArray.length();i++){
                                    JSONObject productOjb = topSellingArray.getJSONObject(i);
                                    String productName = productOjb.getString("productName");
                                    String price = productOjb.getString("price");
                                    String discounts = productOjb.getString("discounts");
                                    String sold = productOjb.getString("sold");
                                    String totalOrders = productOjb.getString("totalOrder");
                                    topSellingProductModelArrayList.add(new TopSellingProductModel(productName,price,discounts,sold,totalOrders));
                                }

                                //Stock Report fetching/setting
                                JSONArray stockReportJArray = dataObj.getJSONArray("stockReport");
                                for (int i = 0; i<stockReportJArray.length();i++){
                                    JSONObject productOjb = stockReportJArray.getJSONObject(i);
                                    String title = productOjb.getString("title");
                                    String stock = productOjb.getString("stock");
                                    String price = productOjb.getString("price");

                                    stockReportModelArrayList.add(new StockReportModel(title,price,stock));
                                }
                                //monthWiseRevenue
                                JSONArray monthWiseRevenueJAr = dataObj.getJSONArray("monthWiseRevenue");
                                for (int i = 0; i<monthWiseRevenueJAr.length();i++){
                                    JSONObject productOjb = monthWiseRevenueJAr.getJSONObject(i);
                                    String totalAmount = productOjb.getString("totalAmount");
                                    barGraphArrayList.add(new BarEntry(i, Integer.parseInt(totalAmount)));
                                }
                                setAllData();
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

    private void setAllData() throws ParseException {

        topCategoryRecyclerView.setAdapter(new TopCategoryAdapter(getContext(),topCategoryModelArrayList));
        topCategoryRecyclerView.setVisibility(View.VISIBLE);

        topCustomersRecyclerView.setAdapter(new TopCustomerAdapter(getContext(),topCustomersModelArrayList));
        topCustomersRecyclerView.setVisibility(View.VISIBLE);

        for(int i = 0; i<recentOrdersModelArrayList.size();i++){
            TableRow tableRow = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.admin_orders_section_table_item_layout, null);

            ((TextView) tableRow.findViewById(R.id.orderIdTxt)).setText("# " +recentOrdersModelArrayList.get(i).getOrderId());

            String customerName = recentOrdersModelArrayList.get(i).getCustomerName();
            Character customerFirstLetter = customerName.charAt(0);
            ((TextView) tableRow.findViewById(R.id.profileTxt)).setText(customerFirstLetter.toString().toUpperCase());
            ((TextView) tableRow.findViewById(R.id.customerNameTxt)).setText(customerName);
            ((TextView) tableRow.findViewById(R.id.customerEmailTxt)).setText(recentOrdersModelArrayList.get(i).getCustomerEmail());

            String inputDate = recentOrdersModelArrayList.get(i).getDate();
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

            String paymentStatus = recentOrdersModelArrayList.get(i).getPayment();
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
            ((TextView) tableRow.findViewById(R.id.totalPriceTxt)).setText("₹ " + recentOrdersModelArrayList.get(i).getTotalPrice());

            String bookTitle = recentOrdersModelArrayList.get(i).getProductsName();
            String bookPrice = recentOrdersModelArrayList.get(i).getProductsPrice();
            String bookImg = recentOrdersModelArrayList.get(i).getProductImg();
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

            orderStatusTxt.setText(recentOrdersModelArrayList.get(i).getOrderStatus());

            recentOrderTableLayout.addView(tableRow);
        }
        setupBarChart();
        for(int i = 0; i<topSellingProductModelArrayList.size();i++){
            TableRow tableRow = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.admin_home_top_selling_product_table_item_layout, null);
            TextView titleTxt = tableRow.findViewById(R.id.itemNameTxt);
            String titleStr = topSellingProductModelArrayList.get(i).getItemName();
            titleTxt.setEllipsize(TextUtils.TruncateAt.END);  // Always set ellipsize
            titleTxt.setMaxWidth(500);  // Constrain width to allow ellipsize
            titleTxt.setSingleLine(true);  // Ensure it's a single line for ellipsize
            titleTxt.setText(titleStr);
            ((TextView) tableRow.findViewById(R.id.priceTxt)).setText(topSellingProductModelArrayList.get(i).getPrice());
            ((TextView) tableRow.findViewById(R.id.discountTxt)).setText(topSellingProductModelArrayList.get(i).getDiscount());
            ((TextView) tableRow.findViewById(R.id.soldTxt)).setText(topSellingProductModelArrayList.get(i).getSold());
            ((TextView) tableRow.findViewById(R.id.totalOrdersTxt)).setText(topSellingProductModelArrayList.get(i).getTotalOrders());
            topSellingTableLayout.addView(tableRow);
        }
        for(int i = 0; i<stockReportModelArrayList.size();i++){
            TableRow tableRow = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.admin_home_stock_report_table_item_layout, null);
            TextView titleTxt = tableRow.findViewById(R.id.itemNameTxt);
            String titleStr = stockReportModelArrayList.get(i).getItem();
            titleTxt.setEllipsize(TextUtils.TruncateAt.END);  // Always set ellipsize
            titleTxt.setMaxWidth(500);  // Constrain width to allow ellipsize
            titleTxt.setSingleLine(true);  // Ensure it's a single line for ellipsize
            titleTxt.setText(titleStr);
            ((TextView) tableRow.findViewById(R.id.priceTxt)).setText(stockReportModelArrayList.get(i).getPrice());
            ((TextView) tableRow.findViewById(R.id.stockTxt)).setText(stockReportModelArrayList.get(i).getStock());
            stockReportTableLayout.addView(tableRow);
        }

        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        mainRelativeLayout.setVisibility(View.VISIBLE);

    }

    private void setupBarChart() {

        // Ensure the barGraphArrayList has 4 entries
        for (int i = barGraphArrayList.size(); i < 4; i++) {
                barGraphArrayList.add(new BarEntry(i, 0f));
        }

        BarDataSet barDataSet = new BarDataSet(barGraphArrayList, "Earnings");
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);

        // Customize the bar chart
        barDataSet.setColor(getResources().getColor(R.color.seed));
        barDataSet.setValueTextColors(Collections.singletonList(getResources().getColor(R.color.green)));
        barDataSet.setValueTextSize(12f);

        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);

        // Set up X-axis
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Jan-Mar", "Apr-Jun", "Jul-Sep", "Oct-Dec"}));
        barChart.getXAxis().setGranularity(1f);  // Show one label per bar
        barChart.getXAxis().setLabelCount(barGraphArrayList.size(), false);  // Fixed count of labels
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  // Ensure labels are at the bottom
        barChart.getXAxis().setDrawGridLines(false);  // Disable vertical grid lines

        // Set up Y-axis

        barChart.getAxisLeft().setDrawGridLines(true);  // Enable horizontal grid lines
        barChart.getAxisRight().setEnabled(false);  // Disable right Y-axis
        barChart.getAxisLeft().setAxisMinimum(0f); // Ensure Y-axis starts from 0
        barChart.getAxisLeft().setAxisMaximum(5000f);
        barChart.getAxisLeft().setGranularity(1000f);
        // Define Y-axis labels and scale
        final String[] yAxisLabels = new String[]{"₹0", "₹1000", "₹2000", "₹3000", "₹4000", "₹5000"};
        barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Return the appropriate label based on the value
                int index = (int) (value / 1000);
                if (index < 0 || index >= yAxisLabels.length) {
                    return "";
                }
                return yAxisLabels[index];
            }
        });

        barChart.invalidate();  // Refresh the chart
    }

}
