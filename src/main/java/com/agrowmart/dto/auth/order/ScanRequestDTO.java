package com.agrowmart.dto.auth.order;

import jakarta.validation.constraints.NotBlank;

public record ScanRequestDTO(
    @NotBlank(message = "Token is required")
    String token,

    @NotBlank(message = "Scan type is required")
    String type   // Should be "VENDOR_PICKUP" or "USER_DELIVERY"
) {
}