package com.examatlas.models.Books;

public class SingleBookTrackingItemModel {
    String shipmentId,title,status;

    public SingleBookTrackingItemModel(String shipmentId,String title, String status) {
        this.shipmentId = shipmentId;
        this.title = title;
        this.status = status;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
