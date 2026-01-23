package com.agrowmart.dto.auth.order;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
	    String id,
	    Long customerId,
	    Long merchantId,
	    BigDecimal subtotal,
	    BigDecimal discountAmount,
	    BigDecimal deliveryCharge,
	    BigDecimal totalPrice,
	    String promoCode,
	    String status,
	    LocalDateTime createdAt,
	    LocalDateTime updatedAt,
	    List<OrderItemResponseDTO> items,
	    
	    String cancelReason,
	    String cancelledBy,
	    LocalDateTime cancelledAt,
	    String vendorPickupToken,
	    String userDeliveryToken,
	    Long deliveryPartnerId,
	    LocalDateTime pickupTime,
	    OrderAddressDTO deliveryAddress  // ← NEW FIELD - THIS WAS MISSING!
	) {}