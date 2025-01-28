package com.examatlas.models;

public class CurrentAffairsModel {
    String cfID,cfImageURL,cfTitle, cfCategoryId,cfContent,cfTags,totalItems,totalPages;

    public CurrentAffairsModel(String cfID, String cfImageURL, String cfTitle, String cfCategoryId, String cfContent, String cfTags, String totalItems, String totalPages) {
        this.cfID = cfID;
        this.cfImageURL = cfImageURL;
        this.cfTitle = cfTitle;
        this.cfCategoryId = cfCategoryId;
        this.cfContent = cfContent;
        this.cfTags = cfTags;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public String getCfImageURL() {
        return cfImageURL;
    }

    public void setCfImageURL(String cfImageURL) {
        this.cfImageURL = cfImageURL;
    }

    public String getTotalRows() {
        return totalItems;
    }

    public void setTotalRows(String totalItems) {
        this.totalItems = totalItems;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getCfID() {
        return cfID;
    }

    public void setCfID(String cfID) {
        this.cfID = cfID;
    }

    public String getCfTitle() {
        return cfTitle;
    }

    public void setCfTitle(String cfTitle) {
        this.cfTitle = cfTitle;
    }

    public String getCfCategoryId() {
        return cfCategoryId;
    }

    public void setCfCategoryId(String cfCategoryId) {
        this.cfCategoryId = cfCategoryId;
    }

    public String getCfContent() {
        return cfContent;
    }

    public void setCfContent(String cfContent) {
        this.cfContent = cfContent;
    }

    public String getCfTags() {
        return cfTags;
    }

    public void setCfTags(String cfTags) {
        this.cfTags = cfTags;
    }

}
