package com.agrowmart.controller;

import com.agrowmart.dto.auth.subscription.SubscriptionResponse;
import com.agrowmart.dto.auth.subscription.SubscriptionUpgradeRequest;
import com.agrowmart.entity.User;
import com.agrowmart.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // GET: Current subscription status (for logged-in AGRI vendor)
    @GetMapping("/status")
    public ResponseEntity<SubscriptionResponse> getStatus(Authentication auth) {
        User user = getCurrentUser(auth);

        // Only AGRI vendors can check subscription
        if (!"AGRI".equals(user.getRole().getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }

        SubscriptionResponse status = subscriptionService.getCurrentStatus(user);
        return ResponseEntity.ok(status);
    }

    // POST: Initiate Razorpay order for plan upgrade
    @PostMapping("/upgrade")
    public ResponseEntity<Map<String, String>> upgradePlan(
            @RequestBody SubscriptionUpgradeRequest request,
            Authentication auth) {

        User user = getCurrentUser(auth);

        // Only AGRI vendors can upgrade
        if (!"AGRI".equals(user.getRole().getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only AGRI vendors can upgrade subscription"));
        }

        Map<String, String> orderDetails = subscriptionService.createRazorpayOrder(user, request.plan());
        return ResponseEntity.ok(orderDetails);
    }

    // POST: Razorpay webhook (called by Razorpay when payment succeeds)
    // This endpoint must be PUBLIC (no auth required)
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // In production: Add signature verification here (very important!)
            // String signature = (String) request.getHeader("X-Razorpay-Signature");
            // RazorpaySignatureUtil.verify(payloadJson, signature);

            String orderId = (String) payload.get("razorpay_order_id");
            String paymentId = (String) payload.get("razorpay_payment_id");

            if (orderId == null || paymentId == null) {
                return ResponseEntity.badRequest().body("Invalid webhook payload");
            }

            subscriptionService.processSuccessfulPayment(orderId, paymentId);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            // Log in production
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Webhook processing error: " + e.getMessage());
        }
    }

    // Helper: Extract current user safely
    private User getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                "Invalid authentication principal: " + principal.getClass().getName());
    }
}