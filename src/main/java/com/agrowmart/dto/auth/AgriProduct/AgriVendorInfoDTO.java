package com.agrowmart.dto.auth.AgriProduct;


public record AgriVendorInfoDTO(
    Long id,
    String name,
    String phone,
    String businessName,
    String photoUrl,
    String city,
    String state
) {}