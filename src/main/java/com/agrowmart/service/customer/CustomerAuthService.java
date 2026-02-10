//// src/main/java/com/agrowmart/service/customer/CustomerAuthService.java
//
//package com.agrowmart.service.customer;
//
//import com.agrowmart.dto.auth.JwtResponse;
//import com.agrowmart.dto.auth.customer.*;
//import com.agrowmart.entity.customer.Customer;
//import com.agrowmart.enums.OtpPurpose;
//import com.agrowmart.repository.customer.CustomerRepository;
//import com.agrowmart.service.CloudinaryService;
//import com.agrowmart.service.Fast2SmsService;
//import com.agrowmart.service.JwtService;
//import com.agrowmart.util.InMemoryOtpStore;
//import com.agrowmart.util.InMemoryOtpStore.OtpData;
//import com.agrowmart.util.RateLimiterUtil;
//import com.agrowmart.util.RedisOtpStore;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.security.SecureRandom;
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.Set;
//
//@Service
//public class CustomerAuthService {
//
//	
//	
//	
//    private final CustomerRepository customerRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final CloudinaryService cloudinaryService;
//    private final Fast2SmsService fast2SmsService;  // NEW
//    private final RedisOtpStore redisOtpStore;
//
//    public CustomerAuthService(CustomerRepository customerRepository,
//                               PasswordEncoder passwordEncoder,
//                               JwtService jwtService,
//                               CloudinaryService cloudinaryService,
//                               Fast2SmsService fast2SmsService,
//                               RedisOtpStore redisOtpStore) {
//        this.customerRepository = customerRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtService = jwtService;
//        this.cloudinaryService = cloudinaryService;
//        this.fast2SmsService = fast2SmsService;
//        this.redisOtpStore = redisOtpStore;
//    }
//
//
//    @Transactional
//    public Customer register(CustomerRegisterRequest req) throws Exception {
//        String phone = normalizePhone(req.phone());
//        String email = req.email() != null ? req.email().trim().toLowerCase() : null;
//
//        if (email != null && customerRepository.existsByEmail(email)) {
//            throw new IllegalArgumentException("Email already registered");
//        }
//        if (customerRepository.existsByPhone(phone)) {
//            throw new IllegalArgumentException("Phone number already registered");
//        }
//
//        Customer customer = new Customer();
//        customer.setFullName(req.fullName().trim());
//        customer.setEmail(email);
//        customer.setPhone(phone);
//        customer.setPasswordHash(passwordEncoder.encode(req.password()));
//        customer.setPhoneVerified(false);
//        customer.setActive(true);
//
//        Customer saved = customerRepository.save(customer);
//
//        // Auto-send OTP for phone verification
////        sendOtp(phone, OtpPurpose.PHONE_VERIFY);
//
//        return saved;
//    }
//    
//    
//
//    public JwtResponse login(CustomerLoginRequest req) {
//        String input = req.login().trim();
//        Optional<Customer> customerOpt = customerRepository.findByEmail(input);
//
//        if (customerOpt.isEmpty()) {
//            String phone = normalizePhone(input);
//            customerOpt = customerRepository.findByPhone(phone);
//        }
//
//        Customer customer = customerOpt.orElseThrow(
//                () -> new IllegalArgumentException("Invalid credentials"));
//
//        if (!passwordEncoder.matches(req.password(), customer.getPasswordHash())) {
//            throw new IllegalArgumentException("Invalid credentials");
//        }
//
//        String token = jwtService.issueTokenForCustomer(customer);
//        return new JwtResponse(token, null, LocalDateTime.now().plusDays(7));
//    }
//
//    public CustomerProfileResponse getProfile(Customer customer) {
//        return new CustomerProfileResponse(
//                customer.getId(),
//                customer.getFullName(),
//                customer.getEmail(),
//                customer.getPhone(),
//                customer.getGender(),
//                customer.getProfileImage(),
//                customer.isPhoneVerified()
//        );
//    }
//
//    @Transactional
//    public String uploadPhoto(MultipartFile file, Customer customer) throws IOException {
//        if (file == null || file.isEmpty()) {
//            throw new IllegalArgumentException("Photo file is required");
//        }
//        if (!file.getContentType().startsWith("image/")) {
//            throw new IllegalArgumentException("Only image files are allowed");
//        }
//
//        String photoUrl = cloudinaryService.upload(file);
//        customer.setProfileImage(photoUrl);
//        customer.setUpdatedAt(LocalDateTime.now());
//        customerRepository.save(customer);
//        return photoUrl;
//    }
//
//    
//  //--------------------  
// // UPDATED: Now using Fast2SMS instead of Twilio
////    @Transactional
////    public void sendOtp(String phone, OtpPurpose purpose) {
////        String normalizedPhone = normalizePhone(phone);
////        
////     // Rate limiting
////        String rateKey = "rate:otp:customer:" + normalizedPhone;
////        if (!RateLimiterUtil.isAllowed(rateKey, MAX_OTP_PER_HOUR, RATE_LIMIT_WINDOW_SECONDS)) {
////            log.warn("OTP rate limit exceeded for customer phone: {}", normalizedPhone);
////            throw new Exception("Too many OTP requests. Try again after 1 hour.");
////        }
////        String code = String.format("%06d", new SecureRandom().nextInt(999999));
////
////        InMemoryOtpStore.saveOtp(normalizedPhone, code, purpose.name(), 300);
////
////        String message = "AgroMart OTP: " + code + " (valid 5 min)";
////
////        // Send via Fast2SMS (Twilio removed)
////        fast2SmsService.sendOtp(normalizedPhone, code, purpose.name());
////    }
////    
//    
////    
//    
//    @Transactional
//    public void sendOtp(String phone, OtpPurpose purpose) throws Exception {
//        redisOtpStore.sendOtp(phone, purpose);
//    }
//    
//
//    @Transactional
//    public void verifyOtp(VerifyOtpRequestDto req) {
//        String normalizedPhone = normalizePhone(req.phone());
//        OtpPurpose purpose = OtpPurpose.valueOf(req.purpose());
//
//        boolean valid = redisOtpStore.verifyOtp(normalizedPhone, req.code(), purpose);
//
//        if (!valid) {
//            throw new IllegalArgumentException("Invalid or expired OTP");
//        }
//        
//        
//        if (purpose == OtpPurpose.PHONE_VERIFY) {
//            Customer customer = customerRepository.findByPhone(normalizedPhone)
//                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
//            customer.setPhoneVerified(true);
//            customerRepository.save(customer);
//        }
//    }
//
//    @Transactional
//    public void forgotPassword(String phone) throws Exception {
//        String normalized = normalizePhone(phone);
//        if (!customerRepository.existsByPhone(normalized)) {
//            throw new IllegalArgumentException("No account found with this phone number");
//        }
//        sendOtp(normalized, OtpPurpose.FORGOT_PASSWORD);
//    }
//
//    @Transactional
//    public void resetPassword(String phone, String newPassword, String code) {
//        String normalized = normalizePhone(phone);
//
//        VerifyOtpRequestDto verifyReq = new VerifyOtpRequestDto(normalized, code, OtpPurpose.FORGOT_PASSWORD.name());
//        verifyOtp(verifyReq);
//
//        Customer customer = customerRepository.findByPhone(normalized)
//                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
//
//        if (newPassword == null || newPassword.trim().length() < 6) {
//            throw new IllegalArgumentException("Password must be at least 6 characters");
//        }
//
//        customer.setPasswordHash(passwordEncoder.encode(newPassword.trim()));
//        customerRepository.save(customer);
//    }
//
//    private String normalizePhone(String phone) {
//        if (phone == null || phone.isBlank()) throw new IllegalArgumentException("Phone cannot be empty");
//        String cleaned = phone.replaceAll("[^0-9]", "");
//        if (cleaned.startsWith("91") && cleaned.length() > 10) cleaned = cleaned.substring(cleaned.length() - 10);
//        else if (cleaned.startsWith("0") && cleaned.length() > 10) cleaned = cleaned.substring(1);
//        if (!cleaned.matches("^[6-9]\\d{9}$")) throw new IllegalArgumentException("Invalid mobile number");
//        return "+91" + cleaned;
//    }
//    
//    
//    
//    
//  //----------------------------------------
//    
//    
//    @Transactional
//    public Customer updateProfile(
//            Customer customer,
//            String fullName,
//            String email,
//            String gender,
//            MultipartFile profileImageFile,
//            String newPhone
//    ) throws IOException {
//
//        boolean changed = false;
//
//        // 1. Full Name
//        if (fullName != null && !fullName.trim().isEmpty()) {
//            customer.setFullName(fullName.trim());
//            changed = true;
//        }
//
//        // 2. Email (optional - you can add uniqueness check)
//        if (email != null && !email.trim().isEmpty()) {
//            String normalizedEmail = email.trim().toLowerCase();
//            if (!normalizedEmail.equals(customer.getEmail())) {
//                if (customerRepository.existsByEmail(normalizedEmail)) {
//                    throw new IllegalArgumentException("Email already in use");
//                }
//                customer.setEmail(normalizedEmail);
//                changed = true;
//            }
//        }
//
//        // 3. Gender (validate allowed values if you want)
//        if (gender != null && !gender.trim().isEmpty()) {
//            String g = gender.trim().toUpperCase();
//            if (!Set.of("MALE", "FEMALE", "OTHER").contains(g)) {
//                throw new IllegalArgumentException("Invalid gender value");
//            }
//            customer.setGender(g);
//            changed = true;
//        }
//
//        // 4. Profile Image upload
//        if (profileImageFile != null && !profileImageFile.isEmpty()) {
//            if (!profileImageFile.getContentType().startsWith("image/")) {
//                throw new IllegalArgumentException("Only image files are allowed for profile photo");
//            }
//            String photoUrl = cloudinaryService.upload(profileImageFile);
//            customer.setProfileImage(photoUrl);
//            changed = true;
//        }
//
//        // 5. Phone change (very sensitive - usually needs OTP verification)
//        // For security, we recommend NOT allowing direct phone change here
//        // If you really want to support it, implement OTP flow separately
//        if (newPhone != null && !newPhone.trim().isEmpty()) {
//            throw new IllegalArgumentException("Phone number change is not allowed via this endpoint. " +
//                    "Please use a separate OTP-verified flow.");
//        }
//
//        if (!changed) {
//            throw new IllegalArgumentException("No fields were provided to update");
//        }
//
//        customer.setUpdatedAt(LocalDateTime.now());
//        return customerRepository.save(customer);
//    }
//}


