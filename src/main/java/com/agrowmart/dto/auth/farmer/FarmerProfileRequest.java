package com.agrowmart.dto.auth.farmer;



public record FarmerProfileRequest(
    String name,
    String phone,
    String email,
    String state,
    String bankName,
    String accountHolderName,
    String bankAccountNumber,
    String ifscCode
) {}