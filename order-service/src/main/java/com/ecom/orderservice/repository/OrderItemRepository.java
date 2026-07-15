package com.ecom.orderservice.repository;

import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByProductId(Long productId);

    List<OrderItem> findByOrderId(Long orderId);
}