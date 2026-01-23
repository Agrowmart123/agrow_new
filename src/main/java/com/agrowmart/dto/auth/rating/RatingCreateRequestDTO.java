package com.agrowmart.dto.auth.rating;

public record RatingCreateRequestDTO(
	    Long vendorId,
	    Integer stars,      // 1-5
	    String feedback     // optional
	) {}