package com.agrowmart.dto.auth.subscription;

//SubscriptionUpgradeRequest.java


import com.agrowmart.enums.SubscriptionPlan;

public record SubscriptionUpgradeRequest(SubscriptionPlan plan) {}