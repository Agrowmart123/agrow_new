

package com.agrowmart.service;
import com.agrowmart.admin_seller_management.enums.AccountStatus;
import com.agrowmart.dto.auth.*;

import com.agrowmart.dto.auth.customer.CustomerRegisterRequest;
import com.agrowmart.dto.auth.farmer.FarmerProfileRequest;
import com.agrowmart.dto.auth.farmer.FarmerRegisterRequest;
import com.agrowmart.entity.*;
import com.agrowmart.entity.Product.ProductStatus;
import com.agrowmart.enums.OtpPurpose;
import com.agrowmart.enums.RoleName;
import com.agrowmart.exception.AuthExceptions.AuthenticationFailedException;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.AuthExceptions.DuplicateResourceException;
import com.agrowmart.exception.AuthExceptions.InvalidOtpException;
import com.agrowmart.repository.*;
import com.agrowmart.util.InMemoryOtpStore;
import com.agrowmart.util.RedisOtpStore;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final CloudinaryService cloudinaryService;
    private final FarmerProfileRepository farmerProfileRepo;
    private final Fast2SmsService fast2SmsService;  // NEW: Fast2SMS service
    private final RedisOtpStore redisOtpStore;
    private final ProductRepository productRepo;
    @Value("${file.upload-dir}") private String localUploadDir;
    public AuthService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       PasswordEncoder encoder,
                       JwtService jwtService,
                       CloudinaryService cloudinaryService,
                       FarmerProfileRepository farmerProfileRepo,
                       Fast2SmsService fast2SmsService ,
                       RedisOtpStore redisOtpStore,
                       ProductRepository productRepo
                       ) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.cloudinaryService = cloudinaryService;
        this.farmerProfileRepo = farmerProfileRepo;
        this.fast2SmsService = fast2SmsService;
        this.redisOtpStore = redisOtpStore;
        this.productRepo = productRepo;
      
    }
    @PostConstruct
    public void init() {
        new File(localUploadDir).mkdirs();
        fixLegacyPhoneNumbers();
        // Removed Twilio.init() — no longer needed
    }
    /* ------------------------------------------------- AUTO-FIX LEGACY PHONES ------------------------------------------------- */
    @Transactional
    public void fixLegacyPhoneNumbers() {
        List<User> users = userRepo.findAll();
        int fixed = 0;
        for (User user : users) {
            String phone = user.getPhone();
            if (phone != null && phone.length() == 10 && !phone.startsWith("+91")) {
                String normalized = "+91" + phone;
                // Use method that excludes the same user (existsByPhoneAndIdNot) is not applicable here
                // we'll check existence ignoring current user id
                if (!userRepo.existsByPhone(normalized)) {
                    user.setPhone(normalized);
                    userRepo.save(user);
                    System.out.println("AUTO-FIXED PHONE: " + phone + " → " + normalized);
                    fixed++;
                }
            }
        }
        if (fixed > 0) {
            System.out.println("TOTAL LEGACY PHONES FIXED: " + fixed);
        }
    }
    /* ------------------------------------------------- REGISTER ------------------------------------------------- */
    /* ------------------------------------------------- REGISTER ------------------------------------------------- */
    @Transactional
    public User register(RegisterRequest r) {
     String name = r.name() != null ? r.name().trim() : "";
        String email = r.email() != null ? r.email().trim().toLowerCase() : null; String phone = r.phone().trim();@NotBlank
        String password = r.password();
        String type = r.vendorType().trim().toUpperCase();
        
        
        
        if (userRepo.existsByEmail(email)) {
            throw new DuplicateResourceException("This email is already registered.");
        }

        String normalizedPhone = normalizePhone(phone);
        if (userRepo.existsByPhone(normalizedPhone)) {
            throw new DuplicateResourceException("This phone number is already registered.");
        }
        
        Role role = roleRepo.findByName(type)
            .orElseGet(() -> roleRepo.save(new Role(type)));
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(normalizedPhone);
        user.setPasswordHash(encoder.encode(password));
        user.setRole(role);
        user.setPhoneVerified(false);
        user.setProfileCompleted("NO");
        // NEW: Default onlineStatus to "OFFLINE"
        user.setOnlineStatus("OFFLINE");
        user.setAccountStatus(AccountStatus.PENDING);   // ← ADD THIS
        return userRepo.save(user);
    }
   
    
    
   
   
    @Transactional
    public User completeProfile(CompleteProfileRequest r, User user) throws FileUploadException {
        // Trim & clean every string
        Optional.ofNullable(r.businessName()).ifPresent(v -> user.setBusinessName(v.trim()));
        Optional.ofNullable(r.address()).ifPresent(v -> user.setAddress(v.trim()));
        Optional.ofNullable(r.city()).ifPresent(v -> user.setCity(v.trim()));
        Optional.ofNullable(r.state()).ifPresent(v -> user.setState(v.trim()));
        Optional.ofNullable(r.country()).ifPresent(v -> user.setCountry(v.trim()));
        Optional.ofNullable(r.postalCode()).ifPresent(v -> user.setPostalCode(v.trim()));
        Optional.ofNullable(r.aadhaarNumber()).ifPresent(v -> user.setAadhaarNumber(v.trim()));
        Optional.ofNullable(r.panNumber()).ifPresent(v -> user.setPanNumber(v.trim().toUpperCase()));
      
        
        Optional.ofNullable(r.udyamRegistrationNumber()).ifPresent(v -> user.setUdyamRegistrationNumber(v.trim()));
      
        
        Optional.ofNullable(r.gstCertificateNumber()).ifPresent(v -> user.setGstCertificateNumber(v.trim().toUpperCase()));
        Optional.ofNullable(r.tradeLicenseNumber()).ifPresent(v -> user.setTradeLicenseNumber(v.trim()));
        Optional.ofNullable(r.fssaiLicenseNumber()).ifPresent(v -> user.setFssaiLicenseNumber(v.trim()));
        Optional.ofNullable(r.bankName()).ifPresent(v -> user.setBankName(v.trim()));
        Optional.ofNullable(r.accountHolderName()).ifPresent(v -> user.setAccountHolderName(v.trim()));
        Optional.ofNullable(r.bankAccountNumber()).ifPresent(v -> user.setBankAccountNumber(v.trim()));
        Optional.ofNullable(r.ifscCode()).ifPresent(v -> user.setIfscCode(v.trim().toUpperCase()));
        Optional.ofNullable(r.upiId()).ifPresent(v -> user.setUpiId(v.trim().toLowerCase()));
        // Upload files safely
        try {
        	if (r.aadhaarImage() != null && !r.aadhaarImage().isEmpty()) {
                user.setAadhaarImagePath(cloudinaryService.upload(r.aadhaarImage()));
            }
            if (r.panImage() != null && !r.panImage().isEmpty()) {
                user.setPanImagePath(cloudinaryService.upload(r.panImage()));
            }
            if (r.udyamRegistrationImage() != null && !r.udyamRegistrationImage().isEmpty()) {
                user.setUdyamRegistrationImagePath(cloudinaryService.upload(r.udyamRegistrationImage()));
            }
         // NEW: Save KYC consent
            if (r.kycConsentGiven() != null && r.kycConsentGiven()) {
                user.setKycConsentGiven(true);
                user.setKycConsentGivenAt(LocalDateTime.now());
            } else {
                throw new IllegalArgumentException("KYC authenticity consent is required");
            }
            
            if (r.fssaiLicenseFile() != null && !r.fssaiLicenseFile().isEmpty())
                user.setFssaiLicensePath(cloudinaryService.upload(r.fssaiLicenseFile()));
            if (r.photo() != null && !r.photo().isEmpty())
                user.setPhotoUrl(cloudinaryService.upload(r.photo()));
        } catch (Exception e) {
            throw new FileUploadException("Upload failed: " + e.getMessage());
        }
        user.setProfileCompleted("YES");
        return userRepo.save(user);
    }
   
  //-----------------------------------
