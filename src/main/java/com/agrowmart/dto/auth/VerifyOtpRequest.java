
package com.agrowmart.dto.auth;

import com.agrowmart.enums.OtpPurpose;

import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(
    @NotBlank String phone,
    @NotBlank String code,
    OtpPurpose purpose,
    String newPassword // Only required for FORGOT_PASSWORD
) {}