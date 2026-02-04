package com.agrowmart.dto.auth.shop;

public record ShopSummaryDTO(
        Long shopId,
        String shopName,
        String shopPhoto,
        String shopAddress,
        String shopType
) {}