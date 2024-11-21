package com.examatlas.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final Map<String, String> mParams;
    private final Map<String, File> mFiles;
    private final String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
    private final String lineEnd = "\r\n";
    private final String twoHyphens = "--";
    private String authToken;  // Auth token for the header, if needed

    public MultipartRequest(String url, Map<String, String> params, Map<String, File> files,
                            Response.Listener<String> listener, Response.ErrorListener errorListener, String authToken) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mParams = params;
        mFiles = files;
        this.authToken = authToken;
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return mParams;  // Return the form parameters (text data)
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=" + boundary); // Boundary for multipart data
        if (authToken != null) {
            headers.put("Authorization", "Bearer " + authToken);  // Add Authorization header if provided
        }
        return headers;
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);  // Handle the response
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return Response.success(new String(response.data), HttpHeaderParser.parseCacheHeaders(response));  // Parse the response
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        try {
            // Write form parameters (text fields)
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                writeFormField(entry.getKey(), entry.getValue(), dataOutputStream);
            }

            // Write files (image files)
            for (Map.Entry<String, File> entry : mFiles.entrySet()) {
                writeFormFile(entry.getKey(), entry.getValue(), dataOutputStream);
            }

            // Closing boundary
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            dataOutputStream.flush();
            return outputStream.toByteArray();  // Return the body byte array
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeFormField(String fieldName, String fieldValue, DataOutputStream outputStream) throws IOException {
        // Write normal form field (text field)
        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"" + lineEnd);
        outputStream.writeBytes(lineEnd);
        outputStream.writeBytes(fieldValue + lineEnd);
    }

    private void writeFormFile(String fieldName, File file, DataOutputStream outputStream) throws IOException {
        // Write file to upload
        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"" + lineEnd);
        outputStream.writeBytes("Content-Type: " + getMimeType(file) + lineEnd);  // Get MIME type based on file type
        outputStream.writeBytes(lineEnd);

        // File input stream for reading the file
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();

        outputStream.writeBytes(lineEnd);
    }

    private String getMimeType(File file) {
        String mimeType = "application/octet-stream";  // Default MIME type
        String fileName = file.getName();
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            mimeType = "image/png";
        } else if (fileName.endsWith(".gif")) {
            mimeType = "image/gif";
        }
        return mimeType;
    }
}
