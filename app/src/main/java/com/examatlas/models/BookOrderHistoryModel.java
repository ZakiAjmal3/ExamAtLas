package com.examatlas.models;

import com.examatlas.models.extraModels.BookOrderItemsArrayListModel;

import java.util.ArrayList;

public class BookOrderHistoryModel {
    String order_id,totalAmount,paymentMethod,status,billingDetailId,shippingAddressUserName,shippingAddressFull,razorpay_order_id,razorpay_payment_id,razorpay_signature;
    ArrayList<BookOrderItemsArrayListModel> orderItemsArrayList;

    public BookOrderHistoryModel(String order_id, String totalAmount, String paymentMethod, String status, String billingDetailId, String shippingAddressUserName, String shippingAddressFull, String razorpay_order_id, String razorpay_payment_id, String razorpay_signature, ArrayList<BookOrderItemsArrayListModel> orderItemsArrayList) {
        this.order_id = order_id;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.billingDetailId = billingDetailId;
        this.shippingAddressUserName = shippingAddressUserName;
        this.shippingAddressFull = shippingAddressFull;
        this.razorpay_order_id = razorpay_order_id;
        this.razorpay_payment_id = razorpay_payment_id;
        this.razorpay_signature = razorpay_signature;
        this.orderItemsArrayList = orderItemsArrayList;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBillingDetailId() {
        return billingDetailId;
    }

    public void setBillingDetailId(String billingDetailId) {
        this.billingDetailId = billingDetailId;
    }

    public String getShippingAddressUserName() {
        return shippingAddressUserName;
    }

    public void setShippingAddressUserName(String shippingAddressUserName) {
        this.shippingAddressUserName = shippingAddressUserName;
    }

    public String getShippingAddressFull() {
        return shippingAddressFull;
    }

    public void setShippingAddressFull(String shippingAddressFull) {
        this.shippingAddressFull = shippingAddressFull;
    }

    public String getRazorpay_order_id() {
        return razorpay_order_id;
    }

    public void setRazorpay_order_id(String razorpay_order_id) {
        this.razorpay_order_id = razorpay_order_id;
    }

    public String getRazorpay_payment_id() {
        return razorpay_payment_id;
    }

    public void setRazorpay_payment_id(String razorpay_payment_id) {
        this.razorpay_payment_id = razorpay_payment_id;
    }

    public String getRazorpay_signature() {
        return razorpay_signature;
    }

    public void setRazorpay_signature(String razorpay_signature) {
        this.razorpay_signature = razorpay_signature;
    }

    public ArrayList<BookOrderItemsArrayListModel> getOrderItemsArrayList() {
        return orderItemsArrayList;
    }

    public void setOrderItemsArrayList(ArrayList<BookOrderItemsArrayListModel> orderItemsArrayList) {
        this.orderItemsArrayList = orderItemsArrayList;
    }
}
