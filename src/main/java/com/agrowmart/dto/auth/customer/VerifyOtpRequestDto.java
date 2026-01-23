package com.agrowmart.dto.auth.customer;

//VerifyOtpRequestDto.java
public record VerifyOtpRequestDto(String phone, String code, String purpose) {}