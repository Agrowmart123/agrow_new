package com.agrowmart.dto.auth.order;



import java.time.LocalDateTime;

public record OrderStatusUpdateDTO(
    String orderId,
    String status,              // e.g. "ACCEPTED", "OUT_FOR_DELIVERY", "DELIVERED"
    String message,             // e.g. "Order is now out for delivery"
    LocalDateTime timestamp,
    String type,                // e.g. "STATUS_UPDATE", "NEW_PICKUP", "DELIVERED"
    String deliveryPartnerName  // optional, can be null
) {}