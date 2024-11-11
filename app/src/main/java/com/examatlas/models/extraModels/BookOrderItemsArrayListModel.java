package com.examatlas.models.extraModels;

public class BookOrderItemsArrayListModel {
    String cartId,itemId,title,sellPrice,quantity,bookId;

    public BookOrderItemsArrayListModel(String cartId, String itemId, String title, String sellPrice, String quantity, String bookId) {
        this.cartId = cartId;
        this.itemId = itemId;
        this.title = title;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.bookId = bookId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
