package com.agrowmart.dto.auth.customer;

//Request DTO


public record WishlistAddRequest(
 Long productId,
 String productType  // "REGULAR" or "WOMEN"
) {}