
//-------
//// befoer add the custmer
//package com.agrowmart.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import com.agrowmart.entity.User;
//import com.agrowmart.repository.UserRepository;
//import com.agrowmart.util.JwtUtil;
//import java.io.IOException;
//import java.util.Collections;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
//
//    private final JwtUtil jwtUtil;
//    private final UserRepository userRepo;
//
//    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepo) {
//        this.jwtUtil = jwtUtil;
//        this.userRepo = userRepo;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain chain) throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//
//        if (header != null && header.startsWith("Bearer ")) {
//            String token = header.substring(7);
//
//            try {
//                Long userId = jwtUtil.extractUserId(token);
//
//                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                    User user = userRepo.findById(userId).orElse(null);
//
//                    if (user != null && jwtUtil.validateToken(token, userId)) {
//                        var authorities = Collections.singletonList(
//                            new SimpleGrantedAuthority(user.getRole().getName())
//                        );
//
//                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                            user, null, authorities
//                        );
//                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                        SecurityContextHolder.getContext().setAuthentication(auth);
//
//                        logger.debug("Authenticated user: {} (ID: {}) with role: {}",
//                            user.getName(), user.getId(), user.getRole().getName());
//                    } else {
//                        logger.warn("Invalid JWT: User not found or token invalid for userId: {}", userId);
//                    }
//                }
//            } catch (Exception e) {
//                logger.warn("JWT validation failed: {}", e.getMessage());
//                // Common causes: expired, malformed, invalid signature
//            }
//        } else {
//            logger.debug("No Bearer token found in request to {}", request.getRequestURI());
//        }
//
//        // ALWAYS continue the filter chain
//        chain.doFilter(request, response);
//    }
//    
//    
//    
//    
//  //---------------
//    
//    
//    
//}





// src/main/java/com/agrowmart/config/JwtAuthenticationFilter.java

package com.agrowmart.config;

import com.agrowmart.entity.User;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.repository.UserRepository;
import com.agrowmart.repository.customer.CustomerRepository;
import com.agrowmart.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   UserRepository userRepository,
                                   CustomerRepository customerRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Long userId = jwtUtil.extractUserId(token);
                String userType = jwtUtil.extractUserType(token); // "vendor" or "customer"

                if (userId != null && userType != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                    if ("vendor".equalsIgnoreCase(userType)) {
                        authenticateVendor(userId, token, request);

                    } else if ("customer".equalsIgnoreCase(userType)) {
                        authenticateCustomer(userId, token, request);

                    } else {
                        logger.warn("Unknown user type in JWT: {}", userType);
                    }
                }

            } catch (Exception e) {
                logger.warn("JWT processing failed: {}", e.getMessage());
            }
        } else {
            logger.debug("No Bearer token found for request: {}", request.getRequestURI());
        }

        chain.doFilter(request, response);
    }

    private void authenticateVendor(Long userId, String token, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElse(null);

        if (user != null && jwtUtil.validateToken(token, userId)) {
            String role = user.getRole().getName();

            var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);

            logger.debug("Authenticated VENDOR: {} (ID: {}) with role: {}", user.getName(), user.getId(), role);
        } else {
            logger.warn("Invalid vendor JWT for userId: {}", userId);
        }
    }

    private void authenticateCustomer(Long userId, String token, HttpServletRequest request) {
        Customer customer = customerRepository.findById(userId).orElse(null);

        if (customer != null && jwtUtil.validateToken(token, userId)) {
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("CUSTOMER"));

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(customer, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);

            logger.debug("Authenticated CUSTOMER: {} (ID: {})", customer.getFullName(), customer.getId());
        } else {
            logger.warn("Invalid customer JWT for userId: {}", userId);
        }
    }
}