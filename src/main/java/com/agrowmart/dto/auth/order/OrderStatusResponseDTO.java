package com.agrowmart.dto.auth.order;

import java.time.LocalDateTime;

public record OrderStatusResponseDTO(
	    String orderId,
	    String status,
	    LocalDateTime deliveredAt,
	    String deliveryPartnerName,
	    String deliveryMode
	) {}