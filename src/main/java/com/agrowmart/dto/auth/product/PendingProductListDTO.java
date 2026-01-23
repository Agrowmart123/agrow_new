// com.agrowmart.dto.auth.product.PendingProductListDTO.java
package com.agrowmart.dto.auth.product;

import java.util.List;

public record PendingProductListDTO(
    Long id,
    String productName,
    String merchantName,
    String categoryName,
    String createdAt,
    List<String> imageUrls,
    String shortDescription,
    String productType
) {}