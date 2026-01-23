package com.agrowmart.dto.auth;

//src/main/java/com/agrowmart/dto/auth/CompleteProfileRequest.java

import org.springframework.web.multipart.MultipartFile;

public record CompleteProfileRequest(
 String businessName,
 String address,
 String city,
 String state,
 String country,
 String postalCode,

 String aadhaarNumber,
 String panNumber,
 String udyamRegistrationNumber,
 
 String gstCertificateNumber,
 String tradeLicenseNumber,
 String fssaiLicenseNumber,

 String bankName,
 String accountHolderName,
 String bankAccountNumber,
 String ifscCode,
 String upiId,

 MultipartFile fssaiLicenseFile,
 MultipartFile photo,
 
//NEW: Specific document uploads
 MultipartFile aadhaarImage,
 MultipartFile panImage,
 MultipartFile udyamRegistrationImage
 
 
) {}