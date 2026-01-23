package com.agrowmart.dto.auth.rating;



import java.util.Date;

public record ProductRatingResponseDTO(
        Long id,
        int stars,
        String feedback,
        String customerName,
        Date createdAt
) {}