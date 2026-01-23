package com.agrowmart.dto.auth.customer;

//ResetPasswordRequestDto.java
public record ResetPasswordRequestDto(String phone, String newPassword, String code) {}