package com.agrowmart.dto.auth.product;

//ProductSearchDTO.java (for UI filter)
public record ProductSearchDTO(
 String name,
 Long categoryId,
 String status,
 Integer page,
 Integer size
) {}