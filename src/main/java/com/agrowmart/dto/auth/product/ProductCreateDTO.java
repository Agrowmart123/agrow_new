package com.agrowmart.dto.auth.product;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
//
//public record ProductCreateDTO(
//        String productName,
//        String shortDescription,
//        Long categoryId,
//        Double stockQuantityKg,
//        BigDecimal price,
//        List<MultipartFile> images
//) {
//	
//
//}


import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

public record ProductCreateDTO(
    String productName,
    String shortDescription,
    
    Long categoryId,
    List<MultipartFile> images,

    // Vendor Registration Info (saved per product)
   
    Double stockQuantity,
  
    String shelfLife,
   
    
    
    
    // Vegetable Fields
    String vegWeight,
    String vegUnit,
    BigDecimal vegMinPrice,
    BigDecimal vegMaxPrice,
    String vegDisclaimer,

    // Dairy Fields
    String dairyQuantity,
   
    String dairyBrand,
    String dairyIngredients,
    String dairyPackagingType,
    String dairyProductInfo,
    String dairyUsageInfo,
    String dairyUnit,
    String dairyStorage,
    // Changes :- Ankita 
    BigDecimal dairyMinPrice,   // ⭐ ADD
    BigDecimal dairyMaxPrice,   // ⭐ ADD

    // Meat Fields
    String meatQuantity,
   
    String meatBrand,
    String meatKeyFeatures,
    String meatCutType,
    String meatServingSize,
    String meatStorageInstruction,
    String meatUsage,
    String meatEnergy,
    Boolean meatMarinated,
    String meatPackagingType,
    String meatDisclaimer,
    String meatRefundPolicy,
    // Chnages- Ankita 
    BigDecimal meatMinPrice,   // ⭐ ADD
    BigDecimal meatMaxPrice  // ⭐ ADD
) {}