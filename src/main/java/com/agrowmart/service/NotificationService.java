//
//package com.agrowmart.service;
//
//import com.agrowmart.entity.Notification;
//import com.agrowmart.entity.User;
//import com.agrowmart.repository.NotificationRepository;
//import com.agrowmart.repository.UserRepository;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.Message;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//
//@Service
//public class NotificationService {
//
//    private final UserRepository userRepository;
//    private final NotificationRepository notificationRepository;
//
//    public NotificationService(UserRepository userRepository, NotificationRepository notificationRepository) {
//        this.userRepository = userRepository;
//        this.notificationRepository = notificationRepository;
//    }
//
//    public void sendNotification(Long userId, String title, String body, Map<String, String> data) {
//        User user = userRepository.findById(userId).orElse(null);
//
//        if (user == null || user.getFcmToken() == null || user.getFcmToken().trim().isEmpty()) {
//            System.out.println("Skipped notification → User ID: " + userId + " (No FCM token)");
//            return;
//        }
//
//        String token = user.getFcmToken();
//
//        Notification log = new Notification(user, token, title, body);
//        log.setSuccess(false);
//        notificationRepository.save(log);
//
//        try {
//            Message.Builder msg = Message.builder()
//                    .setToken(token)
//                    .putData("title", title)
//                    .putData("body", body)
//                    .putData("click_action", "FLUTTER_NOTIFICATION_CLICK");
//
//            if (data != null) {
//                data.forEach(msg::putData);
//            }
//
//            String messageId = FirebaseMessaging.getInstance().send(msg.build());
//
//            log.setSuccess(true);
//            log.setMessageId(messageId);
//            notificationRepository.save(log);
//
//            System.out.println("Sent → " + user.getName() + " | " + title + " | ID: " + messageId);
//
//        } catch (Exception e) {
//            log.setMessageId("FAILED: " + e.getMessage());
//            notificationRepository.save(log);
//            System.err.println("FCM Failed for " + user.getName() + ": " + e.getMessage());
//        }
//    }
//}
//
//
//


//-----------
package com.agrowmart.service;

import com.agrowmart.entity.Notification;
import com.agrowmart.entity.User;
import com.agrowmart.repository.NotificationRepository;
import com.agrowmart.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificationService {

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
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getFcmToken() == null || user.getFcmToken().trim().isEmpty()) {
            System.out.println("Skipped notification → User ID: " + userId + " (No valid FCM token)");
            return;
        }

        String token = user.getFcmToken();

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
        // Assuming your User entity has a relationship with Role
        // Adjust query according to your actual User/Role structure
        List<User> usersInRole = userRepository.findByRoleName(roleName); // ← you need this method in UserRepository

        if (usersInRole == null || usersInRole.isEmpty()) {
            System.out.println("No users found with role: " + roleName);
            return;
        }

        for (User user : usersInRole) {
            sendNotification(user.getId(), title, body, data);
        }
    }
}