package com.agrowmart.service.customer;

import com.agrowmart.dto.auth.JwtResponse;
import com.agrowmart.dto.auth.customer.*;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.enums.OtpPurpose;
import com.agrowmart.exception.AuthExceptions.AuthenticationFailedException;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.AuthExceptions.DuplicateResourceException;
import com.agrowmart.exception.AuthExceptions.FileUploadException;
import com.agrowmart.exception.AuthExceptions.InvalidOtpException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.customer.CustomerRepository;
import com.agrowmart.service.CloudinaryService;
import com.agrowmart.service.Fast2SmsService;
import com.agrowmart.service.JwtService;
import com.agrowmart.util.RedisOtpStore;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomerAuthService {

    private static final Logger log = LoggerFactory.getLogger(CustomerAuthService.class);

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CloudinaryService cloudinaryService;
    private final Fast2SmsService fast2SmsService;
    private final RedisOtpStore redisOtpStore;

    public CustomerAuthService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            CloudinaryService cloudinaryService,
            Fast2SmsService fast2SmsService,
            RedisOtpStore redisOtpStore) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.cloudinaryService = cloudinaryService;
        this.fast2SmsService = fast2SmsService;
        this.redisOtpStore = redisOtpStore;
    }

    @Transactional
    public Customer register(CustomerRegisterRequest req) throws Exception {
        if (req == null) {
            log.warn("Registration attempt with null request");
            throw new BusinessValidationException("Registration request cannot be null");
        }

        String phone = normalizePhone(req.phone());
        String email = req.email() != null ? req.email().trim().toLowerCase() : null;

        log.info("New customer registration - Phone: {}, Email: {}", phone, email != null ? email : "none");

        if (email != null && customerRepository.existsByEmail(email)) {
            log.warn("Duplicate email during registration: {}", email);
            throw new DuplicateResourceException("This email is already registered");
        }

        if (customerRepository.existsByPhone(phone)) {
            log.warn("Duplicate phone during registration: {}", phone);
            throw new DuplicateResourceException("This phone number is already registered");
        }

        Customer customer = new Customer();
        customer.setFullName(req.fullName().trim());
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setPasswordHash(passwordEncoder.encode(req.password()));
        customer.setPhoneVerified(false);
        customer.setActive(true);

        Customer saved = customerRepository.save(customer);
        log.info("Customer registered successfully - ID: {}, Phone: {}", saved.getId(), phone);

        // Auto-send OTP for phone verification (uncomment when needed)
        // sendOtp(phone, OtpPurpose.PHONE_VERIFY);

        return saved;
    }

    public JwtResponse login(CustomerLoginRequest req) {
        if (req == null || req.login() == null || req.password() == null) {
            log.warn("Login attempt with missing credentials");
            throw new AuthenticationFailedException("Login identifier and password are required");
        }

        String input = req.login().trim();
        log.debug("Login attempt - Identifier: {}", input);

        Optional<Customer> customerOpt = customerRepository.findByEmail(input);
        if (customerOpt.isEmpty()) {
            String phone = normalizePhone(input);
            customerOpt = customerRepository.findByPhone(phone);
        }

        Customer customer = customerOpt.orElseThrow(() -> {
            log.warn("Login failed - no account found for: {}", input);
            return new BusinessValidationException("Invalid email/phone or password");
        });

        if (!passwordEncoder.matches(req.password(), customer.getPasswordHash())) {
            log.warn("Login failed - incorrect password - Customer ID: {}", customer.getId());
            throw new BusinessValidationException("Invalid email/phone or password");
        }

        String token = jwtService.issueTokenForCustomer(customer);
        log.info("Login successful - Customer ID: {}", customer.getId());

        return new JwtResponse(token, null, LocalDateTime.now().plusDays(7));
    }

    public CustomerProfileResponse getProfile(Customer customer) {
        log.debug("Fetching profile - Customer ID: {}", customer.getId());
        return new CustomerProfileResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getGender(),
                customer.getProfileImage(),
                customer.isPhoneVerified()
        );
    }

    @Transactional
    public String uploadPhoto(MultipartFile file, Customer customer) throws IOException {
        if (file == null || file.isEmpty()) {
            log.warn("Profile photo upload failed - empty file - Customer ID: {}", customer.getId());
            throw new FileUploadException("Profile photo file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("Invalid file type for profile photo - Customer ID: {}", customer.getId());
            throw new FileUploadException("Only image files (jpg, png, etc.) are allowed for profile photo");
        }

        log.info("Uploading profile photo - Customer ID: {}", customer.getId());

        String photoUrl = cloudinaryService.upload(file);
        customer.setProfileImage(photoUrl);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);

        log.info("Profile photo uploaded - URL: {}, Customer ID: {}", photoUrl, customer.getId());

        return photoUrl;
    }

    @Transactional
    public void sendOtp(String phone, OtpPurpose purpose) throws Exception {
        String normalizedPhone = normalizePhone(phone);
        log.info("Sending OTP - Phone: {}, Purpose: {}", normalizedPhone, purpose);

        redisOtpStore.sendOtp(normalizedPhone, purpose);

        log.debug("OTP sent successfully to: {}", normalizedPhone);
    }

    @Transactional
    public void verifyOtp(VerifyOtpRequestDto req) {
        if (req == null || req.phone() == null || req.code() == null || req.purpose() == null) {
            log.warn("OTP verification failed - incomplete request");
            throw new BusinessValidationException("Phone number, OTP code, and purpose are required");
        }

        String normalizedPhone = normalizePhone(req.phone());
        OtpPurpose purpose;

        try {
            purpose = OtpPurpose.valueOf(req.purpose());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid OTP purpose value: {}", req.purpose());
            throw new InvalidOtpException("Invalid OTP purpose value");
        }

        log.debug("Verifying OTP - Phone: {}, Purpose: {}", normalizedPhone, purpose);

        boolean valid = redisOtpStore.verifyOtp(normalizedPhone, req.code(), purpose);
        if (!valid) {
            log.warn("Invalid or expired OTP - Phone: {}", normalizedPhone);
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        if (purpose == OtpPurpose.PHONE_VERIFY) {
            Customer customer = customerRepository.findByPhone(normalizedPhone)
                    .orElseThrow(() -> {
                        log.warn("Phone verification failed - customer not found: {}", normalizedPhone);
                        return new ResourceNotFoundException("Customer not found for this phone number");
                    });

            customer.setPhoneVerified(true);
            customerRepository.save(customer);
            log.info("Phone verified successfully - Customer ID: {}, Phone: {}", customer.getId(), normalizedPhone);
        }
    }

    @Transactional
    public void forgotPassword(String phone) throws Exception {
        String normalized = normalizePhone(phone);
        log.info("Forgot password request - Phone: {}", normalized);

        if (!customerRepository.existsByPhone(normalized)) {
            log.warn("Forgot password - no account found: {}", normalized);
            throw new BusinessValidationException("No account found with this phone number");
        }

        sendOtp(normalized, OtpPurpose.FORGOT_PASSWORD);
    }

    @Transactional
    public void resetPassword(String phone, String newPassword, String code) {
        String normalized = normalizePhone(phone);
        log.info("Password reset attempt - Phone: {}", normalized);

        VerifyOtpRequestDto verifyReq = new VerifyOtpRequestDto(normalized, code, OtpPurpose.FORGOT_PASSWORD.name());
        verifyOtp(verifyReq);

        Customer customer = customerRepository.findByPhone(normalized)
                .orElseThrow(() -> {
                    log.error("Reset password failed after OTP - customer not found: {}", normalized);
                    return new BusinessValidationException("Customer not found");
                });

        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new BusinessValidationException("New password must be at least 6 characters long");
        }

        customer.setPasswordHash(passwordEncoder.encode(newPassword.trim()));
        customerRepository.save(customer);

        log.info("Password reset successful - Customer ID: {}", customer.getId());
    }

    @Transactional
    public Customer updateProfile(
            Customer customer,
            String fullName,
            String email,
            String gender,
            MultipartFile profileImageFile,
            String newPhone
    ) throws IOException {

        log.info("Profile update request - Customer ID: {}", customer.getId());

        boolean changed = false;

        // Full Name
        if (fullName != null && !fullName.trim().isEmpty()) {
            customer.setFullName(fullName.trim());
            changed = true;
        }

        // Email
        if (email != null && !email.trim().isEmpty()) {
            String normalizedEmail = email.trim().toLowerCase();
            if (!normalizedEmail.equals(customer.getEmail())) {
                if (customerRepository.existsByEmail(normalizedEmail)) {
                    log.warn("Email update failed - already in use: {}", normalizedEmail);
                    throw new BusinessValidationException("This email is already in use by another account");
                }
                customer.setEmail(normalizedEmail);
                changed = true;
            }
        }

        // Gender
        if (gender != null && !gender.trim().isEmpty()) {
            String g = gender.trim().toUpperCase();
            Set<String> allowed = Set.of("MALE", "FEMALE", "OTHER");
            if (!allowed.contains(g)) {
                log.warn("Invalid gender value: {}", g);
                throw new BusinessValidationException("Invalid gender value. Allowed: MALE, FEMALE, OTHER");
            }
            customer.setGender(g);
            changed = true;
        }

        // Profile Image
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            if (!profileImageFile.getContentType().startsWith("image/")) {
                log.warn("Invalid profile photo file type - Customer ID: {}", customer.getId());
                throw new BusinessValidationException("Only image files are allowed for profile photo");
            }

            String photoUrl = cloudinaryService.upload(profileImageFile);
            customer.setProfileImage(photoUrl);
            changed = true;
            log.info("Profile photo updated - URL: {}", photoUrl);
        }

        // Phone change blocked
        if (newPhone != null && !newPhone.trim().isEmpty()) {
            log.warn("Direct phone change attempt blocked - Customer ID: {}", customer.getId());
            throw new BusinessValidationException("Phone number change is not allowed here. Use OTP-verified flow.");
        }

        if (!changed) {
            log.debug("No changes in profile update - Customer ID: {}", customer.getId());
            throw new BusinessValidationException("No valid fields provided for update");
        }

        customer.setUpdatedAt(LocalDateTime.now());
        Customer updated = customerRepository.save(customer);
        log.info("Profile updated successfully - Customer ID: {}", updated.getId());

        return updated;
    }

    // ──────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            log.warn("Phone normalization failed - empty input");
            throw new BusinessValidationException("Phone number cannot be empty");
        }

        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.startsWith("91") && cleaned.length() > 10) {
            cleaned = cleaned.substring(cleaned.length() - 10);
        } else if (cleaned.startsWith("0") && cleaned.length() > 10) {
            cleaned = cleaned.substring(1);
        }

        if (!cleaned.matches("^[6-9]\\d{9}$")) {
            log.warn("Invalid phone format after normalization: {}", phone);
            throw new BusinessValidationException("Invalid Indian mobile number (must be 10 digits starting with 6-9)");
        }

        return "+91" + cleaned;
    }
}