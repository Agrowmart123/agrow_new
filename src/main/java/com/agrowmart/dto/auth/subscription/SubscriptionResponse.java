package com.agrowmart.dto.auth.subscription;

//SubscriptionResponse.java

import com.agrowmart.enums.SubscriptionPlan;
import java.time.LocalDateTime;

public record SubscriptionResponse(
 SubscriptionPlan currentPlan,
 LocalDateTime startDate,
 LocalDateTime expiryDate,
 boolean isActive,
 int maxAllowedProducts,
 int currentProductCount,
 String planDescription
) {}