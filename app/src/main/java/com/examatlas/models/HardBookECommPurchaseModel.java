package com.examatlas.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HardBookECommPurchaseModel {
    String id,title,keyword,content,price,sellPrice,tags,author, categoryId,createdAt,updatedAt,itemId = null,type,stock,subCategoryId,subjectId,bookUrl,isInCart,isInWishList;
    ArrayList images;
    int totalRows,totalPages,currentPage,pageSize;

    public HardBookECommPurchaseModel(String id, String type, String title, String keyword, String stock, String price, String sellPrice, String content, String author, String categoryId, String subCategoryId, String subjectId, String tags, String bookUrl, ArrayList images, String createdAt, String updatedAt, String isInCart, String isInWishList, int totalRows, int totalPages,int currentPage,int pageSize) {
        this.id = id;
        this.title = title;
        this.keyword = keyword;
        this.content = content;
        this.price = price;
        this.sellPrice = sellPrice;
        this.tags = tags;
        this.author = author;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stock = stock;
        this.type = type;
        this.subCategoryId = subCategoryId;
        this.subjectId = subjectId;
        this.bookUrl = bookUrl;
        this.images = images;
        this.isInCart = isInCart;
        this.isInWishList = isInWishList;
        this.totalRows = totalRows;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getIsInWishList() {
        return isInWishList;
    }

    public void setIsInWishList(String isInWishList) {
        this.isInWishList = isInWishList;
    }

    public String getIsInCart() {
        return isInCart;
    }

    public void setIsInCart(String isInCart) {
        this.isInCart = isInCart;
    }

    public ArrayList getImages() {
        return images;
    }

    public void setImages(ArrayList images) {
        this.images = images;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
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

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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
}
