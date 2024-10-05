package com.examatlas.models;

public class CurrentAffairsModel {
    String cfID,cfTitle,cfKeyword,cfContent,cfTags,cfImage,cfCreatedDate;

    public CurrentAffairsModel(String cfID, String cfTitle, String cfKeyword, String cfContent, String cfTags, String cfImage, String cfCreatedDate) {
        this.cfID = cfID;
        this.cfTitle = cfTitle;
        this.cfKeyword = cfKeyword;
        this.cfContent = cfContent;
        this.cfTags = cfTags;
        this.cfImage = cfImage;
        this.cfCreatedDate = cfCreatedDate;
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

    public String getCfImage() {
        return cfImage;
    }

    public void setCfImage(String cfImage) {
        this.cfImage = cfImage;
    }

    public String getCfCreatedDate() {
        return cfCreatedDate;
    }

    public void setCfCreatedDate(String cfCreatedDate) {
        this.cfCreatedDate = cfCreatedDate;
    }
}
