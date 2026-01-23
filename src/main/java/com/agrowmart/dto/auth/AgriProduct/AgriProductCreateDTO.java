package com.agrowmart.dto.auth.AgriProduct;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record AgriProductCreateDTO(

    @NotBlank(message = "Product name is required")
    String AgriproductName,

    @NotBlank(message = "Category is required")
    String category, // FERTILIZER, SEEDS, PESTICIDE, PIPE

    String Agridescription,

    @NotNull(message = "Price is required")
    @Positive
    BigDecimal Agriprice,

    @NotBlank
    String Agriunit,

    @NotNull
    @PositiveOrZero
    Integer Agriquantity,

    List<String> AgriimageUrl,
    String AgribrandName,
    String AgripackagingType,
    String AgrilicenseNumber,
    String AgrilicenseType,
    String AgrilicenseImageUrl,
    String AgribatchNumber,
    String AgrimanufacturerName,
    LocalDate AgrimanufacturingDate,
    LocalDate AgriexpiryDate,

    // Specific fields
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