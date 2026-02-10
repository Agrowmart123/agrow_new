package com.agrowmart.dto.auth.AgriProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.agrowmart.entity.AgriProduct.BaseAgriProduct.ApprovalStatus;

public record AgriProductResponseDTO(

    Long id,
    String AgriproductName,
    String category,
    String Agridescription,
    BigDecimal Agriprice,
    String Agriunit,
    Integer Agriquantity,

    List<String> AgriimageUrl,   // ✅ images FIRST
    LocalDateTime createdAt,     // ✅ created time AFTER images

    String AgribrandName,
    String AgripackagingType,
    String AgrilicenseNumber,
    String AgrilicenseType,
    Boolean verified,
    Boolean visibleToCustomers,
    ApprovalStatus approvalStatus,
    String rejectionReason,

    AgriVendorInfoDTO vendor,

    // category specific
    String fertilizerType,
    String nutrientComposition,
    String fcoNumber,

    String SeedscropType,
    String Seedsvariety,
    String seedClass,
    Double SeedsgerminationPercentage,
    Double SeedsphysicalPurityPercentage,
    String SeedslotNumber,

    String Pesticidetype,
    String PesticideactiveIngredient,
    String Pesticidetoxicity,
    String PesticidecibrcNumber,
    String Pesticideformulation,

    String Pipetype,
    String Pipesize,
    Double Pipelength,
    String PipebisNumber
) {}