package com.examatlas.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.R;
import com.examatlas.adapter.AdminShowAllLiveCoursesAdapter;
import com.examatlas.adapter.AdminShowAllSubCategoryAdapter;
import com.examatlas.adapter.AdminShowAllSubjectAdapter;
import com.examatlas.adapter.AdminTagsForDataALLAdapter;
import com.examatlas.models.AdminShowAllCategoryModel;
import com.examatlas.models.AdminShowAllLiveCoursesModel;
import com.examatlas.models.AdminShowAllSubCategoryModel;
import com.examatlas.models.AdminShowAllSubjectModel;
import com.examatlas.models.AdminTagsForDataALLModel;
import com.examatlas.models.LiveCoursesModel;
import com.examatlas.models.extraModels.AdminCategoryModel;
import com.examatlas.models.extraModels.BookImageModels;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminCreateLiveCoursesFragment extends Fragment {

    ProgressBar liveClassesProgress;
    AdminShowAllLiveCoursesAdapter liveCoursesAdapter;
    RecyclerView liveClassesRecycler;
    ArrayList<AdminShowAllLiveCoursesModel> liveCoursesModelArrayList = new ArrayList<>();
    ArrayList<AdminShowAllSubCategoryModel> subCategoryModelArrayList;
    ArrayList<AdminShowAllSubjectModel> subjectModelArrayList;
    private final String liveClassURL = Constant.BASE_URL + "liveclass/getAllLiveClass";
    SessionManager sessionManager;
    String authToken;
    Button createCourseBtn;
    ArrayList<AdminShowAllCategoryModel> categoryModelArrayList;
    private int totalPages = 1,currentPage = 1;
    private final int itemsPerPage = 10;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private Uri image_uri;
    private File imageFile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_fragment_create_live_courses, container, false);

        createCourseBtn = view.findViewById(R.id.btnCreate);

        liveClassesProgress = view.findViewById(R.id.showAllLiveCoursesProgressBar);
        liveClassesRecycler = view.findViewById(R.id.showLiveCoursesRecycler);
        liveClassesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        liveClassesProgress.setVisibility(View.VISIBLE);
        liveClassesRecycler.setVisibility(View.GONE);

        sessionManager = new SessionManager(getContext());
        authToken = sessionManager.getUserData().get("authToken");

        categoryModelArrayList = new ArrayList<>();
        getLiveClasses();
        getCategory();

        createCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateSendLiveCourseDialog();
            }
        });
        // Setup ActivityResultLaunchers
        setupActivityResultLaunchers();

        return view;
    }
    Dialog dialog;
    Spinner categorySpinner,subCategorySpinner,subjectSpinner;
    ImageView crossBtn,uploadImageBtn;
    private TextView uploadImageName;;
    Button submitBtn;
    private TextView startDateDisplayText,endDateDisplayText;
    String categoryId = null,subCategoryId = null,subjectId = null;
    EditText courseTitleEditText,courseSubTitleEditText,teacherNameEditText,languageEditText,priceOfCourseEditText,finalPriceOfCourseEditText,descriptionEditText,courseContentEditText,tagsEditTxt;
    private RecyclerView tagsRecyclerView;
    private AdminTagsForDataALLAdapter adminTagsForDataALLAdapter;
    private AdminTagsForDataALLModel adminTagsForDataALLModel;
    private ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList;
    ProgressBar subCategoryProgressBar,subjectProgressBar;
    String startDateIso,endDateIso;
    @SuppressLint("ClickableViewAccessibility")
    private void openCreateSendLiveCourseDialog() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.admin_create_live_courses_dialog_box);

        startDateDisplayText = dialog.findViewById(R.id.dateDisplayText);
        endDateDisplayText = dialog.findViewById(R.id.endDateText);
        categorySpinner = dialog.findViewById(R.id.categorySpinner);
        subCategorySpinner = dialog.findViewById(R.id.subCategorySpinner);
        subCategoryProgressBar = dialog.findViewById(R.id.subCategoryProgressBar);
        subjectSpinner = dialog.findViewById(R.id.subjectSpinner);
        subjectProgressBar = dialog.findViewById(R.id.subjectProgressBar);

        uploadImageBtn = dialog.findViewById(R.id.uploadImage);
        uploadImageName = dialog.findViewById(R.id.txtNoFileChosen);

        submitBtn = dialog.findViewById(R.id.btnSubmit);
        crossBtn = dialog.findViewById(R.id.btnCross);
        courseTitleEditText = dialog.findViewById(R.id.titleEditTxt);
        courseSubTitleEditText = dialog.findViewById(R.id.subTitleEditTxt);
        teacherNameEditText = dialog.findViewById(R.id.teacherNameEditText);
        languageEditText = dialog.findViewById(R.id.languageEditText);
        priceOfCourseEditText = dialog.findViewById(R.id.priceOfCourseEditText);
        finalPriceOfCourseEditText = dialog.findViewById(R.id.finalPriceOfCourseEditText);
        descriptionEditText = dialog.findViewById(R.id.descriptionEditText);
        courseContentEditText = dialog.findViewById(R.id.courseContentEditText);
        tagsEditTxt = dialog.findViewById(R.id.tagsEditText);

        tagsRecyclerView = dialog.findViewById(R.id.tagsRecycler);
        adminTagsForDataALLModelArrayList = new ArrayList<>();
        tagsRecyclerView = dialog.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        setupCategorySpinner(categorySpinner,null);

        dialog.findViewById(android.R.id.content).setOnTouchListener((v, event) -> {
            if (categoryId == null) {
                // Show toast if no category is selected
                Toast.makeText(getContext(), "Please select a category first", Toast.LENGTH_SHORT).show();
            }
            return false; // Return false to allow the touch event to propagate
        });
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        courseTitleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        courseSubTitleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        teacherNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        languageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        priceOfCourseEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        descriptionEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        courseContentEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tagsEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null){
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tagsEditTxt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String tagText = tagsEditTxt.getText().toString().trim();
                if (!tagText.isEmpty()) {
                    adminTagsForDataALLModelArrayList.add(new AdminTagsForDataALLModel(tagText));
                    adminTagsForDataALLAdapter.notifyItemInserted(adminTagsForDataALLModelArrayList.size() - 1);
                    tagsEditTxt.setText(""); // Clear the EditText
                    tagsRecyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
                }
                return true; // Indicate that we've handled the event
            }
            return false; // Pass the event on
        });

        endDateDisplayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                                // Display the selected date in the EditText
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, month, dayOfMonth);

                                // Format the date to a readable format
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = dateFormat.format(selectedDate.getTime());

                                // Set the formatted date in EditText
                                endDateDisplayText.setText(formattedDate);
                                SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                endDateIso = isoDateFormat.format(selectedDate.getTime());
                            }
                        },
                        year, month, dayOfMonth);

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });
        // Set click listener for the button to show the date picker dialog
        startDateDisplayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                                // Display the selected date in the EditText
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, month, dayOfMonth);

                                // Format the date to a readable format
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = dateFormat.format(selectedDate.getTime());

                                // Set the formatted date in EditText
                                startDateDisplayText.setText(formattedDate);
                                SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                startDateIso = isoDateFormat.format(selectedDate.getTime());

                            }
                        },
                        year, month, dayOfMonth);

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCreateLiveCourseData(dialog);
            }
        });

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

