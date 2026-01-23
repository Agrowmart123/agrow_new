package com.agrowmart.dto.auth.customer;

//AddressRequest.java
public record AddressRequest(
 String societyName,
 String houseNo,
 String buildingName,
 String landmark,
 String area,
 String pincode,
  String state,
 
 Double latitude,
 Double longitude,
 String addressType, // HOME, WORK, OTHER
 Boolean isDefault
) {}