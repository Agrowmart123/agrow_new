package com.agrowmart.admin_seller_management.service;

import com.agrowmart.admin_seller_management.dto.*;
import com.agrowmart.admin_seller_management.entity.PaginationInfo;
import com.agrowmart.admin_seller_management.enums.AccountStatus;
import com.agrowmart.admin_seller_management.enums.DocumentStatus;
import com.agrowmart.admin_seller_management.enums.RejectReason;
import com.agrowmart.entity.*;
import com.agrowmart.repository.*;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

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

    public AdminSellerService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ShopRepository shopRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.shopRepository = shopRepository;
    }

    // ================= ROLE IDS =================
    private List<Short> vendorRoleIds() {
        return roleRepository.findByNameIn(
                List.of("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN")
        ).stream()
         .map(Role::getId)
         .toList();
    }

    // ================= LIST VENDORS =================
    public ApiResponseDTO<List<Map<String, Object>>> getVendors(
            int page, int size, String search, AccountStatus status) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending()
        );

        Page<User> users;

        if (search != null && !search.isBlank() && status != null) {
            users = userRepository.searchVendorsWithStatus(
                    vendorRoleIds(), search, status, pageable);
        } else if (search != null && !search.isBlank()) {
            users = userRepository.searchVendors(
                    vendorRoleIds(), search, pageable);
        } else if (status != null) {
            users = userRepository.findByRoleIdInAndAccountStatus(
                    vendorRoleIds(), status, pageable);
        } else {
            users = userRepository.findByRoleIdIn(
                    vendorRoleIds(), pageable);
        }

        List<Map<String, Object>> data = users.getContent().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("storeName", u.getBusinessName());
            map.put("sellerName", u.getName());
            map.put("phone", u.getPhone());
            map.put("email", u.getEmail());
            map.put("photoUrl", u.getPhotoUrl());
            map.put("status",
                    u.getAccountStatus() != null
                            ? u.getAccountStatus().name()
                            : AccountStatus.PENDING.name());
            map.put("createdAt", u.getCreatedAt());
            //Added by Aakanksha-22/01/2026
         // ✅ Vendor Type (ROLE)
            map.put("vendorType",
                    u.getRole() != null ? u.getRole().getName() : null);
            

            // ✅ Subscription info
         // ✅ FIX: get active subscription properly
            Subscription activeSub = u.getActiveSubscription();

            map.put("hasActiveSubscription", activeSub != null);

            if (activeSub != null) {
                map.put("subscriptionPlan", activeSub.getPlan().name());
                map.put("subscriptionExpiry", activeSub.getExpiryDate());
            } else {
                map.put("subscriptionPlan", null);
                map.put("subscriptionExpiry", null);
            }


            map.put("createdAt", u.getCreatedAt());

            return map;
        }).toList();

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

    // ================= PROFILE =================
   
    public VendorProfileResponseDTO getVendorProfile(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        // ================= BASIC VENDOR INFO =================
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

        // ================= DOCUMENTS =================
        DocumentsDTO documents = new DocumentsDTO();

        documents.setAadhaar(user.getAadhaarImagePath());
        documents.setAadhaarStatus(user.getAadhaarStatus());

        documents.setPan(user.getPanImagePath());
        documents.setPanStatus(user.getPanStatus());

        documents.setUdyam(user.getUdyamRegistrationImagePath());
        documents.setUdyamStatus(user.getUdhyamStatus());

        // ================= SHOP + SHOP LICENSE DOCUMENT =================
        Shop shop = shopRepository.findByUserId(id).orElse(null);

        if (shop != null) {
            documents.setShopLicensePhoto(shop.getShopLicensePhoto());
            documents.setShopLicensePhotoStatus(shop.getShopLicensePhotoStatus());
        } else {
            documents.setShopLicensePhoto(null);
            documents.setShopLicensePhotoStatus(DocumentStatus.PENDING);
        }

        dto.setDocuments(documents);

        // ================= SHOP DETAILS =================
        if (shop != null) {
            ShopDetailsDTO shopDto = new ShopDetailsDTO();

            shopDto.setId(shop.getId());
            shopDto.setShopName(shop.getShopName());
            shopDto.setShopType(shop.getShopType());
            shopDto.setShopAddress(shop.getShopAddress());
            shopDto.setDescription(shop.getShopDescription());
            shopDto.setWorkingHours(shop.getWorkingHours());
            shopDto.setShopPhoto(shop.getShopPhoto());
            shopDto.setShopCoverPhoto(shop.getShopCoverPhoto());
            shopDto.setShopLicense(shop.getShopLicense());
            shopDto.setShopLicensePhoto(shop.getShopLicensePhoto());
            shopDto.setApproved(shop.isApproved());
            shopDto.setActive(shop.isActive());

            // ✅ Vendor Type (Role)
            shopDto.setVendorType(
                    user.getRole() != null ? user.getRole().getName() : null
            );

            // ✅ GST Number
            shopDto.setGstCertificateNumber(user.getGstCertificateNumber());

            dto.setShop(shopDto);
        }

        return dto;
    }


    // ================= APPROVE VENDOR (🔥 MAIN FIX) =================
    @Transactional
    public void approveVendor(Long vendorId) {

        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        // ✅ ACCOUNT STATUS
        user.setAccountStatus(AccountStatus.APPROVED);
        user.setStatusReason("Approved by admin");
        user.setStatusUpdatedAt(LocalDateTime.now());

        // ✅ DOCUMENT APPROVALS
        user.setAadhaarStatus(DocumentStatus.APPROVED);
        user.setPanStatus(DocumentStatus.APPROVED);
        user.setUdhyamStatus(DocumentStatus.APPROVED);

        // ✅ SHOP (must exist)
        Shop shop = shopRepository.findByUserId(vendorId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // ✅ SHOP LICENSE PHOTO APPROVAL
        shop.setShopLicensePhotoStatus(DocumentStatus.APPROVED);

        // ✅ CENTRALIZED STATE CONTROL
        syncShopWithVendor(user, shop);

        userRepository.save(user);
        shopRepository.save(shop);
    }


        // 21 Jan 
        
    private void syncShopWithVendor(User vendor, Shop shop) {
        if (vendor.getAccountStatus() == AccountStatus.APPROVED) {
            shop.setApproved(true);
            shop.setActive(true);
        } else {
            shop.setApproved(false);
            shop.setActive(false);
        }
//        shopRepository.save(shop);
    }

    // ================= REJECT =================
//    public void rejectVendor(Long vendorId, String reason) {
//
//        User user = userRepository.findById(vendorId)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        user.setAccountStatus(AccountStatus.REJECTED);
//        user.setStatusReason(reason);
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        shopRepository.findByUserId(vendorId).ifPresent(shop -> {
//            shop.setApproved(false);
//            shop.setActive(false);
//            shopRepository.findByUserId(vendorId)
//            .ifPresent(foundShop -> syncShopWithVendor(user, foundShop));
//        });
//
//        userRepository.save(user);
//    }
//    @Transactional
//    public void rejectVendor(Long vendorId, String reason) {
//
//        User user = userRepository.findById(vendorId)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        // ✅ Update vendor status
//        user.setAccountStatus(AccountStatus.REJECTED);
//        user.setStatusReason(reason);
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        // ✅ Sync shop state with vendor
//        shopRepository.findByUserId(vendorId)
//                .ifPresent(shop -> {
//                    syncShopWithVendor(user, shop);
//                    shopRepository.save(shop);
//                });
//
//        userRepository.save(user);
//    }
//    @Transactional
//    public void rejectVendor(Long vendorId, @Valid DocumentVerificationRequestDTO request) {
//
//        User user = userRepository.findById(vendorId)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        user.setAccountStatus(AccountStatus.REJECTED);
//        user.setStatusReason(request);
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        // ================= SET DOCUMENT STATUS BASED ON REASON =================
//        switch (request) {
//            case "AADHAAR_MISMATCH":
//                user.setAadhaarStatus(DocumentStatus.REJECTED);
//                break;
//            case "PAN_MISMATCH":
//                user.setPanStatus(DocumentStatus.REJECTED);
//                break;
//            case "UDYAM_MISMATCH":
//                user.setUdhyamStatus(DocumentStatus.REJECTED);
//                break;
//            case "SHOP_LICENSE_MISMATCH":
//                shopRepository.findByUserId(vendorId).ifPresent(shop -> 
//                    shop.setShopLicensePhotoStatus(DocumentStatus.REJECTED)
//                );
//                break;
//            default:
//                // If reason is OTHER or general, reject all
//                user.setAadhaarStatus(DocumentStatus.REJECTED);
//                user.setPanStatus(DocumentStatus.REJECTED);
//                user.setUdhyamStatus(DocumentStatus.REJECTED);
//                shopRepository.findByUserId(vendorId).ifPresent(shop -> 
//                    shop.setShopLicensePhotoStatus(DocumentStatus.REJECTED)
//                );
//                break;
//        }
//
//        // ================= SYNC SHOP STATUS =================
//        shopRepository.findByUserId(vendorId)
//                .ifPresent(shop -> {
//                    syncShopWithVendor(user, shop);
//                    shopRepository.save(shop);
//                });
//
//        userRepository.save(user);
//    }
    @Transactional
    public void rejectVendor(Long vendorId, DocumentVerificationRequestDTO request) {

        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Shop shop = shopRepository.findByUserId(vendorId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // ================= RESET ALL DOCUMENT STATUSES =================
        user.setAadhaarStatus(DocumentStatus.APPROVED);
        user.setPanStatus(DocumentStatus.APPROVED);
        user.setUdhyamStatus(DocumentStatus.APPROVED);
        shop.setShopLicensePhotoStatus(DocumentStatus.APPROVED);

        // ================= RESOLVE REASON =================
        String reason = (request.getRejectReason() == RejectReason.OTHER)
                ? request.getCustomReason()
                : request.getRejectReason().name();

        // ================= ACCOUNT STATUS =================
        user.setAccountStatus(AccountStatus.REJECTED);
        user.setStatusReason(reason);
        user.setStatusUpdatedAt(LocalDateTime.now());

        // ================= REJECT ONLY SELECTED DOCUMENT =================
        switch (request.getRejectReason()) {
            case AADHAAR_MISMATCH ->
                    user.setAadhaarStatus(DocumentStatus.REJECTED);

            case PAN_MISMATCH ->
                    user.setPanStatus(DocumentStatus.REJECTED);

            case UDYAM_MISMATCH ->
                    user.setUdhyamStatus(DocumentStatus.REJECTED);

            case SHOP_LICENSE_MISMATCH ->
                    shop.setShopLicensePhotoStatus(DocumentStatus.REJECTED);

            case OTHER -> {
                // no specific document rejected
            }
        }

        // ================= SHOP STATE =================
        shop.setApproved(false);
        shop.setActive(false);

        // ================= SAVE =================
        userRepository.save(user);
        shopRepository.save(shop);
    }



    // ================= BLOCK =================
//    public void blockVendor(Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        user.setAccountStatus(AccountStatus.BLOCKED);
//        user.setStatusReason("Blocked by admin");
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        shopRepository.findByUserId(id).ifPresent(shop -> {
//            shop.setApproved(false);
//            shop.setActive(false);
//            shopRepository.findByUserId(vendorId)
//            .ifPresent(foundShop -> syncShopWithVendor(user, foundShop));
//        });
//
//        userRepository.save(user);
//    }
    @Transactional
    public void blockVendor(Long vendorId) {

        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        user.setAccountStatus(AccountStatus.BLOCKED);
        user.setStatusReason("Blocked by admin");
        user.setStatusUpdatedAt(LocalDateTime.now());

        shopRepository.findByUserId(vendorId)
                .ifPresent(shop -> {
                    syncShopWithVendor(user, shop);
                    shopRepository.save(shop);
                });

        userRepository.save(user);
    }

    // ================= UNBLOCK =================
//    public void unblockVendor(Long id) {
//
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        user.setAccountStatus(AccountStatus.APPROVED);
//        user.setStatusReason("Unblocked by admin");
//        user.setStatusUpdatedAt(LocalDateTime.now());
//
//        shopRepository.findByUserId(id).ifPresent(shop -> {
//            shop.setApproved(true);
//            shop.setActive(true);
//            shopRepository.save(shop);
//        });
//
//        userRepository.save(user);
//    }
//}
    @Transactional
    public void unblockVendor(Long vendorId) {

        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        user.setAccountStatus(AccountStatus.APPROVED);
        user.setStatusReason("Unblocked by admin");
        user.setStatusUpdatedAt(LocalDateTime.now());

        shopRepository.findByUserId(vendorId)
                .ifPresent(shop -> {
                    syncShopWithVendor(user, shop);
                    shopRepository.save(shop);
                });

        userRepository.save(user);
    }
}
