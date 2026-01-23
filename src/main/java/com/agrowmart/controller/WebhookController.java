package com.agrowmart.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.agrowmart.service.RazorpayService;
import com.agrowmart.util.RazorpaySignatureUtil;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final RazorpayService razorpayService;
    private final RazorpaySignatureUtil signatureUtil;  // ← Add this field

    public WebhookController(RazorpayService razorpayService, RazorpaySignatureUtil signatureUtil) {
        this.razorpayService = razorpayService;
        this.signatureUtil = signatureUtil;  // ← Inject it
    }

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        // Use the injected instance method (non-static)
        if (!signatureUtil.verify(payload, signature)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
        }

        razorpayService.processWebhookEvent(new JSONObject(payload));
        return ResponseEntity.ok("Webhook processed");
    }
}