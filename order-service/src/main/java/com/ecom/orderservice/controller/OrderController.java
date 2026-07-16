package com.ecom.orderservice.controller;

import com.ecom.orderservice.dto.OrderRequestDto;
import com.ecom.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequestDto request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }
}
