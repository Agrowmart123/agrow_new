package com.agrowmart.dto.auth.AgriProduct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.agrowmart.entity.AgriProduct.BaseAgriProduct;

public record PendingAgriProductDTO(
    Long id,
    String AgriproductName,
    String category,
    String Agridescription,
    BigDecimal Agriprice,
    String Agriunit,
    Integer Agriquantity,
    String AgribrandName,
    String AgrilicenseNumber,
    String AgrilicenseType,
    String AgribatchNumber,
    List<String> AgriimageUrl,   // ✅ image list
    LocalDateTime createdAt,     // ✅ created time
    String AgrimanufacturerName,
    LocalDate AgrimanufacturingDate,
    LocalDate AgriexpiryDate,
    BaseAgriProduct.ApprovalStatus approvalStatus,
    String rejectionReason,
    AgriVendorInfoDTO vendor,
    // category specific fields (same as before)
    String fertilizerType, String nutrientComposition, String fcoNumber,
    String SeedscropType, String Seedsvariety, String seedClass,
    Double SeedsgerminationPercentage, Double SeedsphysicalPurityPercentage, String SeedslotNumber,
    String Pesticidetype, String PesticideactiveIngredient, String Pesticidetoxicity,
    String PesticidecibrcNumber, String Pesticideformulation,
    String Pipetype, String Pipesize, Double Pipelength, String PipebisNumber
) {}