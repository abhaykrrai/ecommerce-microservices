package com.com.cartservice.service;

import com.com.cartservice.dto.CartRequestDto;
import com.com.cartservice.dto.ProductResponseDto;
import com.com.cartservice.entity.Cart;
import com.com.cartservice.entity.CartItem;
import com.com.cartservice.exception.ProductIsLessOrderException;
import com.com.cartservice.exception.ProductNotFoundException;
import com.com.cartservice.feign.ProductClient;
import com.com.cartservice.repository.CartItemRepository;
import com.com.cartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductClient productClient;

    public ProductResponseDto testFeign(Long productId) {
        return productClient.getProductById(productId);
    }

    private Long getLoggedInUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @Transactional
    public String addProduct(CartRequestDto cartRequestDto) {

        Long userId = getLoggedInUserId();

        ProductResponseDto product =
                productClient.getProductById(cartRequestDto.getProductId());

        if (product == null) {
            throw new ProductNotFoundException("Product not found");
        }

        if (product.getQuantity() < cartRequestDto.getQuantity()) {
            throw new ProductIsLessOrderException("Insufficient stock");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> optionalCartItem =
                cartItemRepository.findByCartIdAndProductId(
                        cart.getId(),
                        cartRequestDto.getProductId());

        CartItem cartItem;

        if (optionalCartItem.isPresent()) {

            cartItem = optionalCartItem.get();

            int newQuantity = cartItem.getQuantity() + cartRequestDto.getQuantity();

            if (newQuantity > product.getQuantity()) {
                throw new ProductIsLessOrderException("Insufficient stock");
            }

            cartItem.setQuantity(newQuantity);

        } else {

            cartItem = new CartItem();
            cartItem.setCartId(cart.getId());
            cartItem.setProductId(cartRequestDto.getProductId());
            cartItem.setQuantity(cartRequestDto.getQuantity());
        }

        cartItemRepository.save(cartItem);

        return "Product added to cart";
    }

    public List<CartItem> getCart() {

        Long userId = getLoggedInUserId();

        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);

        if (optionalCart.isEmpty()) {
            return List.of();
        }

        return cartItemRepository.findByCartId(optionalCart.get().getId());
    }

    @Transactional
    public String updateQuantity(Long cartItemId, Integer quantity) {

        Long userId = getLoggedInUserId();

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = cartRepository.findById(cartItem.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (!cart.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this cart");
        }

        ProductResponseDto product =
                productClient.getProductById(cartItem.getProductId());

        if (quantity > product.getQuantity()) {
            throw new ProductIsLessOrderException("Insufficient stock");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return "Quantity updated";
    }

    @Transactional
    public String removeCartItem(Long cartItemId) {

        Long userId = getLoggedInUserId();

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = cartRepository.findById(cartItem.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (!cart.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to remove this cart item");
        }

        cartItemRepository.delete(cartItem);

        return "Cart item removed";
    }

    @Transactional
    public String clearCart() {

        Long userId = getLoggedInUserId();

        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);

        if (optionalCart.isEmpty()) {
            return "No cart found";
        }

        cartItemRepository.deleteByCartId(optionalCart.get().getId());
        cartRepository.deleteByUserId(userId);

        return "Cart cleared successfully";
    }
}