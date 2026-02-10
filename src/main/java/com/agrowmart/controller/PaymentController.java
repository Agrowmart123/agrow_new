package com.agrowmart.controller;

import com.agrowmart.dto.auth.order.CreateOrderRequest;
import com.agrowmart.dto.auth.order.PaymentResponse;
import com.agrowmart.entity.User;
import com.agrowmart.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPaymentOrder(
            @AuthenticationPrincipal User customer,
            @Valid @RequestBody CreateOrderRequest request) {

        // No try-catch needed → GlobalExceptionHandler handles everything
        PaymentResponse response = paymentService.createPaymentOrder(customer, request);
        return ResponseEntity.ok(response);
    }
}