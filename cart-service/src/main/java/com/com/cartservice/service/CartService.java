package com.com.cartservice.service;

import com.com.cartservice.dto.CartRequestDto;
import com.com.cartservice.dto.ProductResponseDto;
import com.com.cartservice.dto.UserResponseDto;
import com.com.cartservice.entity.Cart;
import com.com.cartservice.entity.CartItem;
import com.com.cartservice.exception.ProductIsLessOrderException;
import com.com.cartservice.exception.ProductNotFoundException;
import com.com.cartservice.exception.UserNotFoundException;
import com.com.cartservice.feign.ProductClient;
import com.com.cartservice.feign.UserClient;
import com.com.cartservice.repository.CartItemRepository;
import com.com.cartservice.repository.CartRepository;
import feign.FeignException;
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

    @Autowired
    private ProductClient productClient;

    @Autowired
    private UserClient userClient;


    public ProductResponseDto testFeign(Long productId) {
        return productClient.getProductById(productId);
    }



    @Transactional
    public String addProduct(Long userId, CartRequestDto cartRequestDto) {

        try {
            UserResponseDto user = userClient.getUserByID(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("User not found BY the Id");
        }



        ProductResponseDto product = productClient.getProductById(cartRequestDto.getProductId());

        if(product==null)
            throw new ProductNotFoundException("The product you are looking for does'nt exsist");

        if(product.getQuantity()<cartRequestDto.getQuantity())
            throw new ProductIsLessOrderException("the product is less then expected");

        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);

        Cart cart;

        if(optionalCart.isPresent())
            cart = optionalCart.get();
        else{
            cart= new Cart();
            cart.setUserId(userId);
            cart = cartRepository.save(cart);
        }
        // Find Cart Item
        Optional<CartItem> optionalCartItem =
                cartItemRepository.findByCartIdAndProductId(
                        cart.getId(),
                        cartRequestDto.getProductId());

        CartItem cartItem;

        if (optionalCartItem.isPresent()) {

            cartItem = optionalCartItem.get();

            int newQuantity = cartItem.getQuantity() + cartRequestDto.getQuantity();

            if (newQuantity > product.getQuantity()) {
                return "Insufficient stock";
            }

            cartItem.setQuantity(newQuantity);

        } else {

            cartItem = new CartItem();
            cartItem.setCartId(cart.getId());
            cartItem.setProductId(cartRequestDto.getProductId());
            cartItem.setQuantity(cartRequestDto.getQuantity());
        }

        cartItemRepository.save(cartItem);

        productClient.reduceStock(cartRequestDto.getProductId()
        ,cartRequestDto.getQuantity());

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