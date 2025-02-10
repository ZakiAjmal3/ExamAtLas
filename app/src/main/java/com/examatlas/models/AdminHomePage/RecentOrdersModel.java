package com.examatlas.models.AdminHomePage;

public class RecentOrdersModel {
    String orderId, customerName, date, payment, totalPrice, products, orderStatus;

    public RecentOrdersModel(String orderId, String customerName, String date, String payment, String totalPrice, String products, String orderStatus) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.date = date;
        this.payment = payment;
        this.totalPrice = totalPrice;
        this.products = products;
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
