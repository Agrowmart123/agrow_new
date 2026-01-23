package com.agrowmart.dto.auth.rating;


import java.util.Map;
import java.util.List;

public record ProductRatingSummaryDTO(
        double averageRating,
        long totalReviews,
        Map<Integer, Long> starCounts,
        List<ProductRatingResponseDTO> reviews
) {}