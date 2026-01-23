package com.agrowmart.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class Fast2SmsService {

    @Value("${fast2sms.authorization}")
    private String authorization;

    @Value("${fast2sms.sender_id}")
    private String senderId;

    @Value("${fast2sms.route}")
    private String route;

    @Value("${fast2sms.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Send OTP using Fast2SMS
     * @param phone 10-digit number (without +91)
     * @param otpCode 6-digit OTP
     * @param purpose (optional - for logging)
     */
    public void sendOtp(String phone, String otpCode, String purpose) {
        // Clean phone number (remove +91 or spaces)
        String cleanPhone = phone.replace("+91", "").replace(" ", "").trim();

        // Build message (customize as needed)
        String message = "Your AgroMart OTP is: " + otpCode + " (valid for 5 minutes). Do not share.";

        // Build query params
        Map<String, String> params = new HashMap<>();
        params.put("authorization", authorization);
        params.put("route", route);
        params.put("sender_id", senderId);
        params.put("message", message);
        params.put("numbers", cleanPhone);

        // Optional: If using DLT template
        // params.put("template_id", "your_template_id");
        // params.put("variables_values", otpCode + "|" + purpose);

        // Build full URL
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("authorization", "{authorization}")
                .queryParam("route", "{route}")
                .queryParam("sender_id", "{sender_id}")
                .queryParam("message", "{message}")
                .queryParam("numbers", "{numbers}")
                .buildAndExpand(params)
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Fast2SMS sent to " + cleanPhone + " | Response: " + response);
        } catch (Exception e) {
            System.err.println("Fast2SMS failed for " + cleanPhone + ": " + e.getMessage());
            // Fallback for development
            System.out.println("DEV MODE - OTP: " + otpCode + " → " + cleanPhone);
        }
    }
}