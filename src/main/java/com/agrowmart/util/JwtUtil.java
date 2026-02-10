
package com.agrowmart.util;

import com.agrowmart.admin_seller_management.entity.Admin;
import com.agrowmart.entity.User;
import com.agrowmart.entity.customer.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ==================== GENERATE TOKENS ====================

    public String generateTokenForVendor(User user) {
        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("role", user.getRole().getName())
                .claim("type", "vendor")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateTokenForCustomer(Customer customer) {
        return Jwts.builder()
                .claim("userId", customer.getId())
                .claim("role", "CUSTOMER")
                .claim("type", "customer")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ==================== EXTRACT CLAIMS ====================

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // THIS WAS MISSING — NOW ADDED!
    public String extractUserType(String token) {
        return extractAllClaims(token).get("type", String.class); // returns "vendor" or "customer"
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // ==================== VALIDATION ====================

    public boolean validateToken(String token, Long userId) {
        Long tokenUserId = extractUserId(token);
        return tokenUserId != null && tokenUserId.equals(userId) && !isTokenExpired(token);
    }

    public boolean validateToken(String token, String email) {
        // Optional fallback if you ever store email as subject
        return !isTokenExpired(token);
    }
    
    
    
   // Admin  Side 
    
    // ── Admin Token Support ──
    public String generateTokenForAdmin(Admin admin) {
        return Jwts.builder()
                .claim("adminId", admin.getId())
                .claim("role", admin.getRole().name())
                .claim("type", "admin")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isAdminToken(String token) {
        try {
            return "admin".equals(extractAllClaims(token).get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public Long extractAdminId(String token) {
        try {
            return extractAllClaims(token).get("adminId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }
}