package com.agrowmart.dto.auth.customer;
public record UpdateCartItemRequest(
        Long itemId,
        Integer quantity
) {}
