package com.agrowmart.dto.auth;

//src/main/java/com/agrowmart/dto/auth/FullKycResponse.java


import com.agrowmart.entity.User;

public record FullKycResponse(
 String fullAadhaarNumber,
 String fullPanNumber,
 String fullBankAccountNumber,
 String bankName,
 String accountHolderName,
 String ifscCode,
 String message,
 int visibleForSeconds
) {
 public FullKycResponse(User user) {
     this(
         user.getAadhaarNumber(),
         user.getPanNumber(),
         user.getBankAccountNumber(),
         user.getBankName(),
         user.getAccountHolderName(),
         user.getIfscCode(),
         "Visible for 30 seconds only",
         30
     );
 }
}