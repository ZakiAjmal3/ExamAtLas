package com.examatlas.fragment.Admin;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
import com.examatlas.adapter.Admin.AdminShowAllBooksAdapter;
import com.examatlas.adapter.AdminTagsForDataALLAdapter;
import com.examatlas.adapter.books.AllBookShowingAdapter;
import com.examatlas.adapter.books.ReviewImagesListAdapter;
import com.examatlas.models.Admin.AdminShowAllCategoryModel;
import com.examatlas.models.Admin.AdminShowAllSubCategoryModel;
import com.examatlas.models.AdminTagsForDataALLModel;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MultipartRequest;
import com.examatlas.utils.MySingleton;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.utils.SessionManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminCreateBookTabFragment extends Fragment {

    private SearchView searchView;
    private TextView createBookBtn;
    private Dialog createBookDialogBox;
    Spinner bookTypeSpinnerForCreatingBook,categorySpinner,subCategorySpinner;
    ImageView crossBtn,uploadImgBtn,uploadEBookBtn;
    RelativeLayout noDataLayout,uploadEBookLayout;
    TextView uploadEBookNameTxt;
    RecyclerView uploadImageRecyclerView,tagsRecyclerView;
    Button submitBookBtn;
    private ReviewImagesListAdapter imageAdapter;
    ArrayList<File> uploadImageArrayList = new ArrayList<>();
    ArrayList<AdminTagsForDataALLModel> tagsArrayList = new ArrayList<>();
    private EditText titleEditTxt, authorEditTxt, publicationEditTxt, contentEditTxt, priceEditTxt,
            sellingPriceEditTxt, stockEditTxt, sKUEditTxt, lengthEditTxt, widthEditTxt, heightEditTxt,
            weightEditTxt, tagsEditTxt, slugEditTxt, totalPagesEditTxt, isbnEditTxt, languageEditTxt, editionEditTxt;
    ArrayList<AdminShowAllCategoryModel> categoryModelArrayList = new ArrayList<>();
    ArrayList<AdminShowAllSubCategoryModel> subCategoryModelArrayList = new ArrayList<>();
    private final String[] bookTypeArraylist = {"Books", "E-Books"};
    String bookTypeStr = "book";
    ArrayList<String> categoryNameList;
    ArrayList<String> subCategoryNameList;
    String categoryId = null;
    String subCategoryId = null;
    private SessionManager sessionManager;
    private String token;
    ProgressBar showAllBooksProgressBar;
    RecyclerView showAllBooksRecyclerView;
    AdminShowAllBooksAdapter adminShowAllBooksAdapter;
    AllBookShowingAdapter adminShowAllBooksModel;
    ArrayList<AllBooksModel> adminShowAllBooksModelArrayList;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Uri image_uri;
    private ActivityResultLauncher<String[]> permissionsLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean cameraPermissionGranted = result.get(Manifest.permission.CAMERA);
                Boolean readMediaImagesPermissionGranted = result.get(Manifest.permission.READ_MEDIA_IMAGES);
                Boolean readExternalStoragePermissionGranted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);

                if (cameraPermissionGranted != null && readMediaImagesPermissionGranted != null
                        && cameraPermissionGranted && (readMediaImagesPermissionGranted || readExternalStoragePermissionGranted)) {
                    openGallery(); // Permissions granted, open gallery or camera
                } else {
                    Toast.makeText(AdminCreateBookTabFragment.this.getContext(), "Permissions required", Toast.LENGTH_SHORT).show();
                }
            });
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BooksFragment", "onCreate called tab");
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_create_book_tab_layout, container, false);

        Log.d("BooksFragment", "onCreateView called tab");

        searchView = view.findViewById(R.id.searchView);
        createBookBtn = view.findViewById(R.id.btnCreate);

        showAllBooksRecyclerView = view.findViewById(R.id.showBookRecycler);
        showAllBooksProgressBar = view.findViewById(R.id.showAllBookProgressBar);

        noDataLayout = view.findViewById(R.id.noDataLayout);

        adminShowAllBooksModelArrayList = new ArrayList<>();
        showAllBooksRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        sessionManager = new SessionManager(getContext());
        token = sessionManager.getUserData().get("authToken");

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKeyboard();
            }
        });
        // Setup ActivityResultLaunchers
        setupActivityResultLaunchers();
        getAllCategory();
        getAllSubCategory();
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
        initializeDialogContent();
        createBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateBookDialog();
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
                if (adminShowAllBooksAdapter != null) {
                    adminShowAllBooksAdapter.filter(newText);
                }
                return true;
            }
        });
        getAllBooks();
        return view;
    }
    public void initializeDialogContent(){
        createBookDialogBox = new Dialog(requireContext());
        createBookDialogBox.setContentView(R.layout.admin_create_book_dialog_box);

        crossBtn = createBookDialogBox.findViewById(R.id.btnCross);
        uploadImgBtn = createBookDialogBox.findViewById(R.id.addImgView);

        uploadImageRecyclerView = createBookDialogBox.findViewById(R.id.selectedImagesRV);
        tagsRecyclerView = createBookDialogBox.findViewById(R.id.tagsRecycler);

        uploadImageRecyclerView.setLayoutManager(new LinearLayoutManager(AdminCreateBookTabFragment.this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ReviewImagesListAdapter(AdminCreateBookTabFragment.this.getContext(),uploadImageArrayList,null);
        uploadImageRecyclerView.setAdapter(imageAdapter);

        uploadEBookBtn = createBookDialogBox.findViewById(R.id.addEBookImgView);
        uploadEBookLayout = createBookDialogBox.findViewById(R.id.eBookUpLL);
        uploadEBookNameTxt = createBookDialogBox.findViewById(R.id.uploadEBookNameTxt);

        submitBookBtn = createBookDialogBox.findViewById(R.id.btnSubmit);

        bookTypeSpinnerForCreatingBook = createBookDialogBox.findViewById(R.id.bookTypeSpinner);
        categorySpinner = createBookDialogBox.findViewById(R.id.categorySpinner);
        subCategorySpinner = createBookDialogBox.findViewById(R.id.subCategorySpinner);
        titleEditTxt = createBookDialogBox.findViewById(R.id.titleEditTxt);
        authorEditTxt = createBookDialogBox.findViewById(R.id.authorEditText);
        publicationEditTxt = createBookDialogBox.findViewById(R.id.publicationEditText);
        contentEditTxt = createBookDialogBox.findViewById(R.id.contentEditText);
        priceEditTxt = createBookDialogBox.findViewById(R.id.priceEditText);
        sellingPriceEditTxt = createBookDialogBox.findViewById(R.id.sellPriceEditText);
        stockEditTxt = createBookDialogBox.findViewById(R.id.stockEditText);
        sKUEditTxt = createBookDialogBox.findViewById(R.id.skuEditText);
        lengthEditTxt = createBookDialogBox.findViewById(R.id.lengthEditText);
        widthEditTxt = createBookDialogBox.findViewById(R.id.widthEditText);
        heightEditTxt = createBookDialogBox.findViewById(R.id.heightEditText);
        weightEditTxt = createBookDialogBox.findViewById(R.id.weightEditText);
        slugEditTxt = createBookDialogBox.findViewById(R.id.slugEditText);
        totalPagesEditTxt = createBookDialogBox.findViewById(R.id.totalPageEditText);
        isbnEditTxt = createBookDialogBox.findViewById(R.id.isbnEditText);
        languageEditTxt = createBookDialogBox.findViewById(R.id.languageEditText);
        editionEditTxt = createBookDialogBox.findViewById(R.id.editionEditText);
        tagsEditTxt = createBookDialogBox.findViewById(R.id.tagsEditText);
    }
    private void openCreateBookDialog() {
        tagsArrayList = new ArrayList<>();
        tagsRecyclerView = createBookDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        AdminTagsForDataALLAdapter adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(tagsArrayList);
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);

        setupBookTypeSpinner(bookTypeSpinnerForCreatingBook);
        setupCategorySpinner(categorySpinner,null);
        setupSubCategorySpinner(subCategorySpinner,null);

        tagsEditTxt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String tagText = tagsEditTxt.getText().toString().trim();
                if (!tagText.isEmpty()) {
                    tagsArrayList.add(new AdminTagsForDataALLModel(tagText));
                    adminTagsForDataALLAdapter.notifyItemInserted(tagsArrayList.size() - 1);
                    tagsEditTxt.setText(""); // Clear the EditText
                    tagsRecyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
                }
                return true; // Indicate that we've handled the event
            }
            return false; // Pass the event on
        });

        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionsAndOpenGalleryOrCamera();
            }
        });
        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBookDialogBox.dismiss();
            }
        });
        submitBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAllFields();
            }
        });

        uploadEBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAndUploadEBook();
            }
        });

        createBookDialogBox.show();
        WindowManager.LayoutParams params = createBookDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

