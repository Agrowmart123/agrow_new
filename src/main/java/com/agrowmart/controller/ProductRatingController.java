package com.agrowmart.controller;


import com.agrowmart.dto.auth.rating.ProductRatingCreateRequestDTO;
import com.agrowmart.dto.auth.rating.ProductRatingResponseDTO;
import com.agrowmart.dto.auth.rating.ProductRatingSummaryDTO;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.service.ProductRatingService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-ratings")
public class ProductRatingController {

    private final ProductRatingService ratingService;

    public ProductRatingController(ProductRatingService ratingService) {
        this.ratingService = ratingService;
    }

    // CUSTOMER: Add or update product rating
    @PostMapping
    public ResponseEntity<ProductRatingResponseDTO> rateProduct(
            @AuthenticationPrincipal Customer customer,
            @RequestBody ProductRatingCreateRequestDTO req) {

        return ResponseEntity.ok(
                ratingService.createOrUpdateProductRating(customer, req)
        );
    }

    // CUSTOMER: Delete own rating
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(
            @AuthenticationPrincipal Customer customer,
            @PathVariable Long ratingId) {

        ratingService.deleteRating(customer, ratingId);
        return ResponseEntity.noContent().build();
    }

    // PUBLIC: Get product rating summary
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductRatingSummaryDTO> getProductRatings(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                ratingService.getProductRatingSummary(productId)
        );
    }
}