package com.agrowmart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

import com.agrowmart.entity.VendorPaymentDetails;
import com.agrowmart.entity.order.Settlement;
import com.agrowmart.service.SettlementService;
@RestController
@RequestMapping("/admin/settlements")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettlementController {
    private final SettlementService settlementService;

    public AdminSettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Settlement>> getPendingSettlements() {
        return ResponseEntity.ok(settlementService.getPendingSettlements());
    }
    
    
    

    @PostMapping("/{id}/pay")
    public ResponseEntity<String> manualPayout(@PathVariable Long id) {
        settlementService.manualPayout(id);
        return ResponseEntity.ok("Payout processed");
    }

    @GetMapping("/failed")
    public ResponseEntity<List<Settlement>> getFailedSettlements() {
        return ResponseEntity.ok(settlementService.getFailedSettlements());
    }

    @GetMapping("/vendor/{vendorId}/wallet")
    public ResponseEntity<VendorPaymentDetails> getVendorWallet(@PathVariable Long vendorId) {
        return ResponseEntity.ok(settlementService.getVendorWallet(vendorId));
    }
}