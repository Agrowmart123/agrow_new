package com.agrowmart.controller;

import com.agrowmart.dto.auth.customer.*;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.service.customer.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/cart")

public class CartController {

    public CartController(CartService cartService) {
		super();
		this.cartService = cartService;
	}

	private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartSummaryDTO> addItem(
            @AuthenticationPrincipal Customer customer,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(customer, request));
    }

    @PatchMapping("/update")
    public ResponseEntity<CartSummaryDTO> updateItem(
            @AuthenticationPrincipal Customer customer,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(customer, request));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<CartSummaryDTO> removeItem(
            @AuthenticationPrincipal Customer customer,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(customer, itemId));
    }

    @GetMapping
    public ResponseEntity<CartSummaryDTO> getCart(@AuthenticationPrincipal Customer customer) {
        return ResponseEntity.ok(cartService.getCart(customer));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal Customer customer) {
        cartService.clearCart(customer);
        return ResponseEntity.noContent().build();
    }
}
