// com.agrowmart.dto.auth.product.PendingProductListDTO.java
package com.agrowmart.dto.auth.product;

import java.util.List;

import com.agrowmart.dto.auth.shop.ShopSummaryDTO;

public record PendingProductListDTO(
    Long id,
    String productName,
    String merchantName,
    String categoryName,
    String approvalStatus,
    String createdAt,
    List<String> imageUrls,
    String shortDescription,
    String productType,
    ShopSummaryDTO shop
) {}