// Set the window attributes
        dialog.getWindow().setAttributes(params);

// Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) dialog.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

// Set top and bottom margins (adjust values as needed)
        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

// Background and animation settings
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    }
    public void setupCategorySpinner(Spinner categorySpinners,AdminCategoryModel currentCategory){
        // Assuming `subCategoryModelArrayList` contains the categories data
        ArrayList<String> categoryNameList = new ArrayList<>();
        categoryNameList.add("Select Category"); // First item is "Select Category"

        // Populate category names from your subCategoryModelArrayList
        for (AdminShowAllCategoryModel category : categoryModelArrayList) {
            categoryNameList.add(category.getCategoryName());
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
                if (position > 0) { // Ensure that a category is selected (not "Select Category")
                    categoryId = categoryModelArrayList.get(position - 1).getId(); // Get the corresponding categoryId
                    courseContentEditText.setEnabled(true);
                    courseTitleEditText.setEnabled(true);
                    courseSubTitleEditText.setEnabled(true);
                    teacherNameEditText.setEnabled(true);
                    languageEditText.setEnabled(true);
                    priceOfCourseEditText.setEnabled(true);
                    startDateDisplayText.setEnabled(true);
                    endDateDisplayText.setEnabled(true);
                    descriptionEditText.setEnabled(true);
                    tagsEditTxt.setEnabled(true);
                    tagsRecyclerView.setVisibility(View.VISIBLE);
                    subCategoryProgressBar.setVisibility(View.VISIBLE);
                    subjectProgressBar.setVisibility(View.VISIBLE);
                    subCategoryModelArrayList = new ArrayList<>();
                    subjectModelArrayList = new ArrayList<>();
                    getAllSubCategory();
                    getAllSubject();
                } else {
                    categoryId = null;
                    courseContentEditText.setEnabled(false);
                    courseTitleEditText.setEnabled(false);
                    courseSubTitleEditText.setEnabled(false);
                    teacherNameEditText.setEnabled(false);
                    languageEditText.setEnabled(false);
                    priceOfCourseEditText.setEnabled(false);
                    startDateDisplayText.setEnabled(false);
                    endDateDisplayText.setEnabled(false);
                    tagsRecyclerView.setVisibility(View.GONE);
                    subCategoryProgressBar.setVisibility(View.GONE);
                    subjectProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                categoryId = null;
                courseContentEditText.setEnabled(false);
                courseTitleEditText.setEnabled(false);
                courseSubTitleEditText.setEnabled(false);
                teacherNameEditText.setEnabled(false);
                languageEditText.setEnabled(false);
                priceOfCourseEditText.setEnabled(false);
                startDateDisplayText.setEnabled(false);
                endDateDisplayText.setEnabled(false);
                tagsRecyclerView.setVisibility(View.GONE);
            }
        });
    }
    private void getLiveClasses() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, liveClassURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            liveClassesRecycler.setVisibility(View.VISIBLE);
                            liveClassesProgress.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("classes");
                                liveCoursesModelArrayList.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String classID = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String subTitle = jsonObject2.getString("subTitle");
                                    String description = jsonObject2.getString("description");
                                    String teacherName = null;
                                    if (jsonObject2.has("teacher"))
                                        teacherName = jsonObject2.getString("teacher");                                    String language = jsonObject2.getString("language");
                                    String price = jsonObject2.getString("price");

                                    // Use StringBuilder for tags
                                    StringBuilder tags = new StringBuilder();
                                    JSONArray jsonArray1 = jsonObject2.getJSONArray("tags");
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        String singleTag = jsonArray1.getString(j);
                                        tags.append(singleTag).append(", ");
                                    }
                                    if (tags.length() > 0) {
                                        tags.setLength(tags.length() - 2);
                                    }

                                    String categoryId = jsonObject2.getString("categoryId");
                                    String subCategoryId = jsonObject2.getString("subCategoryId");
                                    String subjectId = jsonObject2.getString("subjectId");

                                    ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();

                                    // Check if "images" is a JSONArray or JSONObject
                                    if (jsonObject2.has("images")) {
                                        Object imagesObj = jsonObject2.get("images");
                                        if (imagesObj instanceof JSONArray) {
                                            // If images is a JSONArray
                                            JSONArray jsonImageArray = (JSONArray) imagesObj;
                                            for (int j = 0; j < jsonImageArray.length(); j++) {
                                                JSONObject jsonImageObject = jsonImageArray.getJSONObject(j);
                                                BookImageModels bookImageModels = new BookImageModels(
                                                        jsonImageObject.getString("url"),
                                                        jsonImageObject.getString("filename")
                                                );
                                                bookImageArrayList.add(bookImageModels);
                                            }
                                        } else if (imagesObj instanceof JSONObject) {
                                            // If images is a JSONObject
                                            JSONObject jsonImageObject = (JSONObject) imagesObj;
                                            BookImageModels bookImageModels = new BookImageModels(
                                                    jsonImageObject.getString("url"),
                                                    jsonImageObject.getString("filename")
                                            );
                                            bookImageArrayList.add(bookImageModels);
                                        }
                                    }

                                    String startDate = "",endDate = "";
                                    if (jsonObject2.has("startDate")) {
                                        startDate = jsonObject2.getString("startDate");
                                        endDate = jsonObject2.getString("endDate");
                                    }

                                    JSONArray jsonStudentArray = jsonObject2.getJSONArray("students");
                                    ArrayList<BookImageModels> studentsArrayList = new ArrayList<>();
