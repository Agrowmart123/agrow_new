//// src/main/java/com/agrowmart/controller/WomenProductController.java
//package com.agrowmart.controller;
//
//import com.agrowmart.dto.auth.OtpRequest;
//import com.agrowmart.dto.auth.VerifyOtpRequest;
//import com.agrowmart.dto.auth.women.WomenProductCreateDTO;
//import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
//import com.agrowmart.entity.User;
//import com.agrowmart.enums.OtpPurpose;
//import com.agrowmart.service.AuthService;
//import com.agrowmart.service.WomenProductService;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/women-products")
//@RequiredArgsConstructor
//public class WomenProductController {
//
//    private final WomenProductService service;
//    private final AuthService authService;  // ← Added for OTP
//    public WomenProductController(WomenProductService service, AuthService authService) {
//        this.service = service;
//        this.authService=authService;
//    }
//
//    // CREATE PRODUCT — INDIVIDUAL FIELDS (Perfect for Postman)
////    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
////    @PreAuthorize("hasAuthority('WOMEN')")
////    public WomenProductResponseDTO create(
////            @AuthenticationPrincipal User user,
////            @RequestParam String name,
////            @RequestParam String category,
////            @RequestParam String description,
////            @RequestParam BigDecimal price,
////            @RequestParam Integer stock,
////            @RequestParam String unit,
////            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws Exception {
////
////        WomenProductCreateDTO dto = new WomenProductCreateDTO(
////                name, category, description, price, stock, unit, null
////        );
////
////        return service.createProduct(user.getId(), dto, images);
////    }
//       
//    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('WOMEN')")
//    public ResponseEntity<WomenProductResponseDTO> create(
//            @AuthenticationPrincipal User user,
//
//            // === Product basic fields ===
//            @RequestParam String name,
//            @RequestParam String category,
//            @RequestParam(required = false) String description,
//            @RequestParam BigDecimal price,
//            @RequestParam(required = false) Integer stock,
//            @RequestParam String unit,
//
//            // === Shop branding fields ===
//            @RequestParam(required = false) String shopName,
//            @RequestParam(required = false) String shopAddress,
//            @RequestParam(defaultValue = "India") String country,
//
//            // === Files ===
//            @RequestParam(value = "images", required = false) List<MultipartFile> images,
//            @RequestParam(value = "shopCoverImage", required = false) MultipartFile shopCoverImage,
//            @RequestParam(value = "shopLogoImage", required = false) MultipartFile shopLogoImage
//
//    ) throws Exception {
//
//        // Build the updated DTO (now has 9 fields)
//        WomenProductCreateDTO dto = new WomenProductCreateDTO(
//                name,
//                category,
//                description,
//                price,
//                stock,
//                unit,
//                shopName,
//                shopAddress,
//                
//                country,
//                images,
//                shopCoverImage,
//                shopLogoImage,
//                
//        );
//
//        WomenProductResponseDTO response = service.createProduct(
//                user.getId(),
//                dto,
//                images != null ? images : List.of(),
//                shopCoverImage,
//                shopLogoImage
//        );
//
//        return ResponseEntity.ok(response);
//    }
//    // GET MY PRODUCTS
//    @GetMapping("/my")
//    @PreAuthorize("hasAuthority('WOMEN')")
//    public List<WomenProductResponseDTO> getMyProducts(@AuthenticationPrincipal User user) {
//        return service.getMyProducts(user.getId());
//    }
//
//    // GET ALL PRODUCTS (Public)
//    @GetMapping
//    public List<WomenProductResponseDTO> getAll() {
//        return service.getAllWomenProducts();
//    }
//
//    // GET SINGLE PRODUCT
//    @GetMapping("/{id}")
//    public WomenProductResponseDTO getById(@PathVariable Long id) {
//        return service.getProductById(id);
//    }
//    
//    
//    
//    
//    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('WOMEN')")
//    public ResponseEntity<WomenProductResponseDTO> update(
//            @AuthenticationPrincipal User user,
//            @PathVariable("id") Long id,
//
//            // === Basic Product Fields ===
//            @RequestParam String name,
//            @RequestParam String category,
//            @RequestParam(required = false) String description,
//            @RequestParam BigDecimal price,
//            @RequestParam(required = false) Integer stock,
//            @RequestParam String unit,
//
//            // === Shop Branding Fields ===
//            @RequestParam(required = false) String shopName,
//            @RequestParam(required = false) String shopAddress,
//            @RequestParam(defaultValue = "India") String country,
//
//            // === Files (optional - if not sent, old ones are kept) ===
//            @RequestParam(value = "images", required = false) List<MultipartFile> images,
//            @RequestParam(value = "shopCoverImage", required = false) MultipartFile shopCoverImage,
//            @RequestParam(value = "shopLogoImage", required = false) MultipartFile shopLogoImage
//
//    ) throws Exception {
//
//        // Build the correct DTO (9 fields only - no files in DTO!)
//    	 WomenProductCreateDTO dto = new WomenProductCreateDTO(
//                 name,
//                 category,
//                 description,
//                 price,
//                 stock,
//                 unit,
//                 shopName,
//                 shopAddress,
//                 country,
//                 images,
//                 shopCoverImage,
//                 shopLogoImage,
//                 
//         );
//
//        WomenProductResponseDTO updated = service.updateProduct(
//                user.getId(),
//                id,
//                dto,
//                images != null ? images : List.of(),   // product images
//                shopCoverImage,                         // new cover (or null → keep old)
//                shopLogoImage                           // new logo (or null → keep old)
//        );
//
//        return ResponseEntity.ok(updated);
//    }
//    
//    
// // ====================== PUBLIC OTP & AUTH APIs (Women Vendor) ======================
//
//    @PostMapping("/send-otp")
//    public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpRequest req) {
//        authService.sendOtp(req);
//        return ResponseEntity.ok(Map.of(
//                "success", true,
//                "message", "OTP sent to " + req.phone()
//        ));
//    }
//
//    @PostMapping("/verify-otp")
//    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
//        authService.verifyOtp(req);
//        String msg = req.purpose() == OtpPurpose.FORGOT_PASSWORD
//                ? "OTP verified! Set new password"
//                : "Phone verified! You can now login";
//        return ResponseEntity.ok(Map.of("success", true, "message", msg));
//    }
//
//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
//        String phone = body.get("phone");
//        if (phone == null || phone.isBlank()) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("success", false, "error", "Phone number required"));
//        }
//        try {
//            authService.forgotPassword(phone);
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "message", "Password reset OTP sent to " + phone
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("success", false, "error", e.getMessage()));
//        }
//    }
//
//    // DELETE PRODUCT
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('WOMEN')")
//    public ResponseEntity<String> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
//        service.deleteProduct(user.getId(), id);
//        return ResponseEntity.ok("Product deleted successfully");
//    }
//}
package com.agrowmart.controller;

