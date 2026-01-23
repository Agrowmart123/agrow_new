package com.agrowmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrowmart.entity.order.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}