package com.examatlas.models.Books;

import java.util.ArrayList;

public class WishListModel {
    String wishListId,userId,productId,categoryId,subCategoryId,bookTitle,bookAuthor,bookPrice,bookSellingPrice;
    ArrayList<BookImageModels> imageUrlArraylist;

    public WishListModel(String wishListId, String userId, String productId, String categoryId, String subCategoryId, String bookTitle, String bookAuthor, String bookPrice, String bookSellingPrice, ArrayList<BookImageModels> imageUrlArraylist) {
        this.wishListId = wishListId;
        this.userId = userId;
        this.productId = productId;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
        this.bookAuthor = bookAuthor;
        this.bookSellingPrice = bookSellingPrice;
        this.imageUrlArraylist = imageUrlArraylist;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getWishListId() {
        return wishListId;
    }

    public void setWishListId(String wishListId) {
        this.wishListId = wishListId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getBookSellingPrice() {
        return bookSellingPrice;
    }

    public void setBookSellingPrice(String bookSellingPrice) {
        this.bookSellingPrice = bookSellingPrice;
    }

    public ArrayList<BookImageModels> getImageUrlArraylist() {
        return imageUrlArraylist;
    }

    public void setImageUrlArraylist(ArrayList<BookImageModels> imageUrlArraylist) {
        this.imageUrlArraylist = imageUrlArraylist;
    }
}
