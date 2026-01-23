package com.agrowmart.dto.auth.farmer;



public record FarmerRegisterRequest(
    String name,
    String phone,
    String email,
    String password
) {}