package com.agrowmart.service;



import com.agrowmart.dto.auth.shop.ShopRequest;
import com.agrowmart.dto.auth.shop.ShopResponse;
import com.agrowmart.dto.auth.shop.ShopSearchDTO;
import com.agrowmart.dto.auth.shop.ShopSummaryDTO;
import com.agrowmart.dto.auth.shop.WorkingHourDTO;
import com.agrowmart.entity.Shop;
import com.agrowmart.entity.User;
import com.agrowmart.exception.AuthExceptions.AuthenticationFailedException;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.AuthExceptions.FileUploadException;
import com.agrowmart.exception.ForbiddenException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.ShopRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service

public class ShopService {
	private static final Logger log = LoggerFactory.getLogger(ShopService.class);
 private final ShopRepository shopRepository;
 private final CloudinaryService cloudinaryService; // ✅ ONLY cloudinary
 
// ✅ MANUAL CONSTRUCTOR (REQUIRED)
 public ShopService(ShopRepository shopRepository,
                    CloudinaryService cloudinaryService) {
     this.shopRepository = shopRepository;
     this.cloudinaryService = cloudinaryService;
 }

 
 private static final Set<String> VENDOR_ROLES = Set.of(
         "FARMER", "VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN","AGRI"
 );

 // ===================== CREATE SHOP =====================
 public Shop createShop(ShopRequest req, User user) throws IOException {

	 if (user == null) {
         throw new AuthenticationFailedException("User must be authenticated to create a shop");
     }

     if (!isVendor(user)) {
         throw new ForbiddenException("Only vendors can create a shop");
     }

     if (shopRepository.existsByUser(user)) {
         throw new BusinessValidationException("You already have a registered shop");
     }

     Shop shop = new Shop();
     shop.setShopName(req.shopName());
     shop.setShopType(req.shopType());
     shop.setShopAddress(req.shopAddress());
     shop.setWorkingHoursJson(req.workingHoursJson());
     shop.setShopDescription(req.shopDescription());
     shop.setShopLicense(req.shopLicense());
     shop.setUser(user);
     shop.setApproved(false);
     shop.setActive(true);

     shop.setOpensAt(req.opensAt());
     shop.setClosesAt(req.closesAt());

     // ✅ CLOUDINARY UPLOAD
     shop.setShopPhoto(uploadIfPresent(req.shopPhoto()));
     shop.setShopCoverPhoto(uploadIfPresent(req.shopCoverPhoto()));
     shop.setShopLicensePhoto(uploadIfPresent(req.shopLicensePhoto()));
     log.info("Shop created successfully for user {} → Shop ID: {}", user.getId(), shop.getId());
     return shopRepository.save(shop);
 }

