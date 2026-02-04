package com.agrowmart.dto.auth.shop;


import java.time.LocalTime;
import java.util.Map;

import com.agrowmart.entity.Shop.DayHours;

public record ShopResponse(
 Long shopId,
 String shopName,
 String shopType,
 String shopAddress,
 String shopPhoto,
 String shopCoverPhoto,
 String shopLicensePhoto,
 
//Changed: return the JSON string directly
 String workingHoursJson,     // ← this is what frontend will receive
 
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