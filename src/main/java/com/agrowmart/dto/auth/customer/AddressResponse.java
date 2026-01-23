package com.agrowmart.dto.auth.customer;

//AddressResponse.java
public record AddressResponse(
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
 String addressType,
 boolean isDefault
) {}