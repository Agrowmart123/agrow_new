package com.agrowmart.dto.auth.product;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//public record ProductResponseDTO(
//        Long id,
//        String productName,
//        String shortDescription,
//        String status,
//        Long categoryId,
//        String categoryName,
//        Double stockQuantityKg,
//        BigDecimal price,
//        List<String> imageUrls,
//        Long merchantId
//) {}

import java.util.List;

public record ProductResponseDTO(
    Long id,
    String productName,
    String shortDescription,
    String status,
    Long categoryId,
    String categoryName,
    List<String> imageUrls,
    Long merchantId,
    String productType, // VEGETABLE, DAIRY, MEAT
    Object details,    // Full detail object
    
   String stockStatus,
   // Code Change :- Aakansha
   // Changes:- Merge code Ankita 
   Long serialNo
) {}