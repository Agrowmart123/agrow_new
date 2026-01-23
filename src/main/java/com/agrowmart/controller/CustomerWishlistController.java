package com.agrowmart.controller;

//src/main/java/com/agrowmart/controller/CustomerWishlistController.java

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agrowmart.dto.auth.customer.WishlistAddRequest;
import com.agrowmart.dto.auth.customer.WishlistProductDTO;
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
         @RequestHeader("Customer-Id") Long customerId) {
     return ResponseEntity.ok(service.addToWishlist(customerId, req));
 }

 @DeleteMapping("/remove")
 public ResponseEntity<Void> remove(
         @RequestParam Long productId,
         @RequestParam String productType,
         @RequestHeader("Customer-Id") Long customerId) {
     service.removeFromWishlist(customerId, productId, productType);
     return ResponseEntity.noContent().build();
 }

 @GetMapping
 public ResponseEntity<List<WishlistProductDTO>> getAll(
         @RequestHeader("Customer-Id") Long customerId) {
     return ResponseEntity.ok(service.getWishlist(customerId));
 }

 @GetMapping("/check")
 public ResponseEntity<Boolean> check(
         @RequestParam Long productId,
         @RequestParam String productType,
         @RequestHeader("Customer-Id") Long customerId) {
     return ResponseEntity.ok(service.isInWishlist(customerId, productId, productType));
 }
}