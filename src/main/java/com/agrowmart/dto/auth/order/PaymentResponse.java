package com.agrowmart.dto.auth.order;

import java.math.BigDecimal;

public record PaymentResponse(
    String razorpayOrderId,
    BigDecimal amount,
    String currency
) {

}