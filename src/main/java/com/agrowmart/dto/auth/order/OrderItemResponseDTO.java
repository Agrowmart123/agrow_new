//package com.agrowmart.dto.auth.order;
//
//import java.math.BigDecimal;
//
//public record OrderItemResponseDTO(
//        Long id,
//        Long productId,
//        int quantity,
//        BigDecimal pricePerUnit,
//        BigDecimal totalPrice
//) {}

package com.agrowmart.dto.auth.order;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,                // order item ID
        Long productId,         // product ID (works for both normal & women)
        int quantity,           // or Integer if you prefer wrapper
        BigDecimal pricePerUnit,
        BigDecimal totalPrice,
        String productName      // ← ADD THIS - very important!
) {}