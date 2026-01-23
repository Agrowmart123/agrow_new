package com.agrowmart.dto.auth.customer;

import java.math.BigDecimal;
import java.util.List;

public record CartSummaryDTO(
        Long cartId,
        List<CartItemDTO> items,
        int totalItems,
        BigDecimal subtotal,
        BigDecimal totalPayable,
        String currency
) {}
