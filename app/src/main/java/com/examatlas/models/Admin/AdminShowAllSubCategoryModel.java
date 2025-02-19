package com.examatlas.models.Admin;

public class AdminShowAllSubCategoryModel {
    String categoryId,categoryName,subCategoryId,subCategoryName,imageURL,icActive,updatedAt;
    int totalRows,totalPages,currentPages;

    public AdminShowAllSubCategoryModel(String categoryId, String categoryName, String subCategoryId, String subCategoryName, String imageURL, String icActive, String updatedAt, int totalRows, int totalPages, int currentPages) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.imageURL = imageURL;
        this.icActive = icActive;
        this.updatedAt = updatedAt;
        this.totalRows = totalRows;
        this.totalPages = totalPages;
        this.currentPages = currentPages;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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

    public String getIcActive() {
        return icActive;
    }

    public void setIcActive(String icActive) {
        this.icActive = icActive;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPages() {
        return currentPages;
    }

    public void setCurrentPages(int currentPages) {
        this.currentPages = currentPages;
    }
}
