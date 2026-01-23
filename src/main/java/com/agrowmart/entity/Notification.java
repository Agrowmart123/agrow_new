//// src/main/java/com/agrowmart/entity/Notification.java
//package com.agrowmart.entity;
//
//import jakarta.persistence.*;
//import org.hibernate.annotations.CreationTimestamp;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "notifications")
//public class Notification {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String token;
//    private String title;
//    private String body;
//    private String messageId;
//    private boolean success = false;
//
//    ;
//
//    @CreationTimestamp
//    @Column(updatable = false)
//    private LocalDateTime sentAt;
//
//    // Default constructor
//    public Notification() {}
//
//    // Constructor without id (for new records)
//    public Notification(String token, String title, String body) {
//        this.token = token;
//        this.title = title;
//        this.body = body;
//    }
//
//    // Getters and Setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getToken() { return token; }
//    public void setToken(String token) { this.token = token; }
//
//    public String getTitle() { return title; }
//    public void setTitle(String title) { this.title = title; }
//
//    public String getBody() { return body; }
//    public void setBody(String body) { this.body = body; }
//
//    public String getMessageId() { return messageId; }
//    public void setMessageId(String messageId) { this.messageId = messageId; }
//
//    public boolean isSuccess() { return success; }
//    public void setSuccess(boolean success) { this.success = success; }
//
//    public LocalDateTime getSentAt() { return sentAt; }
//    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
//}


// src/main/java/com/agrowmart/entity/Notification.java
package com.agrowmart.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fcm_token", nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String body;

    @Column(name = "message_id", length = 255)
    private String messageId;

    private boolean success = false;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    // Required empty constructor
    public Notification() {}

    public Notification(User user, String token, String title, String body) {
        this.user = user;
        this.token = token;
        this.title = title;
        this.body = body;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}