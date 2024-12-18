package com.examatlas.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.examatlas.adapter.AdminTagsForDataALLAdapter;
import com.examatlas.models.AdminShowAllLiveCoursesModel;
import com.examatlas.models.AdminTagsForDataALLModel;
import com.examatlas.models.extraModels.AdminCategoryModel;
import com.examatlas.models.extraModels.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminCreateLiveCoursesFragment extends Fragment {

    ProgressBar liveClassesProgress;
    AdminShowAllLiveCoursesAdapter liveCoursesAdapter;
    RecyclerView liveClassesRecycler;
    ArrayList<AdminCategoryModel> categoryModelArrayList;
    ArrayList<AdminShowAllLiveCoursesModel> liveCoursesModelArrayList = new ArrayList<>();
    private final String liveClassURL = Constant.BASE_URL + "liveclass/getAllLiveClass";
    SessionManager sessionManager;
    String authToken;
    Button createCourseBtn;
    @Nullable
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

        return view;
    }
    Dialog dialog;
    private TextView startDateDisplayText,endDateDisplayText;
    private RecyclerView tagsRecyclerView;
    private AdminTagsForDataALLAdapter adminTagsForDataALLAdapter;
    private AdminTagsForDataALLModel adminTagsForDataALLModel;
    private ArrayList<AdminTagsForDataALLModel> adminTagsForDataALLModelArrayList;
    Spinner categorySpinner,subCategorySpinner,subjectSpinner;
    EditText courseTitleEditText,courseSubTitleEditText,teacherNameEditText,languageEditText,priceOfCourseEditText,
            descriptionEditText,courseContentEditText,tagsEditTxt;
    String categoryId;
    @SuppressLint("ClickableViewAccessibility")
    private void openCreateSendLiveCourseDialog() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.admin_create_live_courses_dialog_box);

        startDateDisplayText = dialog.findViewById(R.id.dateDisplayText);
        endDateDisplayText = dialog.findViewById(R.id.endDateText);
        categorySpinner = dialog.findViewById(R.id.categorySpinner);

        courseTitleEditText = dialog.findViewById(R.id.titleEditTxt);
        courseSubTitleEditText = dialog.findViewById(R.id.subTitleEditTxt);
        teacherNameEditText = dialog.findViewById(R.id.teacherNameEditText);
        languageEditText = dialog.findViewById(R.id.languageEditText);
        priceOfCourseEditText = dialog.findViewById(R.id.priceOfCourseEditText);
        descriptionEditText = dialog.findViewById(R.id.descriptionEditText);
        courseContentEditText = dialog.findViewById(R.id.courseContentEditText);
        tagsEditTxt = dialog.findViewById(R.id.tagsEditText);

        tagsRecyclerView = dialog.findViewById(R.id.tagsRecycler);
        adminTagsForDataALLModelArrayList = new ArrayList<>();
        tagsRecyclerView = dialog.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(adminTagsForDataALLModelArrayList);
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        setupCategorySpinner(categorySpinner, null);

        dialog.findViewById(android.R.id.content).setOnTouchListener((v, event) -> {
            if (categoryId == null) {
                // Show toast if no category is selected
                Toast.makeText(getContext(), "Please select a category first", Toast.LENGTH_SHORT).show();
            }
            return false; // Return false to allow the touch event to propagate
        });
        courseTitleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null) {
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        courseSubTitleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null) {
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        teacherNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null) {
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        languageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null) {
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        priceOfCourseEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null) {
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        descriptionEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null) {
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        courseContentEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null) {
                    Toast.makeText(getContext(), "Please Select a Category first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tagsEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryId == null) {
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
                            }
                        },
                        year, month, dayOfMonth);

                // Show the DatePickerDialog
                datePickerDialog.show();
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
    public void setupCategorySpinner(Spinner categorySpinners,AdminCategoryModel currentCategory) {
        // Assuming `subCategoryModelArrayList` contains the categories data
        ArrayList<String> categoryNameList = new ArrayList<>();
        categoryNameList.add("Select Category"); // First item is "Select Category"

        // Populate category names from your subCategoryModelArrayList
        categoryNameList.add(currentCategory.getCategoryName());

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
                                    String teacher = jsonObject2.getString("teacher");
                                    String price = jsonObject2.getString("price");
                                    String categoryId = jsonObject2.getString("categoryId");
                                    String subCategoryId = jsonObject2.getString("subCategoryId");
                                    String subjectId = jsonObject2.getString("subjectId");
                                    String courseContent = jsonObject2.getString("courseContent");
                                    String finalPrice = jsonObject2.getString("finalPrice");

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
                                    JSONArray jsonImageArray = jsonObject2.getJSONArray("images");

                                    ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();

                                    for (int j = 0; j < jsonImageArray.length(); j++) {
                                        JSONObject jsonImageObject = jsonImageArray.getJSONObject(j);
                                        BookImageModels bookImageModels = new BookImageModels(
                                                jsonImageObject.getString("url"),
                                                jsonImageObject.getString("filename"));
                                        bookImageArrayList.add(bookImageModels);
                                    }

                                    String startDate = "", endDate = "";
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
                                    AdminShowAllLiveCoursesModel liveCoursesModel = new AdminShowAllLiveCoursesModel(classID, title,subTitle, description,teacher,tags.toString(), categoryId, subCategoryId, subjectId,price,finalPrice,courseContent, startDate, endDate,bookImageArrayList, null, null);

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
                                Log.e("onResponse", response.toString());
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("catch", e.toString());
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
                                JSONArray jsonArray = response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {

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
}
