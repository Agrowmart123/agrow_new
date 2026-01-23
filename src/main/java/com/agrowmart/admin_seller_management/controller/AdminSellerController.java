package com.agrowmart.admin_seller_management.controller;

import com.agrowmart.admin_seller_management.dto.ApiResponseDTO;
import com.agrowmart.admin_seller_management.dto.DocumentVerificationRequestDTO;
import com.agrowmart.admin_seller_management.service.AdminSellerService;
import com.agrowmart.admin_seller_management.service.AdminVerificationService;
import com.agrowmart.admin_seller_management.enums.AccountStatus;
import com.agrowmart.admin_seller_management.enums.RejectReason;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/vendors")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminSellerController {

    private final AdminSellerService sellerService;
    private final AdminVerificationService verificationService;

    public AdminSellerController(
            AdminSellerService sellerService,
            AdminVerificationService verificationService) {
        this.sellerService = sellerService;
        this.verificationService = verificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<?>> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AccountStatus status) {

        return ResponseEntity.ok(
                sellerService.getVendors(page, size, search, status)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getVendorProfile(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiResponseDTO<>(
                        true,
                        "Vendor profile fetched",
                        sellerService.getVendorProfile(id)
                )
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponseDTO<?>> approve(
            @PathVariable Long id) {

//        verificationService.approveVendor(id);
    	// 21 Jan 
    	sellerService.approveVendor(id);

        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Vendor approved successfully")
        );
    }

//    @PutMapping("/{id}/reject")
//    public ResponseEntity<ApiResponseDTO<?>> reject(
//            @PathVariable Long id,
//            @Valid @RequestBody DocumentVerificationRequestDTO request) {
//
//        verificationService.rejectVendor(id, request);
//        return ResponseEntity.ok(
//                new ApiResponseDTO<>(true, "Vendor rejected successfully")
//        );
//    }
//    @PutMapping("/{id}/reject")
//    public ResponseEntity<ApiResponseDTO<?>> reject(
//            @PathVariable Long id,
//            @Valid @RequestBody DocumentVerificationRequestDTO request) {
//
//        String reason;
//
//        if (request.getRejectReason() == RejectReason.OTHER) {
//            reason = request.getCustomReason();
//        } else {
//            reason = request.getRejectReason().name(); // enum → string
//        }
//
//        sellerService.rejectVendor(id, reason);
//
//        return ResponseEntity.ok(
//                new ApiResponseDTO<>(true, "Vendor rejected successfully")
//        );
//    }
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponseDTO<?>> reject(
            @PathVariable Long id,
            @Valid @RequestBody DocumentVerificationRequestDTO request) {

        String reason;
        if (request.getRejectReason() == RejectReason.OTHER) {
            reason = request.getCustomReason();
        } else {
            reason = request.getRejectReason().name();
        }

        sellerService.rejectVendor(id, request);

        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Vendor rejected successfully")
        );
    }




    @PutMapping("/{id}/block")
    public ResponseEntity<ApiResponseDTO<?>> block(
            @PathVariable Long id) {

        sellerService.blockVendor(id);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Vendor blocked")
        );
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<ApiResponseDTO<?>> unblock(
            @PathVariable Long id) {

        sellerService.unblockVendor(id);
        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Vendor unblocked")
        );
    }
}