// Set the window attributes
        createBookDialogBox.getWindow().setAttributes(params);

// Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) createBookDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

// Set top and bottom margins (adjust values as needed)
        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

// Background and animation settings
        createBookDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createBookDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        createBookDialogBox.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }
    private static final int PICK_FILE_REQUEST_CODE = 1;
    Dialog progressDialog;
    File selectedEBookFile;
    private void openAndUploadEBook() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/epub+zip");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/epub+zip", "application/pdf"});
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            Log.e("fileuri", fileUri.toString());

            // Check the MIME type
            String mimeType = getContext().getContentResolver().getType(fileUri);
            Log.e("Mime Type", mimeType);

            // Ensure the MIME type is either epub or pdf
            if (mimeType.equals("application/epub+zip") || mimeType.equals("application/pdf")) {
                try {
                    // Convert URI to a File
                    selectedEBookFile = convertUriToFile(fileUri);

                    uploadEBookNameTxt.setText(selectedEBookFile.getName());
                    Log.e("Converted File", selectedEBookFile.getAbsolutePath());

                    // Show progress bar (for uploading, if applicable)
                    progressDialog = new Dialog(AdminCreateBookTabFragment.this.getContext());
                    progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    progressDialog.setContentView(R.layout.progress_bar_drawer);
                    progressDialog.setCancelable(false);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                    progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                    progressDialog.show();

                    // Handle the uploaded file (for example, show a toast or proceed with the upload logic)
                    Toast.makeText(getContext(), "File selected: " + selectedEBookFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                    // Hide progress dialog
                    progressDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error converting URI to file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Invalid file type. Please select an EPUB or PDF file.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File convertUriToFile(Uri uri) throws IOException {
        // Get the context's cache directory (You can also use external storage if needed)
        File tempFile = new File(getContext().getCacheDir(), getFileNameFromUri(uri));

        try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {

            if (inputStream == null) {
                throw new IOException("Unable to open input stream for URI: " + uri.toString());
            }

            // Buffer to copy the data
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        return tempFile;
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = "temp_file";  // Default name
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (columnIndex != -1) {
                fileName = cursor.getString(columnIndex); // Use the original file name
            }
            cursor.close();
        }

        return fileName;
    }


    private void checkAllFields() {
        if (titleEditTxt.getText().toString().isEmpty()){
            titleEditTxt.setError("Title is required");
            return;
        }
        if (authorEditTxt.getText().toString().isEmpty()){
            authorEditTxt.setError("Author is required");
            return;
        }
        if (publicationEditTxt.getText().toString().isEmpty()){
            publicationEditTxt.setError("Publication is required");
            return;
        }
        if (contentEditTxt.getText().toString().isEmpty()){
            contentEditTxt.setError("Content is required");
            return;
        }
        if (priceEditTxt.getText().toString().isEmpty()){
            priceEditTxt.setError("Price is required");
            return;
        }
        if (stockEditTxt.getText().toString().isEmpty()){
            stockEditTxt.setError("Stock is required");
            return;
        }
        if (sKUEditTxt.getText().toString().isEmpty()){
            sKUEditTxt.setError("SKU is required");
            return;
        }
        if (lengthEditTxt.getText().toString().isEmpty()){
            lengthEditTxt.setError("Length is required");
            return;
        }
        if (widthEditTxt.getText().toString().isEmpty()){
            widthEditTxt.setError("Width is required");
            return;
        }
        if (heightEditTxt.getText().toString().isEmpty()){
            heightEditTxt.setError("Height is required");
            return;
        }
        if (weightEditTxt.getText().toString().isEmpty()){
            weightEditTxt.setError("Weight is required");
            return;
        }
        if (slugEditTxt.getText().toString().isEmpty()){
            slugEditTxt.setError("Slug is required");
            return;
        }
        if (totalPagesEditTxt.getText().toString().isEmpty()){
            totalPagesEditTxt.setError("Total Pages is required");
            return;
        }
        if (isbnEditTxt.getText().toString().isEmpty()){
            isbnEditTxt.setError("ISBN is required");
            return;
        }
        if (languageEditTxt.getText().toString().isEmpty()){
            languageEditTxt.setError("Language is required");
            return;
        }
        if (editionEditTxt.getText().toString().isEmpty()){
            editionEditTxt.setError("Edition is required");
            return;
        }
        createBook();
    }

    private void createBook() {

        String createBookUrl = Constant.BASE_URL + "v1/book";

        String title = titleEditTxt.getText().toString();
        String slug = slugEditTxt.getText().toString();
        String content = contentEditTxt.getText().toString();
        String author = authorEditTxt.getText().toString();
        String publication = publicationEditTxt.getText().toString();
        String price = priceEditTxt.getText().toString();
        String sellingPrice = sellingPriceEditTxt.getText().toString();
        String stock = stockEditTxt.getText().toString();
        String sku = sKUEditTxt.getText().toString();
        String length = lengthEditTxt.getText().toString();
        String width = widthEditTxt.getText().toString();
        String height = heightEditTxt.getText().toString();
        String weight = weightEditTxt.getText().toString();
        String totalPages = totalPagesEditTxt.getText().toString();
        String isbn = isbnEditTxt.getText().toString();
        String language = languageEditTxt.getText().toString();
        String edition = editionEditTxt.getText().toString();

        // Prepare form data
        Map<String, String> params = new HashMap<>();

        Map<String,String> dataObj = new HashMap<>();
        dataObj.put("title",title);
        dataObj.put("slug",slug);
        dataObj.put("description",content);
        dataObj.put("author",author);
        dataObj.put("publication",publication);
        dataObj.put("price",price);
        dataObj.put("sellingPrice",sellingPrice);
        dataObj.put("stock",stock);
        dataObj.put("sku",sku);
        JSONObject dimension = new JSONObject();
        try {
            dimension.put("length",length);
            dimension.put("width",width);
            dimension.put("height",height);
            dimension.put("weight",weight);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        dataObj.put("dimensions",dimension.toString());
        dataObj.put("totalPages",totalPages);
        dataObj.put("isbn",isbn);
        dataObj.put("language",language);
        dataObj.put("edition",edition);
        dataObj.put("type",bookTypeStr);
        dataObj.put("categoryId",categoryId);
        dataObj.put("subCategoryId",subCategoryId);
        JSONArray tagsArray = new JSONArray();
// Loop through the tags and add them to the JSONArray
        for (AdminTagsForDataALLModel tag : tagsArrayList) {
            tagsArray.put(tag.getTagName());  // Add each tag to the array
        }
// Add the tags array to the params map
        dataObj.put("tags", tagsArray.toString());

        String dataJson = new Gson().toJson(dataObj);
        params.put("data",dataJson);

        List<File> imageFiles = new ArrayList<>();
        for (int i = 0; i < uploadImageArrayList.size(); i++) {
            imageFiles.add(uploadImageArrayList.get(i));
        }
        Map<String, File> files = new HashMap<>();
        for (int i = 0; i < uploadImageArrayList.size(); i++) {
            files.put("images", uploadImageArrayList.get(i)); // Add each image to the request map
        }
        if (bookTypeStr.equals("ebook")) {
            files.put("files", selectedEBookFile);
        }
        Log.e("Files" ,files.toString());
        Log.e("Data Obj" ,dataJson.toString());

        // Create and send the multipart request
        MultipartRequest multipartRequest = new MultipartRequest(createBookUrl, params, files,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            boolean status = responseObject.getBoolean("status");
                            if (status) {
                                String message = responseObject.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                getAllBooks();
                                createBookDialogBox.dismiss();
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
                        Log.e("BlogFetchError", errorMessage);
                    }
                }, token);

        // Add the request to the queue
        MySingletonFragment.getInstance(this).addToRequestQueue(multipartRequest);
    }
    private void setupBookTypeSpinner(Spinner bookTypeSpinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, bookTypeArraylist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookTypeSpinner.setAdapter(adapter);

        bookTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String bookType = bookTypeArraylist[i];
                if (bookType.equals("E-Book")){
                    bookTypeStr = "ebook";
                    uploadEBookLayout.setVisibility(View.VISIBLE);
                }else {
                    bookTypeStr = "book";
                    uploadEBookLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void setupSubCategorySpinner(Spinner subCategorySpinner, AllBooksModel subCategoryModel) {
        // Assuming `subCategoryModelArrayList` contains the categories data
        subCategoryNameList = new ArrayList<>();
        subCategoryNameList.add("Select Sub Category"); // First item is "Select Category"

        for (int i = 0; i < subCategoryModelArrayList.size(); i++) {
            // Populate category names from your subCategoryModelArrayList
            subCategoryNameList.add(subCategoryModelArrayList.get(i).getSubCategoryName());
        }
        // Set the adapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subCategoryNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subCategorySpinner.setAdapter(adapter);
        if (subCategoryModel != null) {
            for (int i = 0; i < subCategoryNameList.size(); i++) {
                if (subCategoryNameList.get(i).equals(subCategoryModel.getString("subCategoryName"))) {
                    subCategorySpinner.setSelection(i);
                    subCategorySpinner.setEnabled(false);
                }
            }
        }

        // Set the OnItemSelectedListener to handle category selection
        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0 && adminShowAllBooksModelArrayList != null) {
                    // Ensure that a category is selected and the position is within bounds
                    subCategoryId = subCategoryModelArrayList.get(position - 1).getSubCategoryId(); // Get the corresponding categoryId
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                subCategoryId = null; // Reset categoryId if nothing is selected
            }
        });
    }
    public void setupCategorySpinner(Spinner categorySpinners, AllBooksModel currentCategory) {
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
                if (categoryNameList.get(i).equals(currentCategory.getString("categoryName"))) {
                    categorySpinners.setSelection(i);
                    categorySpinners.setEnabled(false);
                }
            }
        }

        // Set the OnItemSelectedListener to handle category selection
        categorySpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0 && adminShowAllBooksModelArrayList != null) {
                    // Ensure that a category is selected and the position is within bounds
                    categoryId = categoryModelArrayList.get(position - 1).getId(); // Get the corresponding categoryId
                }
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
                    // Check if multiple images are selected
                    if (data.getClipData() != null) {
                        // Multiple images selected
                        ClipData clipData = data.getClipData();
                        ArrayList<Uri> imageUris = new ArrayList<>();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            imageUris.add(clipData.getItemAt(i).getUri());
                        }
                        handleMultipleImages(imageUris);
                        adminShowAllBooksAdapter.setUploadImageArrayList(uploadImageArrayList);
                    } else {
                        // Single image selected
                        Uri imageUri = data.getData();
                        handleImageUri(imageUri);
                        adminShowAllBooksAdapter.setUploadImageArrayList(uploadImageArrayList);
                    }
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
    public void handleMultipleImages(ArrayList<Uri> imageUris) {
        // Clear the current image files list if necessary
        uploadImageArrayList.clear();  // Clear any previously selected images

        for (Uri uri : imageUris) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                bitmap = getResizedBitmap(bitmap, 400);  // Resize image if needed

                // Convert Bitmap to File and add to the list
                File imageFile = bitmapToFile(bitmap);
                uploadImageArrayList.add(imageFile);

                // Log the file path for each image
                Log.d("ImageFiles", "Image added: " + imageFile.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Log the list size to ensure multiple images are added
        Log.d("ImageFiles", "Number of images selected: " + uploadImageArrayList.size());

        // Now that all images are processed, notify the adapter
        imageAdapter.notifyDataSetChanged();
    }

    public void handleImageUri(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(AdminCreateBookTabFragment.this.getContext().getContentResolver(), uri);
            bitmap = getResizedBitmap(bitmap, 400);  // Resize image if needed

            // Convert Bitmap to File and add to the imageFiles list
            File imageFile = bitmapToFile(bitmap);
            uploadImageArrayList.add(imageFile);

            // Notify adapter to update RecyclerView
            imageAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void handleBitmap(Bitmap bitmap) {
        // Resize image
        bitmap = getResizedBitmap(bitmap, 400);

        // Convert Bitmap to File and add to the imageFiles list
        File imageFile = bitmapToFile(bitmap);
        uploadImageArrayList.add(imageFile);

        // Notify adapter that new image has been added
        imageAdapter.notifyDataSetChanged();
    }
    public File bitmapToFile(Bitmap bitmap) {
        // Generate a unique filename based on the current time
        String fileName = "uploaded_image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(AdminCreateBookTabFragment.this.getContext().getCacheDir(), fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void openGallery() {
        final CharSequence[] options = {"Open Camera", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminCreateBookTabFragment.this.getContext());
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
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    galleryLauncher.launch(Intent.createChooser(intent, "Select Image")); // Use galleryLauncher
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                    Toast.makeText(AdminCreateBookTabFragment.this.getContext(), "Image Uploading Cancelled", Toast.LENGTH_SHORT).show();
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
    public void checkPermissionsAndOpenGalleryOrCamera() {
        // Check if we have both permissions: Camera and Storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            });
        } else {
            permissionsLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
        }
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
    public void getAllBooks() {
        String bookURL = Constant.BASE_URL + "v1/books?type=book";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, bookURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            showAllBooksRecyclerView.setVisibility(View.VISIBLE);
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                adminShowAllBooksModelArrayList.clear();
                                int totalPage = Integer.parseInt(response.getString("totalPage"));
                                int totalItems = Integer.parseInt(response.getString("totalItems"));

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                    // Convert the book object into a Map to make it dynamic
                                    Map<String, Object> bookData = new Gson().fromJson(jsonObject2.toString(), Map.class);

                                    // Extract dimensions (assuming they are present in the 'dimensions' field of the book data)
                                    String length = "";
                                    String width = "";
                                    String height = "";
                                    String weight = "";


                                    if (bookData.containsKey("dimensions")) {
                                        Object dimensionsObj = bookData.get("dimensions");

                                        if (dimensionsObj instanceof Map) {
                                            // If it's already a Map, we can safely cast
                                            Map<String, Object> dimensions = (Map<String, Object>) dimensionsObj;
                                            length = dimensions.containsKey("length") ? dimensions.get("length").toString() : "";
                                            width = dimensions.containsKey("width") ? dimensions.get("width").toString() : "";
                                            height = dimensions.containsKey("height") ? dimensions.get("height").toString() : "";
                                            weight = dimensions.containsKey("weight") ? dimensions.get("weight").toString() : "";
                                        } else if (dimensionsObj instanceof String) {
                                            // If it's a String (likely JSON), parse it into a Map
                                            String dimensionsJson = dimensionsObj.toString();
                                            try {
                                                Map<String, Object> dimensions = new Gson().fromJson(dimensionsJson, Map.class);
                                                length = dimensions.containsKey("length") ? dimensions.get("length").toString() : "";
                                                width = dimensions.containsKey("width") ? dimensions.get("width").toString() : "";
                                                height = dimensions.containsKey("height") ? dimensions.get("height").toString() : "";
                                                weight = dimensions.containsKey("weight") ? dimensions.get("weight").toString() : "";
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                // Handle parsing errors here if necessary
                                            }
                                        }
                                    }

                                    // Pass the data and dimensions to the model constructor
                                    AllBooksModel model = new AllBooksModel(bookData, length, width, height, weight); // Pass map and dimensions

                                    boolean isPresent = false;
                                    for (int j = 0; j < adminShowAllBooksModelArrayList.size(); j++) {
                                        if (adminShowAllBooksModelArrayList.get(j).getString("_id").equals(model.getString("_id"))) {
                                            isPresent = true;
                                            break;
                                        }
                                    }
                                    if (!isPresent) {
                                        adminShowAllBooksModelArrayList.add(model);
                                    }
                                }
                                adminShowAllBooksAdapter = new AdminShowAllBooksAdapter(AdminCreateBookTabFragment.this, adminShowAllBooksModelArrayList);
                                showAllBooksRecyclerView.setAdapter(adminShowAllBooksAdapter);
                                showAllBooksProgressBar.setVisibility(View.GONE);
                                showAllBooksRecyclerView.setVisibility(View.VISIBLE);
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
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        MySingletonFragment.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    public void getAllCategory() {
        String categoryURL = Constant.BASE_URL + "v1/category";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, categoryURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            createBookBtn.setEnabled(true);
                            showAllBooksProgressBar.setVisibility(View.GONE);
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
                                    AdminShowAllCategoryModel categoryModel = new AdminShowAllCategoryModel(id,categoryName,description,is_active,imageUrl,updatedAt);
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
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    public void getAllSubCategory() {
        String subCategoryURL = Constant.BASE_URL + "v1/sub-category";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subCategoryURL, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");

                            if (status) {
                                subCategoryModelArrayList = new ArrayList<>();
                                createBookBtn.setEnabled(true);

                                JSONArray jsonArray = response.getJSONArray("data");

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

                                    AdminShowAllSubCategoryModel subCategoryModel = new AdminShowAllSubCategoryModel(categoryId,
                                            categoryName,subCategoryId,subCategoryName,imageUrl,icActive,updatedAt,0,0,0);
                                    subCategoryModelArrayList.add(subCategoryModel);
                                }
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
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
    public String getCategoryId(){
        return categoryId;
    }
    public String getSubCategoryId(){
        return subCategoryId;
    }
}
