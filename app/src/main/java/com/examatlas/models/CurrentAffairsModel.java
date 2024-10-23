package com.examatlas.models;

public class CurrentAffairsModel {
    String cfID,cfTitle,cfKeyword,cfContent,cfTags,totalRows,totalPages,currentPage;

    public CurrentAffairsModel(String cfID, String cfTitle, String cfKeyword, String cfContent, String cfTags,String totalRows,String totalPages,String currentPage) {
        this.cfID = cfID;
        this.cfTitle = cfTitle;
        this.cfKeyword = cfKeyword;
        this.cfContent = cfContent;
        this.cfTags = cfTags;
        this.totalRows = totalRows;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public String getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(String totalRows) {
        this.totalRows = totalRows;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
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

    public String getCfKeyword() {
        return cfKeyword;
    }

    public void setCfKeyword(String cfKeyword) {
        this.cfKeyword = cfKeyword;
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
