// src/main/java/com/agrowmart/controller/CustomerAuthController.java

package com.agrowmart.controller;

import com.agrowmart.dto.auth.JwtResponse;
import com.agrowmart.dto.auth.customer.*;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.enums.OtpPurpose;
import com.agrowmart.service.customer.CustomerAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/customer/auth")
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;

    public CustomerAuthController(CustomerAuthService customerAuthService) {
        this.customerAuthService = customerAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<Customer> register(@Valid @RequestBody CustomerRegisterRequest req) {
        Customer customer = customerAuthService.register(req);
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody CustomerLoginRequest req) {
        return ResponseEntity.ok(customerAuthService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerProfileResponse> me(@AuthenticationPrincipal Customer customer) {
        return ResponseEntity.ok(customerAuthService.getProfile(customer));
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<String> uploadPhoto(
            @RequestParam("photo") MultipartFile file,
            @AuthenticationPrincipal Customer customer) throws IOException {
        return ResponseEntity.ok(customerAuthService.uploadPhoto(file, customer));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody OtpRequestDto req) {
        customerAuthService.sendOtp(req.phone(), OtpPurpose.valueOf(req.purpose()));
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto req) {
        customerAuthService.verifyOtp(req);
        return ResponseEntity.ok("OTP verified successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto req) {
        customerAuthService.forgotPassword(req.phone());
        return ResponseEntity.ok("OTP sent to your phone for password reset");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto req) {
        customerAuthService.resetPassword(req.phone(), req.newPassword(), req.code());
        return ResponseEntity.ok("Password reset successfully");
    }
    
    
//------------------
    
 // Add this to CustomerAuthController.java

    @PatchMapping(value = "/update-profile", consumes = {"multipart/form-data"})
    public ResponseEntity<CustomerProfileResponse> updateProfile(
            @AuthenticationPrincipal Customer customer,
            
            // Text fields - all optional
            @RequestPart(value = "fullName", required = false) String fullName,
            @RequestPart(value = "email", required = false) String email,
            @RequestPart(value = "gender", required = false) String gender,
            
            // Profile photo (optional file upload)
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImageFile,
            
            // Optional: phone change (rare, needs OTP verification)
            @RequestPart(value = "phone", required = false) String newPhone
    ) throws IOException {
        if (customer == null) {
            return ResponseEntity.status(401).build();
        }

        Customer updatedCustomer = customerAuthService.updateProfile(
                customer,
                fullName,
                email,
                gender,
                profileImageFile,
                newPhone
        );

        CustomerProfileResponse response = new CustomerProfileResponse(
                updatedCustomer.getId(),
                updatedCustomer.getFullName(),
                updatedCustomer.getEmail(),
                updatedCustomer.getPhone(),
                updatedCustomer.getGender(),
                updatedCustomer.getProfileImage(),
                updatedCustomer.isPhoneVerified()
        );

        return ResponseEntity.ok(response);
    }
    
    
    
}