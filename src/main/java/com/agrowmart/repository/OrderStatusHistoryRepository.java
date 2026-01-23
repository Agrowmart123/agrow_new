package com.agrowmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrowmart.entity.order.OrderStatusHistory;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
}