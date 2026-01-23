package com.agrowmart.admin_seller_management.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RejectReason {

    AADHAAR_MISMATCH("AADHAAR_MISMATCH"),
    PAN_MISMATCH("PAN_MISMATCH"),
    UDYAM_MISMATCH("UDYAM_MISMATCH"),
    SHOP_LICENSE_MISMATCH("SHOP_LICENSE_MISMATCH"),
    OTHER("OTHER");

    private final String value;

    RejectReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RejectReason fromValue(String value) {
        for (RejectReason reason : RejectReason.values()) {
            if (reason.value.equalsIgnoreCase(value)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Invalid reject reason: " + value);
    }
}
