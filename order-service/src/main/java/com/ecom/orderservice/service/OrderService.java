package com.ecom.orderservice.service;

import com.ecom.orderservice.dto.CartItemResponseDto;
import com.ecom.orderservice.dto.OrderRequestDto;
import com.ecom.orderservice.dto.ProductResponseDto;
import com.ecom.orderservice.dto.UserResponseDto;
import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.entity.OrderItem;
import com.ecom.orderservice.entity.OrderStatus;
import com.ecom.orderservice.feign.CartClient;
import com.ecom.orderservice.feign.ProductClient;
import com.ecom.orderservice.feign.UserClient;
import com.ecom.orderservice.repository.OrderItemRepository;
import com.ecom.orderservice.repository.OrderRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private CartClient cartClient;

    @Transactional
    public String placeOrder(OrderRequestDto request) {

        // Check User
        UserResponseDto user;
        try {
            user = userClient.getUserById(request.getUserId());
        } catch (FeignException.NotFound e) {
            return "User not found";
        }

        // Get Cart
        List<CartItemResponseDto> cartItems =
                cartClient.getCart(request.getUserId());

        if (cartItems == null || cartItems.isEmpty()) {
            return "Cart is empty";
        }

        // Create Order
        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CONFIRMED);

        double total = 0.0;

        // Calculate Total
        for (CartItemResponseDto item : cartItems) {

            ProductResponseDto product =
                    productClient.getProductById(item.getProductId());

            if (product.getQuantity() < item.getQuantity()) {
                return "Insufficient stock for " + product.getName();
            }

            total += product.getPrice() * item.getQuantity();
        }

        order.setTotalAmount(total);

        order = orderRepository.save(order);

        // Save Order Items
        for (CartItemResponseDto item : cartItems) {

            ProductResponseDto product =
                    productClient.getProductById(item.getProductId());

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setOrderStatus(OrderStatus.CONFIRMED);

            orderItemRepository.save(orderItem);

            productClient.reduceStock(item.getProductId(), item.getQuantity());
        }

        // Delete the Cart and CartItems
        cartClient.clearCart(user.getId());

        return "Order placed successfully";
    }

    public String cancelOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if(optionalOrder.isEmpty())
            return "User has not order";
        Order order = optionalOrder.get();

        List<OrderItem> orderItem = orderItemRepository.findByOrderId(orderId);

        for(OrderItem o:orderItem){
          productClient.restoreStock(o.getProductId(),o.getQuantity());
          o.setOrderStatus(OrderStatus.CANCELLED);
          orderItemRepository.save(o);
        }


        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        return "Order has been cancelled";
    }
}