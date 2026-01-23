package com.agrowmart.controller;


import com.agrowmart.entity.User;
import com.agrowmart.service.RefundService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/refunds")
public class RefundController {
    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    // Vendor or admin initiates refund (e.g., on reject)
    @PostMapping("/initiate/{orderId}")
    @PreAuthorize("hasAnyRole('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'ADMIN')")
    public ResponseEntity<String> initiateRefund(
            @PathVariable String orderId,
            @AuthenticationPrincipal User user) {
        refundService.initiateRefund(orderId, user);
        return ResponseEntity.ok("Refund initiated");
    }

}