//    @Transactional
//    public User completeProfile(UpdateProfileRequest r, User user) throws IOException {
//        if (r.businessName() != null) user.setBusinessName(r.businessName());
//        if (r.address() != null) user.setAddress(r.address());
//        if (r.city() != null) user.setCity(r.city());
//        if (r.state() != null) user.setState(r.state());
//        if (r.country() != null) user.setCountry(r.country());
//        if (r.postalCode() != null) user.setPostalCode(r.postalCode());
//        if (r.aadhaarNumber() != null) user.setAadhaarNumber(r.aadhaarNumber());
//        if (r.panNumber() != null) user.setPanNumber(r.panNumber());
//        
//        if (r.udyamRegistrationNumber() != null) user.setUdyamRegistrationNumber(r.udyamRegistrationNumber());
//        
//        
//        if (r.gstCertificateNumber() != null) user.setGstCertificateNumber(r.gstCertificateNumber());
//        if (r.tradeLicenseNumber() != null) user.setTradeLicenseNumber(r.tradeLicenseNumber());
//        if (r.fssaiLicenseNumber() != null) user.setFssaiLicenseNumber(r.fssaiLicenseNumber());
//        if (r.bankName() != null) user.setBankName(r.bankName());
//        if (r.accountHolderName() != null) user.setAccountHolderName(r.accountHolderName());
//        if (r.bankAccountNumber() != null) user.setBankAccountNumber(r.bankAccountNumber());
//        if (r.ifscCode() != null) user.setIfscCode(r.ifscCode());
//        if (r.upiId() != null) user.setUpiId(r.upiId());
//        // Upload files
//        if (r.aadhaarImage() != null && !r.aadhaarImage().isEmpty()) {
//            user.setAadhaarImagePath(cloudinaryService.upload(r.aadhaarImage()));
//        }
//        if (r.panImage() != null && !r.panImage().isEmpty()) {
//            user.setPanImagePath(cloudinaryService.upload(r.panImage()));
//        }
//        if (r.udyamRegistrationImage() != null && !r.udyamRegistrationImage().isEmpty()) {
//            user.setUdyamRegistrationImagePath(cloudinaryService.upload(r.udyamRegistrationImage()));
//        }
//        if (r.fssaiLicenseFile() != null && !r.fssaiLicenseFile().isEmpty()) {
//            user.setFssaiLicensePath(cloudinaryService.upload(r.fssaiLicenseFile()));
//        }
//        if (r.photo() != null && !r.photo().isEmpty()) {
//            user.setPhotoUrl(cloudinaryService.upload(r.photo()));
//        }
//        user.setProfileCompleted("YES");
//        return userRepo.save(user);
//    }
       
    /* ------------------------------------------------- SEND OTP ------------------------------------------------- */
