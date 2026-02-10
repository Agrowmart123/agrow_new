//package com.agrowmart.admin_seller_management.service;
//
//import com.agrowmart.admin_seller_management.dto.*;
//import com.agrowmart.admin_seller_management.entity.Admin;
//import com.agrowmart.admin_seller_management.entity.PaginationInfo;
//import com.agrowmart.admin_seller_management.enums.AccountStatus;
//import com.agrowmart.admin_seller_management.enums.DocumentStatus;
//import com.agrowmart.admin_seller_management.enums.RejectReason;
//import com.agrowmart.entity.*;
//import com.agrowmart.repository.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//
//import jakarta.transaction.Transactional;
//import jakarta.validation.Valid;
//
//import org.springframework.data.domain.*;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.*;

//
//@Service
//@Transactional
//public class AdminSellerService {
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final ShopRepository shopRepository;
//    private final AdminAuditService adminAuditService;
//
//    public AdminSellerService(
//            UserRepository userRepository,
//            RoleRepository roleRepository,
//            ShopRepository shopRepository,AdminAuditService adminAuditService) {
//
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//        this.shopRepository = shopRepository;
//        this.adminAuditService = adminAuditService;
//    }
//    
//    private Long getAdminId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null || !auth.isAuthenticated()) {
//            throw new IllegalStateException("Admin not authenticated");
//        }
//
//        Object principal = auth.getPrincipal();
//
//        if (!(principal instanceof Admin)) {
//            throw new IllegalStateException("Invalid admin principal - expected Admin, got: " + 
//                                            (principal != null ? principal.getClass().getName() : "null"));
//        }
//
//        Admin admin = (Admin) principal;
//        return admin.getId();
//    }
//    private Admin getAdminUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        
//        if (auth == null || !auth.isAuthenticated()) {
//            throw new IllegalStateException("Admin not authenticated");
//        }
//        
//        Object principal = auth.getPrincipal();
//        
//        if (!(principal instanceof Admin)) {
//            throw new IllegalStateException("Invalid principal type: " + 
//                                            (principal != null ? principal.getClass().getName() : "null"));
//        }
//        
//        return (Admin) principal;
//    }
//
//    // ================= ROLE IDS =================
//    private List<Short> vendorRoleIds() {
//        return roleRepository.findByNameIn(
//                List.of("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN","AGRI")
//        ).stream()
//         .map(Role::getId)
//         .toList();
//    }
//
//    // ================= LIST VENDORS =================
//    public ApiResponseDTO<List<Map<String, Object>>> getVendors(
//            int page, int size, String search, AccountStatus status) {
//
//        Pageable pageable = PageRequest.of(
//                page, size, Sort.by("createdAt").descending()
//        );
//
//        Page<User> users;
//
//        if (search != null && !search.isBlank() && status != null) {
//            users = userRepository.searchVendorsWithStatus(
//                    vendorRoleIds(), search, status, pageable);
//        } else if (search != null && !search.isBlank()) {
//            users = userRepository.searchVendors(
//                    vendorRoleIds(), search, pageable);
//        } else if (status != null) {
//            users = userRepository.findByRoleIdInAndAccountStatus(
//                    vendorRoleIds(), status, pageable);
//        } else {
//            users = userRepository.findByRoleIdIn(
//                    vendorRoleIds(), pageable);
//        }
//
//        List<Map<String, Object>> data = users.getContent().stream().map(u -> {
//            Map<String, Object> map = new HashMap<>();
//            map.put("id", u.getId());
//            Shop shop = shopRepository.findByUserId(u.getId()).orElse(null);
//
//            map.put("storeName",
//                    shop != null ? shop.getShopName() : u.getBusinessName()
//            );
//
//            map.put("sellerName", u.getName());
//            map.put("phone", u.getPhone());
//            map.put("email", u.getEmail());
//            map.put("photoUrl", u.getPhotoUrl());
//            map.put("status",
//                    u.getAccountStatus() != null
//                            ? u.getAccountStatus().name()
//                            : AccountStatus.PENDING.name());
//            map.put("createdAt", u.getCreatedAt());
//            //Added by Aakanksha-22/01/2026
//         // ✅ Vendor Type (ROLE)
//            map.put("vendorType",
//                    u.getRole() != null ? u.getRole().getName() : null);
//            
//
//            // ✅ Subscription info
//         // ✅ FIX: get active subscription properly
//            Subscription activeSub = u.getActiveSubscription();
//
//            map.put("hasActiveSubscription", activeSub != null);
//
//            if (activeSub != null) {
//                map.put("subscriptionPlan", activeSub.getPlan().name());
//                map.put("subscriptionExpiry", activeSub.getExpiryDate());
//            } else {
//                map.put("subscriptionPlan", null);
//                map.put("subscriptionExpiry", null);
//            }
//
//
//            map.put("createdAt", u.getCreatedAt());
//
//            return map;
//        }).toList();
//
//        return new ApiResponseDTO<>(
//                true,
//                "Vendors fetched",
//                data,
//                new PaginationInfo(
//                        users.getTotalElements(),
//                        users.getTotalPages(),
//                        users.getNumber(),
//                        users.getSize()
//                )
//        );
//    }
//
//    // ================= PROFILE =================
//   
//    public VendorProfileResponseDTO getVendorProfile(Long id) {
//
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        // ================= BASIC VENDOR INFO =================
//        VendorProfileResponseDTO dto = new VendorProfileResponseDTO();
//        dto.setId(user.getId());
//        dto.setName(user.getName());
//        dto.setEmail(user.getEmail());
//        dto.setPhone(user.getPhone());
//        dto.setBusinessName(user.getBusinessName());
//        dto.setAddress(user.getAddress());
//        dto.setPhotoUrl(user.getPhotoUrl());
//        dto.setAccountStatus(user.getAccountStatus());
//        dto.setStatusReason(user.getStatusReason());
//
//        // ================= DOCUMENTS =================
//        DocumentsDTO documents = new DocumentsDTO();
//
//        documents.setAadhaar(user.getAadhaarImagePath());
//        documents.setAadhaarStatus(user.getAadhaarStatus());
//
//        documents.setPan(user.getPanImagePath());
//        documents.setPanStatus(user.getPanStatus());
//
//        documents.setUdyam(user.getUdyamRegistrationImagePath());
//        documents.setUdyamStatus(user.getUdhyamStatus());
//
//        // ================= SHOP + SHOP LICENSE DOCUMENT =================
//        Shop shop = shopRepository.findByUserId(id).orElse(null);
//
//        if (shop != null) {
//            documents.setShopLicensePhoto(shop.getShopLicensePhoto());
//            documents.setShopLicensePhotoStatus(shop.getShopLicensePhotoStatus());
//        } else {
//            documents.setShopLicensePhoto(null);
//            documents.setShopLicensePhotoStatus(DocumentStatus.PENDING);
//        }
//
//        dto.setDocuments(documents);
//
//        // ================= SHOP DETAILS =================
//        if (shop != null) {
//            ShopDetailsDTO shopDto = new ShopDetailsDTO();
//
//            shopDto.setId(shop.getId());
//            shopDto.setShopName(shop.getShopName());
//            shopDto.setShopType(shop.getShopType());
//            shopDto.setShopAddress(shop.getShopAddress());
//            shopDto.setDescription(shop.getShopDescription());
//            shopDto.setWorkingHours(shop.getWorkingHoursJson());;
//            shopDto.setShopPhoto(shop.getShopPhoto());
//            shopDto.setShopCoverPhoto(shop.getShopCoverPhoto());
//            shopDto.setShopLicense(shop.getShopLicense());
//            shopDto.setShopLicensePhoto(shop.getShopLicensePhoto());
//            shopDto.setApproved(shop.isApproved());
//            shopDto.setActive(shop.isActive());
//
//            // ✅ Vendor Type (Role)
//            shopDto.setVendorType(
//                    user.getRole() != null ? user.getRole().getName() : null
//            );
//
//            // ✅ GST Number
//            shopDto.setGstCertificateNumber(user.getGstCertificateNumber());
//
//            dto.setShop(shopDto);
//        }
//
//        return dto;
//    }
//
//
//    // ================= APPROVE VENDOR (🔥 MAIN FIX) =================
//    @Transactional
//    public void approveVendor(Long vendorId) {
//
//        User user = userRepository.findById(vendorId)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        // ✅ ACCOUNT STATUS
//        user.setAccountStatus(AccountStatus.APPROVED);
//        user.setStatusReason("Approved by admin");
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        // ✅ DOCUMENT APPROVALS
//        user.setAadhaarStatus(DocumentStatus.APPROVED);
//        user.setPanStatus(DocumentStatus.APPROVED);
//        user.setUdhyamStatus(DocumentStatus.APPROVED);
//
//        // ✅ SHOP (must exist)
//        Shop shop = shopRepository.findByUserId(vendorId)
//                .orElseThrow(() -> new RuntimeException("Shop not found"));
//
//        // ✅ SHOP LICENSE PHOTO APPROVAL
//        shop.setShopLicensePhotoStatus(DocumentStatus.APPROVED);
//
//        // ✅ CENTRALIZED STATE CONTROL
//        syncShopWithVendor(user, shop);
//
//        userRepository.save(user);
//        shopRepository.save(shop);
//    }
//
//
//        // 21 Jan 
//        
//    private void syncShopWithVendor(User vendor, Shop shop) {
//        if (vendor.getAccountStatus() == AccountStatus.APPROVED) {
//            shop.setApproved(true);
//            shop.setActive(true);
//        } else {
//            shop.setApproved(false);
//            shop.setActive(false);
//        }
////        shopRepository.save(shop);
//    }
//
//    // ================= REJECT =================
////    public void rejectVendor(Long vendorId, String reason) {
////
////        User user = userRepository.findById(vendorId)
////                .orElseThrow(() -> new RuntimeException("Vendor not found"));
////
////        user.setAccountStatus(AccountStatus.REJECTED);
////        user.setStatusReason(reason);
////        user.setStatusUpdatedAt(LocalDateTime.now());
////
////        shopRepository.findByUserId(vendorId).ifPresent(shop -> {
////            shop.setApproved(false);
////            shop.setActive(false);
////            shopRepository.findByUserId(vendorId)
////            .ifPresent(foundShop -> syncShopWithVendor(user, foundShop));
////        });
////
////        userRepository.save(user);
////    }
////    @Transactional
////    public void rejectVendor(Long vendorId, String reason) {
////
////        User user = userRepository.findById(vendorId)
////                .orElseThrow(() -> new RuntimeException("Vendor not found"));
////
////        // ✅ Update vendor status
////        user.setAccountStatus(AccountStatus.REJECTED);
////        user.setStatusReason(reason);
////        user.setStatusUpdatedAt(LocalDateTime.now());
////
////        // ✅ Sync shop state with vendor
////        shopRepository.findByUserId(vendorId)
////                .ifPresent(shop -> {
////                    syncShopWithVendor(user, shop);
////                    shopRepository.save(shop);
////                });
////
////        userRepository.save(user);
////    }
////    @Transactional
////    public void rejectVendor(Long vendorId, @Valid DocumentVerificationRequestDTO request) {
////
////        User user = userRepository.findById(vendorId)
////                .orElseThrow(() -> new RuntimeException("Vendor not found"));
////
////        user.setAccountStatus(AccountStatus.REJECTED);
////        user.setStatusReason(request);
////        user.setStatusUpdatedAt(LocalDateTime.now());
////
////        // ================= SET DOCUMENT STATUS BASED ON REASON =================
////        switch (request) {
////            case "AADHAAR_MISMATCH":
////                user.setAadhaarStatus(DocumentStatus.REJECTED);
////                break;
////            case "PAN_MISMATCH":
////                user.setPanStatus(DocumentStatus.REJECTED);
////                break;
////            case "UDYAM_MISMATCH":
////                user.setUdhyamStatus(DocumentStatus.REJECTED);
////                break;
////            case "SHOP_LICENSE_MISMATCH":
////                shopRepository.findByUserId(vendorId).ifPresent(shop -> 
////                    shop.setShopLicensePhotoStatus(DocumentStatus.REJECTED)
////                );
////                break;
////            default:
////                // If reason is OTHER or general, reject all
////                user.setAadhaarStatus(DocumentStatus.REJECTED);
////                user.setPanStatus(DocumentStatus.REJECTED);
////                user.setUdhyamStatus(DocumentStatus.REJECTED);
////                shopRepository.findByUserId(vendorId).ifPresent(shop -> 
////                    shop.setShopLicensePhotoStatus(DocumentStatus.REJECTED)
////                );
////                break;
////        }
////
////        // ================= SYNC SHOP STATUS =================
////        shopRepository.findByUserId(vendorId)
////                .ifPresent(shop -> {
////                    syncShopWithVendor(user, shop);
////                    shopRepository.save(shop);
////                });
////
////        userRepository.save(user);
////    }
//    @Transactional
//    public void rejectVendor(Long vendorId, DocumentVerificationRequestDTO request) {
//
//        User user = userRepository.findById(vendorId)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        Shop shop = shopRepository.findByUserId(vendorId)
//                .orElseThrow(() -> new RuntimeException("Shop not found"));
//
//        // 🔎 PREVIOUS STATUS (for audit)
//        String previousStatus = user.getAccountStatus() != null
//                ? user.getAccountStatus().name()
//                : AccountStatus.PENDING.name();
//
//        // ================= RESET ALL DOCUMENT STATUSES =================
//        user.setAadhaarStatus(DocumentStatus.APPROVED);
//        user.setPanStatus(DocumentStatus.APPROVED);
//        user.setUdhyamStatus(DocumentStatus.APPROVED);
//        shop.setShopLicensePhotoStatus(DocumentStatus.APPROVED);
//
//        // ================= RESOLVE REASON =================
//        String reason = (request.getRejectReason() == RejectReason.OTHER)
//                ? request.getCustomReason()
//                : request.getRejectReason().name();
//
//        // ================= ACCOUNT STATUS =================
//        user.setAccountStatus(AccountStatus.REJECTED);
//        user.setStatusReason(reason);
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        // ================= REJECT ONLY SELECTED DOCUMENT =================
//        switch (request.getRejectReason()) {
//            case AADHAAR_MISMATCH ->
//                    user.setAadhaarStatus(DocumentStatus.REJECTED);
//
//            case PAN_MISMATCH ->
//                    user.setPanStatus(DocumentStatus.REJECTED);
//
//            case UDYAM_MISMATCH ->
//                    user.setUdhyamStatus(DocumentStatus.REJECTED);
//
//            case SHOP_LICENSE_MISMATCH ->
//                    shop.setShopLicensePhotoStatus(DocumentStatus.REJECTED);
//
//            case OTHER -> {
//                // no specific document rejected
//            }
//        }
//
//        // ================= SHOP STATE =================
//        shop.setApproved(false);
//        shop.setActive(false);
//
//        // ================= SAVE =================
//        userRepository.save(user);
//        shopRepository.save(shop);
//
//        // 🔥 AUDIT LOG (ADMIN ACTION)
//        adminAuditService.log(
//                getAdminId(),                 // adminId
//                vendorId,                     // vendorId
//                "REJECT",                     // action
//                reason,                       // reason
//                previousStatus,               // from
//                AccountStatus.REJECTED.name() // to
//        );
//    }
//
//
//
//
//    // ================= BLOCK =================
////    public void blockVendor(Long id) {
////        User user = userRepository.findById(id)
////                .orElseThrow(() -> new RuntimeException("Vendor not found"));
////
////        user.setAccountStatus(AccountStatus.BLOCKED);
////        user.setStatusReason("Blocked by admin");
////        user.setStatusUpdatedAt(LocalDateTime.now());
////
////        shopRepository.findByUserId(id).ifPresent(shop -> {
////            shop.setApproved(false);
////            shop.setActive(false);
////            shopRepository.findByUserId(vendorId)
////            .ifPresent(foundShop -> syncShopWithVendor(user, foundShop));
////        });
////
////        userRepository.save(user);
////    }
//    @Transactional
//    public void blockVendor(Long vendorId) {
//
//        User user = userRepository.findById(vendorId)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        user.setAccountStatus(AccountStatus.BLOCKED);
//        user.setStatusReason("Blocked by admin");
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        shopRepository.findByUserId(vendorId)
//                .ifPresent(shop -> {
//                    syncShopWithVendor(user, shop);
//                    shopRepository.save(shop);
//                });
//
//        userRepository.save(user);
//    }
//
//    // ================= UNBLOCK =================
////    public void unblockVendor(Long id) {
////
////        User user = userRepository.findById(id)
////                .orElseThrow(() -> new RuntimeException("Vendor not found"));
////
////        user.setAccountStatus(AccountStatus.APPROVED);
////        user.setStatusReason("Unblocked by admin");
////        user.setStatusUpdatedAt(LocalDateTime.now());
////
////        shopRepository.findByUserId(id).ifPresent(shop -> {
////            shop.setApproved(true);
////            shop.setActive(true);
////            shopRepository.save(shop);
////        });
////
////        userRepository.save(user);
////    }
////}
//    @Transactional
//    public void unblockVendor(Long vendorId) {
//
//        User user = userRepository.findById(vendorId)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        user.setAccountStatus(AccountStatus.APPROVED);
//        user.setStatusReason("Unblocked by admin");
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        shopRepository.findByUserId(vendorId)
//                .ifPresent(shop -> {
//                    syncShopWithVendor(user, shop);
//                    shopRepository.save(shop);
//                });
//
//        userRepository.save(user);
//    }
//    
//    private void validateDocumentsForRestore(User user, Shop shop) {
//
//        if (user.getAadhaarStatus() == DocumentStatus.REJECTED ||
//            user.getPanStatus() == DocumentStatus.REJECTED ||
//            user.getUdhyamStatus() == DocumentStatus.REJECTED ||
//            shop.getShopLicensePhotoStatus() == DocumentStatus.REJECTED) {
//
//            throw new IllegalStateException(
//                    "Vendor cannot be restored because one or more documents are rejected. " +
//                    "Please approve documents before restoring."
//            );
//        }
//    }
//
//    
//    @Transactional
//    public void adminSoftDeleteVendor(Long vendorId) {
//
//        User user = userRepository.findById(vendorId)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        if (user.isDeleted()) {
//            throw new IllegalStateException("Vendor already deleted");
//        }
//
//        String previousStatus = user.getAccountStatus() != null
//                ? user.getAccountStatus().name()
//                : AccountStatus.PENDING.name();
//
//     // ✅ Soft delete using existing model
//        user.markAsDeletedByAdmin(getAdminUser());
//
//
//        // ✅ Use BLOCKED (not DELETED)
//        user.setAccountStatus(AccountStatus.BLOCKED);
//        user.setStatusReason("Deleted by admin");
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        shopRepository.findByUserId(vendorId)
//                .ifPresent(shop -> {
//                    shop.setActive(false);
//                    shop.setApproved(false);
//                    shopRepository.save(shop);
//                });
//
//        userRepository.save(user);
//
//        adminAuditService.log(
//                getAdminId(),
//                vendorId,
//                "SOFT_DELETE",
//                "Deleted by admin",
//                previousStatus,
//                AccountStatus.BLOCKED.name()
//        );
//    }
//
//    @Transactional
//    public void adminRestoreVendor(Long vendorId) {
//
//        User user = userRepository.findById(vendorId)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        if (!user.isDeleted()) {
//            throw new IllegalStateException("Vendor is not deleted");
//        }
//
//        Shop shop = shopRepository.findByUserId(vendorId)
//                .orElseThrow(() -> new RuntimeException("Shop not found"));
//
//        // 🔒 Document validation (unchanged)
//        validateDocumentsForRestore(user, shop);
//
//        // 🔎 Previous status for audit
//        String previousStatus = user.getAccountStatus() != null
//                ? user.getAccountStatus().name()
//                : AccountStatus.BLOCKED.name();
//
//        // ✅ Restore soft-delete flags
//        user.restore();
//
//        // ✅ Restore business state
//        user.setAccountStatus(AccountStatus.APPROVED);
//        user.setStatusReason("Restored by admin");
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        // 🔼 Restore shop
//        shop.setApproved(true);
//        shop.setActive(true);
//
//        userRepository.save(user);
//        shopRepository.save(shop);
//
//        // 🔥 Audit log
//        adminAuditService.log(
//                getAdminId(),
//                vendorId,
//                "RESTORE",
//                "Restored by admin",
//                previousStatus,
//                AccountStatus.APPROVED.name()
//        );
//    }
//
//
//
//
// // Method to fetch deleted vendors
//    public ApiResponseDTO<List<Map<String, Object>>> getDeletedVendors(int page, int size) {
//
//        Pageable pageable = PageRequest.of(
//                page, size, Sort.by(Sort.Direction.DESC, "deletedAt")
//        );
//
//        // Fetch deleted vendors based on vendor role IDs
//        Page<User> users = userRepository.findByRole_IdInAndDeletedTrue(vendorRoleIds(), pageable);
//
//        // Map Users to response data
//        List<Map<String, Object>> data = users.getContent().stream().map(u -> {
//            Map<String, Object> map = new HashMap<>();
//            map.put("id", u.getId());
//            map.put("sellerName", u.getName());
//            map.put("email", u.getEmail());
//            map.put("phone", u.getPhone());
//            map.put("vendorType", u.getRole().getName());
//            map.put("deletedAt", u.getDeletedAt());
//            map.put("statusReason", u.getStatusReason());
//
//            // Optional shop
//            Optional.ofNullable(u.getShop())
//                    .ifPresent(shop -> map.put("storeName", shop.getShopName()));
//
//            return map;
//        }).toList();
//
//        // Return API response
//        return new ApiResponseDTO<>(
//                true,
//                "Deleted vendors fetched",
//                data,
//                new PaginationInfo(
//                        users.getTotalElements(),
//                        users.getTotalPages(),
//                        users.getNumber(),
//                        users.getSize()
//                )
//        );
//    }
//
//    
//}


