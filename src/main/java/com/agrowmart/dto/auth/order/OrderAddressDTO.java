package com.agrowmart.dto.auth.order;


public record OrderAddressDTO(
    Long id,
    String societyName,
    String houseNo,
    String buildingName,
    String landmark,
    String area,
    String pincode,
    String state,
    Double latitude,
    Double longitude,
    String addressType,      // "HOME", "WORK", "OTHER"
    boolean isDefault
) {}