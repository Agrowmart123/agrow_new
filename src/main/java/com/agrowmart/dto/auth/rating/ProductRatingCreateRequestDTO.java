package com.agrowmart.dto.auth.rating;

public record ProductRatingCreateRequestDTO(
        Long productId,
        Integer stars,
        String feedback
) {}