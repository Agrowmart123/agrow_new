package com.agrowmart.controller;

import com.agrowmart.admin_seller_management.enums.AccountStatus;
import com.agrowmart.dto.auth.*;
import com.agrowmart.dto.auth.shop.ShopRequest;
import com.agrowmart.entity.Shop;
import com.agrowmart.entity.User;
import com.agrowmart.exception.AuthExceptions.AuthenticationFailedException;
import com.agrowmart.service.AuthService;
import com.agrowmart.service.ShopService;

import jakarta.validation.Valid;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final ShopService shopService;

    public AuthController(AuthService authService,ShopService shopService) {
        this.authService = authService;
        this.shopService=shopService;
    }

    // ──────────────────────────────────────────────
    // 1. Register (Vendor)
    // ──────────────────────────────────────────────
    // 1. Simple Register
 
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<User> register(@Valid RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }
    

    
    @PutMapping(value = "/complete-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> completeProfile(
            // ── Profile fields ───────────────────────────────────────────────
            @RequestPart(value = "businessName", required = false) String businessName,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart(value = "city", required = false) String city,
            @RequestPart(value = "state", required = false) String state,
            @RequestPart(value = "country", required = false) String country,
            @RequestPart(value = "postalCode", required = false) String postalCode,
            @RequestPart(value = "aadhaarNumber", required = false) String aadhaarNumber,
            @RequestPart(value = "panNumber", required = false) String panNumber,
            @RequestPart(value = "udyamRegistrationNumber", required = false) String udyamRegistrationNumber,
            @RequestPart(value = "gstCertificateNumber", required = false) String gstCertificateNumber,
            @RequestPart(value = "tradeLicenseNumber", required = false) String tradeLicenseNumber,
            @RequestPart(value = "fssaiLicenseNumber", required = false) String fssaiLicenseNumber,
            @RequestPart(value = "bankName", required = false) String bankName,
            @RequestPart(value = "accountHolderName", required = false) String accountHolderName,
            @RequestPart(value = "bankAccountNumber", required = false) String bankAccountNumber,
            @RequestPart(value = "ifscCode", required = false) String ifscCode,
            @RequestPart(value = "upiId", required = false) String upiId,

            // ── Files ─────────────────────────────────────────────────────────
            @RequestPart(value = "fssaiLicenseFile", required = false) MultipartFile fssaiLicenseFile,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "aadhaarImage", required = false) MultipartFile aadhaarImage,
            @RequestPart(value = "panImage", required = false) MultipartFile panImage,
            @RequestPart(value = "udyamRegistrationImage", required = false) MultipartFile udyamRegistrationImage,
            @RequestPart(value = "kycConsentGiven", required = true)
            String kycConsentGivenStr,  // ← NEW required field
            // ── Shop fields (all optional) ────────────────────────────────────
            @RequestPart(value = "shopName", required = false) String shopName,
            @RequestPart(value = "shopType", required = false) String shopType,
            @RequestPart(value = "shopAddress", required = false) String shopAddress,
         // ← YE NAYA ADD KAR
            @RequestPart(value = "workingHoursJson", required = false) String workingHoursJson,
            
            
            @RequestPart(value = "shopDescription", required = false) String shopDescription,
            @RequestPart(value = "shopLicense", required = false) String shopLicense,
            @RequestPart(value = "shopPhoto", required = false) MultipartFile shopPhoto,
            @RequestPart(value = "shopCoverPhoto", required = false) MultipartFile shopCoverPhoto,
            @RequestPart(value = "shopLicensePhoto", required = false) MultipartFile shopLicensePhoto,
            @RequestPart(value = "opensAt", required = false) String opensAt,
            @RequestPart(value = "closesAt", required = false) String closesAt,

            @AuthenticationPrincipal User currentUser) throws IOException {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
     // ── Safe parsing of consent ────────────────────────────────────────
        boolean kycConsentGiven = false;

        if (kycConsentGivenStr != null) {
            String cleaned = kycConsentGivenStr.trim().toLowerCase();

            // Accept common true values
            if ("true".equals(cleaned) || 
                "1".equals(cleaned) || 
                "yes".equals(cleaned) || 
                "on".equals(cleaned)) {
                kycConsentGiven = true;
            }
            // Explicitly reject false values if you want (optional)
            // else if ("false".equals(cleaned) || "0".equals(cleaned) || "no".equals(cleaned)) {
            //     kycConsentGiven = false;
            // }
        }

        if (!kycConsentGiven) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "You must confirm that the submitted documents are genuine and belong to you."
            );
        }
        // Build DTO for profile update
        CompleteProfileRequest profileReq = new CompleteProfileRequest(
                businessName, address, city, state, country, postalCode,
                aadhaarNumber, panNumber, udyamRegistrationNumber,
                gstCertificateNumber, tradeLicenseNumber, fssaiLicenseNumber,
                bankName, accountHolderName, bankAccountNumber, ifscCode, upiId,
                fssaiLicenseFile, photo, aadhaarImage, panImage, udyamRegistrationImage,
                kycConsentGiven,

                // Shop fields passed to record
                shopName, shopType, shopAddress, workingHoursJson, shopDescription,
                shopLicense, shopPhoto, shopCoverPhoto, shopLicensePhoto,
                opensAt, closesAt
        );

        // Update user profile
        User updatedUser = authService.completeProfile(profileReq, currentUser);

        // ── Optional: Create or update shop if any shop-related field was provided ──
        boolean hasShopData = shopName != null || shopType != null || shopAddress != null ||
        		workingHoursJson != null || shopLicense != null ||
                              shopPhoto != null || shopCoverPhoto != null || shopLicensePhoto != null ||
                              opensAt != null || closesAt != null;

        if (hasShopData) {
            LocalTime openTime  = opensAt  != null && !opensAt.isBlank()  ? LocalTime.parse(opensAt.trim())  : null;
            LocalTime closeTime = closesAt != null && !closesAt.isBlank() ? LocalTime.parse(closesAt.trim()) : null;

            ShopRequest shopReq = new ShopRequest(
                    shopName,
                    shopType,
                    shopAddress,
                    workingHoursJson,
                    shopDescription != null ? shopDescription.trim() : "",
                    shopLicense,
                    openTime,
                    closeTime,
                    shopPhoto,
                    shopCoverPhoto,
                    shopLicensePhoto
            );

            shopService.createOrUpdateShop(shopReq, updatedUser);
        }

        // Final response
        return ResponseEntity.ok(updatedUser);
    }
    // ──────────────────────────────────────────────
    // 3. Update Profile (PATCH - partial + photo)
    // ──────────────────────────────────────────────
    @PatchMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateProfile(
            @RequestPart(value = "businessName", required = false) String businessName,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart(value = "city", required = false) String city,
            @RequestPart(value = "state", required = false) String state,
            @RequestPart(value = "country", required = false) String country,
            @RequestPart(value = "postalCode", required = false) String postalCode,
            @RequestPart(value = "aadhaarNumber", required = false) String aadhaarNumber,
            @RequestPart(value = "panNumber", required = false) String panNumber,
            @RequestPart(value = "udyamRegistrationNumber", required = false) String udyamRegistrationNumber,
            @RequestPart(value = "gstCertificateNumber", required = false) String gstCertificateNumber,
            @RequestPart(value = "tradeLicenseNumber", required = false) String tradeLicenseNumber,
            @RequestPart(value = "fssaiLicenseNumber", required = false) String fssaiLicenseNumber,
            @RequestPart(value = "bankName", required = false) String bankName,
            @RequestPart(value = "accountHolderName", required = false) String accountHolderName,
            @RequestPart(value = "bankAccountNumber", required = false) String bankAccountNumber,
            @RequestPart(value = "ifscCode", required = false) String ifscCode,
            @RequestPart(value = "upiId", required = false) String upiId,
            @RequestPart(value = "fssaiLicenseFile", required = false) MultipartFile fssaiLicenseFile,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "aadhaarImage", required = false) MultipartFile aadhaarImage,
            @RequestPart(value = "panImage", required = false) MultipartFile panImage,
            @RequestPart(value = "udyamRegistrationImage", required = false) MultipartFile udyamRegistrationImage,
            @AuthenticationPrincipal User currentUser) throws FileUploadException {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UpdateProfileRequest req = new UpdateProfileRequest(
                businessName, address, city, state, country, postalCode,
                aadhaarNumber, panNumber, udyamRegistrationNumber,
                gstCertificateNumber, tradeLicenseNumber, fssaiLicenseNumber,
                bankName, accountHolderName, bankAccountNumber, ifscCode, upiId,
                fssaiLicenseFile, photo, aadhaarImage, panImage, udyamRegistrationImage
        );

        User updated = authService.updateProfile(req, currentUser);
        return ResponseEntity.ok(updated);
    }

    // ──────────────────────────────────────────────
    // 4. Login (Vendor/Farmer)
    // ──────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody Map<String, String> body) {
        String loginInput = body.get("login");
        String password = body.get("password");
        String fcmToken = body.get("fcmToken");

        if (loginInput == null || password == null) {
            return ResponseEntity.badRequest().body(null);
        }

        LoginRequest req = new LoginRequest(loginInput, password);
        JwtResponse response = authService.login(req, fcmToken);
        return ResponseEntity.ok(response);
    }

    // ──────────────────────────────────────────────
    // 5. Send OTP (Vendor/Farmer)
    // ──────────────────────────────────────────────
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody OtpRequest req) throws Exception {
        try {
            authService.sendOtp(req);
            return ResponseEntity.ok("OTP sent successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    // ──────────────────────────────────────────────
    // 6. Verify OTP (Vendor/Farmer)
    // ──────────────────────────────────────────────
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
        try {
            authService.verifyOtp(req);
            return ResponseEntity.ok("OTP verified successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ──────────────────────────────────────────────
    // 7. Forgot Password
    // ──────────────────────────────────────────────
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) throws Exception {
        String phone = body.get("phone");
        if (phone == null) {
            return ResponseEntity.badRequest().body("Phone is required");
        }
        authService.forgotPassword(phone);
        return ResponseEntity.ok("OTP sent for password reset");
    }

    // ──────────────────────────────────────────────
    // 8. Get Current User (Vendor/Farmer) - Masked KYC
    // ──────────────────────────────────────────────
    // 6. Get Current User (UPDATED with percentage and onlineStatus)
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal User user) {
    	if (user == null) {
            throw new AuthenticationFailedException("Not authenticated");
        }
        int percentage = authService.calculateProfileCompletion(user);
        boolean isProfileCompleted = "YES".equalsIgnoreCase(user.getProfileCompleted());

        String aadhaarStatusStr = user.getAadhaarStatus() != null
                ? user.getAadhaarStatus().name()
                : "NOT_PROVIDED";

        String panStatusStr = user.getPanStatus() != null
                ? user.getPanStatus().name()
                : "NOT_PROVIDED";

        String udhyamStatusStr = user.getUdhyamStatus() != null
                ? user.getUdhyamStatus().name()
                : "NOT_PROVIDED";

        String roleName = user.getRole() != null
                ? user.getRole().getName()
                : "UNKNOWN";

        
     // ── Extract shop data safely (null if no shop) ───────────────────────
        Shop shop = user.getShop();
        
        MeResponse profile = new MeResponse(
        		
        		
                // --- BASIC INFO ---
                user.getId(),
                user.getUuid(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.isPhoneVerified(),
                user.getAddress(),
                user.getCity(),
                user.getState(),
                user.getCountry(),
                user.getPostalCode(),
                user.getBusinessName(),

                // --- KYC NUMBERS ---
                user.getUdyamRegistrationNumber(),
                user.getGstCertificateNumber(),
                user.getTradeLicenseNumber(),
                user.getFssaiLicenseNumber(),

                // --- DOCUMENT IMAGES ---
                user.getAadhaarImagePath(),
                user.getPanImagePath(),
                user.getUdyamRegistrationImagePath(),
                user.getFssaiLicensePath(),

                // --- BANK DETAILS ---
                user.getBankName(),
                user.getAccountHolderName(),
                user.getIfscCode(),
                user.getUpiId(),

                // ✅ ROLE NAME (CORRECT POSITION)
                roleName,

                // --- PROFILE ---
                user.getPhotoUrl(),
                user.getOnlineStatus() != null ? user.getOnlineStatus() : "OFFLINE",


                // --- PROFILE COMPLETION ---
                isProfileCompleted,
                user.getProfileCompleted(),
                percentage,

                // --- DOCUMENT STATUS ---
                aadhaarStatusStr,
                panStatusStr,
                udhyamStatusStr,

                
             // ── Shop fields (null-safe) ────────────────────────────────
                shop != null ? shop.getShopName()         : null,
                shop != null ? shop.getShopType()         : null,
                shop != null ? shop.getShopAddress()      : null,
                shop != null ? shop.getShopPhoto()        : null,
                shop != null ? shop.getShopCoverPhoto()   : null,
                shop != null ? shop.getShopLicensePhoto() : null,
                		// Changed
                 shop != null ? shop.getWorkingHoursJson() : null,   // ← fix here
                shop != null ? shop.getShopDescription()  : null,
                shop != null ? shop.getShopLicense()      : null,
                shop != null ? shop.getOpensAt()          : null,
                shop != null ? shop.getClosesAt()         : null,
                		shop != null ? shop.isApproved() : false,    // or true — your business rule
                				shop != null ? shop.isActive()   : false,

                user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
                user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null
                
                
        );

        return ResponseEntity.ok(profile);
    }


    // ──────────────────────────────────────────────
    // 9. Upload Profile Photo (Vendor/Farmer)
    // ──────────────────────────────────────────────
    @PostMapping(value = "/upload-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhoto(
            @RequestParam("photo") MultipartFile file,
            @AuthenticationPrincipal User user) throws FileUploadException {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String photoUrl = authService.uploadProfilePhoto(file, user);
        return ResponseEntity.ok(photoUrl);
    }

    // ──────────────────────────────────────────────
    // 10. Update Online/Offline Status (Vendor only)
    // ──────────────────────────────────────────────
//    @PutMapping("/status")
//    public ResponseEntity<String> updateStatus(
//            @RequestBody Map<String, String> body,
//            @AuthenticationPrincipal User user) {
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        if (!"YES".equalsIgnoreCase(user.getProfileCompleted())) {
//            return ResponseEntity.badRequest().body("Please complete your profile first");
//        }
//
//        String newStatus = body.get("status");
//        if (newStatus == null || (!"ONLINE".equalsIgnoreCase(newStatus) && !"OFFLINE".equalsIgnoreCase(newStatus))) {
//            return ResponseEntity.badRequest().body("Status must be 'ONLINE' or 'OFFLINE'");
//        }
//
//        user.setOnlineStatus(newStatus.toUpperCase());
//        authService.save(user);
//        return ResponseEntity.ok(user.getOnlineStatus());
//    }
//    
//    
//   
    
    
    
    
    @PutMapping("/status")
    public ResponseEntity<?> updateOnlineStatus(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String newStatus = body.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().body("status field is required");
        }

        String requested = newStatus.trim().toUpperCase();
        if (!"ONLINE".equals(requested) && !"OFFLINE".equals(requested)) {
            return ResponseEntity.badRequest().body("Status must be ONLINE or OFFLINE");
        }

        // ─── Very important validations ─────────────────────────────────────
        if ("ONLINE".equals(requested)) {

            // 1. Profile must be completed
            if (!"YES".equalsIgnoreCase(user.getProfileCompleted())) {
                return ResponseEntity.badRequest()
                        .body("Please complete your profile first");
            }

            // 2. Admin must have approved the account
            if (user.getAccountStatus() != AccountStatus.APPROVED) {
                String msg = switch (user.getAccountStatus()) {
                    case PENDING  -> "Your account is still under review. Please wait for admin approval.";
                    case REJECTED -> "Your account was rejected. Reason: " + user.getStatusReason();
                    case BLOCKED  -> "Your account is blocked. Contact support.";
                    default       -> "Your account is not approved yet.";
                };
                return ResponseEntity.status(403).body(msg);
            }

            // 3. (Strongly recommended) Shop must be approved
            Shop shop = user.getShop();
            if (shop == null || !shop.isApproved()) {
                return ResponseEntity.status(403)
                        .body("Your shop must be approved by admin before going online.");
            }
        }

        // If we reached here → allowed
        user.setOnlineStatus(requested);
        authService.save(user);   // or authService.save(user)

        return ResponseEntity.ok(Map.of(
                "status", user.getOnlineStatus(),
                "message", "Status updated successfully"
        ));
    }
    
    
 // In AuthController    
    @DeleteMapping("/profile/delete")
    @PreAuthorize("hasRole('VENDOR')")   // only the vendor themselves can delete their own profile
    public ResponseEntity<String> softDeleteMyProfile(
            @AuthenticationPrincipal User currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in to perform this action");
        }

        try {
            // Call the service method we prepared earlier
            authService.softDeleteVendor(currentUser, currentUser);  // self-deletion

            return ResponseEntity.ok(
                "Your profile has been deactivated.\n" +
                "All your products are now set to INACTIVE.\n" +
                "Permanent deletion of your account will happen automatically after 7 days."
            );

        } catch (IllegalStateException e) {
            // Already deleted
            return ResponseEntity.status(HttpStatus.GONE)
                    .body("Your profile is already deleted.");
        } catch (Exception e) {
            // Log the error if you have logging
            System.err.println("Error during profile deletion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred. Please contact support.");
        }
    }
        @PostMapping("/profile/restore")
        @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<String> selfRestoreProfile(
                @AuthenticationPrincipal User currentUser) {

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (!currentUser.isDeleted()) {
                return ResponseEntity.badRequest().body("Your account is not deleted");
            }

            try {
                authService.restoreVendor(currentUser);
                return ResponseEntity.ok(
                    "Your account has been successfully restored.\n" +
                    "Your products are now ACTIVE again."
                );
            } catch (Exception e) {
                System.err.println("Restore failed: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Could not restore account at this time.");
            }
        
    }

    
    
    
}