 // ===================== UPDATE MY SHOP =====================
 public Shop updateMyShop(ShopRequest req, User user) throws IOException {

	 if (user == null) {
         throw new AuthenticationFailedException("User must be authenticated to update shop");
     }

     if (!isVendor(user)) {
         throw new ForbiddenException("Only vendors can update shop");
     }

     Shop shop = shopRepository.findByUser(user)
             .orElseThrow(() -> new ResourceNotFoundException("You do not have a registered shop"));
     
     shop.setShopName(req.shopName());
     shop.setShopType(req.shopType());
     shop.setShopAddress(req.shopAddress());
  
     shop.setShopDescription(req.shopDescription());
     shop.setShopLicense(req.shopLicense());

     shop.setOpensAt(req.opensAt());
     shop.setClosesAt(req.closesAt());

  // ── Working hours JSON ────────────────────────────────
     if (req.workingHoursJson() != null && !req.workingHoursJson().trim().isEmpty()) {
         // Optional: you can add basic validation here
         try {
             // Minimal check that it's valid JSON (optional)
             new com.fasterxml.jackson.databind.ObjectMapper().readTree(req.workingHoursJson());
             shop.setWorkingHoursJson(req.workingHoursJson().trim());
         } catch (JsonProcessingException e) {
             throw new IllegalArgumentException("Invalid working hours JSON format", e);
         }
     }
     
     // ✅ Replace image only if new one is provided
     if (req.shopPhoto() != null && !req.shopPhoto().isEmpty()) {
         if (shop.getShopPhoto() != null) {
             cloudinaryService.delete(shop.getShopPhoto());
         }
         shop.setShopPhoto(uploadIfPresent(req.shopPhoto()));
     }

 
     
     if (req.shopCoverPhoto() != null && !req.shopCoverPhoto().isEmpty()) {
         if (shop.getShopCoverPhoto() != null) {
             cloudinaryService.delete(shop.getShopCoverPhoto());
         }
         shop.setShopCoverPhoto(uploadIfPresent(req.shopCoverPhoto()));
     }

     if (req.shopLicensePhoto() != null && !req.shopLicensePhoto().isEmpty()) {
         if (shop.getShopLicensePhoto() != null) {
             cloudinaryService.delete(shop.getShopLicensePhoto());
         }
         shop.setShopLicensePhoto(uploadIfPresent(req.shopLicensePhoto()));
     }
     log.info("Shop updated successfully → Shop ID: {}", shop.getId());
     return shopRepository.save(shop);
 }
 
// ===================== DELETE MY SHOP =====================
 public void deleteMyShop(User user) {

	 if (user == null) {
         throw new AuthenticationFailedException("User must be authenticated to delete shop");
     }

     if (!isVendor(user)) {
         throw new ForbiddenException("Only vendors can delete shop");
     }

     Shop shop = shopRepository.findByUser(user)
             .orElseThrow(() -> new ResourceNotFoundException("You do not have a registered shop"));     // 🔥 Delete images from Cloudinary
   
     
     if (shop.getShopPhoto() != null) {
         cloudinaryService.delete(shop.getShopPhoto());
     }
     if (shop.getShopCoverPhoto() != null) {
         cloudinaryService.delete(shop.getShopCoverPhoto());
     }
     if (shop.getShopLicensePhoto() != null) {
         cloudinaryService.delete(shop.getShopLicensePhoto());
     }

     shopRepository.delete(shop);
     log.info("Shop deleted successfully for user {} → Shop ID: {}", user.getId(), shop.getId());
 }


 // ===================== CLOUDINARY HELPER =====================
// private String uploadIfPresent(MultipartFile file) throws IOException {
//     if (file == null || file.isEmpty()) {
//         return null;
//     }
//     return cloudinaryService.upload(file); // 🔥 ONLY CLOUDINARY
// }
 private String uploadIfPresent(MultipartFile file) {
     if (file == null || file.isEmpty()) {
         return null;
     }
     try {
         return cloudinaryService.upload(file);
     } catch (FileUploadException e) {
         throw e;
     } catch (Exception e) {
         log.error("Failed to upload shop image: {}", file.getOriginalFilename(), e);
         throw new FileUploadException("Failed to upload shop image: " + file.getOriginalFilename(), e);
     }
 }

 // ===================== GET MY SHOP =====================
 public ShopResponse getMyShop(User user) {
	 if (user == null) {
         throw new AuthenticationFailedException("User must be authenticated");
     }
     return shopRepository.findByUser(user)
             .map(this::toResponse)
             .orElse(null);
 }

 // ===================== GET ALL APPROVED SHOPS =====================
 public List<ShopResponse> getAllShops() {
     return shopRepository.findAll().stream()
             .filter(shop -> shop.isApproved() && shop.isActive())
             .map(this::toResponse)
             .toList();
 }

 // ===================== CHECK VENDOR =====================
 private boolean isVendor(User user) {
     return user.getRole() != null && VENDOR_ROLES.contains(user.getRole().getName());
 }

