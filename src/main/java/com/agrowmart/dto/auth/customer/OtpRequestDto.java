package com.agrowmart.dto.auth.customer;

public record OtpRequestDto(String phone, String purpose) {} // "PHONE_VERIFY" or "FORGOT_PASSWORD"