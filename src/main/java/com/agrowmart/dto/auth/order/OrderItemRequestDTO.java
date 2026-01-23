package com.agrowmart.dto.auth.order;

public record OrderItemRequestDTO(
        Long productId,
        int quantity
) {}