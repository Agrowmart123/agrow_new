package com.agrowmart.controller;

import com.agrowmart.dto.auth.order.CreateOrderRequest;
import com.agrowmart.dto.auth.order.PaymentResponse;
import com.agrowmart.entity.User;
import com.agrowmart.service.PaymentService;
import com.razorpay.RazorpayException;

import java.util.Map;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> createPaymentOrder(
            @AuthenticationPrincipal User customer,
            @RequestBody CreateOrderRequest request) {
        try {
            PaymentResponse response = paymentService.createPaymentOrder(customer, request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Payment gateway error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to create payment order"));
        }
    }
}