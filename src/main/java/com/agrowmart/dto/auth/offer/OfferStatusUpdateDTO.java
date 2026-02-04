// ----------------- DTO -----------------
package com.agrowmart.dto.auth.offer;

public record OfferStatusUpdateDTO(
    Boolean active   // true = activate, false = deactivate
) {}