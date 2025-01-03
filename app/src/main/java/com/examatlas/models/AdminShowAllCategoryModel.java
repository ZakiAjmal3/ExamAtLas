package com.examatlas.models;

public class AdminShowAllCategoryModel {
    String id,categoryName, slug,isActive,imageUrl;

    public AdminShowAllCategoryModel(String id, String categoryName, String slug, String isActive, String imageUrl) {
        this.id = id;
        this.categoryName = categoryName;
        this.slug = slug;
        this.isActive = isActive;
        this.imageUrl = imageUrl;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
