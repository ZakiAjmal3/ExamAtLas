package com.examatlas.activities.Books.EBooks;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.mbms.DownloadRequest;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.examatlas.R;
import com.examatlas.activities.Books.SingleBookDetailsActivity;
import com.examatlas.utils.Constant;
import com.examatlas.utils.MySingleton;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class PurchasedEBookViewingBookActivity extends AppCompatActivity {

    String bookIdByIntent, bookTitleStr;
    String ebookURL;
    Dialog progressDialog;
    TextView bookTitleTxt;
    ImageView backBtn;
    PDFView eBookPDFView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_ebook_library);

        progressDialog = new Dialog(PurchasedEBookViewingBookActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_bar_drawer);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressDialog.show();

        backBtn = findViewById(R.id.backBtn);
        bookTitleTxt = findViewById(R.id.bookTitleTxt);
        bookIdByIntent = getIntent().getStringExtra("bookId");
        bookTitleStr = getIntent().getStringExtra("title");
        bookTitleTxt.setText(bookTitleStr);
        bookTitleTxt.setVisibility(View.VISIBLE);
        eBookPDFView = findViewById(R.id.pdfView);

        // Set the flag to prevent screenshots or screen recording
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Fetch the ebook details and download URL
        getEBookById();
    }

    private void getEBookById() {
        String singleBookURL = Constant.BASE_URL + "v1/booksByID?id=" + bookIdByIntent;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, singleBookURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                JSONObject jsonObject = response.getJSONObject("data");
                                JSONArray jsonArray = jsonObject.getJSONArray("ebookFiles");
                                ebookURL = jsonArray.getJSONObject(0).getString("url");
                                if (ebookURL.endsWith(".pdf")) {
                                    Glide.with(PurchasedEBookViewingBookActivity.this)
                                            .downloadOnly()  // Only download the file, not display the image
                                            .load(ebookURL)  // The URL of the PDF
                                            .into(new SimpleTarget<File>() {
                                                @Override
                                                public void onResourceReady(File resource, Transition<? super File> transition) {
                                                    // Once the file is downloaded, load it into the PDFView
                                                    eBookPDFView.fromFile(resource)
                                                            .enableSwipe(true)  // Enable swipe to change pages
                                                            .swipeHorizontal(false)  // Vertical swipe
                                                            .enableDoubletap(true)  // Enable zoom
                                                            .scrollHandle(new DefaultScrollHandle(PurchasedEBookViewingBookActivity.this)) // Enable the scrollbar handle
                                                            .load();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }else if (ebookURL.endsWith(".epub")){
                                    downloadEpubFile(ebookURL);
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
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                Toast.makeText(PurchasedEBookViewingBookActivity.this, message, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("book error", errorMessage);
                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

//    THE BELOW CODE IS FOR EPUB FILE DISPLAYING IN A WEBVIEW

    private void downloadEpubFile(String url) {
        // Use HttpURLConnection to download the EPUB file as InputStream
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create a URL object from the ebook URL
                    URL epubUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) epubUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000); // Timeout for connection
                    connection.setReadTimeout(5000); // Timeout for data download

                    // Open InputStream
                    InputStream inputStream = new BufferedInputStream(connection.getInputStream());

                    // Save the file to local storage
                    File epubFile = new File(getFilesDir(), "downloaded.epub");
                    FileOutputStream fileOutputStream = new FileOutputStream(epubFile);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, length);
                    }

                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();

                    Log.d("EBookDownload", "EPUB downloaded successfully!");

                    // Now, you can process the EPUB file to open it in WebView (Next steps).
                    openEpubInWebView(epubFile);

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PurchasedEBookViewingBookActivity.this, "Error downloading EPUB file.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void openEpubInWebView(File epubFile) {
        try {
            File extractedDir = new File(getFilesDir(), "extracted_epub");
            if (!extractedDir.exists()) {
                extractedDir.mkdirs();
            }

            unzipEpub(epubFile, extractedDir);

            // Try loading an HTML file (for example, ACT I.html or Content.html)
            File htmlFile = new File(extractedDir, "ACT I.html");  // Change this to other files if necessary

            if (htmlFile.exists()) {
                String content = readFile(htmlFile);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Load the HTML content into the WebView
                        WebView eBookWebView = findViewById(R.id.webView);
                        eBookWebView.setVisibility(View.VISIBLE);
                        eBookWebView.getSettings().setJavaScriptEnabled(true);
                        eBookWebView.loadDataWithBaseURL("file:///", content, "text/html", "UTF-8", null);

                        progressDialog.dismiss();
                        bookTitleTxt.setVisibility(View.VISIBLE);
                        eBookWebView.setLongClickable(false);
                        eBookWebView.setHapticFeedbackEnabled(false);
                        eBookWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);

                        // Disable context menu (for copy, paste, etc.)
                        eBookWebView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                        });
                    }
                });
            } else {
                Log.e("EPUB_EXTRACT", "HTML file not found!");
                Toast.makeText(PurchasedEBookViewingBookActivity.this, "EPUB format error: HTML file not found.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("EPUB_EXTRACT", "Error loading EPUB content", e);
        }
    }

    private String readFile(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        reader.close();
        return stringBuilder.toString();
    }

    private void unzipEpub(File epubFile, File destinationDir) throws IOException {
        // Unzip the EPUB file (EPUB is a ZIP file)
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(epubFile));
        ZipEntry entry;

        while ((entry = zipInputStream.getNextEntry()) != null) {
            File entryFile = new File(destinationDir, entry.getName());

            // Log entry information
            Log.d("EPUB_EXTRACT", "Extracting: " + entry.getName());

            // Check if the directory exists, and create it if not
            if (entry.isDirectory()) {
                if (!entryFile.exists()) {
                    entryFile.mkdirs();  // Create directory
                }
            } else {
                // Ensure the parent directory exists before writing the file
                File parentDir = entryFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();  // Create parent directories if they do not exist
                }

                // Write the file content
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(entryFile));
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zipInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.close();
            }

            zipInputStream.closeEntry();
        }
        zipInputStream.close();

        // Log the extracted directory contents for debugging
        listFilesInDirectory(destinationDir);
    }

    private void listFilesInDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    Log.d("EPUB_EXTRACT", "Found file: " + file.getAbsolutePath());
                }
            }
        } else {
            Log.d("EPUB_EXTRACT", "Directory not found: " + directory.getAbsolutePath());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove FLAG_SECURE when the activity is destroyed
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }
}

