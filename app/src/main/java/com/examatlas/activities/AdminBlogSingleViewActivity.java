package com.examatlas.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.adapter.AdminTagsForDataALLAdapter;
import com.examatlas.models.Admin.AdminShowAllBlogModel;
import com.examatlas.models.Admin.AdminShowAllCategoryModel;
import com.examatlas.models.AdminTagsForDataALLModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminBlogSingleViewActivity extends AppCompatActivity {
    ImageView blogIMG,backBtn,editBtn;
    TextView blogTitle,txtCategory,txtTags;
    WebView webviewContent;
    String blogURL;
    String blogID;
    SessionManager sessionManager;
    String token;
    ShimmerFrameLayout shimmer_blog_container;
    RelativeLayout mainContainer;
    String imageURL, titleStr, contentStr, keywordStr,categoryStr,tagsStr;
    private Spinner categorySpinner;
    ArrayList<AdminShowAllCategoryModel> categoryModelArrayList;
    String categoryId,categoryName;
    private File imageFile;
    // ActivityResultLaunchers
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Uri image_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_blog_single_view);

        editBtn = findViewById(R.id.editBtn);
        blogIMG = findViewById(R.id.blogIMG);
        backBtn = findViewById(R.id.backBtn);
        blogTitle = findViewById(R.id.txtBlogTitle);
        txtCategory = findViewById(R.id.categoryTxt);
        txtTags = findViewById(R.id.tagTxt);
        webviewContent = findViewById(R.id.content);
        shimmer_blog_container = findViewById(R.id.shimmer_blog_container);
        mainContainer = findViewById(R.id.mainContainer);

        sessionManager = new SessionManager(this);
        token = sessionManager.getUserData().get("authToken");

        blogID = getIntent().getStringExtra("blogID");
        blogURL = Constant.BASE_URL + "v1/blog/getBlogById/" + blogID;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mainContainer.setVisibility(View.GONE);
        shimmer_blog_container.setVisibility(View.VISIBLE);
        shimmer_blog_container.startShimmer();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getBlog();
                getCategory();
            }
        },1000);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditBlogDialog();
            }
        });
    }
    ImageView uploadImageBtn;
    TextView uploadImageTxt;
    private void openEditBlogDialog() {
        Dialog editBlogDialogBox = new Dialog(this);
        editBlogDialogBox.setContentView(R.layout.admin_create_blog_dialog_box);

        ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList = new ArrayList<>();
        AdminTagsForDataALLAdapter adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        RecyclerView tagsRecyclerView = editBlogDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setVisibility(View.VISIBLE);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        TextView headerTxt = editBlogDialogBox.findViewById(R.id.txtAddData);
        headerTxt.setText("Edit Blog");
        EditText titleEditTxt = editBlogDialogBox.findViewById(R.id.titleEditTxt);
        EditText keywordEditTxt = editBlogDialogBox.findViewById(R.id.keywordEditText);
        EditText contentEditTxt = editBlogDialogBox.findViewById(R.id.contentEditText);
        EditText tagsEditTxt = editBlogDialogBox.findViewById(R.id.tagsEditText);

        uploadImageBtn = editBlogDialogBox.findViewById(R.id.uploadImage);
        uploadImageTxt = editBlogDialogBox.findViewById(R.id.txtUploadImage);

        Glide.with(this)
                .load(imageURL)
                .error(R.drawable.noimage)
                .into(uploadImageBtn);

//        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openGallery();
//            }
//        });

        categorySpinner = editBlogDialogBox.findViewById(R.id.categorySpinner);

        setupCategorySpinner(categorySpinner, titleEditTxt,keywordEditTxt,contentEditTxt,tagsEditTxt,null);


        titleEditTxt.setText(titleStr);
        keywordEditTxt.setText(keywordStr);
        contentEditTxt.setText(contentStr);

        String[] tagsArray = tagsStr.split(",");
        for (String tag : tagsArray) {
            adminTagsForDataALLModelArrayList.add(new AdminTagsForDataALLModel(tag.trim()));
        }
        adminTagsForDataALLAdapter.notifyDataSetChanged();

        Button uploadBlogDetailsBtn = editBlogDialogBox.findViewById(R.id.btnSubmit);
        uploadBlogDetailsBtn.setClickable(true);

        tagsEditTxt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String tagText = tagsEditTxt.getText().toString().trim();
                if (!tagText.isEmpty()) {
                    adminTagsForDataALLModelArrayList.add(new AdminTagsForDataALLModel(tagText));
                    adminTagsForDataALLAdapter.notifyItemInserted(adminTagsForDataALLModelArrayList.size() - 1);
                    tagsEditTxt.setText("");
                    tagsRecyclerView.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        });

        uploadBlogDetailsBtn.setOnClickListener(view -> {
            uploadBlogDetailsBtn.setClickable(false);
            sendingBlogDetails(blogID,
                    titleEditTxt.getText().toString().trim(),
                    keywordEditTxt.getText().toString().trim(),
                    contentEditTxt.getText().toString().trim(),
                    adminTagsForDataALLModelArrayList);
            editBlogDialogBox.dismiss();
        });

        ImageView btnCross = editBlogDialogBox.findViewById(R.id.btnCross);
        btnCross.setOnClickListener(view -> editBlogDialogBox.dismiss());

        editBlogDialogBox.show();
        WindowManager.LayoutParams params = editBlogDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

        // Set the window attributes
        editBlogDialogBox.getWindow().setAttributes(params);

        // Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) editBlogDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

        // Background and animation settings
        editBlogDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editBlogDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    public void setupCategorySpinner(Spinner categorySpinners, EditText titleEditTxt, EditText keywordEditTxt, EditText contentEditTxt, EditText tagsEditTxt , AdminShowAllBlogModel currentCategory) {
        // Assuming `subCategoryModelArrayList` contains the categories data
        ArrayList<String> categoryNameList = new ArrayList<>();
        categoryNameList.add("Select Category"); // First item is "Select Category"

        for (int i = 0; i < categoryModelArrayList.size(); i++) {
            // Populate category names from your subCategoryModelArrayList
            categoryNameList.add(categoryModelArrayList.get(i).getCategoryName());
        }

        // Set the adapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinners.setAdapter(adapter);
        for (int i = 0; i < categoryNameList.size(); i++) {
            if (categoryNameList.get(i).equals(categoryStr)) {
                categorySpinners.setSelection(i);
                categoryName = categoryNameList.get(i);
            }
        }
        // Set the OnItemSelectedListener to handle category selection
        categorySpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) { // Ensure that a category is selected (not "Select Category")
                    categoryId = categoryModelArrayList.get(position - 1).getId();
                    categoryName = categoryModelArrayList.get(position - 1).getCategoryName();
                } else {
                    categoryId = null;
                    categoryName = "null";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                categoryId = null;
                categoryName = null;
            }
        });
    }

    private void sendingBlogDetails(String blogId, String title, String keyword, String content, ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList) {
        String updateURL = Constant.BASE_URL + "v1/blog/updateBlog/" + blogId;

        // Create JSON object to send in the request
        JSONObject blogDetails = new JSONObject();
        try {
            blogDetails.put("title", title);
            blogDetails.put("keyword", keyword);
            blogDetails.put("content", content);
            blogDetails.put("category", categoryName);

            // Convert tags to a JSONArray
            JSONArray tagsArray = new JSONArray();
            for (AdminTagsForDataALLModel tag : adminTagsForDataALLModelArrayList) {
                tagsArray.put(tag.getTagName()); // Assuming `getTag()` returns the tag string
            }
            blogDetails.put("tags", tagsArray);

        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error creating JSON object: " + e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, updateURL, blogDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(AdminBlogSingleViewActivity.this, "Blog Updated Successfully", Toast.LENGTH_SHORT).show();
                                getBlog();
                            } else {
                                Toast.makeText(AdminBlogSingleViewActivity.this, "Failed to update blog", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
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
                Toast.makeText(AdminBlogSingleViewActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("BlogUpdateError", errorMessage);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        MySingleton.getInstance(AdminBlogSingleViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }
    public  void getCategory(){
        String categoryURL = Constant.BASE_URL + "category/getCategory";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, categoryURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
//                                categoryModelArrayList = new ArrayList<>();
//                                JSONArray jsonArray = response.getJSONArray("data");
//                                // Parse books directly here
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
//                                    String id = jsonObject2.getString("_id");
//                                    String categoryName = jsonObject2.getString("categoryName");
//                                    String description = jsonObject2.getString("slug");
//                                    String is_active = jsonObject2.getString("is_active");
//                                    JSONObject imageObj = jsonObject2.getJSONObject("image");
//                                    String imageUrl = imageObj.getString("url");
//
//                                    AdminShowAllCategoryModel categoryModel = new AdminShowAllCategoryModel(id,categoryName,description,is_active,imageUrl);
//                                    categoryModelArrayList.add(categoryModel);
//                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(AdminBlogSingleViewActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("catch",e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminBlogSingleViewActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse",error.toString());
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
        MySingleton.getInstance(AdminBlogSingleViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void getBlog() {
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, blogURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            Log.e("blog Response",response.toString());
                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("data");
//                                keywordStr = jsonObject.getString("keyword");
                                // Use StringBuilder for tags
//                                StringBuilder tags = new StringBuilder();
//                                JSONArray jsonArray1 = jsonObject.getJSONArray("tags");
//                                for (int j = 0; j < jsonArray1.length(); j++) {
//                                    String singleTag = jsonArray1.getString(j);
//                                    tags.append(singleTag).append(", ");
//                                }
//                                // Remove trailing comma and space if any
//                                if (tags.length() > 0) {
//                                    tags.setLength(tags.length() - 2);
//                                }
//                                tagsStr = tags.toString();
//                                txtTags.setText(tagsStr);
//                                JSONObject imgJsonObject = jsonObject.getJSONObject("image");
                                imageURL = jsonObject.getString("coverImage");

                                Glide.with(AdminBlogSingleViewActivity.this)
                                        .load(imageURL)
                                        .error(R.drawable.image1)
                                        .into(blogIMG);

                                titleStr = jsonObject.getString("title");
                                contentStr = jsonObject.getString("content");
                                categoryStr = jsonObject.getString("category");

                                txtCategory.setText(categoryStr);
                                Log.e("content", contentStr);
                                blogTitle.setText(titleStr);

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
                                String fullHtmlContent = injectedCss + contentStr;

                                // Disable scrolling and over-scrolling
                                webviewContent.setVerticalScrollBarEnabled(false);  // Disable vertical scroll bar
                                webviewContent.setOverScrollMode(WebView.OVER_SCROLL_NEVER); // Disable over-scrolling effect

                                // Load the modified HTML content
                                webviewContent.loadData(fullHtmlContent, "text/html", "UTF-8");
                                shimmer_blog_container.stopShimmer();
                                shimmer_blog_container.setVisibility(View.GONE);
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
                Toast.makeText(AdminBlogSingleViewActivity.this, errorMessage, Toast.LENGTH_LONG).show();}
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        MySingleton.getInstance(AdminBlogSingleViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }
    private void setupActivityResultLaunchers() {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    image_uri = data.getData();
                    handleImageUri(image_uri);
                }
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                if (bitmap != null) {
                    handleBitmap(bitmap);
                }
            }
        });
    }
    private void handleImageUri(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            handleBitmap(bitmap);// Process the Bitmap as you did in handleBitmap()
            uploadImageBtn.setImageBitmap(bitmap);
            // Extract the image name from the URI
            String imageName = getFileName(uri);
            // Set the image name to the TextView (uploadImageName)
            uploadImageTxt.setText(imageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getFileName(Uri uri) {
        String fileName = null;

        // If the URI is a content URI (which is usually the case for gallery images)
        if (uri.getScheme().equals("content")) {
            Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Get the column index for the display name (filename)
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex != -1) {
                    fileName = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }
        // If the URI is a file URI
        else if (uri.getScheme().equals("file")) {
            fileName = uri.getLastPathSegment(); // Get the last part of the path (filename)
        }

        return fileName != null ? fileName : "Unknown";
    }
    private void handleBitmap(Bitmap bitmap) {
        // Resize image
        bitmap = getResizedBitmap(bitmap, 400);
        uploadImageBtn.setImageBitmap(bitmap);

        // Convert Bitmap to File
        imageFile = bitmapToFile(bitmap);  // Store the imageFile globally

        // Encode the image to Base64 for further use
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Extract the file name from the imageFile (created from Bitmap)
        String imageName = getFileNameFromFile(imageFile);

        // Set the image name to the TextView (uploadImageName)
        uploadImageTxt.setText(imageName);

    }
    private String getFileNameFromFile(File file) {
        // Extract the file name from the imageFile (file name is the last segment of the path)
        return file != null ? file.getName() : "Unknown";
    }
    private File bitmapToFile(Bitmap bitmap) {
        File file = new File(this.getCacheDir(), "uploaded_image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    private void openGallery() {
        final CharSequence[] options = {"Open Camera", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Open Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraLauncher.launch(intent); // Use cameraLauncher
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryLauncher.launch(Intent.createChooser(intent, "Select Image")); // Use galleryLauncher
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                    Toast.makeText(AdminBlogSingleViewActivity.this, "Image Uploading Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}