 // ===================== MAP TO RESPONSE =====================
 public ShopResponse toResponse(Shop s) {
     User u = s.getUser();

     return new ShopResponse(
             s.getId(),
             s.getShopName(),
             s.getShopType(),
             s.getShopAddress(),
             s.getShopPhoto(),
             s.getShopCoverPhoto(),
             s.getShopLicensePhoto(),
             s.getWorkingHoursJson(),
             s.getShopDescription(),
             s.getShopLicense(),
             s.isApproved(),
             s.isActive(),
             u.getId(),
             u.getName(),
             u.getPhone(),
             u.getEmail(),
             u.getRole().getName(),
             u.getPhotoUrl(),
             s.getOpensAt(),
             s.getClosesAt()
     );
 }

 
 
 //------------
 
 
//Add these methods to your existing ShopService class

public List<ShopResponse> getPopularShops() {
  return shopRepository.findPopularShops()
      .stream()
      .limit(20)
      .map(this::toResponse)
      .toList();
}

public List<ShopResponse> getTop10PopularShops() {
  return shopRepository.findPopularShops()
      .stream()
      .limit(10)
      .map(this::toResponse)
      .toList();
}



@Transactional
public Shop createOrUpdateShop(ShopRequest req, User user) throws IOException {
    // Only vendors allowed
    if (!isVendor(user)) {
        throw new IllegalStateException("Only vendors can create/update shop");
    }

    Optional<Shop> existing = shopRepository.findByUser(user);

    Shop shop = existing.orElseGet(() -> {
        Shop newShop = new Shop();
        newShop.setUser(user);
        newShop.setApproved(false);     // still needs admin approval
        newShop.setActive(true);
        return newShop;
    });

    // Update / set fields
    if (req.shopName()        != null) shop.setShopName(req.shopName());
    if (req.shopType()        != null) shop.setShopType(req.shopType());
    if (req.shopAddress()     != null) shop.setShopAddress(req.shopAddress());
    if (req.workingHoursJson()    != null) shop.setWorkingHoursJson(req.workingHoursJson());
    if (req.shopDescription() != null) shop.setShopDescription(req.shopDescription());
    if (req.shopLicense()     != null) shop.setShopLicense(req.shopLicense());
    if (req.opensAt()         != null) shop.setOpensAt(req.opensAt());
    if (req.closesAt()        != null) shop.setClosesAt(req.closesAt());

    // Images — replace only if sent
    if (req.shopPhoto() != null && !req.shopPhoto().isEmpty()) {
        if (shop.getShopPhoto() != null) cloudinaryService.delete(shop.getShopPhoto());
        shop.setShopPhoto(cloudinaryService.upload(req.shopPhoto()));
    }
    // same for cover & license photo...

    return shopRepository.save(shop);
}

private ShopSummaryDTO toSummary(Shop shop) {

    if (shop == null) {
        return null;
    }

    Boolean open = isShopOpenFromWorkingHours(shop);

    return new ShopSummaryDTO(
            shop.getId(),                         // Long shopId
            shop.getShopName(),                   // String shopName
            shop.getShopPhoto(),                  // String shopPhoto
            shop.getShopAddress(),                // String shopAddress
            shop.getShopType(),                   // String shopType
            extractCity(shop.getShopAddress()),   // String city
            extractPincode(shop.getShopAddress()),// String pincode
            0.0,                                  // Double rating (future-ready)
            open                                  // Boolean open
    );
}
private Boolean isShopOpenFromWorkingHours(Shop shop) {
    if (shop == null || shop.getWorkingHoursJson() == null || shop.getWorkingHoursJson().trim().isEmpty()) {
        System.out.println("Shop " + (shop != null ? shop.getId() : "null") + " has no working hours JSON");
        return false;
    }

    try {
        ObjectMapper mapper = new ObjectMapper();
        List<WorkingHourDTO> hours = mapper.readValue(
                shop.getWorkingHoursJson(),
                new TypeReference<List<WorkingHourDTO>>() {}
        );

        if (hours.isEmpty()) {
            System.out.println("Shop " + shop.getId() + " has empty working hours list");
            return false;
        }

        String today = LocalDate.now(ZoneId.of("Asia/Kolkata"))
                .getDayOfWeek()
                .name();  // MONDAY, TUESDAY, ...

        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));

