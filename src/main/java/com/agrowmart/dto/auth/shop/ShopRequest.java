package com.agrowmart.dto.auth.shop;



import java.time.LocalTime;

import org.springframework.web.multipart.MultipartFile;

public record ShopRequest(
 String shopName,
 String shopType,
 String shopAddress,
//New
 String workingHoursJson,       // ← send JSON string from frontend
 String shopDescription,
 String shopLicense,
 
 LocalTime opensAt,
 LocalTime closesAt,
 
 MultipartFile shopPhoto,       // optional
 MultipartFile shopCoverPhoto,  // optional
 MultipartFile shopLicensePhoto  // optional
) {}