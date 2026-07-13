package com.com.cartservice.controller;

import com.com.cartservice.dto.CartRequestDto;
import com.com.cartservice.entity.CartItem;
import com.com.cartservice.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> addProductToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartRequestDto cartRequestDto) {

        return new ResponseEntity<>(
                cartService.addProduct(userId, cartRequestDto),
                HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Long userId) {

        return new ResponseEntity<>(
                cartService.getCart(userId),
                HttpStatus.OK);
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<String> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {

        return new ResponseEntity<>(
                cartService.updateQuantity(cartItemId, quantity),
                HttpStatus.OK);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> removeCartItem(
            @PathVariable Long cartItemId) {

        return new ResponseEntity<>(
                cartService.removeCartItem(cartItemId),
                HttpStatus.OK);
    }
}