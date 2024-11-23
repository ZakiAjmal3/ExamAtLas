package com.examatlas.models;

public class AdminShowAllSubCategoryModel {
    String categoryId,categoryName,subCategoryId,subCategoryName,icActive;
    int totalRows,totalPages,currentPages;

    public AdminShowAllSubCategoryModel(String categoryId, String categoryName, String subCategoryId, String subCategoryName, String icActive, int totalRows, int totalPages, int currentPages) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.icActive = icActive;
        this.totalRows = totalRows;
        this.totalPages = totalPages;
        this.currentPages = currentPages;
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
