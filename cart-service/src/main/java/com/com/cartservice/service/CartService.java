package com.com.cartservice.service;

import com.com.cartservice.dto.CartRequestDto;
import com.com.cartservice.entity.Cart;
import com.com.cartservice.entity.CartItem;
import com.com.cartservice.repository.CartItemRepository;
import com.com.cartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public String addProduct(Long userId, CartRequestDto cartRequestDto) {

        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);

        Cart cart;

        if (optionalCart.isPresent()) {
            cart = optionalCart.get();
        } else {
            cart = new Cart();
            cart.setUserId(userId);
            cart = cartRepository.save(cart);
        }

        Optional<CartItem> optionalCartItem =
                cartItemRepository.findByCartIdAndProductId(
                        cart.getId(),
                        cartRequestDto.getProductId());

        CartItem cartItem;

        if (optionalCartItem.isPresent()) {
            cartItem = optionalCartItem.get();
            cartItem.setQuantity(
                    cartItem.getQuantity() + cartRequestDto.getQuantity());
        } else {
            cartItem = new CartItem();
            cartItem.setCartId(cart.getId());
            cartItem.setProductId(cartRequestDto.getProductId());
            cartItem.setQuantity(cartRequestDto.getQuantity());
        }

        cartItemRepository.save(cartItem);

        return "Product added to cart";
    }

    public List<CartItem> getCart(Long userId) {

        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);

        if (optionalCart.isEmpty()) {
            return List.of();
        }

        return cartItemRepository.findByCartId(optionalCart.get().getId());
    }

    @Transactional
    public String updateQuantity(Long cartItemId, Integer quantity) {

        Optional<CartItem> optionalCartItem =
                cartItemRepository.findById(cartItemId);

        if (optionalCartItem.isEmpty()) {
            return "Cart item not found";
        }

        CartItem cartItem = optionalCartItem.get();
        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        return "Quantity updated";
    }

    @Transactional
    public String removeCartItem(Long cartItemId) {

        Optional<CartItem> optionalCartItem =
                cartItemRepository.findById(cartItemId);

        if (optionalCartItem.isEmpty()) {
            return "Cart item not found";
        }

        cartItemRepository.delete(optionalCartItem.get());

        return "Cart item removed";
    }
}