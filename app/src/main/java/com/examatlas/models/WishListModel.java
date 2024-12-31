package com.examatlas.models;

import com.examatlas.models.Books.BookImageModels;

import java.util.ArrayList;

public class WishListModel {
    String bookId,type,title,keyword,stock,content,price,sellPrice,tags,bookURL,author,categoryId,subCategoryId,subjectId,createdAt,updatedAt,itemId = null;
    ArrayList<BookImageModels> images;

    public WishListModel(String itemId, String bookId, String type, String title, String keyword, String stock, String price, String sellPrice, String content, String author, String categoryId, String subCategoryId, String subjectId, String tags, String bookURL, ArrayList<BookImageModels> images, String createdAt, String updatedAt) {
        this.itemId = itemId;
        this.bookId = bookId;
        this.type = type;
        this.title = title;
        this.keyword = keyword;
        this.stock = stock;
        this.price = price;
        this.sellPrice = sellPrice;
        this.content = content;
        this.author = author;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.subjectId = subjectId;
        this.tags = tags;
        this.bookURL = bookURL;
        this.images = images;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getBookURL() {
        return bookURL;
    }

    public void setBookURL(String bookURL) {
        this.bookURL = bookURL;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public ArrayList<BookImageModels> getImages() {
        return images;
    }

    public void setImages(ArrayList<BookImageModels> images) {
        this.images = images;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String id) {
        this.bookId = bookId;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
