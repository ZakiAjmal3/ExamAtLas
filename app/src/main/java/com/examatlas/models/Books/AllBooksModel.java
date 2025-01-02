package com.examatlas.models.Books;

import com.examatlas.models.Books.BookImageModels;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;

public class AllBooksModel {

    private Map<String, Object> data;  // Holds dynamic data

    public AllBooksModel(Map<String, Object> data) {
        this.data = data;
    }

    // Getter and Setter for the map
    public Map<String, Object> getData() {
        return data;
    }

    // Getter for individual fields (with defaults to prevent crashes)
    public String getString(String key) {
        return data.containsKey(key) ? data.get(key).toString() : "";
    }

    public int getInt(String key) {
        return data.containsKey(key) ? Integer.parseInt(data.get(key).toString()) : 0;
    }

    // Parse images (assuming the 'images' field is an array of objects in the JSON)
    public ArrayList<BookImageModels> getImages() {
        ArrayList<BookImageModels> images = new ArrayList<>();

        if (data.containsKey("images")) {
            // Assuming 'images' is an array
            ArrayList<Map<String, Object>> imageList = (ArrayList<Map<String, Object>>) data.get("images");

            for (Map<String, Object> imageData : imageList) {
                String url = imageData.containsKey("url") ? imageData.get("url").toString() : "";
                String filename = imageData.containsKey("filename") ? imageData.get("filename").toString() : "";

                BookImageModels image = new BookImageModels(url, filename);
                images.add(image);
            }
        }
        return images;
    }
    // You can add other methods like getTags(), getAuthor(), etc., as needed
}
