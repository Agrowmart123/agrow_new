package com.agrowmart.dto.auth.rating;

public record RatingUpdateRequestDTO(
	    Integer stars,      // optional
	    String feedback     // optional
	) {}