package com.examatlas.adapter.Admin;

import android.app.Dialog;
import androidx.fragment.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.examatlas.R;
import com.examatlas.adapter.AdminTagsForDataALLAdapter;
import com.examatlas.adapter.books.ReviewImagesListAdapter;
import com.examatlas.fragment.Admin.AdminCreateBlogsDeleteFragment;
import com.examatlas.fragment.Admin.AdminCreateBookTabFragment;
import com.examatlas.fragment.Admin.AdminCreateBooksFragment;
import com.examatlas.fragment.Admin.AdminCreateEBookTabFragment;
import com.examatlas.models.AdminTagsForDataALLModel;
import com.examatlas.models.Books.AllBooksModel;
import com.examatlas.models.Books.BookImageModels;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MultipartRequest;
import com.examatlas.utils.MySingletonFragment;
import com.examatlas.utils.SessionManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminShowAllBooksAdapter extends RecyclerView.Adapter<AdminShowAllBooksAdapter.ViewHolder> {
    private final Fragment context;
    private final ArrayList<AllBooksModel> allBooksModelArrayList;
    private final ArrayList<AllBooksModel> orginalAdminShowAllBookModelArrayList;
    SessionManager sessionManager;
    String authToken;
    private String currentQuery = "";
    private final String[] threeDotsArray = {"Edit", "Delete"};
    Dialog progressDialog;
    File imageFile;
    public AdminShowAllBooksAdapter(Fragment context, ArrayList<AllBooksModel> allBooksModelArrayList) {
        this.allBooksModelArrayList = new ArrayList<>(allBooksModelArrayList);
        this.context = context;
        this.orginalAdminShowAllBookModelArrayList = new ArrayList<>(allBooksModelArrayList);
        sessionManager = new SessionManager(context.getContext());
        authToken = sessionManager.getUserData().get("authToken");
        initializeDialogContent();
    }
    @NonNull
    @Override
    public AdminShowAllBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hardcopybook_item_list, parent, false);
        return new AdminShowAllBooksAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AdminShowAllBooksAdapter.ViewHolder holder, int position) {
        AllBooksModel currentBook = allBooksModelArrayList.get(position);
        holder.itemView.setTag(currentBook);

        holder.title.setText(currentBook.getString("title"));
        // Set the title to one line and add ellipsis if it exceeds
        holder.title.setEllipsize(TextUtils.TruncateAt.END);
        holder.title.setMaxLines(1);
        holder.threeDots.setOnClickListener(view -> showThreeDotsOptions(currentBook, position));

// Get prices as Strings (from getString)
        String purchasingPrice = currentBook.getString("sellingPrice");
        String originalPrice = currentBook.getString("price");

// Initialize prices
        int purchasingPriceInt = 0;
        int originalPriceInt = 0;

        try {
            // Parse the purchasing price as Double, then round it to nearest integer
            if (purchasingPrice != null && !purchasingPrice.isEmpty()) {
                try {
                    // Convert to Double first, then round to the nearest integer
                    purchasingPriceInt = (int) Math.round(Double.parseDouble(purchasingPrice));
                } catch (NumberFormatException e) {
                    // Handle invalid format if parsing fails
                    Toast.makeText(context.getContext(), "Invalid purchasing price format", Toast.LENGTH_SHORT).show();
                    holder.price.setText("Invalid Price");
                    return;
                }
            }

            // Parse the original price as Double, then round it to nearest integer
            if (originalPrice != null && !originalPrice.isEmpty()) {
                try {
                    // Convert to Double first, then round to the nearest integer
                    originalPriceInt = (int) Math.round(Double.parseDouble(originalPrice));
                } catch (NumberFormatException e) {
                    // Handle invalid format if parsing fails
                    Toast.makeText(context.getContext(), "Invalid original price format", Toast.LENGTH_SHORT).show();
                    holder.price.setText("Invalid Price");
                    return;
                }
            }

            // Calculate discount only if both prices are valid
            if (originalPriceInt > 0 && purchasingPriceInt > 0) {
                int discount = purchasingPriceInt * 100 / originalPriceInt;
                discount = 100 - discount;

                // Create a SpannableString for the original price with strikethrough
                SpannableString spannableOriginalPrice = new SpannableString("₹" + originalPriceInt);
                spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);

                // Create the discount text
                String discountText = "(-" + discount + "%)";
                SpannableStringBuilder spannableText = new SpannableStringBuilder();
                spannableText.append("₹" + purchasingPriceInt + " ");
                spannableText.append(spannableOriginalPrice);
                spannableText.append(" " + discountText);

                // Set the color for the discount percentage
                int startIndex = spannableText.length() - discountText.length();
                spannableText.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.price.setText(spannableText);
            } else {
                // Fallback: if prices are invalid, show default values or error message
                holder.price.setText("Invalid Price");
            }

        } catch (Exception e) {
            // Catch any other unforeseen exceptions and log
            e.printStackTrace();
            Toast.makeText(context.getContext(), "Error calculating price", Toast.LENGTH_SHORT).show();
            holder.price.setText("Invalid Price");
        }

        // Set the book image
        ArrayList<BookImageModels> bookImageModelsArrayList = currentBook.getImages();
        if (!bookImageModelsArrayList.isEmpty()) {
            String imageUrl = currentBook.getImages().get(0).getUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .error(R.drawable.noimage)
                    .placeholder(R.drawable.noimage)
                    .into(holder.bookImg);
        } else {
            Glide.with(context)
                    .load(R.drawable.noimage)
                    .into(holder.bookImg);
        }
    }
    private void showThreeDotsOptions(AllBooksModel currentBook, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
        builder.setTitle(null)
                .setItems(threeDotsArray, (dialog, which) -> {
                    String selectedItems = threeDotsArray[which];
                    choseItems(currentBook, selectedItems, position);
                });
        builder.create().show();
    }
    private void choseItems(AllBooksModel currentBook, String selectedItems, int position) {
        if (selectedItems.equals("Edit")) {
            editBooksDialogBox(currentBook, position);
        } else if (selectedItems.equals("Delete")) {
            progressDialog = new Dialog(context.getContext());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.progress_bar_drawer);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
            progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
            progressDialog.show();
            deleteAddress(currentBook, position);
        }
    }
    private void deleteAddress(AllBooksModel currentBook, int position) {
        String deleteURL = Constant.BASE_URL + "v1/deleteBook/" + currentBook.getString("_id");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                Toast.makeText(context.getContext(), "Book Deleted Successfully", Toast.LENGTH_SHORT).show();
                                allBooksModelArrayList.remove(position);
                                notifyItemRemoved(position);
                                if (context instanceof AdminCreateBookTabFragment){
                                ((AdminCreateBookTabFragment) context).getAllBooks();
                                }else {
                                    ((AdminCreateEBookTabFragment) context).getAllBooks();
                                }
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
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
                Toast.makeText(context.getContext(), errorMessage, Toast.LENGTH_LONG).show();
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
        MySingletonFragment.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }
    private Dialog editBookDialogBox;
    Spinner bookTypeSpinnerForCreatingBook,categorySpinner,subCategorySpinner;
    ImageView crossBtn,uploadImgBtn;
    RecyclerView uploadImageRecyclerView,tagsRecyclerView;
    Button submitBookBtn;
    private ReviewImagesListAdapter imageAdapter;
    ArrayList<File> uploadImageArrayList = new ArrayList<>();
    ArrayList<String> uploadImageURLList = new ArrayList<>();
    ArrayList<AdminTagsForDataALLModel> tagsArrayList = new ArrayList<>();
    private EditText titleEditTxt, authorEditTxt, publicationEditTxt, contentEditTxt, priceEditTxt,
            sellingPriceEditTxt, stockEditTxt, sKUEditTxt, lengthEditTxt, widthEditTxt, heightEditTxt,
            weightEditTxt, tagsEditTxt, slugEditTxt, totalPagesEditTxt, isbnEditTxt, languageEditTxt, editionEditTxt;
    String categoryId,subCategoryId;
    TextView txtHeader,txtBookType;
    public void initializeDialogContent(){
        editBookDialogBox = new Dialog(context.getContext());
        editBookDialogBox.setContentView(R.layout.admin_create_book_dialog_box);

        crossBtn = editBookDialogBox.findViewById(R.id.btnCross);
        uploadImgBtn = editBookDialogBox.findViewById(R.id.addImgView);

        uploadImageRecyclerView = editBookDialogBox.findViewById(R.id.selectedImagesRV);
        tagsRecyclerView = editBookDialogBox.findViewById(R.id.tagsRecycler);

        uploadImageRecyclerView.setLayoutManager(new LinearLayoutManager(context.getContext(), LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ReviewImagesListAdapter(context.getContext(),uploadImageArrayList,null);
        uploadImageRecyclerView.setAdapter(imageAdapter);

        submitBookBtn = editBookDialogBox.findViewById(R.id.btnSubmit);
        txtHeader = editBookDialogBox.findViewById(R.id.txtAddData);
        txtHeader.setText("Edit Book");
        txtBookType = editBookDialogBox.findViewById(R.id.txtBookType);
        bookTypeSpinnerForCreatingBook = editBookDialogBox.findViewById(R.id.bookTypeSpinner);
        txtBookType.setVisibility(View.GONE);
        bookTypeSpinnerForCreatingBook.setVisibility(View.GONE);
        categorySpinner = editBookDialogBox.findViewById(R.id.categorySpinner);
        subCategorySpinner = editBookDialogBox.findViewById(R.id.subCategorySpinner);
        titleEditTxt = editBookDialogBox.findViewById(R.id.titleEditTxt);
        authorEditTxt = editBookDialogBox.findViewById(R.id.authorEditText);
        publicationEditTxt = editBookDialogBox.findViewById(R.id.publicationEditText);
        contentEditTxt = editBookDialogBox.findViewById(R.id.contentEditText);
        priceEditTxt = editBookDialogBox.findViewById(R.id.priceEditText);
        sellingPriceEditTxt = editBookDialogBox.findViewById(R.id.sellPriceEditText);
        stockEditTxt = editBookDialogBox.findViewById(R.id.stockEditText);
        sKUEditTxt = editBookDialogBox.findViewById(R.id.skuEditText);
        lengthEditTxt = editBookDialogBox.findViewById(R.id.lengthEditText);
        widthEditTxt = editBookDialogBox.findViewById(R.id.widthEditText);
        heightEditTxt = editBookDialogBox.findViewById(R.id.heightEditText);
        weightEditTxt = editBookDialogBox.findViewById(R.id.weightEditText);
        slugEditTxt = editBookDialogBox.findViewById(R.id.slugEditText);
        totalPagesEditTxt = editBookDialogBox.findViewById(R.id.totalPageEditText);
        isbnEditTxt = editBookDialogBox.findViewById(R.id.isbnEditText);
        languageEditTxt = editBookDialogBox.findViewById(R.id.languageEditText);
        editionEditTxt = editBookDialogBox.findViewById(R.id.editionEditText);
        tagsEditTxt = editBookDialogBox.findViewById(R.id.tagsEditText);
    }

    private void editBooksDialogBox(AllBooksModel currentBook,int position) {

        categorySpinner.setEnabled(true);
        subCategorySpinner.setEnabled(true);

        tagsArrayList = new ArrayList<>();
        tagsRecyclerView = editBookDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(context.getContext(),2));

        String[] tagsArray = currentBook.getString("tags").split(",");
        for (String tag : tagsArray) {
            tagsArrayList.add(new AdminTagsForDataALLModel(tag.trim()));
        }
        AdminTagsForDataALLAdapter adminTagsForDataALLAdapter = new AdminTagsForDataALLAdapter(tagsArrayList);
        tagsRecyclerView.setAdapter(adminTagsForDataALLAdapter);
        adminTagsForDataALLAdapter.notifyDataSetChanged();
        for (int i = 0; i < currentBook.getImages().size(); i++) {
            uploadImageURLList.add(i,currentBook.getImages().get(i).getUrl());
        }
        imageAdapter = new ReviewImagesListAdapter(context.getContext(),null,uploadImageURLList);
        uploadImageRecyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
        if (context instanceof AdminCreateBookTabFragment){
            ((AdminCreateBookTabFragment) context).setupCategorySpinner(categorySpinner,currentBook);
            ((AdminCreateBookTabFragment) context).setupSubCategorySpinner(subCategorySpinner,currentBook);
        }else {
            ((AdminCreateEBookTabFragment) context).setupCategorySpinner(categorySpinner,currentBook);
            ((AdminCreateEBookTabFragment) context).setupSubCategorySpinner(subCategorySpinner,currentBook);
        }

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

        titleEditTxt.setText(currentBook.getString("title"));
        authorEditTxt.setText(currentBook.getString("author"));
        publicationEditTxt.setText(currentBook.getString("publication"));
        contentEditTxt.setText(currentBook.getString("description"));
        priceEditTxt.setText(currentBook.getString("price"));
        sellingPriceEditTxt.setText(currentBook.getString("sellingPrice"));
        stockEditTxt.setText(currentBook.getString("stock"));
        sKUEditTxt.setText(currentBook.getString("sku"));
        slugEditTxt.setText(currentBook.getString("slug"));
        totalPagesEditTxt.setText(currentBook.getString("totalPages"));
        isbnEditTxt.setText(currentBook.getString("isbn"));
        languageEditTxt.setText(currentBook.getString("language"));
        editionEditTxt.setText(currentBook.getString("edition"));
        lengthEditTxt.setText(currentBook.getLength());
        widthEditTxt.setText(currentBook.getWidth());
        heightEditTxt.setText(currentBook.getHeight());
        weightEditTxt.setText(currentBook.getWeight());

        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof AdminCreateBookTabFragment){
                    ((AdminCreateBookTabFragment) context).checkPermissionsAndOpenGalleryOrCamera();
                }else {
                    ((AdminCreateEBookTabFragment) context).checkPermissionsAndOpenGalleryOrCamera();
                }
            }
        });
        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBookDialogBox.dismiss();
            }
        });
        submitBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new Dialog(context.getContext());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                checkAllFields(currentBook);
            }
        });

        ImageView btnCross = editBookDialogBox.findViewById(R.id.btnCross);
        btnCross.setOnClickListener(view -> editBookDialogBox.dismiss());

        editBookDialogBox.show();
        WindowManager.LayoutParams params = editBookDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

        // Set the window attributes
        editBookDialogBox.getWindow().setAttributes(params);

        // Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) editBookDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

        // Background and animation settings
        editBookDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editBookDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    public void setUploadImageArrayList(ArrayList<File> uploadImageArrayList) {
        this.uploadImageArrayList = uploadImageArrayList;
        imageAdapter = new ReviewImagesListAdapter(context.getContext(),uploadImageArrayList,null);
        uploadImageRecyclerView.setAdapter(imageAdapter);
    }
    private void checkAllFields(AllBooksModel currentBook) {
        if (titleEditTxt.getText().toString().isEmpty()){
            titleEditTxt.setError("Title is required");
            progressDialog.dismiss();
            return;
        }
        if (authorEditTxt.getText().toString().isEmpty()){
            authorEditTxt.setError("Author is required");
            progressDialog.dismiss();
            return;
        }
        if (publicationEditTxt.getText().toString().isEmpty()){
            publicationEditTxt.setError("Publication is required");
            progressDialog.dismiss();
            return;
        }
        if (contentEditTxt.getText().toString().isEmpty()){
            contentEditTxt.setError("Content is required");
            progressDialog.dismiss();
            return;
        }
        if (priceEditTxt.getText().toString().isEmpty()){
            priceEditTxt.setError("Price is required");
            progressDialog.dismiss();
            return;
        }
        if (stockEditTxt.getText().toString().isEmpty()){
            stockEditTxt.setError("Stock is required");
            progressDialog.dismiss();
            return;
        }
        if (sKUEditTxt.getText().toString().isEmpty()){
            sKUEditTxt.setError("SKU is required");
            progressDialog.dismiss();
            return;
        }
        if (lengthEditTxt.getText().toString().isEmpty()){
            lengthEditTxt.setError("Length is required");
            progressDialog.dismiss();
            return;
        }
        if (widthEditTxt.getText().toString().isEmpty()){
            widthEditTxt.setError("Width is required");
            progressDialog.dismiss();
            return;
        }
        if (heightEditTxt.getText().toString().isEmpty()){
            heightEditTxt.setError("Height is required");
            progressDialog.dismiss();
            return;
        }
        if (weightEditTxt.getText().toString().isEmpty()){
            weightEditTxt.setError("Weight is required");
            progressDialog.dismiss();
            return;
        }
        if (slugEditTxt.getText().toString().isEmpty()){
            slugEditTxt.setError("Slug is required");
            progressDialog.dismiss();
            return;
        }
        if (totalPagesEditTxt.getText().toString().isEmpty()){
            totalPagesEditTxt.setError("Total Pages is required");
            progressDialog.dismiss();
            return;
        }
        if (isbnEditTxt.getText().toString().isEmpty()){
            isbnEditTxt.setError("ISBN is required");
            progressDialog.dismiss();
            return;
        }
        if (languageEditTxt.getText().toString().isEmpty()){
            languageEditTxt.setError("Language is required");
            progressDialog.dismiss();
            return;
        }
        if (editionEditTxt.getText().toString().isEmpty()){
            editionEditTxt.setError("Edition is required");
            progressDialog.dismiss();
            return;
        }
        if (context instanceof AdminCreateBookTabFragment){
            categoryId = ((AdminCreateBookTabFragment) context).getCategoryId();
            subCategoryId = ((AdminCreateBookTabFragment) context).getSubCategoryId();
        }else {
            categoryId = ((AdminCreateEBookTabFragment) context).getCategoryId();
            subCategoryId = ((AdminCreateEBookTabFragment) context).getSubCategoryId();
        }
        editBook(currentBook);
    }

    private void editBook(AllBooksModel currentBook) {
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
        if (context instanceof AdminCreateBookTabFragment){
            dataObj.put("type","book");
        }else if (context instanceof AdminCreateEBookTabFragment){
            dataObj.put("type","ebook");
        }

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
        params.put("id",currentBook.getString("_id"));

        Map<String, File> files = new HashMap<>();
        for (int i = 0; i < uploadImageArrayList.size(); i++) {
            files.put("images" + i, uploadImageArrayList.get(i)); // Add each image to the request map
        }

        Log.e("Data Obj" ,dataJson);
        Log.e("id",currentBook.getString("_id"));
        Log.e("Image" ,files.toString());

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
                                Toast.makeText(context.getContext(), message, Toast.LENGTH_SHORT).show();
                                if ((context instanceof AdminCreateBookTabFragment)){
                                    ((AdminCreateBookTabFragment) context).getAllBooks();
                                }else {
                                    ((AdminCreateEBookTabFragment) context).getAllBooks();
                                }
                                editBookDialogBox.dismiss();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
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
                        Toast.makeText(context.getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("BlogFetchError", errorMessage);
                    }
                }, authToken);

        // Add the request to the queue
        MySingletonFragment.getInstance(context).addToRequestQueue(multipartRequest);
    }

    public void filter(String query) {
        currentQuery = query; // Store current query
        allBooksModelArrayList.clear();
        if (query.isEmpty()) {
            allBooksModelArrayList.addAll(orginalAdminShowAllBookModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AllBooksModel book : orginalAdminShowAllBookModelArrayList) {
                if (book.getString("title").toLowerCase().contains(lowerCaseQuery) ||
                        book.getString("content").toLowerCase().contains(lowerCaseQuery)) {
                    allBooksModelArrayList.add(book); // Add matching blog to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }

    @Override
    public int getItemCount() {
        return allBooksModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView bookImg,threeDots;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            price = itemView.findViewById(R.id.bookPriceInfo);
            bookImg = itemView.findViewById(R.id.imgBook);
            threeDots = itemView.findViewById(R.id.threeDots);

        }
    }
}
