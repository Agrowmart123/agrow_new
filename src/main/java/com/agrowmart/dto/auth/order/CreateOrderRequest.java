package com.agrowmart.dto.auth.order;


public record CreateOrderRequest(
    String orderId,
    double amount
) {}