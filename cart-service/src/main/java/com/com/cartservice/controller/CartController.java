package com.com.cartservice.controller;

import com.com.cartservice.dto.CartRequestDto;
import com.com.cartservice.dto.ProductResponseDto;
import com.com.cartservice.dto.UserResponseDto;
import com.com.cartservice.entity.CartItem;
import com.com.cartservice.feign.UserClient;
import com.com.cartservice.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserClient userClient;

    @PostMapping
    public ResponseEntity<String> addProductToCart(
            @Valid @RequestBody CartRequestDto cartRequestDto) {

        return new ResponseEntity<>(
                cartService.addProduct(cartRequestDto),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart() {

        return new ResponseEntity<>(
                cartService.getCart(),
                HttpStatus.OK);
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<String> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam
            @Min(value = 1, message = "Quantity must be at least 1")
            Integer quantity) {

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

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {

        return new ResponseEntity<>(
                cartService.clearCart(),
                HttpStatus.OK);
    }

    @GetMapping("/test/{productId}")
    public ProductResponseDto testFeign(@PathVariable Long productId) {
        return cartService.testFeign(productId);
    }

    @GetMapping("/user-test/{id}")
    public UserResponseDto testUser(@PathVariable Long id) {
        return userClient.getUserByID(id);
    }
}