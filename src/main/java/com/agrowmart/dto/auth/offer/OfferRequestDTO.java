package com.agrowmart.dto.auth.offer;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfferRequestDTO(
    String title,
    String code,
    String discountType,

    BigDecimal originalPrice,
    BigDecimal offerPrice,     // 0 = FREE
    boolean free,

    Integer discountPercent,
    BigDecimal minOrderAmount,

    String customerGroup,
    String customerType,
    LocalDate startDate,
    LocalDate endDate
) {}
