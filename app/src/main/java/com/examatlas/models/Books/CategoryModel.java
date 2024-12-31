package com.examatlas.models.Books;

import java.util.Map;

public class CategoryModel {
    private Map<String, Object> data;
    private String imageUrl;
    private String imageFilename;

    public CategoryModel(Map<String, Object> data,String imageUrl) {
        this.data = data;
        this.imageUrl = imageUrl;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    // Getter for individual fields (with defaults to prevent crashes)
    public String getString(String key) {
        return data.containsKey(key) ? data.get(key).toString() : "";
    }

    public int getInt(String key) {
        return data.containsKey(key) ? Integer.parseInt(data.get(key).toString()) : 0;
    }

    public boolean getBoolean(String key) {
        return data.containsKey(key) && Boolean.parseBoolean(data.get(key).toString());
    }

    // Example method to get the image URL
    public String getImageUrl() {
        return imageUrl;
    }
}