        // Debug info (you can switch to proper logger later)
        System.out.println("Shop ID: " + shop.getId() +
                " | Today: " + today +
                " | Current time: " + now);

        System.out.println("Working hours JSON: " + shop.getWorkingHoursJson());

        for (WorkingHourDTO h : hours) {
            String storedDay = h.getDay();
            if (storedDay == null || storedDay.trim().isEmpty()) {
                continue;
            }

            String normalizedDay = storedDay.trim().toUpperCase();

            System.out.println("  Checking day: " + storedDay + " → normalized: " + normalizedDay);

            if (today.equals(normalizedDay)) {
                String openStr = h.getOpen();
                String closeStr = h.getClose();

                if (openStr == null || closeStr == null || openStr.trim().isEmpty() || closeStr.trim().isEmpty()) {
                    System.out.println("  Invalid time format for " + normalizedDay);
                    continue;
                }

                LocalTime openTime = LocalTime.parse(openStr.trim());
                LocalTime closeTime = LocalTime.parse(closeStr.trim());

                System.out.println("  → Found matching day: " + normalizedDay +
                        " | Open: " + openTime +
                        " | Close: " + closeTime +
                        " | Now: " + now);

                // Normal opening hours (open < close)
                if (closeTime.isAfter(openTime)) {
                    boolean isOpen = !now.isBefore(openTime) && !now.isAfter(closeTime);
                    System.out.println("  → Normal hours → isOpen = " + isOpen);
                    return isOpen;
                }
                // Overnight hours (e.g. 22:00 – 06:00)
                else {
                    boolean isOpen = !now.isBefore(openTime) || !now.isAfter(closeTime);
                    System.out.println("  → Overnight hours → isOpen = " + isOpen);
                    return isOpen;
                }
            }
        }

        System.out.println("No matching day found for shop " + shop.getId() + " (today is " + today + ")");
        return false;

    } catch (JsonProcessingException e) {
        System.err.println("Invalid JSON format for shop " + shop.getId() + ": " + e.getMessage());
        return false;
    } catch (DateTimeParseException e) {
        System.err.println("Time parsing error for shop " + shop.getId() + ": " + e.getMessage());
        return false;
    } catch (Exception e) {
        System.err.println("Unexpected error checking shop " + shop.getId() + ": " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// ===================== SEARCH =====================
public Page<ShopSummaryDTO> searchShops(ShopSearchDTO filter) {

    PageRequest pageable = PageRequest.of(
            filter.page(),
            filter.size()
    );

    return shopRepository.searchShops(
            filter.keyword(),
            filter.city(),
            filter.pincode(),
            pageable
    ).map(this::toSummary);
}

// ===================== OPEN / CLOSE =====================
//private boolean isShopOpen(LocalTime opensAt, LocalTime closesAt) {
//    if (opensAt == null || closesAt == null) return false;
//    LocalTime now = LocalTime.now();
//    return now.isAfter(opensAt) && now.isBefore(closesAt);
//}
private boolean isShopOpen(LocalTime opensAt, LocalTime closesAt) {

    if (opensAt == null || closesAt == null) {
        return false;
    }

    LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));

    // Normal same-day shop (09:00 → 18:00)
    if (closesAt.isAfter(opensAt)) {
        return !now.isBefore(opensAt) && !now.isAfter(closesAt);
    }

    // Overnight shop (20:00 → 02:00)
    return !now.isBefore(opensAt) || !now.isAfter(closesAt);
}


// ===================== ADDRESS =====================
private String extractCity(String address) {
    if (address == null || address.isBlank()) return null;
    return address;
}

private String extractPincode(String address) {
    return null;
}

// ===================== POPULAR SHOPS (🔥 FIXED) =====================
public List<ShopResponse> getPopularShopsPaginated(int page, int size) {
    Pageable pageable = PageRequest.of(page, size); // ✅ FIXED
    return shopRepository.findPopularShops(pageable)
            .stream()
            .map(this::toResponse)
            .toList();
}
}