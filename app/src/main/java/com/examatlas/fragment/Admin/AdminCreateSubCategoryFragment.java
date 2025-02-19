package com.examatlas.fragment.Admin;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.TextViewKt;
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
import com.examatlas.adapter.Admin.AdminShowAllCategoryAdapter;
import com.examatlas.adapter.Admin.AdminShowAllSubCategoryAdapter;
import com.examatlas.models.Admin.AdminShowAllCategoryModel;
import com.examatlas.models.Admin.AdminShowAllSubCategoryModel;
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

public class AdminCreateSubCategoryFragment extends Fragment {
    TextView createBtn;
    SearchView searchView;
    RecyclerView showSubCategoryRecycler;
    RelativeLayout noDataLayout;
    ProgressBar progressBar,nextItemLoadingProgressBar;
    AdminShowAllSubCategoryAdapter subCategoryAdapter;
    AdminShowAllSubCategoryModel subCategoryModel;
    ArrayList<AdminShowAllSubCategoryModel> subCategoryModelArrayList;
    ArrayList<AdminShowAllSubCategoryModel> subCategoryModelArrayList2;
    private final String subCategoryURL = Constant.BASE_URL + "v1/sub-category";
    private final String categoryURL = Constant.BASE_URL + "v1/category";
    AdminShowAllCategoryModel categoryModel;
    AdminShowAllCategoryAdapter categoryAdapter;
    ArrayList<AdminShowAllCategoryModel> categoryModelArrayList = new ArrayList<>();
    SessionManager sessionManager;
    String authToken;
    private String searchQuery = "";
    private int totalRows = 1,totalPages = 1, currentPage = 1;
    private final int itemsPerPage = 10;
    private boolean isLoading = false;
    ArrayList<String> categoryNameList;
    String categoryId = null;
    private NestedScrollView nestedSV;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private Uri image_uri;
    private File imageFile = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_fragment_create_subcategory, container, false);

        createBtn = view.findViewById(R.id.btnCreate);
        searchView = view.findViewById(R.id.searchView);
        showSubCategoryRecycler = view.findViewById(R.id.showAllSubCategoryRecycler);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        progressBar = view.findViewById(R.id.showAllSubCategoryProgressBar);
        nextItemLoadingProgressBar = view.findViewById(R.id.nextItemLoadingProgressBar);
        nestedSV = view.findViewById(R.id.nestScrollView);

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        subCategoryModelArrayList = new ArrayList<>();

        showSubCategoryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        subCategoryModelArrayList2 = new ArrayList<>();

        getAllCategory();
        getAllSubCategory();
        setupActivityResultLaunchers();
        initializeDialogContent();
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogBoxCreateSubject();
            }
        });
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    currentPage++;
                    int scrollThreshold = 50; // threshold to trigger load more data
                    int diff = (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) - scrollY;
                    Log.e("ScrollDebug", "diff: " + diff);
                    // Check if we have scrolled to the bottom or near bottom
                    if (diff <= scrollThreshold && currentPage <= totalPages) {
                        nextItemLoadingProgressBar.setVisibility(View.VISIBLE);
                        // on below line we are again calling
                        // a method to load data in our array list.
                        getAllSubCategory();
                    }
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
                if (subCategoryAdapter != null) {
                    subCategoryAdapter.filter(newText);
                }
                return true;
            }
        });

        return view;
    }

    private Dialog createSubCategoryDialogBox;
    Spinner categorySpinner;
    private EditText titleEditTxt;
    TextView uploadImgName;
    private Button submitBtn;
    private ImageView uploadImg,crossBtn;
    public void initializeDialogContent(){
        createSubCategoryDialogBox = new Dialog(requireContext());
        createSubCategoryDialogBox.setContentView(R.layout.admin_create_subcategory_dialog_box);

        titleEditTxt = createSubCategoryDialogBox.findViewById(R.id.titleEditTxt);
        categorySpinner = createSubCategoryDialogBox.findViewById(R.id.categorySpinner);
        uploadImg = createSubCategoryDialogBox.findViewById(R.id.uploadImage);
        uploadImgName = createSubCategoryDialogBox.findViewById(R.id.txtNoFileChosen);
        submitBtn = createSubCategoryDialogBox.findViewById(R.id.btnSubmit);
        crossBtn = createSubCategoryDialogBox.findViewById(R.id.btnCross);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void openDialogBoxCreateSubject() {

        createSubCategoryDialogBox = new Dialog(requireContext());
        createSubCategoryDialogBox.setContentView(R.layout.admin_create_subcategory_dialog_box);

        titleEditTxt = createSubCategoryDialogBox.findViewById(R.id.titleEditTxt);
        categorySpinner = createSubCategoryDialogBox.findViewById(R.id.categorySpinner);
        uploadImg = createSubCategoryDialogBox.findViewById(R.id.uploadImage);
        uploadImgName = createSubCategoryDialogBox.findViewById(R.id.txtNoFileChosen);
        submitBtn = createSubCategoryDialogBox.findViewById(R.id.btnSubmit);
        crossBtn = createSubCategoryDialogBox.findViewById(R.id.btnCross);

        setupCategorySpinner(categorySpinner,titleEditTxt,null);

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSubCategoryDialogBox.dismiss();
            }
        });
        createSubCategoryDialogBox.findViewById(android.R.id.content).setOnTouchListener((v, event) -> {
            if (categoryId == null) {
                // Show toast if no category is selected
                Toast.makeText(getContext(), "Please select a category first", Toast.LENGTH_SHORT).show();
            }
            return false; // Return false to allow the touch event to propagate
        });
        titleEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category", Toast.LENGTH_SHORT).show();
                }
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a category", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (titleEditTxt.getText().toString().trim().isEmpty()){
                titleEditTxt.setError("Please enter Title");
                }
                sendSubCategoryDetails(categoryId,titleEditTxt.getText().toString().trim(), createSubCategoryDialogBox);
            }
        });

        createSubCategoryDialogBox.show();
        WindowManager.LayoutParams params = createSubCategoryDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

