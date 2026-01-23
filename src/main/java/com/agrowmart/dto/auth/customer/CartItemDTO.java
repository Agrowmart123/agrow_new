package com.agrowmart.dto.auth.customer;

import java.math.BigDecimal;

public record CartItemDTO(
        Long id,
        Long productId,
        String productType,
        String name,
        String image,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal
) {}
