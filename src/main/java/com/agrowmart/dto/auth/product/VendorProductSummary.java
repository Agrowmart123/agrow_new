package com.agrowmart.dto.auth.product;

public record VendorProductSummary(
    Long id,
    String productName,
    String shortDescription,
    String imagePaths,
    String status,

    Long categoryId,
    String categoryName,
    Long serialNo
) {}