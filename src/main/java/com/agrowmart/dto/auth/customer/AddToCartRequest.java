package com.agrowmart.dto.auth.customer;
public record AddToCartRequest(
        Long productId,
        String productType,
        Integer quantity
) {}
