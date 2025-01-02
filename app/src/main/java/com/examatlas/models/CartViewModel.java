package com.examatlas.models;

import com.examatlas.models.Books.BookImageModels;

import java.util.ArrayList;

public class CartViewModel {
    String cartId,itemId,bookId,type,title,keyword,stock,price,sellPrice,content,author, categoryId,subCategoryId,subjectId,tags,createdAt,updatedAt,quantity;
    ArrayList<BookImageModels> bookImageArrayList;
    public CartViewModel(String cartId, String itemId, String bookId, String type, String title, String keyword, String stock, String price, String sellPrice, String content, String author, String categoryId, String subCategoryId, String subjectId, String tags,ArrayList bookImageArrayList, String createdAt, String updatedAt, String quantity) {
        this.cartId = cartId;
        this.itemId = itemId;
        this.type = type;
        this.bookId = bookId;
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
        this.bookImageArrayList = bookImageArrayList;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.quantity = quantity;
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

    public ArrayList<BookImageModels> getBookImageArrayList() {
        return bookImageArrayList;
    }

    public void setBookImageArrayList(ArrayList<BookImageModels> bookImageArrayList) {
        this.bookImageArrayList = bookImageArrayList;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
