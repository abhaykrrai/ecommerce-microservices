package com.ecom.orderservice.feign;

import com.ecom.orderservice.dto.PaymentRequestDto;
import com.ecom.orderservice.dto.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {
    @PostMapping("/payment/pay")
    public PaymentResponseDto makePayment(@RequestBody PaymentRequestDto request) ;
}
