package com.agrowmart.dto.auth.customer;

//CustomerProfileResponse.java
public record CustomerProfileResponse(
 Long id,
 String fullName,
 String email,
 String phone,
 String gender,
 String profileImage,
 boolean phoneVerified
) {}