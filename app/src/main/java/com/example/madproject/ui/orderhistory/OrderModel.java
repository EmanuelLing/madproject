package com.example.madproject.ui.orderhistory;

import java.io.Serializable;

public class OrderModel implements Serializable {
    private int orderId;
    private String customerId;
    private double totalPrice;
    private String status;
    private String date;
    private String quantity;

    public OrderModel(int orderId, String customerId, double totalPrice, String status, String date, String quantity) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.date = date;
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
