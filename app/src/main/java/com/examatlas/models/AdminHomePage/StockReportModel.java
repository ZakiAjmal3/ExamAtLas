package com.examatlas.models.AdminHomePage;

public class StockReportModel {
    String item,price,stock;

    public StockReportModel(String item, String price, String stock) {
        this.item = item;
        this.price = price;
        this.stock = stock;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