// Set the window attributes
        createSubCategoryDialogBox.getWindow().setAttributes(params);

// Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) createSubCategoryDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

// Set top and bottom margins (adjust values as needed)
        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

// Background and animation settings
        createSubCategoryDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createSubCategoryDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        createSubCategoryDialogBox.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }

    public void setupCategorySpinner(Spinner categorySpinners, EditText titleEditTxt, AdminShowAllSubCategoryModel currentCategory) {
        // Assuming `subCategoryModelArrayList` contains the categories data
        categoryNameList = new ArrayList<>();
        categoryNameList.add("Select Category"); // First item is "Select Category"

        for (int i = 0; i < categoryModelArrayList.size(); i++) {
            // Populate category names from your subCategoryModelArrayList
            categoryNameList.add(categoryModelArrayList.get(i).getCategoryName());
        }
        // Set the adapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinners.setAdapter(adapter);
        if (currentCategory != null) {
            for (int i = 0; i < categoryNameList.size(); i++) {
                if (categoryNameList.get(i).equals(currentCategory.getCategoryName())) {
                    categorySpinners.setSelection(i);
                    categorySpinners.setEnabled(false);
                }
            }
        }

        // Set the OnItemSelectedListener to handle category selection
        categorySpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0 && subCategoryModelArrayList != null) {
                    // Ensure that a category is selected and the position is within bounds
                    categoryId = categoryModelArrayList.get(position - 1).getId(); // Get the corresponding categoryId
                }
                titleEditTxt.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                categoryId = null; // Reset categoryId if nothing is selected
            }
        });
    }
    public void setupActivityResultLaunchers() {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    image_uri = data.getData();
                    handleImageUri(image_uri);
                    subCategoryAdapter.setCategoryImage(image_uri,null);
                }
            }
        });
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                if (bitmap != null) {
                    handleBitmap(bitmap);
                    subCategoryAdapter.setCategoryImage(null,bitmap);
                }
            }
        });
    }
    public void handleImageUri(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
            handleBitmap(bitmap);// Process the Bitmap as you did in handleBitmap()
            uploadImg.setImageBitmap(bitmap);
            // Extract the image name from the URI
            String imageName = getFileName(uri);
            // Set the image name to the TextView (uploadImageName)
            uploadImgName.setText(imageName);
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
        uploadImg.setImageBitmap(bitmap);

        // Convert Bitmap to File
        imageFile = bitmapToFile(bitmap);  // Store the imageFile globally

        // Encode the image to Base64 for further use
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        // Extract the file name from the imageFile (created from Bitmap)
        String imageName = getFileNameFromFile(imageFile);

        // Set the image name to the TextView (uploadImageName)
        uploadImgName.setText(imageName);

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
    public void sendSubCategoryDetails(String categoryId, String title, Dialog createCategoryDialogBox) {
        String subCreateCategoryURL = Constant.BASE_URL + "v1/sub-category";  // Update the URL for subcategory creation

        // Prepare form data
        Map<String, String> params = new HashMap<>();
        params.put("categoryId", categoryId);   // Send the category ID
        params.put("name", title);   // Send the subcategory name

        // Create a Map for files (if any files exist)
        Map<String, File> files = new HashMap<>();

        // If an image file is selected (optional), add it to the files map
        // If your subcategory creation requires an image, uncomment the below lines

        if (imageFile != null && imageFile.exists()) {
            files.put("image", imageFile);
        }
        // Create and send the multipart request
        MultipartRequest multipartRequest = new MultipartRequest(subCreateCategoryURL, params, files,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            boolean status = responseObject.getBoolean("success");
                            if (status) {
                                String message = responseObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                subCategoryModelArrayList.clear();
                                getAllSubCategory();  // Refresh the subcategories list
                                createCategoryDialogBox.dismiss();  // Close the dialog
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
                                String responseData = new String(error.networkResponse.data, "UTF-8");
                                errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                                errorMessage += "\nResponse Data: " + responseData;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("SubCategoryFetchError", errorMessage);
                    }
                }, authToken);

        // Add the request to the queue
        MySingletonFragment.getInstance(this).addToRequestQueue(multipartRequest);
    }

    public void getAllSubCategory() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subCategoryURL, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("SubCategoryResponse", response.toString());
                            showSubCategoryRecycler.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");

                            if (status) {
                                createBtn.setEnabled(true);

                                JSONArray jsonArray = response.getJSONArray("data");

                                totalPages = Integer.parseInt(response.getString("totalPage"));

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String subCategoryId = jsonObject2.getString("_id");
                                    String updatedAt = jsonObject2.getString("updatedAt");

                                    // Check if categoryId is null
                                    JSONObject jsonObject3 = jsonObject2.optJSONObject("category");
                                    String categoryId = "";
                                    String categoryName = "";
                                    // If categoryId is not null, parse its data
                                    if (jsonObject3 != null) {
                                        categoryId = jsonObject3.getString("_id");
                                        categoryName = jsonObject3.getString("categoryName");
                                    }

                                    String subCategoryName = jsonObject2.getString("name");
                                    String imageUrl = "";
                                    if (jsonObject2.has("imageUrl") && !jsonObject2.isNull("imageUrl")) {
                                        JSONObject imageObj = jsonObject2.getJSONObject("imageUrl");
                                        if (imageObj != null && !imageObj.isNull("url")) {
                                            imageUrl = imageObj.getString("url");
                                        }
                                    }
                                    String icActive = jsonObject2.getString("isActive");

                                    subCategoryModel = new AdminShowAllSubCategoryModel(categoryId,categoryName,subCategoryId,subCategoryName,imageUrl,icActive,updatedAt,totalRows,totalPages,currentPage);
                                    boolean isPresent = false;
                                    for (int j = 0; j < subCategoryModelArrayList.size(); j++) {
                                        if (subCategoryModelArrayList.get(j).getSubCategoryId().equals(subCategoryModel.getSubCategoryId())) {
                                            isPresent = true;
                                            break;
                                        }
                                    }
                                    if (!isPresent) {
                                        subCategoryModelArrayList.add(subCategoryModel);
                                    }
                                }
