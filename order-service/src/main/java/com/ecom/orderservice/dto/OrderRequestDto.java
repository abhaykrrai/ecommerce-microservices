package com.ecom.orderservice.dto;

public class OrderRequestDto {

    private Long userId;

    public OrderRequestDto() {
    }

    public OrderRequestDto(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
