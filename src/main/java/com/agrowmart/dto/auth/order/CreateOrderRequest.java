package com.agrowmart.dto.auth.order;

import java.math.BigDecimal;

public record CreateOrderRequest(
    String orderId,
    BigDecimal amount
) {}