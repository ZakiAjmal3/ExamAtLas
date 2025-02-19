package com.examatlas.fragment.Admin;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.adapter.Admin.AdminShowAllCategoryAdapter;
import com.examatlas.models.Admin.AdminShowAllCategoryModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MultipartRequest;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.utils.SessionManager;

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

public class AdminCreateCategoryFragment extends Fragment {
    TextView createBtn;
    SearchView searchView;
    RecyclerView showCategoryRecycler;
    RelativeLayout noDataLayout;
    ProgressBar progressBar,nextItemLoadingProgressBar;
    AdminShowAllCategoryAdapter categoryAdapter;
    AdminShowAllCategoryModel categoryModel;
    ArrayList<AdminShowAllCategoryModel> categoryModelArrayList;
    private final String categoryURL = Constant.BASE_URL + "v1/category";
    SessionManager sessionManager;
    String authToken;
    private String searchQuery = "";
    private int totalPages = 1,currentPage = 1;
    private final int itemsPerPage = 10;
    private boolean isLoading = false;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private Uri image_uri = null;
    private File imageFile = null;
    private NestedScrollView nestedSV;
    Dialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_fragment_create_category, container, false);

        createBtn = view.findViewById(R.id.btnCreate);
        searchView = view.findViewById(R.id.searchView);
        showCategoryRecycler = view.findViewById(R.id.showAllCategoryRecycler);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        progressBar = view.findViewById(R.id.showAllCategoryProgressBar);
        nextItemLoadingProgressBar = view.findViewById(R.id.nextItemLoadingProgressBar);
        nestedSV = view.findViewById(R.id.nestScrollView);

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        showCategoryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        getAllCategory();
        initializeDialogBoxContent();
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageFile = null;
                image_uri = null;
                titleEditTxt.setText(null);
                slugEditTxt.setText(null);
                uploadImageBtn.setImageResource(R.drawable.noimage);
                openDialogBoxCreateSubject();
            }
        });
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Check if we are near the bottom, but leave a small threshold to avoid issues with small screens
                Log.e("ScrollDebug", "scrollY: " + scrollY +
                        " measuredHeight: " + v.getChildAt(0).getMeasuredHeight() +
                        " scrollHeight: " + v.getMeasuredHeight());
                int scrollThreshold = 50; // threshold to trigger load more data
                int diff = (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) - scrollY;
                Log.e("ScrollDebug", "diff: " + diff);
                // Check if we have scrolled to the bottom or near bottom
                if (diff <= scrollThreshold && currentPage <= totalPages) {
                    // Only increment the page and load more data if there's more data to load
                    currentPage++;
                    nextItemLoadingProgressBar.setVisibility(View.VISIBLE);
                    getAllCategory();
                    Log.e("Scroll","Scroll Happened");
                }
            }
        });


        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKeyboard();
            }
        });

        // Set up touch listener for the parent layout
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (searchView.isShown() && !isPointInsideView(event.getRawX(), event.getRawY(), searchView)) {
                        searchView.setIconified(true); // Collapse the search view
                        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // Hide the keyboard
                    }
                }
                return false; // Allow other events to be handled
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not needed for this implementation
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Call the filter method when the search text changes
                if (categoryAdapter != null) {
                    categoryAdapter.filter(newText);
                }
                return true;
            }
        });
        // Setup ActivityResultLaunchers
        setupActivityResultLaunchers();
        categoryModelArrayList = new ArrayList<>();
        return view;
    }

    private void initializeDialogBoxContent() {
        createCategoryDialogBox = new Dialog(requireContext());
        createCategoryDialogBox.setContentView(R.layout.admin_create_category_dialog_box);

        titleEditTxt = createCategoryDialogBox.findViewById(R.id.titleEditTxt);
        slugEditTxt = createCategoryDialogBox.findViewById(R.id.slugEditText);
        submitBtn = createCategoryDialogBox.findViewById(R.id.btnSubmit);
        crossBtn = createCategoryDialogBox.findViewById(R.id.btnCross);
        uploadImageBtn = createCategoryDialogBox.findViewById(R.id.uploadImage);
        uploadImageName = createCategoryDialogBox.findViewById(R.id.txtNoFileChosen);
    }

    private Dialog createCategoryDialogBox;
    private EditText titleEditTxt, slugEditTxt;
    private Button submitBtn;
    private ImageView crossBtn,uploadImageBtn;
    private TextView uploadImageName;

    private void openDialogBoxCreateSubject() {

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCategoryDialogBox.dismiss();
            }
        });
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new Dialog(getContext());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                if (titleEditTxt.getText().toString().trim().isEmpty()){
                    titleEditTxt.setError("Title is required");
                    progressDialog.dismiss();
                    return;
                }
                if (slugEditTxt.getText().toString().trim().isEmpty()){
                    titleEditTxt.setError("Slug is required");
                    progressDialog.dismiss();
                    return;
                }
                if (imageFile == null){
                    progressDialog.dismiss();
                    return;
                }
                sendCategoryDetails(titleEditTxt.getText().toString().trim(), slugEditTxt.getText().toString().trim(),createCategoryDialogBox);
            }
        });

        createCategoryDialogBox.show();
        WindowManager.LayoutParams params = createCategoryDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