//                                    for (int j = 0; j<jsonImageArray.length();j++){
//                                    }
                                    JSONArray jsonLiveClasses = jsonObject2.getJSONArray("liveClasses");
                                    ArrayList<BookImageModels> liveClassesArrayList = new ArrayList<>();
//                                    for (int j = 0; j<jsonImageArray.length();j++){
//                                    }
                                    String courseContent = jsonObject2.getString("courseContent");
                                    String isActive = jsonObject2.getString("is_active");

                                    JSONArray jsonRatingArray = jsonObject2.getJSONArray("ratings");
                                    ArrayList<BookImageModels> ratingArrayList = new ArrayList<>();
//                                    for (int j = 0; j<jsonImageArray.length();j++){
//                                    }
                                    String finalPrice = jsonObject2.getString("finalPrice");

                                    AdminShowAllLiveCoursesModel liveCoursesModel = new AdminShowAllLiveCoursesModel(classID, title,subTitle, description,language,price, teacherName, tags.toString(),categoryId,subCategoryId,subjectId,courseContent,isActive,finalPrice,startDate,endDate,bookImageArrayList,studentsArrayList,liveClassesArrayList,ratingArrayList);
                                    liveCoursesModelArrayList.add(liveCoursesModel);
                                }
                                if (liveCoursesAdapter == null) {
                                    liveCoursesAdapter = new AdminShowAllLiveCoursesAdapter(liveCoursesModelArrayList, AdminCreateLiveCoursesFragment.this);
                                    liveClassesRecycler.setAdapter(liveCoursesAdapter);
                                } else {
                                    liveCoursesAdapter.notifyDataSetChanged();
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                Log.e("onResponse",response.toString());
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("catch",e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse",error.toString());
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
    public  void getCategory(){
        String categoryURL = Constant.BASE_URL + "category/getCategory";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, categoryURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("pagination");

                                int totalRows = Integer.parseInt(jsonObject.getString("totalRows"));
                                totalPages = Integer.parseInt(jsonObject.getString("totalPages"));
                                currentPage = Integer.parseInt(jsonObject.getString("currentPage"));

                                JSONArray jsonArray = response.getJSONArray("data");
                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String id = jsonObject2.getString("_id");
                                    String categoryName = jsonObject2.getString("categoryName");
                                    String description = jsonObject2.getString("slug");
                                    String is_active = jsonObject2.getString("is_active");
                                    JSONObject imageObj = jsonObject2.getJSONObject("image");
                                    String imageUrl = imageObj.getString("url");

                                    AdminShowAllCategoryModel showAllCategoryModel = new AdminShowAllCategoryModel(id,categoryName,description,is_active,imageUrl);
                                    categoryModelArrayList.add(showAllCategoryModel);
                                    Log.e("CategoryArray",categoryModelArrayList.toString());
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("catch",e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse",error.toString());
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
    public void getAllSubCategory() {
        String subCategoryURL = Constant.BASE_URL + "category/getSubCategoryBycategoryId";
        JSONObject jsonObject = new JSONObject();
        try {
            // Create a JSON array with the single categoryId
            JSONArray categoryArray = new JSONArray();
            categoryArray.put(categoryId);  // categoryId is a single string
            jsonObject.put("categoryIds", categoryArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;  // Exit early if there is a JSON error
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, subCategoryURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("SubCategoryResponse", response.toString());
                            boolean status = response.getBoolean("status");

                            if (status) {
                                // Get pagination data (if any) from response
                                JSONObject pagination = response.optJSONObject("pagination");
                                if (pagination != null) {
                                    totalPages = pagination.optInt("totalPages", 0);
                                    currentPage = pagination.optInt("currentPage", 0);
                                }

                                // Parse subcategories data
                                JSONArray jsonArray = response.getJSONArray("data");

                                // Loop through the subcategories and extract the necessary information
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject subCategory = jsonArray.getJSONObject(i);

                                    // SubCategory details
                                    String subCategoryId = subCategory.getString("_id");
                                    // Category details (from categoryId)
                                    String categoryId = subCategory.getString("categoryId");  // Category ID
                                    String subCategoryName = subCategory.getString("subCategoryName");
                                    String slug = subCategory.getString("slug");
                                    String isActive = subCategory.getString("is_active");

                                    // Create and add the model to the list
                                    AdminShowAllSubCategoryModel subCategoryModel = new AdminShowAllSubCategoryModel(categoryId,null,subCategoryId,subCategoryName,isActive,0,0,0);
                                    subCategoryModelArrayList.add(subCategoryModel);
                                }

                                setSubCategorySpinner();

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
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
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

        // Add the request to the request queue
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    public  void setSubCategorySpinner(){

        ArrayList<String> subCategoryNameStringList = new ArrayList<>();
        subCategoryNameStringList.add("Select Sub Category");

        for (int i = 0; i < subCategoryModelArrayList.size(); i++) {
            subCategoryNameStringList.add(subCategoryModelArrayList.get(i).getSubCategoryName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subCategoryNameStringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subCategorySpinner.setAdapter(adapter);
        subCategoryProgressBar.setVisibility(View.GONE);

        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    subCategoryId = subCategoryModelArrayList.get(i - 1).getSubCategoryId();
                    Log.e("subCategoryId", subCategoryId);
                    Log.e("Subarray", subCategoryModelArrayList.toArray().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void getAllSubject() {
        String subjectURL = Constant.BASE_URL + "subject/getSubject";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subjectURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("pagination");

                                int totalRows = Integer.parseInt(jsonObject.getString("totalRows"));
                                totalPages = Integer.parseInt(jsonObject.getString("totalPages"));
                                currentPage = Integer.parseInt(jsonObject.getString("currentPage"));

                                JSONArray jsonArray = response.getJSONArray("data");
                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String id = jsonObject2.getString("_id");
                                    String title = jsonObject2.getString("title");
                                    String isActive = jsonObject2.getString("is_active");

                                    AdminShowAllSubjectModel subjectModel = new AdminShowAllSubjectModel(id,title,isActive);
                                    subjectModelArrayList.add(subjectModel);
                                }
                                setSubjectSpinner();
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
    public  void setSubjectSpinner(){

        ArrayList<String> subjectNameStringList = new ArrayList<>();
        subjectNameStringList.add("Select Subject");

        for (int i = 0; i < subjectModelArrayList.size(); i++) {
            subjectNameStringList.add(subjectModelArrayList.get(i).getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subjectNameStringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);
        subjectProgressBar.setVisibility(View.GONE);

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    subjectId = subjectModelArrayList.get(i - 1).getId();
                    Log.e("subCategoryId", subCategoryId);
                    Log.e("Subarray", subjectModelArrayList.toArray().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void sendCreateLiveCourseData(Dialog createCourseDialogBox) {
        String subCategoryURL = Constant.BASE_URL + "liveclass/createliveClass";

        // Form parameters
        String title = courseTitleEditText.getText().toString().trim();
        String subTitle = courseSubTitleEditText.getText().toString().trim();
        String teacherName = teacherNameEditText.getText().toString().trim();
        String language = languageEditText.getText().toString().trim();
        String price = priceOfCourseEditText.getText().toString().trim();
        String finalPrice = finalPriceOfCourseEditText.getText().toString().trim();
        String startDate = startDateDisplayText.getText().toString().trim();
        String endDate = endDateDisplayText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String courseContent = courseContentEditText.getText().toString().trim();

        // Tags
        StringBuilder tagsBuilder = new StringBuilder();
        for (AdminTagsForDataALLModel tag : adminTagsForDataALLModelArrayList) {
            if (tagsBuilder.length() > 0) {
                tagsBuilder.append(",");
            }
            tagsBuilder.append(tag.getTagName());
        }

        // Validation for required fields
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || tagsBuilder.length() == 0 || TextUtils.isEmpty(categoryId) || TextUtils.isEmpty(subCategoryId)) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare form data map
        Map<String, String> params = new HashMap<>();
        params.put("categoryId", categoryId);
        params.put("subCategoryId", subCategoryId);
        params.put("subjectId", subjectId);  // Ensure correct subjectId is passed
        params.put("title", title);
        params.put("subTitle", subTitle);
        params.put("language", language);
        params.put("price", price);
        params.put("finalPrice", finalPrice);

        if (!TextUtils.isEmpty(startDate)) {
            params.put("startDate", startDateIso);
        }
        if (!TextUtils.isEmpty(endDate)) {
            params.put("endDate", endDateIso);
        }
        params.put("courseContent", courseContent);
        params.put("tags", tagsBuilder.toString());
        params.put("description", description);

        // Map for files (if imageFile exists)
        Map<String, File> files = new HashMap<>();
        if (imageFile != null && imageFile.exists()) {
            files.put("image", imageFile);  // Ensure the key is correct
        }

        // Create MultipartRequest to send form data and file
        MultipartRequest multipartRequest = new MultipartRequest(subCategoryURL, params, files,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            boolean status = responseObject.getBoolean("status");

                            if (status) {
                                String message = responseObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                getLiveClasses();
                                createCourseDialogBox.dismiss();  // Dismiss the dialog on success
                            } else {
                                String message = responseObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
}
