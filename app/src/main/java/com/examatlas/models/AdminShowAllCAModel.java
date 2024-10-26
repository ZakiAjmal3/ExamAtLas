package com.examatlas.models;

public class AdminShowAllCAModel {
    String caID,title,keyword,content,tags,createdDate,image;

    public AdminShowAllCAModel(String caID, String title, String keyword, String content, String tags, String createdDate, String image) {
        this.caID = caID;
        this.title = title;
        this.keyword = keyword;
        this.content = content;
        this.tags = tags;
        this.createdDate = createdDate;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCaID() {
        return caID;
    }

    public void setCaID(String caID) {
        this.caID = caID;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