//                                updateUI();
                                // Update UI after fetching the data
                                if (subCategoryModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    showSubCategoryRecycler.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    subCategoryAdapter = new AdminShowAllSubCategoryAdapter(subCategoryModelArrayList, AdminCreateSubCategoryFragment.this);
                                    showSubCategoryRecycler.setAdapter(subCategoryAdapter);
                                }
                                isLoading = false;
                                nextItemLoadingProgressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
    public void getAllCategory() {
        String subjectURLPage = categoryURL;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subjectURLPage, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            createBtn.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("success");

                            if (status) {
                                categoryModelArrayList = new ArrayList<>();
                                JSONArray jsonArray = response.getJSONArray("data");
                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String id = jsonObject2.getString("_id");
                                    String categoryName = jsonObject2.getString("categoryName");
                                    String description = jsonObject2.getString("slug");
                                    String is_active = jsonObject2.getString("isActive");
                                    String updatedAt = jsonObject2.getString("updatedAt");
                                    String imageUrl = "";
                                    if (jsonObject2.has("image") && !jsonObject2.isNull("image")) {
                                        JSONObject imageObj = jsonObject2.getJSONObject("image");
                                        if (imageObj != null && !imageObj.isNull("url")) {
                                            imageUrl = imageObj.getString("url");
                                        }
                                    }
                                    categoryModel = new AdminShowAllCategoryModel(id,categoryName,description,is_active,imageUrl,updatedAt);
                                    categoryModelArrayList.add(categoryModel);
                                }
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
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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

    private void openKeyboard() {
        searchView.setIconified(false); // Expands the search view
        searchView.requestFocus(); // Requests focus
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT); // Show the keyboard
    }

    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return (x >= location[0] && x <= (location[0] + view.getWidth()) &&
                y >= location[1] && y <= (location[1] + view.getHeight()));
    }
    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        int previousLength = subCategoryModelArrayList.size(); // Initial size of the list
        subCategoryModelArrayList.addAll(subCategoryModelArrayList2); // Add new items to the existing list
        int newAddedLength = subCategoryModelArrayList2.size(); // Length of the newly added items

        subCategoryModelArrayList2.clear(); // Clear temporary list

        Log.d("UpdateUI", "Previous Length: " + previousLength);
        Log.d("UpdateUI", "New Length: " + subCategoryModelArrayList.size());
        Log.d("UpdateUI", "New Items Added: " + newAddedLength);

        if (subCategoryModelArrayList.isEmpty()) {
            noDataLayout.setVisibility(View.VISIBLE);
            showSubCategoryRecycler.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            noDataLayout.setVisibility(View.GONE);
            showSubCategoryRecycler.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            if (subCategoryAdapter == null) {
                // Initialize the adapter if it is null
                subCategoryAdapter = new AdminShowAllSubCategoryAdapter(subCategoryModelArrayList, AdminCreateSubCategoryFragment.this);
                showSubCategoryRecycler.setAdapter(subCategoryAdapter);
                Log.d("UpdateUI", "Adapter was null, now set.");
            } else {
                // Notify the adapter about the newly added items
                if (newAddedLength > 0) {
                    // Notify the adapter about the range of new items inserted
                    subCategoryAdapter.notifyItemRangeInserted(previousLength, newAddedLength);
                    Log.d("UpdateUI", "Notified adapter about new items.");
                } else {
                    Log.d("UpdateUI", "No new items to notify.");
                }
            }
        }

        // If you want to scroll to the newly loaded items, you can scroll to the last item
        if (newAddedLength > 0) {
            showSubCategoryRecycler.scrollToPosition(subCategoryModelArrayList.size() - 1);
        }
    }
    public File getImageFile(){
        return imageFile;
    }
}
