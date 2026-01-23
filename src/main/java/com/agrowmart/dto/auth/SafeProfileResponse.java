// src/main/java/com/agrowmart/dto/auth/SafeProfileResponse.java
package com.agrowmart.dto.auth;

import com.agrowmart.entity.User;
import com.agrowmart.util.MaskingUtil;

public record SafeProfileResponse(
    Long id,
    String name,
    String email,
    String phone,
    String businessName,

    // Masked sensitive data
    String aadhaarNumber,      // masked
    String panNumber,          // masked
    String bankAccountNumber,  // masked

    String bankName,
    String accountHolderName,
    String ifscCode,
    String upiId,

    String photoUrl,
    boolean profileCompleted,
    int profileCompletionPercentage,
    String onlineStatus
) {
    public SafeProfileResponse(User user, int percentage) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getBusinessName(),

            MaskingUtil.maskAadhaar(user.getAadhaarNumber()),
            MaskingUtil.maskPan(user.getPanNumber()),
            MaskingUtil.maskBankAccount(user.getBankAccountNumber()),

            user.getBankName(),
            user.getAccountHolderName(),
            user.getIfscCode(),
            user.getUpiId(),

            user.getPhotoUrl(),
            "YES".equals(user.getProfileCompleted()),
            percentage,
            user.getOnlineStatus()
        );
    }
}