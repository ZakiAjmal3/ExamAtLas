package com.examatlas.models.AdminHomePage;

public class TopCustomersModel {
    String customerName,customerEmail,numberOfOrders;

    public TopCustomersModel(String customerName, String customerEmail, String numberOfOrders) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.numberOfOrders = numberOfOrders;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(String numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }
}
