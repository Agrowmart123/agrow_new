//// Author Vishal Sapkal 
////Date :- 10-12-2025
//// this is not work because payment  for google console subscraption - 15 k 
//package com.agrowmart.config;
//
//import com.agrowmart.dto.auth.JwtResponse;
//import com.agrowmart.entity.User;
//import com.agrowmart.service.AuthService;
//import com.agrowmart.service.CustomOAuth2User;
//import com.agrowmart.util.JwtUtil;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//
//@Component
//public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final JwtUtil jwtUtil;
//    private final AuthService authService;
//    private final ObjectMapper objectMapper;
//
//    // Constructor injection – IntelliJ loves this, no warnings ever
//    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil, AuthService authService, ObjectMapper objectMapper) {
//        this.jwtUtil = jwtUtil;
//        this.authService = authService;
//        this.objectMapper = objectMapper;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        Authentication authentication) throws IOException {
//
//        // Get the logged-in user from our CustomOAuth2User
//        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
//        User user = oauthUser.getUser();
//
//        // Generate tokens
//        String accessToken = jwtUtil.generateToken(user.getId());
//        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
//
//        // Save FCM token if sent from mobile app (e.g., Android/iOS)
//        String fcmToken = request.getParameter("fcmToken");
//        if (fcmToken != null && !fcmToken.isBlank()) {
//            user.setFcmToken(fcmToken);
//            authService.updateFcmToken(user.getId(), fcmToken);
//        }
//
//        // Build response
//        JwtResponse jwtResponse = new JwtResponse(
//            accessToken,
//            refreshToken,
//            LocalDateTime.now().plusHours(24)  // token expires in 24 hours
//        );
//
//        // Send JSON response
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(objectMapper.writeValueAsString(jwtResponse));
//    }
//}