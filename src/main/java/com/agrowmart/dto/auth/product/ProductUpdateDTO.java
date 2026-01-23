package com.agrowmart.dto.auth.product;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateDTO(
    String productName,
    String shortDescription,
    Long categoryId,
  

    // Images
    List<MultipartFile> images ,        // renamed from newImages → matches frontend
    List<String> removeImageUrls,

    String shelfLife,
    Double stockQuantity,// ⭐

    // Vegetable
    String vegWeight,
    String vegUnit,
    BigDecimal vegMinPrice,
    BigDecimal vegMaxPrice,
    String vegDisclaimer,

    // Dairy
    String dairyQuantity,
 
    String dairyBrand, 
    String dairyIngredients,
    String dairyPackagingType,
    String dairyProductInfo,
    String dairyUsageInfo,
    String dairyUnit,
    String dairyStorage,
    BigDecimal dairyMinPrice,   // ⭐ ADD
    BigDecimal dairyMaxPrice,   // ⭐

    // Meat
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
    BigDecimal meatMinPrice,    // ⭐ ADD
    BigDecimal meatMaxPrice
) {}