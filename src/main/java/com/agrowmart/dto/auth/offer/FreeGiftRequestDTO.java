package com.agrowmart.dto.auth.offer;

import java.math.BigDecimal;

//FreeGiftRequestDTO.java
public record FreeGiftRequestDTO(
 String productName,
 String description,
 String quantity,
 BigDecimal originalPrice,
 BigDecimal offerPrice,
 boolean free,
 BigDecimal minPurchaseAmount
) {}