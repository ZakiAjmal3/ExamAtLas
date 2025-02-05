package com.examatlas.activities.Books;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.books.SingleOrderTrackingItemAdapter;
import com.examatlas.models.Books.SingleBookTrackingItemModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrackingSingleActivity extends AppCompatActivity {
    ImageView backBtn;
    RecyclerView trackingOrderLineRV;
    ArrayList<SingleBookTrackingItemModel> singleBookTrackingItemModelArrayList;
    private SessionManager sessionManager;
    private String token;
    String shipment_id = null,orderPlacedDate = "1234";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_single);

        sessionManager = new SessionManager(TrackingSingleActivity.this);
        token = sessionManager.getUserData().get("authToken");

        shipment_id = getIntent().getStringExtra("shipment_id");
        orderPlacedDate = getIntent().getStringExtra("orderPlacedDate");

        backBtn = findViewById(R.id.backBtn);
        trackingOrderLineRV = findViewById(R.id.trackOrderLineRV);
        trackingOrderLineRV.setLayoutManager(new LinearLayoutManager(this));

        singleBookTrackingItemModelArrayList = new ArrayList<>();
//        singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel("Order Confirmed, 17 Jan","Your order has been confirmed"));
//        singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel("Shipped, Expected by 27 Jan","Tracking Status 2"));
//        singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel("Out For Delivery","Tracking Status 3"));
//        singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel("Delivered","Tracking Status 3"));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getTrackingOrderDetails();
    }
    private void getTrackingOrderDetails() {
        String trackingURL = Constant.BASE_URL + "v1/order/track_parcel/" + shipment_id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, trackingURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel(shipment_id,"Order Confirmed," + orderPlacedDate,"Your order has been confirmed"));
                                JSONObject data = response.optJSONObject("data");

                                if (data != null) {
                                    // Get the shipment track array
                                    JSONArray shipmentTrackArray = data.optJSONArray("shipment_track");

                                    if (shipmentTrackArray != null && shipmentTrackArray.length() > 0) {
                                        JSONObject shipmentTrack = shipmentTrackArray.optJSONObject(0);
                                        String awbCode = shipmentTrack != null ? shipmentTrack.optString("awb_code", "N/A") : "N/A";
                                        String courierCompanyId = shipmentTrack != null && shipmentTrack.has("courier_company_id") ? shipmentTrack.optString("courier_company_id", "N/A") : "N/A";
                                        String shipmentId = shipmentTrack != null ? shipmentTrack.optString("shipment_id", "N/A") : "N/A";
                                        String orderId = shipmentTrack != null ? shipmentTrack.optString("order_id", "N/A") : "N/A";
                                        String pickupDate = shipmentTrack != null ? shipmentTrack.optString("pickup_date", "Not Available") : "Not Available";
                                        String deliveredDate = shipmentTrack != null ? shipmentTrack.optString("delivered_date", "Not Available") : "Not Available";

                                        // Handle shipment activities (if any)
                                        JSONArray shipmentTrackActivities = data.optJSONArray("shipment_track_activities");
                                        if (shipmentTrackActivities != null) {
                                            for (int i = 0; i < shipmentTrackActivities.length(); i++) {
                                                JSONObject activity = shipmentTrackActivities.optJSONObject(i);
                                                if (activity != null) {
                                                    String activityStatus = activity.optString("status", "Unknown");
                                                    String activityDescription = activity.optString("activity", "No Activity Description");

                                                    singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel(shipment_id,activityStatus,activityDescription));
                                                }
                                            }
                                        } else {
                                            singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel(shipment_id,"Yet to be Shipped",""));
                                            singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel(shipment_id,"Out For Delivery",""));
                                            singleBookTrackingItemModelArrayList.add(new SingleBookTrackingItemModel(shipment_id,"Delivered",""));
                                        }
                                    }
                                }
                                trackingOrderLineRV.setAdapter(new SingleOrderTrackingItemAdapter(TrackingSingleActivity.this,singleBookTrackingItemModelArrayList));
                            } else {
                                Toast.makeText(TrackingSingleActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                Toast.makeText(TrackingSingleActivity.this, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(TrackingSingleActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}