package com.agrowmart.admin_seller_management.service;

import com.agrowmart.admin_seller_management.dto.DocumentVerificationRequestDTO;
import com.agrowmart.entity.User;
import com.agrowmart.admin_seller_management.enums.AccountStatus;
import com.agrowmart.admin_seller_management.enums.RejectReason;
import com.agrowmart.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class AdminVerificationService {

    private final UserRepository userRepository;

    public AdminVerificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void approveVendor(Long vendorId) {

        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        user.setAccountStatus(AccountStatus.APPROVED);
        user.setStatusReason("Approved by admin");
        user.setStatusUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Transactional
    public void rejectVendor(Long vendorId, DocumentVerificationRequestDTO request) {

        User user = userRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        user.setAccountStatus(AccountStatus.REJECTED);

        if (request.getRejectReason() == RejectReason.OTHER) {
            user.setStatusReason(request.getCustomReason());
        } else {
            user.setStatusReason(request.getRejectReason().getValue());
        }

        user.setStatusUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}