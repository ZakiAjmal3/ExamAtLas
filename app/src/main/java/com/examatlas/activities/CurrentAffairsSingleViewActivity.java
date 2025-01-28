package com.examatlas.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CurrentAffairsSingleViewActivity extends AppCompatActivity {
    ImageView cAIMG,backBtn;
    TextView caTitleHeader;
    WebView webviewContent;
    String cAURL;
    String cAID;
    SessionManager sessionManager;
    String token;
    ShimmerFrameLayout shimmerFrameLayout;
    RelativeLayout mainContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_affairs_single_view);

        cAIMG = findViewById(R.id.cAIMG);
        backBtn = findViewById(R.id.backBtn);
        caTitleHeader = findViewById(R.id.caTitleHeader);
        webviewContent = findViewById(R.id.content);

        shimmerFrameLayout = findViewById(R.id.shimmer_blog_container);
        shimmerFrameLayout.startShimmer();
        mainContainer = findViewById(R.id.mainContainer);
        mainContainer.setVisibility(View.GONE);

        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");
        cAURL = Constant.BASE_URL + "v1/blog/getBlogById/" + getIntent().getStringExtra("CAID");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCA();
            }
        },1000);
    }

    private void getCA() {
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, cAURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            Log.e("CA Response",response.toString());
                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("data");
                                JSONObject imgJsonObject = jsonObject.getJSONObject("image");
                                String imageURL = imgJsonObject.getString("url");

                                Glide.with(CurrentAffairsSingleViewActivity.this)
                                        .load(imageURL)
                                        .error(R.drawable.image1)
                                        .into(cAIMG);
                                String title = jsonObject.getString("title");
//                                String tags = jsonObject.getString("tags");
                                String content = jsonObject.getString("content");
                                caTitleHeader.setText(title);
                                caTitleHeader.setEllipsize(TextUtils.TruncateAt.END);
                                caTitleHeader.setMaxLines(1);
                                caTitleHeader.setVisibility(View.VISIBLE);
                                // Enable JavaScript (optional, depending on your content)
                                WebSettings webSettings = webviewContent.getSettings();
                                webSettings.setJavaScriptEnabled(true);

                                String injectedCss = "<style type=\"text/css\">"
                                        + "p { font-size: 12px !important; line-height: 1.4; }"    // Set <p> font size to 10px and line-height for readability
                                        + "h1 { font-size: 16px !important; }"    // Set <h1> font size to 14px (smaller)
                                        + "h2 { font-size: 14px !important; }"    // Set <h2> font size to 12px (smaller)
                                        + "h3 { font-size: 13px !important; }"    // Set <h3> font size to 11px (smaller)
                                        + "h4 { font-size: 12px !important; }"    // Set <h4> font size to 10px (smaller)
                                        + "h5 { font-size: 11px !important; }"     // Set <h5> font size to 9px (smaller)
                                        + "h6 { font-size: 10px !important; }"     // Set <h6> font size to 8px (smaller)
                                        + "ul, ol { font-size: 12px !important; }" // Set list items font size to 10px
                                        + "li { font-size: 12px !important; }"    // Set list item font size to 10px
                                        + "img { width: 100% !important; height: auto !important; }"  // Adjust image size to fit container
                                        + "</style>";
                                String fullHtmlContent = injectedCss + content;

                                // Disable scrolling and over-scrolling
                                webviewContent.setVerticalScrollBarEnabled(false);  // Disable vertical scroll bar
                                webviewContent.setOverScrollMode(WebView.OVER_SCROLL_NEVER); // Disable over-scrolling effect

                                // Load the modified HTML content
                                webviewContent.loadData(fullHtmlContent, "text/html", "UTF-8");
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                mainContainer.setVisibility(View.VISIBLE);
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
                Toast.makeText(CurrentAffairsSingleViewActivity.this, errorMessage, Toast.LENGTH_LONG).show();}
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        MySingleton.getInstance(CurrentAffairsSingleViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}