// src/main/java/com/example/demo/dto/auth/JwtResponse.java
package com.agrowmart.dto.auth;

import java.time.LocalDateTime;

public record JwtResponse(String token, String refreshToken, LocalDateTime expiresAt) {}