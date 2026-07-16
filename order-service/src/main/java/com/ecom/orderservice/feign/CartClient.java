package com.ecom.orderservice.feign;

import com.ecom.orderservice.dto.CartItemResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "CART-SERVICE")
public interface CartClient {

    @GetMapping("/cart/{userId}")
    List<CartItemResponseDto> getCart(@PathVariable("userId") Long userId);

    @DeleteMapping("/cart/clear/{userId}")
    void clearCart(@PathVariable Long userId);

}