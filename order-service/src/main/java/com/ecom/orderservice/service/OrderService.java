package com.ecom.orderservice.service;

import com.ecom.orderservice.config.RabbitMqConfig;
import com.ecom.orderservice.dto.CartItemResponseDto;
import com.ecom.orderservice.dto.OrderNotificationEvent;
import com.ecom.orderservice.dto.PaymentRequestDto;
import com.ecom.orderservice.dto.PaymentResponseDto;
import com.ecom.orderservice.dto.ProductResponseDto;
import com.ecom.orderservice.dto.UserResponseDto;
import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.entity.OrderItem;
import com.ecom.orderservice.entity.OrderStatus;
import com.ecom.orderservice.entity.PaymentMethod;
import com.ecom.orderservice.entity.PaymentStatus;
import com.ecom.orderservice.feign.CartClient;
import com.ecom.orderservice.feign.PaymentClient;
import com.ecom.orderservice.feign.ProductClient;
import com.ecom.orderservice.feign.UserClient;
import com.ecom.orderservice.repository.OrderItemRepository;
import com.ecom.orderservice.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private PaymentClient paymentClient;

    private final RabbitTemplate rabbitTemplate;

    public OrderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public String placeOrder() {

        // ============================
        // 1. Get logged-in user ID
        // ============================

        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        System.out.println("ORDER USER ID = " + userId);

        // ============================
        // 2. Get user information
        // ============================

        UserResponseDto user = userClient.getUserById(userId);

        if (user == null) {
            return "User not found";
        }

        System.out.println("ORDER USER EMAIL = " + user.getEmail());

        // ============================
        // 3. Get cart
        // ============================

        List<CartItemResponseDto> cartItems =
                cartClient.getCart();

        if (cartItems == null || cartItems.isEmpty()) {
            return "Cart is empty";
        }

        // ============================
        // 4. Create order
        // ============================

        Order order = new Order();

        order.setUserId(userId);
        order.setOrderedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CONFIRMED);

        double total = 0.0;

        // ============================
        // 5. Calculate total
        // ============================

        for (CartItemResponseDto item : cartItems) {

            ProductResponseDto product =
                    productClient.getProductById(
                            item.getProductId()
                    );

            if (product.getQuantity() < item.getQuantity()) {
                return "Insufficient stock for "
                        + product.getName();
            }

            total += product.getPrice()
                    * item.getQuantity();
        }

        order.setTotalAmount(total);

        order = orderRepository.save(order);

        // ============================
        // 6. Make payment
        // ============================

        PaymentRequestDto paymentRequest =
                new PaymentRequestDto();

        paymentRequest.setOrderId(order.getId());
        paymentRequest.setUserId(order.getUserId());
        paymentRequest.setAmount(order.getTotalAmount());
        paymentRequest.setPaymentMethod(
                PaymentMethod.UPI
        );

        PaymentResponseDto paymentResponse =
                paymentClient.makePayment(
                        paymentRequest
                );

        if (paymentResponse.getPaymentStatus()
                == PaymentStatus.SUCCESS) {

            order.setStatus(OrderStatus.PAID);

            orderRepository.save(order);

        } else {

            return "Payment Failed";
        }

        // ============================
        // 7. Save order items
        // ============================

        for (CartItemResponseDto item : cartItems) {

            ProductResponseDto product =
                    productClient.getProductById(
                            item.getProductId()
                    );

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProductId(
                    product.getId()
            );

            orderItem.setQuantity(
                    item.getQuantity()
            );

            orderItem.setPrice(
                    product.getPrice()
            );

            orderItem.setOrderStatus(
                    OrderStatus.PAID
            );

            orderItemRepository.save(orderItem);

            // Reduce product stock
            productClient.reduceStock(
                    item.getProductId(),
                    item.getQuantity()
            );
        }

        // ============================
        // 8. Clear cart
        // ============================

        cartClient.clearCart();

        System.out.println(
                "######## ORDER PLACED ########"
        );

        // ============================
        // 9. Send RabbitMQ notification
        // ============================

        OrderNotificationEvent notificationEvent =
                new OrderNotificationEvent(
                        order.getId(),
                        order.getUserId(),

                        order.getTotalAmount(),
                        order.getStatus().name(),
                        user.getEmail()
                );

        System.out.println(
                "######## ABOUT TO SEND RABBIT MESSAGE ########"
        );

        System.out.println(
                "Order ID = "
                        + notificationEvent.getOrderId()
        );

        System.out.println(
                "Email = "
                        + notificationEvent.getEmail()
        );

        System.out.println(
                "Amount = "
                        + notificationEvent.getAmount()
        );

        System.out.println(
                "Status = "
                        + notificationEvent.getStatus()
        );

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_KEY,
                notificationEvent
        );

        System.out.println(
                "######## RABBIT MESSAGE SENT ########"
        );

        return "Order placed successfully";
    }

    // =========================================================
    // CANCEL ORDER
    // =========================================================

    public String cancelOrder(Long orderId) {

        Optional<Order> optionalOrder =
                orderRepository.findById(orderId);

        Long userId =
                (Long) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        if (optionalOrder.isEmpty()) {
            return "User has no order";
        }

        Order order = optionalOrder.get();

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException(
                    "You can't cancel the order"
            );
        }

        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(
                        orderId
                );

        for (OrderItem item : orderItems) {

            productClient.restoreStock(
                    item.getProductId(),
                    item.getQuantity()
            );

            item.setOrderStatus(
                    OrderStatus.CANCELLED
            );

            orderItemRepository.save(item);
        }

        order.setStatus(
                OrderStatus.CANCELLED
        );

        orderRepository.save(order);

        return "Order has been cancelled";
    }

    // =========================================================
    // GET MY ORDERS
    // =========================================================

    public List<Order> getMyOrders() {

        Long userId =
                (Long) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        return orderRepository.findByUserId(userId);
    }
}