
package com.agrowmart.dto.auth;

import com.agrowmart.enums.OtpPurpose;

import jakarta.validation.constraints.NotBlank;

public record OtpRequest(
    @NotBlank String phone,
    @NotBlank OtpPurpose purpose      // PHONE_VERIFY  or  FORGOT_PASSWORD
) {}