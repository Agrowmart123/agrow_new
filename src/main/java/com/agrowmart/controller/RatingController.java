// src/main/java/com/agrowmart/controller/RatingController.java

package com.agrowmart.controller;

import com.agrowmart.dto.auth.rating.RatingCreateRequestDTO;
import com.agrowmart.dto.auth.rating.RatingResponseDTO;
import com.agrowmart.dto.auth.rating.VendorRatingSummaryDTO;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // Customer submits or updates rating
    @PostMapping
    public ResponseEntity<RatingResponseDTO> submitRating(
            @AuthenticationPrincipal Customer customer,
            @Valid @RequestBody RatingCreateRequestDTO request) {
        return ResponseEntity.ok(ratingService.createOrUpdateRating(customer, request));
    }

    // Customer deletes their own rating
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(
            @AuthenticationPrincipal Customer customer,
            @PathVariable Long id) {
        ratingService.deleteRating(customer, id);
        return ResponseEntity.noContent().build();
    }

    // PUBLIC: Guests can view vendor ratings & reviews
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<VendorRatingSummaryDTO> getVendorRatings(@PathVariable Long vendorId) {
        return ResponseEntity.ok(ratingService.getVendorRatingSummary(vendorId));
    }
}