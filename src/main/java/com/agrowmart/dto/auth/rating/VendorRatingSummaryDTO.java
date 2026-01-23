package com.agrowmart.dto.auth.rating;

import java.util.List;
import java.util.Map;

public record VendorRatingSummaryDTO(
    double averageRating,           // e.g., 4.4
    long totalReviews,              // e.g., 4175
    Map<Integer, Long> starCounts,  // {5: 3000, 4: 800, 3: 200, 2: 100, 1: 75}
    List<RatingResponseDTO> reviews // latest first
) {}