package com.examatlas.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.AdminShowAllCoursesAdapter;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MultipartRequest;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.models.AdminShowAllCourseModel;
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

public class AdminCoursesCreateDeleteFragment extends Fragment {
    SearchView searchView;
    RecyclerView showAllCoursesRecycler;
    RelativeLayout noDataLayout;
    ProgressBar courseProgress;
    ArrayList<AdminShowAllCourseModel> adminShowAllCourseModelArrayList;
    AdminShowAllCoursesAdapter adminShowAllCoursesAdapter;
    Button btnCreateCourses;
    private final String courseURL = Constant.BASE_URL + "course/getAllCourse";
    private final String createCoursesURL = Constant.BASE_URL + "course/createcourse";
    private Dialog createCoursesDialogBox;
    private EditText titleEditTxt, priceEditTxt;
    Button submitCourseDetailsBtn;
    ImageView crossBtn,uploadImageBtn;
    private Uri image_uri;
    TextView uploadImageName;
    SessionManager sessionManager;
    String authToken;
    private File imageFile;
    // ActivityResultLaunchers
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_create_courses, container, false);

        searchView = view.findViewById(R.id.searchView);

        btnCreateCourses = view.findViewById(R.id.btnCreate);
        showAllCoursesRecycler = view.findViewById(R.id.showCoursesRecycler);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        courseProgress = view.findViewById(R.id.showAllCoursesProgressBar);
        adminShowAllCourseModelArrayList = new ArrayList<>();

        showAllCoursesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        showAllCoursesFunction();

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
                if (adminShowAllCoursesAdapter != null) {
                    adminShowAllCoursesAdapter.filter(newText);
                }
                return true;
            }
        });
        btnCreateCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateCoursesDialog();
            }
        });

        // Setup ActivityResultLaunchers
        setupActivityResultLaunchers();

        return view;
    }

    private void showAllCoursesFunction() {
        noDataLayout.setVisibility(View.GONE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, courseURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            showAllCoursesRecycler.setVisibility(View.VISIBLE);
                            courseProgress.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("course");
                                adminShowAllCourseModelArrayList.clear(); // Clear the list before adding new items

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String courseID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String price = jsonObject2.getString("price");
                                    String image = jsonObject2.getString("image");
                                    String createdDate = jsonObject2.getString("createdAt");

                                    AdminShowAllCourseModel courseModel = new AdminShowAllCourseModel(courseID, title, price, image, createdDate);
                                    adminShowAllCourseModelArrayList.add(courseModel);
                                }

                                // Update the original list in the adapter
                                if (adminShowAllCoursesAdapter != null) {
                                    adminShowAllCoursesAdapter.updateOriginalList(adminShowAllCourseModelArrayList);
                                }

                                // Check if the list is empty
                                if (adminShowAllCourseModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    courseProgress.setVisibility(View.GONE);
                                } else {
                                    if (adminShowAllCoursesAdapter == null) {
                                        adminShowAllCoursesAdapter = new AdminShowAllCoursesAdapter(AdminCoursesCreateDeleteFragment.this, adminShowAllCourseModelArrayList);
                                        showAllCoursesRecycler.setAdapter(adminShowAllCoursesAdapter);
                                    } else {
                                        adminShowAllCoursesAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                // Handle the case where status is false
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
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

        MySingletonFragment.getInstance(this).addToRequestQueue(jsonObjectRequest);
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

    private void openCreateCoursesDialog(){
        createCoursesDialogBox = new Dialog(requireContext());
        createCoursesDialogBox.setContentView(R.layout.admin_create_course_dialog);

        titleEditTxt = createCoursesDialogBox.findViewById(R.id.titleEditTxt);
        priceEditTxt = createCoursesDialogBox.findViewById(R.id.priceEditTxt);
        crossBtn = createCoursesDialogBox.findViewById(R.id.btnCross);
        uploadImageBtn = createCoursesDialogBox.findViewById(R.id.uploadImage);
        uploadImageName = createCoursesDialogBox.findViewById(R.id.txtNoFileChosen);

        submitCourseDetailsBtn = createCoursesDialogBox.findViewById(R.id.btnSubmit);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCoursesDialogBox.dismiss();
            }
        });

        submitCourseDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCourseDetailsFunction(titleEditTxt.getText().toString().trim(),priceEditTxt.getText().toString().trim());
            }
        });

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        createCoursesDialogBox.show();
        WindowManager.LayoutParams params = createCoursesDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        createCoursesDialogBox.getWindow().setAttributes(params);
        createCoursesDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createCoursesDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        createCoursesDialogBox.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
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
    private String getFileName(Uri uri) {
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
        uploadImageName.setText(imageName);

    }
    private String getFileNameFromFile(File file) {
        // Extract the file name from the imageFile (file name is the last segment of the path)
        return file != null ? file.getName() : "Unknown";
    }
    private File bitmapToFile(Bitmap bitmap) {
        File file = new File(getContext().getCacheDir(), "uploaded_image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    private void openGallery() {
        final CharSequence[] options = {"Open Camera", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add RC!");
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
                    Toast.makeText(getContext(), "RC Uploading Cancelled", Toast.LENGTH_SHORT).show();
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

    public void submitCourseDetailsFunction(String title, String price) {
        // Prepare form data
        Map<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("price", price);
        // Create a Map for files (if imageFile exists)
        Map<String, File> files = new HashMap<>();

        // If an image file is selected, add it to the files map
        if (imageFile != null && imageFile.exists()) {
            files.put("image", imageFile);
        }
        // Create and send the multipart request
        MultipartRequest multipartRequest = new MultipartRequest(createCoursesURL, params, files,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            boolean status = responseObject.getBoolean("status");
                            if (status) {
                                String message = responseObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                                // Refresh the course list
                                adminShowAllCourseModelArrayList.clear();
                                showAllCoursesFunction(); // Fetch the updated list

                                // Dismiss the dialog
                                createCoursesDialogBox.dismiss();
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
                        Log.e("CourseFetchError", errorMessage);
                    }
                }, authToken);

        // Add the request to the queue
        MySingletonFragment.getInstance(this).addToRequestQueue(multipartRequest);
    }
}
