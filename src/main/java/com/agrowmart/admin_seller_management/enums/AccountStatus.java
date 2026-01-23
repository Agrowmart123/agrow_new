package com.agrowmart.admin_seller_management.enums;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountStatus {

    PENDING("Pending approval"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    BLOCKED("Blocked");

    private final String label;

    AccountStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // Used when returning response to frontend
    @JsonValue
    public String toValue() {
        return this.name();
    }

    // Used when receiving request param or request body
    @JsonCreator
    public static AccountStatus fromValue(String value) {
        for (AccountStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid account status: " + value);
    }
}
