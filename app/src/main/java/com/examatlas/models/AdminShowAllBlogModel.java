package com.examatlas.models;

public class AdminShowAllBlogModel {
    String blogID,title,keyword,content,tags,createdDate;

    public AdminShowAllBlogModel(String blogID, String title, String keyword, String content, String tags, String createdDate) {
        this.blogID = blogID;
        this.title = title;
        this.keyword = keyword;
        this.content = content;
        this.tags = tags;
        this.createdDate = createdDate;
    }

    public String getBlogID() {
        return blogID;
    }

    public void setBlogID(String blogID) {
        this.blogID = blogID;
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
