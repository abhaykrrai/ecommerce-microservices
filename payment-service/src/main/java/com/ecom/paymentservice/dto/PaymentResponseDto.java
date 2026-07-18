package com.ecom.paymentservice.dto;

import com.ecom.paymentservice.entity.PaymentStatus;

public class PaymentResponseDto {

    private Long paymentId;
    private String transactionId;
    private PaymentStatus paymentStatus;

    public PaymentResponseDto() {
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
