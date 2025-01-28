package com.examatlas.models.Books;

public class BookOrderSummaryItemsDetailsRecyclerViewModel {
    String itemName,itemPrice,itemQuantity;

    public BookOrderSummaryItemsDetailsRecyclerViewModel(String itemName, String itemPrice,String itemQuantity) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }
}
