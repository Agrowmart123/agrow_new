//package com.agrowmart.dto.auth.order;
//
//import java.util.List;
//
//import java.util.List;
//
//public record OrderRequestDTO(
//     Long merchantId,
//     List<OrderItemRequestDTO> items,
//     String promoCode  // ← Change this line
//) {
// // ADD THIS CONSTRUCTOR — THIS IS THE MAGIC!
// public OrderRequestDTO {
//     if (promoCode != null) {
//         promoCode = promoCode.trim().isEmpty() ? null : promoCode.toUpperCase();
//     }
// }
//
//
//}

package com.agrowmart.dto.auth.order;

import java.time.LocalDate;
import java.util.List;

import com.agrowmart.enums.DeliveryMode;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDTO(
        Long merchantId,
        List<OrderItemRequestDTO> items,
        String paymentMode,   // <-- THIS WAS MISSING!
        String promoCode,
        
        
     // New: For multi-vendor (recommended)
        List<VendorItemsGroup> vendorGroups,      // ← List of vendor + their items
        
        
     // New scheduling & delivery mode fields
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate scheduledDate,          // null = today / immediate

        String scheduledSlot,             // null = as soon as possible

        @NotNull(message = "Delivery mode is required")
        DeliveryMode deliveryMode,
        
        @NotNull(message = "Delivery address is required") Long deliveryAddressId  // ← NEW & MANDATORY
) {
    public OrderRequestDTO {
        if (promoCode != null) {
            promoCode = promoCode.trim().isEmpty() ? null : promoCode.toUpperCase();
        }
        // Optional: validate or normalize paymentMode
        if (paymentMode != null) {
            paymentMode = paymentMode.toUpperCase();
            if (!paymentMode.equals("ONLINE") && !paymentMode.equals("COD")) {
                throw new IllegalArgumentException("paymentMode must be ONLINE or COD");
            }
        }
    }
    public record VendorItemsGroup(
            @NotNull(message = "Merchant ID required for group")
            Long merchantId,
            @NotEmpty(message = "Items required for group")
            List<OrderItemRequestDTO> items
        ) {}
}