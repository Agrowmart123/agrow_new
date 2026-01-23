


package com.agrowmart.controller;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.agrowmart.dto.auth.*;
import com.agrowmart.entity.User;
import com.agrowmart.service.AuthService;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    // 1. Simple Register
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<User> register(@Valid RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }
    
    
    
 //======   
//    // 2. Complete Profile (after register)
//    @PutMapping(value = "/complete-profile", consumes = "multipart/form-data")
//    public ResponseEntity<User> completeProfile(
//    		
//            @RequestParam(required = false) String businessName,
//            @RequestParam(required = false) String address,
//            @RequestParam(required = false) String city,
//            @RequestParam(required = false) String state,
//            @RequestParam(required = false) String country,
//            @RequestParam(required = false) String postalCode,
//            
//            
//            
//            @RequestParam(required = false) String aadhaarNumber,
//            @RequestParam(required = false) String panNumber,
//            
//
//            @RequestParam(required = false) String udyamRegistrationNumber,
//            
//             
//            
//            @RequestParam(required = false) String gstCertificateNumber,
//            @RequestParam(required = false) String tradeLicenseNumber,
//            @RequestParam(required = false) String fssaiLicenseNumber,
//            @RequestParam(required = false) String bankName,
//            @RequestParam(required = false) String accountHolderName,
//            @RequestParam(required = false) String bankAccountNumber,
//            @RequestParam(required = false) String ifscCode,
//            @RequestParam(required = false) String upiId,
//        
//            @RequestPart(required = false) MultipartFile fssaiLicenseFile,
//            @RequestPart(required = false) MultipartFile photo,
//            
//            @RequestPart(required = false) MultipartFile aadhaarImage,
//            @RequestPart(required = false) MultipartFile panImage,
//            @RequestPart(required = false) MultipartFile udyamRegistrationImage,
//            @AuthenticationPrincipal User currentUser
//    ) {
//        if (currentUser == null) return ResponseEntity.status(401).build();
//        CompleteProfileRequest req = new CompleteProfileRequest(
//                businessName, address, city, state, country, postalCode,
//                aadhaarNumber, panNumber,udyamRegistrationNumber, gstCertificateNumber, tradeLicenseNumber, fssaiLicenseNumber,
//                bankName, accountHolderName, bankAccountNumber, ifscCode, upiId,
//                fssaiLicenseFile, photo,aadhaarImage,panImage,udyamRegistrationImage
//        );
//        return ResponseEntity.ok(authService.completeProfile(req, currentUser));
//    }
    
    
    
    @PutMapping(value = "/complete-profile", consumes = "multipart/form-data")
    public ResponseEntity<User> completeProfile(
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

            // ── Files ───────────────────────────────────────────────
          
            @RequestPart(value = "fssaiLicenseFile", required = false) MultipartFile fssaiLicenseFile,
            @RequestPart(value = "photo", required = false) MultipartFile photo,

            @RequestPart(value = "aadhaarImage", required = false) MultipartFile aadhaarImage,
            @RequestPart(value = "panImage", required = false) MultipartFile panImage,
            @RequestPart(value = "udyamRegistrationImage", required = false) MultipartFile udyamRegistrationImage,

            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) return ResponseEntity.status(401).build();

        CompleteProfileRequest req = new CompleteProfileRequest(
                businessName, address, city, state, country, postalCode,
                aadhaarNumber, panNumber, udyamRegistrationNumber,
                gstCertificateNumber, tradeLicenseNumber, fssaiLicenseNumber,
                bankName, accountHolderName, bankAccountNumber, ifscCode, upiId,
                 fssaiLicenseFile, photo,
                aadhaarImage, panImage, udyamRegistrationImage
        );

        return ResponseEntity.ok(authService.completeProfile(req, currentUser));
    }
    
    
    
    // 3. Update Profile (NEW API - FULLY WORKING)
    @PutMapping(value = "/update-profile", consumes = "multipart/form-data")
    public ResponseEntity<User> updateProfile(
            @RequestParam(required = false) String businessName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String aadhaarNumber,
            @RequestParam(required = false) String panNumber,
            
            @RequestParam(required = false) String udyamRegistrationNumber,
            
            
            
            
            @RequestParam(required = false) String gstCertificateNumber,
            @RequestParam(required = false) String tradeLicenseNumber,
            @RequestParam(required = false) String fssaiLicenseNumber,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String accountHolderName,
            @RequestParam(required = false) String bankAccountNumber,
            @RequestParam(required = false) String ifscCode,
            @RequestParam(required = false) String upiId,
       
            @RequestPart(required = false) MultipartFile fssaiLicenseFile,
            @RequestPart(required = false) MultipartFile photo,
            
            @RequestPart(required = false) MultipartFile aadhaarImage,
            @RequestPart(required = false) MultipartFile panImage,
            @RequestPart(required = false) MultipartFile udyamRegistrationImage,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) return ResponseEntity.status(401).build();
        UpdateProfileRequest req = new UpdateProfileRequest(
                businessName, address, city, state, country, postalCode,
                aadhaarNumber, panNumber,udyamRegistrationNumber, gstCertificateNumber, tradeLicenseNumber, fssaiLicenseNumber,
                bankName, accountHolderName, bankAccountNumber, ifscCode, upiId,
                fssaiLicenseFile, photo,aadhaarImage,panImage,udyamRegistrationImage
        );
        return ResponseEntity.ok(authService.updateProfile(req, currentUser));
    }
    // 4. Login
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        String password = body.get("password");
        String fcm = body.get("fcmToken");
        if (login == null || password == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(authService.login(new LoginRequest(login, password), fcm));
    }
    // 5. OTP Endpoints
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody OtpRequest r) {
        authService.sendOtp(r);
        return ResponseEntity.ok("OTP sent");
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpRequest r) {
        authService.verifyOtp(r);
        return ResponseEntity.ok("Success");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        if (phone == null) return ResponseEntity.badRequest().body("Phone required");
        authService.forgotPassword(phone);
        return ResponseEntity.ok("OTP sent");
    }
    // 6. Get Current User (UPDATED with percentage and onlineStatus)
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
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

                // --- TIMESTAMPS ---
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
                user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null
        );

        return ResponseEntity.ok(profile);
    }

    
