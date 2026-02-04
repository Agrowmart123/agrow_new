package com.agrowmart.service;

import com.agrowmart.entity.User;
import com.agrowmart.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled service that permanently deletes users who have been soft-deleted
 * for more than 7 days (including their associated Cloudinary images).
 */
@Service
public class UserCleanupService {

    private static final Logger log = LoggerFactory.getLogger(UserCleanupService.class);

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public UserCleanupService(
            UserRepository userRepository,
            CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Runs every day at 03:15 AM server time.
     * Deletes users soft-deleted more than 7 days ago.
     */
    @Scheduled(cron = "0 15 3 * * *")   // 03:15 every day
    @Transactional
    public void permanentlyDeleteOldMarkedUsers() {

        LocalDateTime threshold = LocalDateTime.now().minusDays(7);

        // Make sure this method exists in UserRepository
        List<User> toDelete = userRepository.findAllByDeletedTrueAndDeletedAtBefore(threshold);

        if (toDelete.isEmpty()) {
            log.info("No soft-deleted users older than 7 days found. Cleanup skipped.");
            return;
        }

        log.info("Starting permanent deletion of {} soft-deleted users older than 7 days", toDelete.size());

        int successCount = 0;
        int failureCount = 0;

        for (User user : toDelete) {
            try {
                // 1. Clean up Cloudinary images
                deleteCloudinaryImageIfExists(user.getPhotoUrl(), "profile photo");
                deleteCloudinaryImageIfExists(user.getAadhaarImagePath(), "Aadhaar");
                deleteCloudinaryImageIfExists(user.getPanImagePath(), "PAN");
                deleteCloudinaryImageIfExists(user.getUdyamRegistrationImagePath(), "Udyam");
                deleteCloudinaryImageIfExists(user.getFssaiLicensePath(), "FSSAI");

                // 2. Shop related images
                if (user.getShop() != null) {
                    deleteCloudinaryImageIfExists(user.getShop().getShopPhoto(), "shop photo");
                    deleteCloudinaryImageIfExists(user.getShop().getShopCoverPhoto(), "shop cover");
                    deleteCloudinaryImageIfExists(user.getShop().getShopLicensePhoto(), "shop license");
                }

                // 3. Finally delete the user entity
                userRepository.delete(user);

                successCount++;
                log.info("Permanently deleted user id={} | phone={} | deletedAt={}",
                        user.getId(), user.getPhone(), user.getDeletedAt());

            } catch (Exception e) {
                failureCount++;
                log.error("Failed to permanently delete user id={} | phone={}. Reason: {}",
                        user.getId(), user.getPhone(), e.getMessage(), e);
                // Optional: collect failed IDs → send alert / retry later
            }
        }

        log.info("Cleanup finished → Success: {}, Failed: {}", successCount, failureCount);
    }

    /**
     * Safely attempts to delete a Cloudinary asset if the URL looks valid.
     */
    private void deleteCloudinaryImageIfExists(String url, String description) {
        if (url == null || url.trim().isEmpty()) {
            return;
        }

        if (!url.contains("cloudinary.com") && !url.contains("res.cloudinary.com")) {
            log.debug("Skipping non-Cloudinary URL for {}: {}", description, url);
            return;
        }

        try {
            cloudinaryService.deleteByUrl(url);
            log.debug("Deleted Cloudinary asset for {}: {}", description, url);
        } catch (Exception e) {
            log.warn("Failed to delete Cloudinary asset for {}: {} → {}", description, url, e.getMessage());
            // Not critical — continue with DB deletion
        }
    }
}