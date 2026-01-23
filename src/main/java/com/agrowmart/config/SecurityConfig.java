

//-----------------------------------------------

//
//package com.agrowmart.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.List;
//
//@Configuration
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtFilter;
//
//    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
//        this.jwtFilter = jwtFilter;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOriginPatterns(List.of(
//                "http://localhost:*",
//                "http://192.168.*:*",
//                "https://*.vercel.app",
//                "https://agrowmart.vercel.app",
//                "capacitor://localhost",
//                "http://localhost"
//        ));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setExposedHeaders(List.of("Authorization"));
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .csrf(csrf -> csrf.disable())
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authorizeHttpRequests(auth -> auth
//
//                // ========================= PUBLIC ACCESS (No Login Needed) =========================
//                .requestMatchers(
//                        "/api/auth/**",
//                        "/api/customer/**",           // registration, etc.
//                        "/api/farmer/**",
//                        "/api/social/**",
//                        "/api/products/search",
//                        "/api/doctors/all",
//                        "/api/doctors/{id}",
//                        "/api/notification/**",
//                        "/swagger-ui/**",
//                        // ✅ ADD THIS
//                        "/api/public",
//
//                        "/v3/api-docs/**",
//                        "/swagger-ui.html",
//                        "/api/public/top-rated-vendors"  // ← Explicit for safety
//                ).permitAll()
//
//                .requestMatchers("/api/public/**").permitAll()
//
//                
//             // All product browsing is PUBLIC (Guest Mode)
//                .requestMatchers(HttpMethod.GET,
//                        "/api/products",
//                        "/api/products/**",
//                        "/api/women-products",
//                        "/api/women-products/**",
//                        "/api/v1/agri/products/**",
//                        "/api/v1/agri/products/search",
//                        "/api/categories",
//                        "/api/categories/**"
//
//                ).permitAll()
//                
//                
//                
//                .requestMatchers("/api/customer/auth/**", "/api/customer/addresses/**", "/api/auth/update-profile","/api/customer/**")
//                .hasAuthority("CUSTOMER")
//                
//                .requestMatchers("/api/v1/subscription/webhook").permitAll()     // Razorpay must call this
//                .requestMatchers("/api/v1/subscription/**").authenticated()     // status & upgrade need login
//                
//             // === RATING MODULE - PUBLIC & PROTECTED ENDPOINTS ===
//
//                // Public: Anyone can view vendor rating summary (average, 5-star bars, reviews)
//                // Fixed with regex to properly match numeric vendor IDs (prevents dot confusion)
//                .requestMatchers(HttpMethod.GET, "/api/ratings/vendor/{vendorId:\\d+}").permitAll()
//
//                // Alternative fallback (if you prefer Ant-style) - both work together
//                .requestMatchers(HttpMethod.GET, "/api/ratings/vendor/**").permitAll()
//
//                // Only authenticated CUSTOMER can submit or update rating
//                .requestMatchers(HttpMethod.POST, "/api/ratings").hasAuthority("CUSTOMER")
//
//                // Only the customer who owns the rating can delete it
//                .requestMatchers(HttpMethod.DELETE, "/api/ratings/{id}").hasAuthority("CUSTOMER")
//                .requestMatchers(HttpMethod.DELETE, "/api/ratings/**").hasAuthority("CUSTOMER") // safety
//                
//                // Public can VIEW all products (regular + women products)
//                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
//                .requestMatchers(HttpMethod.GET, "/api/women-products", "/api/women-products/**").permitAll()
//
//                
//         //----------------
//             // NEW: Public can view and search Agri products
//                .requestMatchers(HttpMethod.GET, "/api/v1/agri/products/**").permitAll()
//                .requestMatchers(HttpMethod.GET, "/api/v1/agri/products/search").permitAll()      
//                
//             // ========================= PROTECTED ENDPOINTS =========================
//
//                // AGRI Vendors: Full CRUD on their own agri products (POST, PUT, DELETE, etc.)
//                .requestMatchers("/api/v1/agri/products/**").hasAuthority("AGRI")
//                // Temporary testing endpoint
//                .requestMatchers("/api/vendor/free-gift-offer/**").permitAll()
//
//                // Block OAuth2 junk paths
//                .requestMatchers("/oauth2/**", "/login/oauth2/code/**", "/login").denyAll()
//
//                // ========================= PROTECTED ENDPOINTS =========================
//
//                
//                
//             // NEW: Vendor Online/Offline Status (restricted to vendors)
//                .requestMatchers("/api/auth/status")
//                    .hasAnyAuthority("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN", "FARMER", "AGRI")
//
//                    
//                    .requestMatchers("/api/orders/{orderId}/scan")
//                    .hasAnyAuthority("DELIVERY")
//                
//                    
//                // Customer profile & orders
//                .requestMatchers("/api/customer/me", "/api/customer/profile", "/api/customer/**")
//                    .hasAuthority("CUSTOMER")
//
//                .requestMatchers(HttpMethod.POST, "/api/orders", "/api/orders/create")
//                    .hasAuthority("CUSTOMER")
//
//                .requestMatchers("/api/orders/my", "/api/orders/customer/**")
//                    .hasAuthority("CUSTOMER")
//
//                // Regular vendors (vegetable, dairy, etc.) manage their products
//                .requestMatchers("/api/products", "/api/products/**")
//                    .hasAnyAuthority("VEGETABLE", "DAIRY", "SEAFOODMEAT", "FARMER")
//
//                // Women vendors manage their products (POST, PUT, DELETE to /api/women-products/**)
//                .requestMatchers("/api/women-products/**")
//                    .hasAuthority("WOMEN")
//
//                // Doctors
//                .requestMatchers("/api/doctors/profile", "/api/doctors/profile/**")
//                    .hasAuthority("DOCTOR")
//
//                // Vendors see and accept/reject orders
//                .requestMatchers("/api/orders/vendor/**", "/api/orders/pending")
//                    .hasAnyAuthority("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN", "FARMER")
//
//                .requestMatchers(HttpMethod.POST, "/api/orders/accept/**", "/api/orders/reject/**")
//                    .hasAnyAuthority("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN", "FARMER")
//
//                // Authenticated user info
//                .requestMatchers("/api/auth/me", "/api/auth/update-profile", "/api/auth/upload-photo")
//                    .authenticated()
//
//                // Free Gift Offers - Secured for all vendors including WOMEN
//                .requestMatchers(HttpMethod.POST, "/api/vendor/free-gift-offer")
//                    .hasAnyAuthority("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN", "FARMER")
//                .requestMatchers(HttpMethod.GET, "/api/vendor/free-gift-offer")
//                    .hasAnyAuthority("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN", "FARMER")
//                .requestMatchers(HttpMethod.PUT, "/api/vendor/free-gift-offer/**")
//                    .hasAnyAuthority("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN", "FARMER")
//                .requestMatchers(HttpMethod.DELETE, "/api/vendor/free-gift-offer/**")
//                    .hasAnyAuthority("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN", "FARMER")
//
//                // All other requests need authentication
//                .anyRequest().authenticated()
//            )
//            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}



package com.agrowmart.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://192.168.*:*",
                "https://*.vercel.app",
                "https://agrowmart.vercel.app",
                "capacitor://localhost",
                "http://localhost"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ──────────────────────────────────────────────
                // 1. COMPLETELY PUBLIC ENDPOINTS (No auth required)
                // ──────────────────────────────────────────────
                .requestMatchers(
                        "/api/auth/**",                     // login, register, OTP, forgot password
                        "/api/customer/auth/**",            // customer login/register
                        "/api/farmer/**",                   // farmer register
                        "/api/social/**",
                        "/api/public/**",                   // public products, top vendors, etc.
                        "/api/products/search",
                        "/api/doctors/all",
                        "/api/doctors/{id}",
                        "/api/notification/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                ).permitAll()

                // Public GET access to all products (regular, women, agri)
                .requestMatchers(HttpMethod.GET,
                        "/api/products/**",
                        "/api/women-products/**",
                        "/api/v1/agri/products/**",
                        "/api/v1/agri/products/search",
                        "/api/categories/**"
                ).permitAll()

                // Razorpay webhooks (must be public)
                .requestMatchers("/webhook/razorpay", "/api/v1/subscription/webhook").permitAll()

                // ──────────────────────────────────────────────
                // 2. WEBSOCKET ENDPOINT - Allow connection (important!)
                // ──────────────────────────────────────────────
                .requestMatchers("/ws/**", "/ws").permitAll()  // ← Required for real-time

                // ──────────────────────────────────────────────
                // 3. PROTECTED ENDPOINTS (Require authentication + role)
                // ──────────────────────────────────────────────

                // Customer protected routes
                .requestMatchers(
                        "/api/customer/me",
                        "/api/customer/profile",
                        "/api/customer/addresses/**",
                        "/api/orders/my",
                        "/api/orders/my/active",
                        "/api/orders/{orderId}/status",
                        "/api/orders/create",
                        "/api/orders/cancel/**"
                ).hasAuthority("CUSTOMER")

                // Vendor protected routes (all vendor types)
                .requestMatchers(
                        "/api/orders/vendor/**",
                        "/api/orders/pending",
                        "/api/orders/scheduled",
                        "/api/orders/accept/**",
                        "/api/orders/reject/**",
                        "/api/orders/{orderId}/ready",
                        "/api/orders/{orderId}/generate-pickup-qr",
                        "/api/orders/vendor/cancel/**",
                        "/api/orders/cod-collected/**",
                        "/api/products/**",
                        "/api/women-products/**",
                        "/api/v1/agri/products/**",
                        "/api/auth/status",  // online/offline status
                        "/api/vendor/free-gift-offer/**"
                ).hasAnyAuthority("VEGETABLE", "DAIRY", "SEAFOODMEAT", "WOMEN", "FARMER", "AGRI")

                // Delivery Partner protected routes
                .requestMatchers(
                        "/api/orders/{orderId}/scan",
                        "/api/orders/{orderId}/deliver",
                        "/api/orders/delivery/active"
                ).hasAuthority("DELIVERY")

                // Doctor protected routes
                .requestMatchers("/api/doctors/profile/**").hasAuthority("DOCTOR")

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}