// // 6. Get Current User - SAFE VERSION (Masked KYC + Bank Account)
//    @GetMapping("/me")
//    public ResponseEntity<SafeProfileResponse> me(@AuthenticationPrincipal User user) {
//        if (user == null) {
//            return ResponseEntity.status(401).build();
//        }
//        int percentage = authService.calculateProfileCompletion(user);
//        SafeProfileResponse response = new SafeProfileResponse(user, percentage);
//        return ResponseEntity.ok(response);
//    }
    
    
    // 7. Upload Photo Only
    @PostMapping(value = "/upload-photo", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadPhoto(
            @RequestParam("photo") MultipartFile file,
            @AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(authService.uploadProfilePhoto(file, user));
    }
    // NEW: Get Vendor Online/Offline Status
    @GetMapping("/status")
    public ResponseEntity<String> getStatus(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(currentUser.getOnlineStatus());
    }
    
    
    // NEW: Update Vendor Online/Offline Status (only if profile complete)
    @PutMapping("/status")
    public ResponseEntity<String> updateStatus(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) return ResponseEntity.status(401).build();
        if (!"YES".equals(currentUser.getProfileCompleted())) {
            return ResponseEntity.badRequest().body("Please complete your profile 100% first");
        }
        String newStatus = body.get("status");
        if (newStatus == null || (!newStatus.equalsIgnoreCase("ONLINE") && !newStatus.equalsIgnoreCase("OFFLINE"))) {
            return ResponseEntity.badRequest().body("Invalid status: must be 'ONLINE' or 'OFFLINE'");
        }
        currentUser.setOnlineStatus(newStatus.toUpperCase());
        authService.save(currentUser);
        return ResponseEntity.ok(currentUser.getOnlineStatus());
    }
    
    
 //--------------------------
    
// // 2. Admin Only: View FULL KYC of any vendor (temporary 30 seconds)
//    @GetMapping("/admin/vendor/{vendorId}/full-kyc")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public ResponseEntity<FullKycResponse> getFullKyc(
//            @PathVariable Long vendorId,
//            @AuthenticationPrincipal User admin) {
//
//        User vendor = userRepository.findById(vendorId)
//                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
//
//        return ResponseEntity.ok(new FullKycResponse(vendor));
//    }
//
//    // Optional: Admin list all vendors (masked)
//    @GetMapping("/admin/vendors")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public ResponseEntity<List<SafeProfileResponse>> getAllVendors() {
//        List<User> vendors = userRepository.findAll(); // or filter by vendor roles
//        List<SafeProfileResponse> response = vendors.stream()
//                .map(v -> new SafeProfileResponse(v, authService.calculateProfileCompletion(v)))
//                .toList();
//        return ResponseEntity.ok(response);
//    }
//    
}