package com.agrowmart.admin_seller_management.service;

import com.agrowmart.admin_seller_management.dto.*;
import com.agrowmart.admin_seller_management.entity.Admin;
import com.agrowmart.admin_seller_management.entity.PaginationInfo;
import com.agrowmart.admin_seller_management.enums.AccountStatus;
import com.agrowmart.admin_seller_management.enums.DocumentStatus;
import com.agrowmart.admin_seller_management.enums.RejectReason;
import com.agrowmart.entity.*;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class AdminSellerService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ShopRepository shopRepository;
    private final AdminAuditService adminAuditService;
 // New Repositories for Product Fetching
    private final ProductRepository productRepository;
    private final WomenProductRepository womenProductRepository;
    public AdminSellerService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ShopRepository shopRepository,AdminAuditService adminAuditService,ProductRepository productRepository,WomenProductRepository womenproductRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.shopRepository = shopRepository;
        this.adminAuditService = adminAuditService;
        this.productRepository= productRepository;
        this.womenProductRepository=womenproductRepository;
    }

    // ──────────────────────────────────────────────
    // Helper methods
    // ──────────────────────────────────────────────

    private Long getAdminId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessValidationException("Admin not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof Admin admin)) {
            throw new BusinessValidationException("Invalid principal type: expected Admin, got " +
                    (principal != null ? principal.getClass().getName() : "null"));
        }
        return admin.getId();
    }

    private Admin getAdminUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessValidationException("Admin not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof Admin admin)) {
            throw new BusinessValidationException("Invalid principal type: expected Admin, got " +
                    (principal != null ? principal.getClass().getName() : "null"));
        }
        return admin;
    }

    private List<Short> vendorRoleIds() {
        return roleRepository.findByNameIn(
                        List.of("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN", "AGRI"))
                .stream()
                .map(Role::getId)
                .toList();
    }

    // ──────────────────────────────────────────────
    // LIST VENDORS
    // ──────────────────────────────────────────────

    public ApiResponseDTO<List<Map<String, Object>>> getVendors(
            int page, int size, String search, AccountStatus status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<User> users;
        List<Short> roles = vendorRoleIds();

        if (search != null && !search.isBlank() && status != null) {
            users = userRepository.searchVendorsWithStatus(roles, search, status, pageable);
        } else if (search != null && !search.isBlank()) {
            users = userRepository.searchVendors(roles, search, pageable);
        } else if (status != null) {
            users = userRepository.findByRoleIdInAndAccountStatus(roles, status, pageable);
        } else {
            users = userRepository.findByRoleIdIn(roles, pageable);
        }

        List<Map<String, Object>> data = users.getContent().stream().map(this::mapToVendorListItem).toList();

        return new ApiResponseDTO<>(
                true,
                "Vendors fetched",
                data,
                new PaginationInfo(
                        users.getTotalElements(),
                        users.getTotalPages(),
                        users.getNumber(),
                        users.getSize()
                )
        );
    }

    private Map<String, Object> mapToVendorListItem(User u) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", u.getId());

        Shop shop = shopRepository.findByUserId(u.getId()).orElse(null);
        map.put("storeName", shop != null ? shop.getShopName() : u.getBusinessName());
        map.put("sellerName", u.getName());
        map.put("phone", u.getPhone());
        map.put("email", u.getEmail());
        map.put("photoUrl", u.getPhotoUrl());
        map.put("status", Optional.ofNullable(u.getAccountStatus())
                .map(AccountStatus::name)
                .orElse(AccountStatus.PENDING.name()));
        map.put("createdAt", u.getCreatedAt());
        map.put("vendorType", u.getRole() != null ? u.getRole().getName() : null);

        Subscription activeSub = u.getActiveSubscription();
        map.put("hasActiveSubscription", activeSub != null);
        if (activeSub != null) {
            map.put("subscriptionPlan", activeSub.getPlan().name());
            map.put("subscriptionExpiry", activeSub.getExpiryDate());
        }

        return map;
    }

    // ──────────────────────────────────────────────
    // GET PROFILE
    // ──────────────────────────────────────────────

    public VendorProfileResponseDTO getVendorProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));

        VendorProfileResponseDTO dto = new VendorProfileResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setBusinessName(user.getBusinessName());
        dto.setAddress(user.getAddress());
        dto.setPhotoUrl(user.getPhotoUrl());
        dto.setAccountStatus(user.getAccountStatus());
        dto.setStatusReason(user.getStatusReason());

        DocumentsDTO documents = new DocumentsDTO();
        documents.setAadhaar(user.getAadhaarImagePath());
        documents.setAadhaarStatus(user.getAadhaarStatus());
        documents.setPan(user.getPanImagePath());
        documents.setPanStatus(user.getPanStatus());
        documents.setUdyam(user.getUdyamRegistrationImagePath());
        documents.setUdyamStatus(user.getUdhyamStatus());

        Shop shop = shopRepository.findByUserId(id).orElse(null);
        if (shop != null) {
            documents.setShopLicensePhoto(shop.getShopLicensePhoto());
            documents.setShopLicensePhotoStatus(shop.getShopLicensePhotoStatus());
        } else {
            documents.setShopLicensePhoto(null);
            documents.setShopLicensePhotoStatus(DocumentStatus.PENDING);
        }
        dto.setDocuments(documents);

        if (shop != null) {
            ShopDetailsDTO shopDto = new ShopDetailsDTO();
            shopDto.setId(shop.getId());
            shopDto.setShopName(shop.getShopName());
            shopDto.setShopType(shop.getShopType());
            shopDto.setShopAddress(shop.getShopAddress());
            shopDto.setDescription(shop.getShopDescription());
            shopDto.setWorkingHours(shop.getWorkingHoursJson());
            shopDto.setShopPhoto(shop.getShopPhoto());
            shopDto.setShopCoverPhoto(shop.getShopCoverPhoto());
            shopDto.setShopLicense(shop.getShopLicense());
            shopDto.setShopLicensePhoto(shop.getShopLicensePhoto());
            shopDto.setApproved(shop.isApproved());
            shopDto.setActive(shop.isActive());
            shopDto.setVendorType(user.getRole() != null ? user.getRole().getName() : null);
            shopDto.setGstCertificateNumber(user.getGstCertificateNumber());
            dto.setShop(shopDto);
        }

        return dto;
    }

    // ──────────────────────────────────────────────
    // APPROVE VENDOR
    // ──────────────────────────────────────────────

    @Transactional
    public void approveVendor(Long vendorId) {
        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));

        if (user.isDeleted()) {
            throw new BusinessValidationException("Cannot approve a deleted vendor. Restore first.");
        }

        if (user.getAccountStatus() == AccountStatus.APPROVED) {
            throw new BusinessValidationException("Vendor is already approved.");
        }

        user.setAccountStatus(AccountStatus.APPROVED);
        user.setStatusReason("Approved by admin");
        user.setStatusUpdatedAt(LocalDateTime.now());

        user.setAadhaarStatus(DocumentStatus.APPROVED);
        user.setPanStatus(DocumentStatus.APPROVED);
        user.setUdhyamStatus(DocumentStatus.APPROVED);

        Shop shop = shopRepository.findByUserId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found for vendor id: " + vendorId));

        shop.setShopLicensePhotoStatus(DocumentStatus.APPROVED);

        syncShopWithVendor(user, shop);

        userRepository.save(user);
        shopRepository.save(shop);
    }

    private void syncShopWithVendor(User vendor, Shop shop) {
        boolean shouldBeActive = vendor.getAccountStatus() == AccountStatus.APPROVED;
        shop.setApproved(shouldBeActive);
        shop.setActive(shouldBeActive);
    }

    // ──────────────────────────────────────────────
    // REJECT VENDOR
    // ──────────────────────────────────────────────

    @Transactional
    public void rejectVendor(Long vendorId, DocumentVerificationRequestDTO request) {
        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));

        if (user.getAccountStatus() == AccountStatus.REJECTED) {
            throw new BusinessValidationException("Vendor is already rejected.");
        }

        Shop shop = shopRepository.findByUserId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found for vendor id: " + vendorId));

        String previousStatus = Optional.ofNullable(user.getAccountStatus())
                .map(AccountStatus::name)
                .orElse(AccountStatus.PENDING.name());

        // Reset documents before selective reject
        user.setAadhaarStatus(DocumentStatus.APPROVED);
        user.setPanStatus(DocumentStatus.APPROVED);
        user.setUdhyamStatus(DocumentStatus.APPROVED);
        shop.setShopLicensePhotoStatus(DocumentStatus.APPROVED);

        String reason = switch (request.getRejectReason()) {
            case OTHER -> {
                String custom = request.getCustomReason();
                yield (custom != null && !custom.trim().isEmpty()) ? custom.trim() : "Other reason (no details provided)";
            }
            default -> request.getRejectReason().name();
        };

        user.setAccountStatus(AccountStatus.REJECTED);
        user.setStatusReason(reason);
        user.setStatusUpdatedAt(LocalDateTime.now());

        // Selective document rejection
        switch (request.getRejectReason()) {
            case AADHAAR_MISMATCH   -> user.setAadhaarStatus(DocumentStatus.REJECTED);
            case PAN_MISMATCH       -> user.setPanStatus(DocumentStatus.REJECTED);
            case UDYAM_MISMATCH     -> user.setUdhyamStatus(DocumentStatus.REJECTED);
            case SHOP_LICENSE_MISMATCH -> shop.setShopLicensePhotoStatus(DocumentStatus.REJECTED);
            case OTHER -> { /* no specific document change */ }
        }

        shop.setApproved(false);
        shop.setActive(false);

        userRepository.save(user);
        shopRepository.save(shop);

        adminAuditService.log(
                getAdminId(),
                vendorId,
                "REJECT",
                reason,
                previousStatus,
                AccountStatus.REJECTED.name()
        );
    }

    // ──────────────────────────────────────────────
    // BLOCK / UNBLOCK
    // ──────────────────────────────────────────────

    @Transactional
    public void blockVendor(Long vendorId) {
        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));

        if (user.getAccountStatus() == AccountStatus.BLOCKED) {
            throw new BusinessValidationException("Vendor is already blocked.");
        }

        if (user.isDeleted()) {
            throw new BusinessValidationException("Cannot block a deleted vendor.");
        }

        String previousStatus = Optional.ofNullable(user.getAccountStatus())
                .map(AccountStatus::name)
                .orElse("UNKNOWN");

        user.setAccountStatus(AccountStatus.BLOCKED);
        user.setStatusReason("Blocked by admin");
        user.setStatusUpdatedAt(LocalDateTime.now());

        shopRepository.findByUserId(vendorId).ifPresent(shop -> {
            syncShopWithVendor(user, shop);
            shopRepository.save(shop);
        });

        userRepository.save(user);

        adminAuditService.log(
                getAdminId(),
                vendorId,
                "BLOCK",
                "Blocked by admin",
                previousStatus,
                AccountStatus.BLOCKED.name()
        );
    }

    @Transactional
    public void unblockVendor(Long vendorId) {
        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));

        if (user.getAccountStatus() != AccountStatus.BLOCKED) {
            throw new BusinessValidationException("Vendor is not in blocked state.");
        }

        user.setAccountStatus(AccountStatus.APPROVED);
        user.setStatusReason("Unblocked by admin");
        user.setStatusUpdatedAt(LocalDateTime.now());

        shopRepository.findByUserId(vendorId).ifPresent(shop -> {
            syncShopWithVendor(user, shop);
            shopRepository.save(shop);
        });

        userRepository.save(user);

        adminAuditService.log(
                getAdminId(),
                vendorId,
                "UNBLOCK",
                "Unblocked by admin",
                AccountStatus.BLOCKED.name(),
                AccountStatus.APPROVED.name()
        );
    }

    // ──────────────────────────────────────────────
    // SOFT DELETE & RESTORE
    // ──────────────────────────────────────────────

    @Transactional
    public void adminSoftDeleteVendor(Long vendorId) {
        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));

        if (user.isDeleted()) {
            throw new BusinessValidationException("Vendor is already deleted.");
        }

        String previousStatus = Optional.ofNullable(user.getAccountStatus())
                .map(AccountStatus::name)
                .orElse(AccountStatus.PENDING.name());

        user.markAsDeletedByAdmin(getAdminUser());
        user.setAccountStatus(AccountStatus.BLOCKED);
        user.setStatusReason("Deleted by admin");
        user.setStatusUpdatedAt(LocalDateTime.now());

        shopRepository.findByUserId(vendorId).ifPresent(shop -> {
            shop.setActive(false);
            shop.setApproved(false);
            shopRepository.save(shop);
        });

        userRepository.save(user);

        adminAuditService.log(
                getAdminId(),
                vendorId,
                "SOFT_DELETE",
                "Deleted by admin",
                previousStatus,
                AccountStatus.BLOCKED.name()
        );
    }

    @Transactional
    public void adminRestoreVendor(Long vendorId) {
        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));

        if (!user.isDeleted()) {
            throw new BusinessValidationException("Vendor is not deleted.");
        }

        Shop shop = shopRepository.findByUserId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found for vendor id: " + vendorId));

        validateDocumentsForRestore(user, shop);

        String previousStatus = Optional.ofNullable(user.getAccountStatus())
                .map(AccountStatus::name)
                .orElse(AccountStatus.BLOCKED.name());

        user.restore();
        user.setAccountStatus(AccountStatus.APPROVED);
        user.setStatusReason("Restored by admin");
        user.setStatusUpdatedAt(LocalDateTime.now());

        shop.setApproved(true);
        shop.setActive(true);

        userRepository.save(user);
        shopRepository.save(shop);

        adminAuditService.log(
                getAdminId(),
                vendorId,
                "RESTORE",
                "Restored by admin",
                previousStatus,
                AccountStatus.APPROVED.name()
        );
    }

    private void validateDocumentsForRestore(User user, Shop shop) {
        if (user.getAadhaarStatus() == DocumentStatus.REJECTED ||
            user.getPanStatus() == DocumentStatus.REJECTED ||
            user.getUdhyamStatus() == DocumentStatus.REJECTED ||
            shop.getShopLicensePhotoStatus() == DocumentStatus.REJECTED) {

            throw new BusinessValidationException(
                    "Cannot restore vendor: one or more documents are still rejected. " +
                    "Please approve all required documents first."
            );
        }
    }

    // ──────────────────────────────────────────────
    // DELETED VENDORS LIST
    // ──────────────────────────────────────────────

    public ApiResponseDTO<List<Map<String, Object>>> getDeletedVendors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "deletedAt"));

        Page<User> users = userRepository.findByRole_IdInAndDeletedTrue(vendorRoleIds(), pageable);

        List<Map<String, Object>> data = users.getContent().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("sellerName", u.getName());
            map.put("email", u.getEmail());
            map.put("phone", u.getPhone());
            map.put("vendorType", u.getRole() != null ? u.getRole().getName() : null);
            map.put("deletedAt", u.getDeletedAt());
            map.put("statusReason", u.getStatusReason());

            Optional.ofNullable(u.getShop())
                    .ifPresent(shop -> map.put("storeName", shop.getShopName()));

            return map;
        }).toList();

        return new ApiResponseDTO<>(
                true,
                "Deleted vendors fetched",
                data,
                new PaginationInfo(
                        users.getTotalElements(),
                        users.getTotalPages(),
                        users.getNumber(),
                        users.getSize()
                )
        );
    }
 // 🔥 NEW: Specific Seller Products Logic (Regular + Women Products)
    public List<Map<String, Object>> getProductsByVendorId(Long vendorId) {
        List<Map<String, Object>> regularProducts = productRepository.findByMerchantId(vendorId)
                .stream().map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("name", p.getProductName());
                    map.put("price", "Price N/A"); 
                    map.put("img", (p.getImagePaths() != null && !p.getImagePaths().isBlank()) ? p.getImagePaths().split(",")[0] : null);
                    map.put("status", p.getApprovalStatus());
                    map.put("type", "REGULAR");
                    return map;
                }).toList();

        List<Map<String, Object>> womenProducts = womenProductRepository.findBySellerId(vendorId)
                .stream().map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("name", p.getName());
                    map.put("price", "₹" + p.getMinPrice()); 
                    map.put("img", (p.getImageUrlList() != null && !p.getImageUrlList().isEmpty()) ? p.getImageUrlList().get(0) : null);
                    map.put("status", p.getApprovalStatus());
                    map.put("type", "WOMEN");
                    return map;
                }).toList();

        List<Map<String, Object>> allProducts = new ArrayList<>();
        allProducts.addAll(regularProducts);
        allProducts.addAll(womenProducts);
        return allProducts;
    }

}