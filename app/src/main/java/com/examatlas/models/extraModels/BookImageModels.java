package com.examatlas.models.extraModels;

public class BookImageModels {
    String url,fileName,contentType,size,uploadDate,id;

    public BookImageModels(String url, String fileName, String contentType, String size, String uploadDate, String id) {
        this.url = url;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.uploadDate = uploadDate;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
