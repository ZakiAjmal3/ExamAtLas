package com.examatlas.activities;

import android.os.Bundle;
import android.util.Log;
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

public class SingleBlogViewActivity extends AppCompatActivity {
    ImageView blogIMG;
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
        blogTitle = findViewById(R.id.txtBlogTitle);
        webviewContent = findViewById(R.id.content);

        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");

        blogURL = Constant.BASE_URL + "blog/getBlogById/" + getIntent().getStringExtra("blogID");
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

                                Glide.with(SingleBlogViewActivity.this)
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

                                // Inject CSS to control the image size
                                String injectedCss = "<style>"
                                        + "p { font-size: 20px; }" // Increase text size only for <p> tags (paragraphs)
                                        + "img { width: 100%; height: auto; }" // Adjust image size as needed
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
                Toast.makeText(SingleBlogViewActivity.this, errorMessage, Toast.LENGTH_LONG).show();}
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        MySingleton.getInstance(SingleBlogViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}