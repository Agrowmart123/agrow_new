////package com.agrowmart.dto.auth;
////
////import jakarta.validation.constraints.Email;
////import jakarta.validation.constraints.NotBlank;
////
////public record UpdateProfileRequest(
////        @NotBlank(message = "Name is required") String name,
////        @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
////        String address,
////        @NotBlank(message = "Phone is required") String phone         // NEW
////                                // optional – role name
////) {}
//
//package com.agrowmart.dto.auth;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//
//public record UpdateProfileRequest(
//        @NotBlank String name,
//        @NotBlank @Email String email,
//        String address,
//        String businessName,
//        String city,
//        String state,
//        String country,
//        String postalCode,
//        String bankName,
//        String accountHolderName,
//        String bankAccountNumber,
//
//        @JsonProperty("photo_url") String photo_url,
//        @JsonProperty("profile_completed") String profile_completed,
//        @NotBlank String phone
//) {}



//package com.agrowmart.dto.auth;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//
//public record UpdateProfileRequest(
//        @NotBlank(message = "Name is required") String name,
//        @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
//        String address,
//        @NotBlank(message = "Phone is required") String phone         // NEW
//                                // optional – role name
//) {}


//------------------------------
//
//package com.agrowmart.dto.auth;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//
//public record UpdateProfileRequest(
//        @NotBlank String name,
//        @NotBlank @Email String email,
//        String address,
//        String businessName,
//        String city,
//        String state,
//        String country,
//        String postalCode,
//        String bankName,
//        String accountHolderName,
//        String bankAccountNumber,
//
//        @JsonProperty("photo_url") String photo_url,
//        @JsonProperty("profile_completed") String profile_completed,
//        @NotBlank String phone
//) {}

//--------------------


package com.agrowmart.dto.auth;

import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileRequest(
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