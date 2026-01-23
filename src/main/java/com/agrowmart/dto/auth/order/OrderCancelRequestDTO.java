package com.agrowmart.dto.auth.order;



import jakarta.validation.constraints.NotBlank;

public record OrderCancelRequestDTO(
        @NotBlank(message = "Cancellation reason is mandatory")
        String reason
) {}