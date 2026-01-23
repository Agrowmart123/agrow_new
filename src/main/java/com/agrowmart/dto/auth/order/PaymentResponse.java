package com.agrowmart.dto.auth.order;



public record PaymentResponse(
    String razorpayOrderId,
    double amount,
    String currency
) {}