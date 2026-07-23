package com.ecom.orderservice.controller;

import com.ecom.orderservice.dto.OrderRequestDto;
import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder() {
        return ResponseEntity.ok(orderService.placeOrder());
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId){
        return new ResponseEntity<>(orderService.cancelOrder(orderId), HttpStatus.OK);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(){
        return new ResponseEntity<>(orderService.getMyOrders(),HttpStatus.OK);
    }
}
