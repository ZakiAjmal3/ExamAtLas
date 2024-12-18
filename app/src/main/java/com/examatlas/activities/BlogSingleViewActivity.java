package com.examatlas.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BlogSingleViewActivity extends AppCompatActivity {
    ImageView blogIMG,backBtn;
    TextView blogTitle;
    WebView webviewContent;
    String blogURL;
    String blogID;
    SessionManager sessionManager;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_blog_view);

        blogIMG = findViewById(R.id.blogIMG);
        backBtn = findViewById(R.id.backBtn);
        blogTitle = findViewById(R.id.txtBlogTitle);
        webviewContent = findViewById(R.id.content);

        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");

        blogURL = Constant.BASE_URL + "blog/getBlogById/" + getIntent().getStringExtra("blogID");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getBlogList();
    }

    private void getBlogList() {
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, blogURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            Log.e("blog Response",response.toString());
                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("blog");
                                JSONObject imgJsonObject = jsonObject.getJSONObject("image");
                                String imageURL = imgJsonObject.getString("url");

                                Glide.with(BlogSingleViewActivity.this)
                                        .load(imageURL)
                                        .error(R.drawable.image1)
                                        .into(blogIMG);
                                String title = jsonObject.getString("title");
//                                String tags = jsonObject.getString("tags");
                                String content = jsonObject.getString("content");
                                blogTitle.setText(title);
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
                Toast.makeText(BlogSingleViewActivity.this, errorMessage, Toast.LENGTH_LONG).show();}
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        MySingleton.getInstance(BlogSingleViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}