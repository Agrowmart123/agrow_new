
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
import com.agrowmart.config.AdminJwtAuthenticationFilter;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final AdminJwtAuthenticationFilter AdminJwtAuthenticationFilter;
    

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
    		 AdminJwtAuthenticationFilter AdminJwtAuthenticationFilter) {
        this.jwtFilter = jwtFilter;
        this.AdminJwtAuthenticationFilter=AdminJwtAuthenticationFilter;
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
                		"/api/admin/auth/**",          
                        "/api/auth/**",                     // login, register, OTP, forgot password
                        "/api/customer/auth/**",            // customer login/register
                        "/api/farmer/**",                   // farmer register
                        "/api/social/**",
                        "/api/public/**",                   // public products, top vendors, etc.
                        "/api/products/search",
                        "/api/shops/search",
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

                
                
                .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/v1/super/**").hasAuthority("SUPER_ADMIN")
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
//            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//             .addFilterBefore(AdminJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
     // Add BOTH filters
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)                // normal user/vendor filter
        .addFilterBefore(AdminJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // admin filter
        return http.build();
    }
}



