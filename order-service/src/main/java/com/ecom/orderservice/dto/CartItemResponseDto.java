package com.ecom.orderservice.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CART-SERVICE")
public interface CartResponseDto {
    @GetMapping("/cart/{userId}")
    List<CartItemResponseDto> getCart(@PathVariable("userId") Long userId);

}
