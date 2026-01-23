package com.agrowmart.dto.auth.shop;


import java.time.LocalTime;

public record ShopResponse(
 Long shopId,
 String shopName,
 String shopType,
 String shopAddress,
 String shopPhoto,
 String shopCoverPhoto,
 String shopLicensePhoto,
 String workingHours,
 String shopDescription,
 String shopLicense,
 boolean isApproved,
 boolean isActive,

 // Vendor Info (shown to customer)
 Long vendorId,
 String vendorName,
 String vendorPhone,
 String vendorEmail,
 String vendorRole,
 String vendorPhotoUrl,

 LocalTime opensAt,
 LocalTime closesAt
) {}