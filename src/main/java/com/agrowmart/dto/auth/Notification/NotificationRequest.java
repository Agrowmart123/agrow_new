
package com.agrowmart.dto.auth.Notification;

public record NotificationRequest(
		Long userId,
     String title,
     String body
) {}