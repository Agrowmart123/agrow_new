package com.agrowmart.controller;


import com.agrowmart.dto.auth.shop.ShopRequest;
import com.agrowmart.dto.auth.shop.ShopResponse;
import com.agrowmart.entity.User;
import com.agrowmart.service.ShopService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

 private final ShopService shopService;
 
 // ✅ THIS is REQUIRED
 public ShopController(ShopService shopService) {
     this.shopService = shopService;
 }


 @PostMapping(consumes = "multipart/form-data")
 public ResponseEntity<?> createShop(

         @RequestParam String shopName,
         @RequestParam String shopType,
         @RequestParam String shopAddress,
         @RequestParam String workingHours,
         @RequestParam(required = false) String shopDescription,
         @RequestParam String shopLicense,

         @RequestParam(required = false) MultipartFile shopPhoto,
         @RequestParam(required = false) MultipartFile shopCoverPhoto,
         @RequestParam(required = false) MultipartFile shopLicensePhoto,

         @RequestParam(required = false) String opensAt,
         @RequestParam(required = false) String closesAt,

         @AuthenticationPrincipal User user
 ) throws IOException {
 	
 	

     LocalTime openTime = opensAt != null ? LocalTime.parse(opensAt) : null;
     LocalTime closeTime = closesAt != null ? LocalTime.parse(closesAt) : null;

     ShopRequest req = new ShopRequest(
             shopName,
             shopType,
             shopAddress,
             workingHours,
             shopDescription != null ? shopDescription : "",
             shopLicense,
             openTime,
             closeTime,
             shopPhoto,
             shopCoverPhoto,
             shopLicensePhoto
     );

     return ResponseEntity.ok(Map.of(
             "success", true,
             "message", "Shop created! Waiting for approval",
             "shop", shopService.toResponse(shopService.createShop(req, user))
     ));
 }

 @PutMapping(value = "/my", consumes = "multipart/form-data")
 public ResponseEntity<?> updateMyShop(

         @RequestParam String shopName,
         @RequestParam String shopType,
         @RequestParam String shopAddress,
         @RequestParam String workingHours,
         @RequestParam(required = false) String shopDescription,
         @RequestParam String shopLicense,

         @RequestParam(required = false) MultipartFile shopPhoto,
         @RequestParam(required = false) MultipartFile shopCoverPhoto,
         @RequestParam(required = false) MultipartFile shopLicensePhoto,

         @RequestParam(required = false) String opensAt,
         @RequestParam(required = false) String closesAt,

         @AuthenticationPrincipal User user
 ) throws IOException {

     LocalTime openTime = opensAt != null ? LocalTime.parse(opensAt) : null;
     LocalTime closeTime = closesAt != null ? LocalTime.parse(closesAt) : null;

     ShopRequest req = new ShopRequest(
             shopName,
             shopType,
             shopAddress,
             workingHours,
             shopDescription != null ? shopDescription : "",
             shopLicense,
             openTime,
             closeTime,
             shopPhoto,
             shopCoverPhoto,
             shopLicensePhoto
     );

     return ResponseEntity.ok(Map.of(
             "success", true,
             "message", "Shop updated successfully!",
             "shop", shopService.toResponse(shopService.updateMyShop(req, user))
     ));
 }

 @GetMapping("/my")
 public ResponseEntity<?> getMyShop(@AuthenticationPrincipal User user) {
     return ResponseEntity.ok(Map.of(
             "success", true,
             "shop", shopService.getMyShop(user)
     ));
 }

 @GetMapping
 public ResponseEntity<List<ShopResponse>> getAllShops() {
     return ResponseEntity.ok(shopService.getAllShops());
 }
 
 @DeleteMapping("/my")
 public ResponseEntity<?> deleteMyShop(@AuthenticationPrincipal User user) {

     shopService.deleteMyShop(user);

     return ResponseEntity.ok(Map.of(
             "success", true,
             "message", "Shop deleted successfully"
     ));
 }

 
}