package com.example.parcelpaluser;
public class parcelListData {

    private int image;
    private String parcelId;
    private String trackingId;
    private String userId;
    private String orderTotal;
    private String paymentType;
    private String courierId;
    private String productName;
    private String deliveryStatus;
    private String orderId;
    private String dateReceived;
    private String paymentCompartment;
    public String getID(){

        return trackingId;
    }
    public parcelListData(int image, String parcelId, String trackingId, String userId, String orderTotal, String paymentType,
                          String courierId, String productName, String deliveryStatus, String orderId, String dateReceived,
        String paymentCompartment) {
        this.image = image;
        this.parcelId = parcelId;
        this.trackingId = trackingId;
        this.userId = userId;
        this.orderTotal = orderTotal;
        this.paymentType = paymentType;
        this.courierId = courierId;
        this.productName = productName;
        this.deliveryStatus = deliveryStatus;
        this.orderId = orderId;
        this.dateReceived = dateReceived;
        this.paymentCompartment = paymentCompartment;
    }

    // Getters and setters for all the fields

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCourierId() {
        return courierId;
    }

    public void setCourierId(String courierId) {
        this.courierId = courierId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public void setPaymentCompartment(String paymentCompartment) {
        this.paymentCompartment = paymentCompartment;
    }
    public String getPaymentCompartment() {
        return paymentCompartment;
    }


    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }
}