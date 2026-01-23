//package com.example.demo.dto.auth;
//
//
//public record RegisterRequest(String name, String email, String password, String phone) {}


//-------------------
//
//package com.agrowmart.dto.auth;
//
//import org.springframework.web.multipart.MultipartFile;
//
//public record RegisterRequest(
//    String name,
//    String email,
//    String phone,
//    String address,
//    String password,
//    String vendorType,
//    String kisanCardNumber,
//    String bankAccountNumber,
//    String ifscCode,
//    MultipartFile idProof
//    // addressProof REMOVED
//) {}

//-------------------


// src/main/java/com/agrowmart/dto/auth/RegisterRequest.java
package com.agrowmart.dto.auth;

import org.springframework.web.multipart.MultipartFile;

public record RegisterRequest(
    String name,
    String email,
    String phone,
    String password,
    String address,
    String businessName,

    // KYC
    String aadhaarNumber,
    String panNumber,
    String gstCertificateNumber,
    String tradeLicenseNumber,
    String fssaiLicenseNumber,
    MultipartFile fssaiLicenseFile, // optional upload
    String healthSafetyCertificateNumber,

    // Bank
    String bankName,
    String accountHolderName,
    String bankAccountNumber,
    String ifscCode,
    String upiId,

    // Proofs
    MultipartFile idProof,

    String vendorType // VEGETABLE, DAIRY, etc.
) {}