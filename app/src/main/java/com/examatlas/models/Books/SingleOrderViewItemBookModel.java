package com.examatlas.models.Books;

public class SingleOrderViewItemBookModel {
    String bookId,bookTitle,bookPrice,bookSellingPrice,bookImage, bookAuthor, bookPublication;

    public SingleOrderViewItemBookModel(String bookId, String bookTitle, String bookPrice, String bookSellingPrice, String bookImage, String bookAuthor, String bookPublication) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
        this.bookSellingPrice = bookSellingPrice;
        this.bookImage = bookImage;
        this.bookAuthor = bookAuthor;
        this.bookPublication = bookPublication;
    }

    public String getBookSellingPrice() {
        return bookSellingPrice;
    }

    public void setBookSellingPrice(String bookSellingPrice) {
        this.bookSellingPrice = bookSellingPrice;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
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

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookPublication() {
        return bookPublication;
    }

    public void setBookPublication(String bookPublication) {
        this.bookPublication = bookPublication;
    }
}