//    @Transactional
//    public void sendOtp(OtpRequest req) {
//        String normalizedPhone = normalizePhone(req.phone());
//        String code = String.format("%06d", new SecureRandom().nextInt(999999));
//        InMemoryOtpStore.saveOtp(
//                normalizedPhone,
//                code,
//                req.purpose(),
//                300 // seconds (5 minutes)
//        );
//        System.out.println("OTP SENT → " + normalizedPhone + " | Code: " + code + " | Purpose: " + req.purpose());
//     // Send via Fast2SMS (replaced Twilio)
//        fast2SmsService.sendOtp(normalizedPhone, code, req.purpose().name());
//    }
    
    @Transactional
    public void sendOtp(OtpRequest req) throws Exception {
        String normalizedPhone = normalizePhone(req.phone());

        redisOtpStore.sendOtp(normalizedPhone, req.purpose());
    }
    /* ------------------------------------------------- VERIFY OTP ------------------------------------------------- */
    @Transactional
    public void verifyOtp(VerifyOtpRequest req) {
        String normalizedPhone = normalizePhone(req.phone());
        OtpPurpose purpose = req.purpose();
      
        
        boolean valid = redisOtpStore.verifyOtp(normalizedPhone, req.code(), purpose);

        if (!valid) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }
        
        // Only fetch user when needed
        if (purpose == OtpPurpose.PHONE_VERIFY) {
            User user = userRepo.findByPhone(normalizedPhone).orElse(null);
            if (user != null) {
                user.setPhoneVerified(true);
                userRepo.save(user);
            }
        }
        if (purpose == OtpPurpose.FORGOT_PASSWORD) {
            // CRITICAL FIX: Throw error if user not found
            User user = userRepo.findByPhone(normalizedPhone)
                    .orElseThrow(() -> new IllegalArgumentException("No account found with this phone number"));
            if (req.newPassword() == null || req.newPassword().trim().isEmpty()) {
                throw new BusinessValidationException("New password is required");
            }
            if (req.newPassword().trim().length() < 6) {
                throw new BusinessValidationException("New password must be at least 6 characters");
            }
            user.setPasswordHash(encoder.encode(req.newPassword().trim()));
            userRepo.save(user);
            System.out.println("PASSWORD SUCCESSFULLY RESET for: " + normalizedPhone);
        }
    }
    /* ------------------------------------------------- NORMALIZE PHONE ------------------------------------------------- */
    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new BusinessValidationException("Phone cannot be empty");
        }
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.startsWith("91") && cleaned.length() > 10) {
            cleaned = cleaned.substring(cleaned.length() - 10);
        } else if (cleaned.startsWith("0") && cleaned.length() > 10) {
            cleaned = cleaned.substring(1);
        }
        if (!cleaned.matches("^[6-9]\\d{9}$")) {
            throw new BusinessValidationException("Invalid phone number format");
        }
        return "+91" + cleaned;
    }
    /* ------------------------------------------------- LOGIN ------------------------------------------------- */
    public JwtResponse login(LoginRequest req, String fcmTokenFromFrontend) {
        String input = req.login().trim();
        Optional<User> userOpt = userRepo.findByEmail(input);
        if (userOpt.isEmpty()) {
            String phone = normalizePhone(input);
            userOpt = userRepo.findByPhone(phone);
        }
        
        
        User user = userOpt.orElseThrow(() ->
        new AuthenticationFailedException("Invalid email/phone or password")
);

if (!encoder.matches(req.password(), user.getPasswordHash())) {
    throw new AuthenticationFailedException("Invalid email/phone or password");
}
        
        if (fcmTokenFromFrontend != null && !fcmTokenFromFrontend.trim().isEmpty()) {
            user.setFcmToken(fcmTokenFromFrontend.trim());
            userRepo.save(user);
            System.out.println("FCM Token saved for user: " + user.getName());
        }
    
        String token = jwtService.issueToken(user);
        return new JwtResponse(token, null, LocalDateTime.now().plusDays(7));
    }
    public JwtResponse login(LoginRequest req) {
        return login(req, null);
    }
   
   
 //---------------------------------------------------
    private String normalizePhoneTo10Digits(String phone) {
        if (phone == null || phone.isBlank()) throw new BusinessValidationException("Phone required");
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.startsWith("91") && cleaned.length() > 10) {
            cleaned = cleaned.substring(cleaned.length() - 10);
        } else if (cleaned.startsWith("0")) {
            cleaned = cleaned.substring(1);
        }
        if (!cleaned.matches("^[6-9]\\d{9}$")) {
            throw new BusinessValidationException("Invalid Indian mobile number");
        }
        return cleaned;
    }
    /* ------------------------------------------------- FORGOT PASSWORD ------------------------------------------------- */
    @Transactional
    public void forgotPassword(String phone) throws Exception {
        String normalized = normalizePhone(phone);
        if (!userRepo.existsByPhone(normalized)) {
            throw new AuthenticationFailedException("No account found with this phone number");
        }
        sendOtp(new OtpRequest(normalized, OtpPurpose.FORGOT_PASSWORD));
    }
    /* ------------------------------------------------- UPDATE PROFILE ------------------------------------------------- */
   
    @Transactional
    public User updateProfile(UpdateProfileRequest r, User user) throws FileUploadException {
        Optional.ofNullable(r.businessName()).filter(s -> !s.isBlank()).ifPresent(v -> user.setBusinessName(v.trim()));
        Optional.ofNullable(r.address()).filter(s -> !s.isBlank()).ifPresent(v -> user.setAddress(v.trim()));
        Optional.ofNullable(r.city()).filter(s -> !s.isBlank()).ifPresent(v -> user.setCity(v.trim()));
        Optional.ofNullable(r.state()).filter(s -> !s.isBlank()).ifPresent(v -> user.setState(v.trim()));
        Optional.ofNullable(r.country()).filter(s -> !s.isBlank()).ifPresent(v -> user.setCountry(v.trim()));
        Optional.ofNullable(r.postalCode()).filter(s -> !s.isBlank()).ifPresent(v -> user.setPostalCode(v.trim()));
        Optional.ofNullable(r.aadhaarNumber()).filter(s -> !s.isBlank()).ifPresent(v -> user.setAadhaarNumber(v.trim()));
        Optional.ofNullable(r.panNumber()).filter(s -> !s.isBlank()).ifPresent(v -> user.setPanNumber(v.trim().toUpperCase()));
     
        
        Optional.ofNullable(r.udyamRegistrationNumber()).ifPresent(v -> user.setUdyamRegistrationNumber(v.trim()));
       
        
        Optional.ofNullable(r.gstCertificateNumber()).filter(s -> !s.isBlank()).ifPresent(v -> user.setGstCertificateNumber(v.trim().toUpperCase()));
        Optional.ofNullable(r.tradeLicenseNumber()).filter(s -> !s.isBlank()).ifPresent(v -> user.setTradeLicenseNumber(v.trim()));
        Optional.ofNullable(r.fssaiLicenseNumber()).filter(s -> !s.isBlank()).ifPresent(v -> user.setFssaiLicenseNumber(v.trim()));
        Optional.ofNullable(r.bankName()).filter(s -> !s.isBlank()).ifPresent(v -> user.setBankName(v.trim()));
        Optional.ofNullable(r.accountHolderName()).filter(s -> !s.isBlank()).ifPresent(v -> user.setAccountHolderName(v.trim()));
        Optional.ofNullable(r.bankAccountNumber()).filter(s -> !s.isBlank()).ifPresent(v -> user.setBankAccountNumber(v.trim()));
        Optional.ofNullable(r.ifscCode()).filter(s -> !s.isBlank()).ifPresent(v -> user.setIfscCode(v.trim().toUpperCase()));
        Optional.ofNullable(r.upiId()).filter(s -> !s.isBlank()).ifPresent(v -> user.setUpiId(v.trim().toLowerCase()));
        try {
        	if (r.aadhaarImage() != null && !r.aadhaarImage().isEmpty()) {
                user.setAadhaarImagePath(cloudinaryService.upload(r.aadhaarImage()));
            }
            if (r.panImage() != null && !r.panImage().isEmpty()) {
                user.setPanImagePath(cloudinaryService.upload(r.panImage()));
            }
            if (r.udyamRegistrationImage() != null && !r.udyamRegistrationImage().isEmpty()) {
                user.setUdyamRegistrationImagePath(cloudinaryService.upload(r.udyamRegistrationImage()));
            }
            if (r.fssaiLicenseFile() != null && !r.fssaiLicenseFile().isEmpty())
                user.setFssaiLicensePath(cloudinaryService.upload(r.fssaiLicenseFile()));
            if (r.photo() != null && !r.photo().isEmpty())
                user.setPhotoUrl(cloudinaryService.upload(r.photo()));
        } catch (Exception e) {
            throw new FileUploadException("Upload failed: " + e.getMessage());
        }
        user.setProfileCompleted("YES");
        return userRepo.save(user);
    }
   
    //-------------------------------
    public User getCurrentUser(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) throw new BusinessValidationException("Invalid token");
        return userRepo.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessValidationException("User not found"));
    }
  
    
    
    @Transactional
    public String uploadProfilePhoto(MultipartFile file, User user) throws FileUploadException {
        if (file == null || file.isEmpty()) {
            throw new BusinessValidationException("Photo file is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessValidationException("Only image files are allowed");
        }
        try {
            String photoUrl = cloudinaryService.upload(file);
            user.setPhotoUrl(photoUrl);
            userRepo.save(user);
            return photoUrl;
        } catch (Exception e) {
            throw new FileUploadException("Failed to upload photo: " + e.getMessage(), e);
        }
    }
    
    
    
    
    
    // ============= FARMER ONLY METHODS (NO DUPLICATES) =============
    @Transactional
    public User registerFarmer(FarmerRegisterRequest r) {
        String phone = normalizePhone(r.phone());
       
        if (userRepo.existsByEmail(r.email())) {
            throw new DuplicateResourceException("This email is already registered");
        }
        if (userRepo.existsByPhone(phone)) {
            throw new DuplicateResourceException("This phone number is already registered");
        }
        
        Role role = roleRepo.findByName("FARMER")
                .orElseGet(() -> roleRepo.save(new Role("FARMER")));
        User user = new User();
        user.setName(r.name());
        user.setEmail(r.email());
        user.setPhone(phone);
        user.setPasswordHash(encoder.encode(r.password()));
        user.setRole(role);
        user.setPhoneVerified(true);
        user = userRepo.save(user);
        FarmerProfile profile = new FarmerProfile(user);
        farmerProfileRepo.save(profile);
        return user;
    }
    @Transactional
    public User updateFarmerFullProfile(FarmerProfileRequest r, User farmer) {
        FarmerProfile profile = farmerProfileRepo.findByUser(farmer)
                .orElseThrow(() -> new BusinessValidationException("Profile not found"));
        profile.setState(r.state());
        profile.setBankName(r.bankName());
        profile.setAccountHolderName(r.accountHolderName());
        profile.setBankAccountNumber(r.bankAccountNumber());
        profile.setIfscCode(r.ifscCode());
        profile.setProfileCompleted("true");
        farmerProfileRepo.save(profile);
        farmer.setName(r.name());
        farmer.setEmail(r.email());
        farmer.setPhone(normalizePhone(r.phone()));
        return userRepo.save(farmer);
    }
    @Transactional
    public String uploadFarmerPhoto(MultipartFile file, User farmer) throws IOException {
        FarmerProfile profile = farmerProfileRepo.findByUser(farmer)
                .orElseThrow(() -> new BusinessValidationException("Profile not found"));
        String url = cloudinaryService.upload(file);
        // Save profile or farmer as required
        farmerProfileRepo.save(profile);
        return url;
    }
    public FarmerProfile getFarmerProfile(User farmer) {
        return farmerProfileRepo.findByUser(farmer).orElse(null);
    }
    public User getUserFromToken(String token) {
        try {
            String email = jwtService.extractSubject(token);
            return userRepo.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
    @Transactional
    public void updateFarmerPartialData(User farmer, Map<String, Object> updates) {
        FarmerProfile profile = farmerProfileRepo.findByUser(farmer)
                .orElseThrow(() -> new IllegalArgumentException("Farmer profile not found"));
        updates.forEach((key, value) -> {
            switch (key) {
                case "state" -> profile.setState((String) value);
                case "bankName" -> profile.setBankName((String) value);
                case "accountHolderName" -> profile.setAccountHolderName((String) value);
                case "bankAccountNumber" -> profile.setBankAccountNumber((String) value);
                case "ifscCode" -> profile.setIfscCode((String) value);
                case "profileCompleted" -> profile.setProfileCompleted((String) value);
                default -> throw new IllegalArgumentException("Invalid field: " + key);
            }
        });
        farmerProfileRepo.save(profile);
    }
  
   
    @Transactional
    public void updateFcmToken(Long userId, String fcmToken) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFcmToken(fcmToken);
        userRepo.save(user);
    }
    // NEW: Calculate Profile Completion Percentage (based on key fields)
    public int calculateProfileCompletion(User user) {
        int total = 12;
        int filled = 0;
        if (user.getBusinessName() != null && !user.getBusinessName().isBlank()) filled++;
        if (user.getAddress() != null && !user.getAddress().isBlank()) filled++;
        if (user.getCity() != null && !user.getCity().isBlank()) filled++;
        if (user.getState() != null && !user.getState().isBlank()) filled++;
        if (user.getCountry() != null && !user.getCountry().isBlank()) filled++;
        if (user.getPostalCode() != null && !user.getPostalCode().isBlank()) filled++;
        if (user.getAadhaarNumber() != null && !user.getAadhaarNumber().isBlank()) filled++;
        if (user.getPanNumber() != null && !user.getPanNumber().isBlank()) filled++;
        
        if (user.getBankName() != null && !user.getBankName().isBlank()) filled++;
        if (user.getAccountHolderName() != null && !user.getAccountHolderName().isBlank()) filled++;
        if (user.getBankAccountNumber() != null && !user.getBankAccountNumber().isBlank()) filled++;
        if (user.getIfscCode() != null && !user.getIfscCode().isBlank()) filled++;
        return (filled * 100) / total;
    }
    // NEW: Helper to save user (for controller use)
    @Transactional
    public User save(User user) {
        return userRepo.save(user);
    }
    
    
    
 // In AuthService        @Transactional
    public void softDeleteVendor(User vendor, User performedBy) {

        if (vendor == null) {
            throw new BusinessValidationException("Vendor cannot be null");
        }

        if (vendor.isDeleted()) {
            throw new BusinessValidationException("Vendor is already deleted");
        }

        
        vendor.markAsDeleted(performedBy);
        userRepo.save(vendor);

        
        List<Product> products = productRepo.findByMerchantId(vendor.getId());

        int inactivatedCount = 0;

        for (Product product : products) {
            
            if (product.getStatus() == ProductStatus.ACTIVE) {
                product.setStatus(ProductStatus.INACTIVE);
                inactivatedCount++;
            }

            
        }

        if (!products.isEmpty()) {
            productRepo.saveAll(products);
        }

        // Replaced log.info with System.out.println
        System.out.println(
            "Soft-deleted vendor id=" + vendor.getId() +
            " (phone=" + vendor.getPhone() +
            "), inactivated " + inactivatedCount + " products"
        );

       
    }

    
    @Transactional
    public void restoreVendor(User vendor) {

        if (vendor == null) {
            throw new IllegalArgumentException("Vendor cannot be null");
        }

        if (!vendor.isDeleted()) {
            throw new IllegalStateException("Account is not deleted");
        }

       
        vendor.restore();
        userRepo.save(vendor);

        
        List<Product> products = productRepo.findByMerchantId(vendor.getId());

        int reactivated = 0;
        for (Product p : products) {
            if (p.getStatus() == ProductStatus.INACTIVE) {
                
                p.setStatus(ProductStatus.ACTIVE);
                reactivated++;
            }
        }

        if (reactivated > 0) {
            productRepo.saveAll(products);
        }

        
        System.out.println(
            "Restored vendor id=" + vendor.getId() +
            ", reactivated " + reactivated + " products"
        );
    }

    

}