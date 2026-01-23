// com.agrowmart.dto.auth.women.PendingWomenProductDTO.java
package com.agrowmart.dto.auth.women;

import java.math.BigDecimal;
import java.util.List;

public record PendingWomenProductDTO(
	    Long id,
	    String uuid,
	    String name,
	    Long sellerId,
	    String sellerName,
	    String category,
	    String approvalStatus,
	    List<String> imageUrls,
	    BigDecimal minPrice,
	    BigDecimal maxPrice,
	    Integer stock,
	    Boolean isAvailable,
	    String createdAt,
	    String rejectionReason
	) {}