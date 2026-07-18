package com.ecom.paymentservice.service;

import com.ecom.paymentservice.dto.PaymentRequestDto;
import com.ecom.paymentservice.dto.PaymentResponseDto;
import com.ecom.paymentservice.entity.Payment;
import com.ecom.paymentservice.entity.PaymentStatus;
import com.ecom.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentResponseDto paymentDone(PaymentRequestDto request) {

        Payment payment = new Payment();

        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());

        // Simulate successful payment
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        payment.setTransactionId(UUID.randomUUID().toString());

        payment.setPaymentDate(LocalDateTime.now());

        payment = paymentRepository.save(payment);

        PaymentResponseDto response = new PaymentResponseDto();

        response.setPaymentId(payment.getId());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentStatus(payment.getPaymentStatus());

        return response;
    }
}