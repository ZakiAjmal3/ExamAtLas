package com.examatlas.models;

public class AdminShowAllSubCategoryModel {
    String categoryId,categoryName,categoryDescription,categoryTags,subCategoryId,subCategoryName,subCategoryDescription,subCategoryTags;

    public AdminShowAllSubCategoryModel(String categoryId, String categoryName, String categoryDescription, String categoryTags, String subCategoryId, String subCategoryName, String subCategoryDescription, String subCategoryTags) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.categoryTags = categoryTags;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.subCategoryDescription = subCategoryDescription;
        this.subCategoryTags = subCategoryTags;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryTags() {
        return categoryTags;
    }

    public void setCategoryTags(String categoryTags) {
        this.categoryTags = categoryTags;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryDescription() {
        return subCategoryDescription;
    }

    public void setSubCategoryDescription(String subCategoryDescription) {
        this.subCategoryDescription = subCategoryDescription;
    }

    public String getSubCategoryTags() {
        return subCategoryTags;
    }

    public void setSubCategoryTags(String subCategoryTags) {
        this.subCategoryTags = subCategoryTags;
    }
}
