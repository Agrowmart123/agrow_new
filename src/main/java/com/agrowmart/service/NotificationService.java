package com.agrowmart.service;

import com.agrowmart.entity.Notification;
import com.agrowmart.entity.User;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.repository.NotificationRepository;
import com.agrowmart.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class NotificationService {
	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public NotificationService(UserRepository userRepository, 
                              NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Sends a push notification via FCM and logs it.
     * The extra data map is optional (can be null).
     *
     * @param userId Recipient user ID
     * @param title Notification title
     * @param body Notification body/message
     * @param data Optional extra key-value data (can be null)
     */
    public void sendNotification(Long userId, String title, String body, Map<String, String> data) {
        // Find user
    	
    	if (userId == null) {
            log.warn("Cannot send notification: userId is null");
            return;
        }

        if (title == null || title.trim().isEmpty() || body == null || body.trim().isEmpty()) {
            throw new BusinessValidationException("Notification title and body are required");
        }
    	
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn("User not found for notification: userId={}", userId);
            return;
        }

        String token = user.getFcmToken();
        if (token == null || token.trim().isEmpty()) {
            log.info("Skipped notification → User ID: {} (No valid FCM token)", userId);
            return;
        }
        // Create log entry
        Notification log = new Notification(user, token, title, body);
        log.setSuccess(false);
        notificationRepository.save(log);

        try {
            // Build FCM message
            Message.Builder msgBuilder = Message.builder()
                    .setToken(token)
                    .putData("title", title)
                    .putData("body", body)
                    .putData("click_action", "FLUTTER_NOTIFICATION_CLICK");

            // Add optional data safely
            if (data != null && !data.isEmpty()) {
                data.forEach(msgBuilder::putData);
            }

            // Send message
            String messageId = FirebaseMessaging.getInstance().send(msgBuilder.build());

            // Update log on success
            log.setSuccess(true);
            log.setMessageId(messageId);
            notificationRepository.save(log);

            System.out.println("Notification sent → User: " + user.getName() + 
                             " | Title: " + title + " | FCM ID: " + messageId);
        } catch (Exception e) {
            // Log failure
            log.setMessageId("FAILED: " + e.getMessage());
            notificationRepository.save(log);

            System.err.println("FCM send failed for user " + user.getName() + 
                             ": " + e.getMessage());
            // Optional: rethrow if you want calling code to handle it
            // throw new RuntimeException("Failed to send notification", e);
        }
    }

    /**
     * Convenience overload - when you don't need extra data
     */
    public void sendNotification(Long userId, String title, String body) {
        sendNotification(userId, title, body, null);
    }

 // In NotificationService.java (add this new method)
    public void sendNotificationToRole(String roleName, String title, String body, Map<String, String> data) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new BusinessValidationException("Role name is required for role-based notification");
        }

        if (title == null || title.trim().isEmpty() || body == null || body.trim().isEmpty()) {
            throw new BusinessValidationException("Title and body are required for notification");
        }

        List<User> users = userRepository.findByRoleName(roleName);
        if (users == null || users.isEmpty()) {
            log.info("No users found with role: {}", roleName);
            return;
        }

        log.info("Sending notification to {} users with role: {}", users.size(), roleName);

        for (User user : users) {
            sendNotification(user.getId(), title, body, data);
        }
    }
}