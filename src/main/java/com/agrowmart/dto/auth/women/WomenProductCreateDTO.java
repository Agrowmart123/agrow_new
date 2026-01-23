package com.agrowmart.dto.auth.women;

import java.math.BigDecimal;

public record WomenProductCreateDTO(
        String name,
        String category,
        String description,
       
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer stock,
        String unit,
     // ============ NEW FIELDS ============
        String ingredients,
        String shelfLife,
        String packagingType,
        String productInfo
) {}