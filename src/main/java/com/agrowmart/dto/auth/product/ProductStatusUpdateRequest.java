package com.agrowmart.dto.auth.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

//1. Create this small DTO (in dto/auth/product or similar)
public record ProductStatusUpdateRequest(
 @NotBlank(message = "status is required")
 @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
 String status
) {}