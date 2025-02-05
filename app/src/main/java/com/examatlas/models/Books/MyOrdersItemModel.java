package com.examatlas.models.Books;

import java.util.ArrayList;

public class MyOrdersItemModel {
    String orderSuccess,orderId,product_id,shipment_id,bookTitle,bookImgURL,reviewId,reviewRating,reviewHeadline,reviewTxt,reviewerUserId;
    ArrayList<String> imageUrlArrayList;
    public MyOrdersItemModel(String orderSuccess,String orderId,String product_id,String shipment_id, String bookTitle, String bookImgURL,String reviewId,String reviewRating,String reviewHeadline,String reviewTxt,String reviewerUserId,ArrayList<String> imageUrlArrayList) {
        this.orderSuccess = orderSuccess;
        this.orderId = orderId;
        this.product_id = product_id;
        this.shipment_id = shipment_id;
        this.bookTitle = bookTitle;
        this.bookImgURL = bookImgURL;
        this.reviewId = reviewId;
        this.reviewRating = reviewRating;
        this.reviewHeadline = reviewHeadline;
        this.reviewTxt = reviewTxt;
        this.reviewerUserId = reviewerUserId;
        this.imageUrlArrayList = imageUrlArrayList;
    }

    public ArrayList<String> getImageUrlArrayList() {
        return imageUrlArrayList;
    }

    public void setImageUrlArrayList(ArrayList<String> imageUrlArrayList) {
        this.imageUrlArrayList = imageUrlArrayList;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(String reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getReviewHeadline() {
        return reviewHeadline;
    }

    public void setReviewHeadline(String reviewHeadline) {
        this.reviewHeadline = reviewHeadline;
    }

    public String getReviewTxt() {
        return reviewTxt;
    }

    public void setReviewTxt(String reviewTxt) {
        this.reviewTxt = reviewTxt;
    }

    public String getReviewerUserId() {
        return reviewerUserId;
    }

    public void setReviewerUserId(String reviewerUserId) {
        this.reviewerUserId = reviewerUserId;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getShipment_id() {
        return shipment_id;
    }

    public void setShipment_id(String shipment_id) {
        this.shipment_id = shipment_id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBookImgURL() {
        return bookImgURL;
    }

    public void setBookImgURL(String bookImgURL) {
        this.bookImgURL = bookImgURL;
    }

    public String getOrderSuccess() {
        return orderSuccess;
    }

    public void setOrderSuccess(String orderSuccess) {
        this.orderSuccess = orderSuccess;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
}
