package com.agrowmart.admin_seller_management.dto;

import com.agrowmart.admin_seller_management.enums.RejectReason;
import jakarta.validation.constraints.NotNull;

public class DocumentVerificationRequestDTO {

    @NotNull(message = "Reject reason is required")
    private RejectReason rejectReason;

    private String customReason;
    

    public RejectReason getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(RejectReason rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getCustomReason() {
        return customReason;
    }

    public void setCustomReason(String customReason) {
        this.customReason = customReason;
    }
}
