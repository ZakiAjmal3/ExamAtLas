package com.examatlas.models;

public class BlogModel {
    String blogID,imageURL,title,content,tags,totalRows,totalPages;

    public BlogModel(String blogID,String imageURL, String title, String content, String tags, String totalRows, String totalPages) {
        this.blogID = blogID;
        this.imageURL = imageURL;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.totalRows = totalRows;
        this.totalPages = totalPages;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(String totalRows) {
        this.totalRows = totalRows;
    }

    public String getBlogID() {
        return blogID;
    }

    public void setBlogID(String blogID) {
        this.blogID = blogID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