import com.agrowmart.dto.auth.women.WomenProductCreateDTO;
import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
import com.agrowmart.dto.auth.women.WomenProductStatusRequest;
import com.agrowmart.entity.User;
import com.agrowmart.service.WomenProductService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/women-products")
public class WomenProductController {

    private final WomenProductService service;

    public WomenProductController(WomenProductService service) {
        this.service = service;
    }

    // ========================= CREATE PRODUCT =========================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('WOMEN')")
    public ResponseEntity<WomenProductResponseDTO> create(
            @AuthenticationPrincipal User user,
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam(required = false) String description,
          
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(required = false) Integer stock,
            @RequestParam String unit,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String shelfLife,
            @RequestParam(required = false) String packagingType,
            @RequestParam(required = false) String productInfo
    ) throws Exception {

        WomenProductCreateDTO dto = new WomenProductCreateDTO(
                name,
                category,
                description,
               
                minPrice,
                maxPrice,
                stock,
                unit,
                ingredients,
                shelfLife,
                packagingType,
                productInfo
        );

        WomenProductResponseDTO response = service.createProduct(
                user.getId(),
                dto,
                images != null ? images : List.of()
        );

        return ResponseEntity.ok(response);
    }

    // ========================= GET MY PRODUCTS =========================
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('WOMEN')")
    public List<WomenProductResponseDTO> getMyProducts(@AuthenticationPrincipal User user) {
        return service.getMyProducts(user.getId());
    }

    // ========================= GET ALL PRODUCTS =========================
    @GetMapping
    public List<WomenProductResponseDTO> getAll() {
        return service.getAllWomenProducts();
    }

    // ========================= GET SINGLE PRODUCT =========================
    @GetMapping("/{id}")
    public WomenProductResponseDTO getById(@PathVariable Long id) {
        return service.getProductById(id);
    }

    // ========================= UPDATE PRODUCT =========================
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('WOMEN')")
    public ResponseEntity<WomenProductResponseDTO> update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam(required = false) String description,
        
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(required = false) Integer stock,
            @RequestParam String unit,
            @RequestParam(value = "images", required = false) List<MultipartFile> images, // Fixed: added this
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String shelfLife,
            @RequestParam(required = false) String packagingType,
            @RequestParam(required = false) String productInfo
    ) throws Exception {

        WomenProductCreateDTO dto = new WomenProductCreateDTO(
                name,
                category,
                description,
                
                minPrice,
                maxPrice,
                stock,
                unit,
                ingredients,
                shelfLife,
                packagingType,
                productInfo
        );

        WomenProductResponseDTO updated = service.updateProduct(
                user.getId(),
                id,
                dto,
                images != null ? images : List.of()
        );

        return ResponseEntity.ok(updated);
    }

    // ========================= DELETE =========================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('WOMEN')")
    public ResponseEntity<String> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.deleteProduct(user.getId(), id);
        return ResponseEntity.ok("Product deleted successfully");
    }
    
    
  //-- status
 // ========================= CHANGE PRODUCT STATUS (ACTIVE / INACTIVE) =========================
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('WOMEN')")
    public ResponseEntity<WomenProductResponseDTO> updateProductStatus(
            @PathVariable Long id,
            @RequestBody @Valid WomenProductStatusRequest request,
            @AuthenticationPrincipal User user) {

        boolean isActive = "ACTIVE".equalsIgnoreCase(request.status());

        WomenProductResponseDTO updated = service.updateWomenProductStatus(
                id,
                isActive,
                user.getId()
        );

        return ResponseEntity.ok(updated);
    }
}