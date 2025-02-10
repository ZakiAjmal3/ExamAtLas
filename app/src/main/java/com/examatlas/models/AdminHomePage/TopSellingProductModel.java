package com.examatlas.models.AdminHomePage;

public class TopSellingProductModel {
    String itemName,price,discount,sold,totalOrders;

    public TopSellingProductModel(String itemName, String price, String discount, String sold, String totalOrders) {
        this.itemName = itemName;
        this.price = price;
        this.discount = discount;
        this.sold = sold;
        this.totalOrders = totalOrders;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(String totalOrders) {
        this.totalOrders = totalOrders;
    }
}