// Set the window attributes
        createCategoryDialogBox.getWindow().setAttributes(params);

// Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) createCategoryDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

// Set top and bottom margins (adjust values as needed)
        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

// Background and animation settings
        createCategoryDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createCategoryDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        createCategoryDialogBox.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }
    public void setupActivityResultLaunchers() {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    image_uri = data.getData();
                    handleImageUri(image_uri);
                    categoryAdapter.setCategoryImage(image_uri,null);
                }
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                if (bitmap != null) {
                    handleBitmap(bitmap);
                    categoryAdapter.setCategoryImage(null,bitmap);
                }
            }
        });
    }
    public void handleImageUri(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
            handleBitmap(bitmap);// Process the Bitmap as you did in handleBitmap()
            uploadImageBtn.setImageBitmap(bitmap);
            // Extract the image name from the URI
            String imageName = getFileName(uri);
            // Set the image name to the TextView (uploadImageName)
            uploadImageName.setText(imageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getFileName(Uri uri) {
        String fileName = null;

        // If the URI is a content URI (which is usually the case for gallery images)
        if (uri.getScheme().equals("content")) {
            Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
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
    public void handleBitmap(Bitmap bitmap) {
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
        uploadImageName.setText(imageName);

    }
    public String getFileNameFromFile(File file) {
        // Extract the file name from the imageFile (file name is the last segment of the path)
        return file != null ? file.getName() : "Unknown";
    }
    public File bitmapToFile(Bitmap bitmap) {
        File file = new File(getContext().getCacheDir(), "uploaded_image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    public void openGallery() {
        final CharSequence[] options = {"Open Camera", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add RC!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Open Camera")) {
                    // Check for camera permission before opening the camera
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        // If permission is granted, open the camera
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraLauncher.launch(intent); // Use cameraLauncher
                    } else {
                        // If permission is not granted, request it
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    // Open gallery to choose image
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryLauncher.launch(Intent.createChooser(intent, "Select Image")); // Use galleryLauncher
                } else if (options[item].equals("Cancel")) {
                    // Cancel the dialog
                    dialog.dismiss();
                    Toast.makeText(getContext(), "RC Uploading Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now open the camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(intent); // Use cameraLauncher
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(getContext(), "Camera permission is required to capture images.", Toast.LENGTH_SHORT).show();
            }
        }
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
    public void sendCategoryDetails(String title, String description, Dialog createCategoryDialogBox) {
        String createCategoryURL = Constant.BASE_URL + "v1/category";
        // Prepare form data
        Map<String, String> params = new HashMap<>();
        params.put("categoryName", title);   // Send the category name
        params.put("slug", description);     // Send the slug

        // Create a Map for files (if imageFile exists)
        Map<String, File> files = new HashMap<>();

        // If an image file is selected, add it to the files map
        if (imageFile != null && imageFile.exists()) {
            files.put("image", imageFile);
        }

        // Create and send the multipart request
        MultipartRequest multipartRequest = new MultipartRequest(createCategoryURL, params, files,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            boolean status = responseObject.getBoolean("success");
                            if (status) {
                                String message = responseObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                                // Handle the response data
                                JSONObject data = responseObject.getJSONObject("data"); // New category details
                                String id = data.getString("_id");                     // Category ID
                                String categoryName = data.getString("categoryName");  // Category name
                                String slug = data.getString("slug");                  // Category slug
                                String isActive = data.getString("isActive");         // Category active status
                                String updatedAt = data.getString("updatedAt");         // Category active status

                                String imageUrl = "";
                                if (data.has("image") && !data.isNull("image")) {
                                    JSONObject imageObj = data.getJSONObject("image");
                                    if (imageObj != null && !imageObj.isNull("url")) {
                                        imageUrl = imageObj.getString("url");
                                    }
                                }
                                int addedPosition = categoryModelArrayList.size();
                                // Create a new category object and add it to the list
                                AdminShowAllCategoryModel newCategory = new AdminShowAllCategoryModel(
                                        id, categoryName, slug, isActive, imageUrl,updatedAt);
                                categoryModelArrayList.add(newCategory); // Add to the existing list
                                getAllCategory();
                                // Dismiss the dialog box
                                createCategoryDialogBox.dismiss();
                                progressDialog.dismiss();
                            } else {
                                // Handle error message if status is false
                                String errorMessage = responseObject.getString("message");
                                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(getContext(), "An error occurred while processing the response.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error response
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                String responseData = new String(error.networkResponse.data, "UTF-8");
                                errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                                errorMessage += "\nResponse Data: " + responseData;
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("CategoryFetchError", errorMessage);
                    }
                }, authToken);

        // Add the request to the queue
        MySingletonFragment.getInstance(this).addToRequestQueue(multipartRequest);
    }


    public void getAllCategory() {
        String subjectURLPage = categoryURL + "?pageNumber=" + currentPage + "&pageSize=" + itemsPerPage;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subjectURLPage, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            createBtn.setEnabled(true);
                            showCategoryRecycler.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                            boolean status = response.getBoolean("success");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");

                                // Parse categories here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    String id = jsonObject2.getString("_id");
                                    String categoryName = jsonObject2.getString("categoryName");
                                    String description = jsonObject2.getString("slug");
                                    String isActive = jsonObject2.getString("isActive");
                                    String updatedAt = jsonObject2.getString("updatedAt");
                                    String imageUrl = "";

                                    if (jsonObject2.has("image") && !jsonObject2.isNull("image")) {
                                        JSONObject imageObj = jsonObject2.getJSONObject("image");
                                        if (imageObj != null && !imageObj.isNull("url")) {
                                            imageUrl = imageObj.getString("url");
                                        }
                                    }

                                    AdminShowAllCategoryModel categoryModel = new AdminShowAllCategoryModel(
                                            id, categoryName, description, isActive, imageUrl, updatedAt);
                                    boolean isPresent = false;
                                    for (int j = 0; j < categoryModelArrayList.size(); j++) {
                                        if (categoryModelArrayList.get(j).getId().equals(categoryModel.getId())) {
                                            isPresent = true;
                                            break;
                                        }
                                    }
                                    if (!isPresent) {
                                        categoryModelArrayList.add(categoryModel);
                                    }
                                    // Avoid adding duplicate categories
                                }
                                // Update UI if categories exist
                                if (categoryModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    showCategoryRecycler.setVisibility(View.GONE);
                                } else {
                                    showCategoryRecycler.setVisibility(View.VISIBLE);
                                    categoryAdapter = new AdminShowAllCategoryAdapter(categoryModelArrayList, AdminCreateCategoryFragment.this);
                                    showCategoryRecycler.setAdapter(categoryAdapter);
                                }

                                isLoading = false;
                                nextItemLoadingProgressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(getContext(), "An error occurred while processing the categories.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                String jsonError = new String(error.networkResponse.data, "UTF-8");
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
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

        // Adding the request to the request queue
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }


    public void openKeyboard() {
        searchView.setIconified(false); // Expands the search view
        searchView.requestFocus(); // Requests focus
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT); // Show the keyboard
    }

    public boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return (x >= location[0] && x <= (location[0] + view.getWidth()) &&
                y >= location[1] && y <= (location[1] + view.getHeight()));
    }
    public File getImageFile(){
        return imageFile;
    }
}
