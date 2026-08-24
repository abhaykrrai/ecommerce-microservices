package com.ecom.orderservice.dto;

public class OrderNotificationEvent {

    private Long orderId;
    private Long userId;
    private double amount;
    private String status;

    public OrderNotificationEvent() {
    }

    public OrderNotificationEvent(
            Long orderId,
            Long userId,
            double amount,
            String status) {

        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}