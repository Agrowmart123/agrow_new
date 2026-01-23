package com.agrowmart.dto.auth.women;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.agrowmart.entity.ApprovalStatus;

public record WomenProductResponseDTO(
        Long id,
        String uuid,
        Long sellerId,
        String sellerName,
        String name,
        String category,
        String description,
        ApprovalStatus status,
       
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer stock,
        String unit,
        List<String> imageUrls,
        Boolean isAvailable,
        LocalDateTime createdAt,
     // ============ NEW FIELDS ============
        String ingredients,
        String shelfLife,
        String packagingType,
        String productInfo
) {}