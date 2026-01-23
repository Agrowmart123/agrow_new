package com.agrowmart.dto.auth.offer;

import java.math.BigDecimal;

public record FreeGiftResponseDTO(
	    Long id,
	    String productName,
	    String productImageUrl,
	    String description,
	    String quantity,
	    BigDecimal originalPrice,
	    BigDecimal minPurchaseAmount,
	    BigDecimal offerPrice,
	    boolean free,
	    boolean isActive
	) {}