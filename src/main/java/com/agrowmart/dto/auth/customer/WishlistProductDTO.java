package com.agrowmart.dto.auth.customer;

//src/main/java/com/agrowmart/dto/customer/WishlistProductDTO.java


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WishlistProductDTO(
 Long wishlistId,
 Long productId,
 String productName,
 String productImage,
 String shopName,
 String vendorName,
 BigDecimal minPrice,
 BigDecimal maxPrice,

 String category,
 String type,               // VEGETABLE, DAIRY, MEAT, WOMEN
 boolean inStock,
 LocalDateTime addedAt,
 String addedTimeAgo
) {}