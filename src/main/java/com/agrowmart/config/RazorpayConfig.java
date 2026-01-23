package com.agrowmart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.razorpay.RazorpayClient;

@Configuration
public class RazorpayConfig {

    // Match your properties file (add _id and _secret suffixes)
    @Value("${razorpay.key}")
    private String razorpayKeyId;

    @Value("${razorpay.secret}")
    private String razorpayKeySecret;
    
    @Bean
    public RazorpayClient razorpayClient() throws Exception {
        return new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }
}