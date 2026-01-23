// File: src/main/java/com/agrowmart/controller/NotificationController.java
package com.agrowmart.controller;


import com.agrowmart.dto.auth.Notification.NotificationRequest;
import com.agrowmart.entity.Notification;
import com.agrowmart.entity.User;
import com.agrowmart.repository.UserRepository;
import com.agrowmart.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository; // To validate user exists

    public NotificationController(NotificationService notificationService,UserRepository  userRepository) {
        this.notificationService = notificationService;
        this.userRepository=userRepository;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody NotificationRequest request) {
        try {
            // Validate user exists
            User user = userRepository.findById(request.userId())
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "User not found with ID: " + request.userId()
                ));
            }

            if (user.getFcmToken() == null || user.getFcmToken().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "User has no FCM token. Login from mobile app first!"
                ));
            }

            // This uses your WORKING sendNotification method
            notificationService.sendNotification(
                request.userId(),
                request.title(),
                request.body(),
                Map.of("type", "manual", "source", "admin_panel")
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Notification sent successfully!",
                "userId", request.userId(),
                "userName", user.getName(),
                "title", request.title()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "Failed to send: " + e.getMessage()
            ));
        }
    }

    /**
     * Health Check
     */
    @GetMapping("/")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification API is 100% WORKING & UP-TO-DATE!");
    }

}