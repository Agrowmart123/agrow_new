package com.agrowmart.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
@Component
public class RazorpaySignatureUtil {
    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public static boolean verify(String payload, String receivedSignature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(null, null);
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder generatedSignature = new StringBuilder();
            for (byte b : hash) {
                generatedSignature.append(String.format("%02x", b));
            }
            return generatedSignature.toString().equals(receivedSignature);
        } catch (Exception e) {
            return false;
        }
    }
}