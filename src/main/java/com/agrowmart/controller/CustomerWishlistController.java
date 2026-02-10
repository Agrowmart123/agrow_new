package com.agrowmart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.agrowmart.dto.auth.customer.WishlistAddRequest;
import com.agrowmart.dto.auth.customer.WishlistProductDTO;
import com.agrowmart.entity.customer.Customer;          // ← import this
import com.agrowmart.service.customer.CustomerWishlistService;
import java.util.List;

@RestController
@RequestMapping("/api/customer/wishlist")
public class CustomerWishlistController {

    private final CustomerWishlistService service;

    public CustomerWishlistController(CustomerWishlistService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<WishlistProductDTO> add(
            @RequestBody WishlistAddRequest req,
            @AuthenticationPrincipal Customer customer) {     // ← Changed to Customer

        Long customerId = customer.getId();                    // ← get ID from entity
        return ResponseEntity.ok(service.addToWishlist(customerId, req));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> remove(
            @RequestParam Long productId,
            @RequestParam String productType,
            @AuthenticationPrincipal Customer customer) {

        Long customerId = customer.getId();
        service.removeFromWishlist(customerId, productId, productType);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WishlistProductDTO>> getAll(
            @AuthenticationPrincipal Customer customer) {

        Long customerId = customer.getId();
        return ResponseEntity.ok(service.getWishlist(customerId));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> check(
            @RequestParam Long productId,
            @RequestParam String productType,
            @AuthenticationPrincipal Customer customer) {

        Long customerId = customer.getId();
        return ResponseEntity.ok(service.isInWishlist(customerId, productId, productType));
    }
}