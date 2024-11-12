package com.examatlas.models;

import java.util.ArrayList;

public class AdminShowAllCategoryModel {
    String id,categoryName,description,isActive,tags;

    public AdminShowAllCategoryModel(String id, String categoryName, String description, String isActive,String tags) {
        this.id = id;
        this.categoryName = categoryName;
        this.description = description;
        this.isActive = isActive;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
