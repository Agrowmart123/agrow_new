package com.agrowmart.dto.auth.AgriProduct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record AgriProductResponseDTO(

    Long id,
    String AgriproductName,
    String category,
    String Agridescription,
    BigDecimal Agriprice,
    String Agriunit,
    Integer Agriquantity,
    List<String> AgriimageUrl, 
    String AgribrandName,
    String AgripackagingType,
    String AgrilicenseNumber,
    String AgrilicenseType,
    Boolean verified,

    AgriVendorInfoDTO vendor,

    // Specific fields (nullable in record, will be null if not that type)
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