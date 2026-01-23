package com.agrowmart.dto.auth.offer;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfferResponseDTO(
    Long id,
    String title,
    String code,
    String discountType,

    BigDecimal originalPrice,
    BigDecimal offerPrice,
    boolean free,

    Integer discountPercent,
    BigDecimal minOrderAmount,

    String customerGroup,
    String customerType,
    LocalDate startDate,
    LocalDate endDate,
    boolean isActive
) {}
