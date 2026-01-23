package com.agrowmart.service;

import com.agrowmart.entity.User;
import com.agrowmart.entity.VendorPaymentDetails;
import com.agrowmart.entity.order.Order;
import com.agrowmart.entity.order.Payment;
import com.agrowmart.entity.order.Settlement;
import com.agrowmart.repository.OrderRepository;
import com.agrowmart.repository.PaymentRepository;
import com.agrowmart.repository.SettlementRepository;
import com.agrowmart.repository.VendorPaymentDetailsRepository;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
public class RazorpayService {
    private static final Logger log = LoggerFactory.getLogger(RazorpayService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final VendorPaymentDetailsRepository vendorPaymentDetailsRepository;
    private final SettlementRepository settlementRepository;
    private final NotificationService notificationService;

    // Match your properties file (add _id and _secret suffixes)
    @Value("${razorpay.key}")
    private String razorpayKeyId;

    @Value("${razorpay.secret}")
    private String razorpayKeySecret;

    @Value("${razorpayx.account}")
    private String razorpayxAccountNumber;

    public RazorpayService(
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            VendorPaymentDetailsRepository vendorPaymentDetailsRepository,
            SettlementRepository settlementRepository,
            NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.vendorPaymentDetailsRepository = vendorPaymentDetailsRepository;
        this.settlementRepository = settlementRepository;
        this.notificationService = notificationService;
    }

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String credentials = razorpayKeyId + ":" + razorpayKeySecret;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        headers.set("Authorization", "Basic " + encoded);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    @Transactional
    public void processWebhookEvent(JSONObject event) {
        if (event == null || !event.has("event")) {
            log.warn("Invalid webhook payload: missing 'event' field");
            return;
        }
        String eventType = event.getString("event");
        log.info("Processing Razorpay webhook: {}", eventType);
        try {
            JSONObject payload = event.getJSONObject("payload");
            JSONObject entity = null;
            if (payload.has("payment")) {
                entity = payload.getJSONObject("payment").getJSONObject("entity");
            } else if (payload.has("payout")) {
                entity = payload.getJSONObject("payout").getJSONObject("entity");
            } else if (payload.has("refund")) {
                entity = payload.getJSONObject("refund").getJSONObject("entity");
            }
            if (entity == null) {
                log.warn("No entity in webhook for event: {}", eventType);
                return;
            }
            switch (eventType) {
                case "payment.captured" -> handlePaymentCaptured(entity);
                case "payout.processed" -> handlePayoutProcessed(entity);
                case "payout.failed" -> handlePayoutFailed(entity);
                case "refund.processed" -> handleRefundProcessed(entity);
                default -> log.debug("Ignored event: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Webhook processing failed for event {}: {}", eventType, e.getMessage(), e);
        }
    }

    private void handlePaymentCaptured(JSONObject entity) {
        String paymentId = entity.getString("id");
        double amount = entity.getLong("amount") / 100.0;
        log.info("Payment captured: ID={}, ₹{}", paymentId, amount);

        Payment payment = paymentRepository.findByRazorpayPaymentId(paymentId).orElse(null);
        if (payment == null) {
            log.error("Payment not found for Razorpay ID: {}", paymentId);
            return;
        }
        payment.setStatus("SUCCESS");
        payment.setAmount(amount);
        paymentRepository.save(payment);

        Order order = orderRepository.findById(payment.getOrderId()).orElse(null);
        if (order != null) {
            order.setPaymentStatus("SUCCESS");
            orderRepository.save(order);
            notificationService.sendNotification(
                    order.getMerchant().getId(),
                    "Payment Success",
                    "₹" + amount + " received for Order #" + order.getId(),
                    Map.of("type", "payment_success")
            );
        }
    }

    private void handlePayoutProcessed(JSONObject entity) {
        String payoutId = entity.getString("id");
        log.info("Payout success: ID={}", payoutId);

        Settlement settlement = settlementRepository.findByRazorpayPayoutId(payoutId).orElse(null);
        if (settlement == null) return;

        settlement.setStatus("PAID");
        settlement.setPayoutDate(LocalDateTime.now());
        settlementRepository.save(settlement);

        Order order = orderRepository.findById(settlement.getOrderId()).orElse(null);
        if (order != null) {
            order.setSettlementStatus("PAID");
            orderRepository.save(order);
        }

        notificationService.sendNotification(
                settlement.getVendorId(),
                "Payout Credited",
                "₹" + settlement.getPayoutAmount() + " credited",
                Map.of("type", "payout_success")
            );
    }

    private void handlePayoutFailed(JSONObject entity) {
        String payoutId = entity.getString("id");
        String reason = entity.optString("failure_reason", "Unknown");
        log.error("Payout failed: ID={}, Reason={}", payoutId, reason);

        Settlement settlement = settlementRepository.findByRazorpayPayoutId(payoutId).orElse(null);
        if (settlement == null) return;

        settlement.setStatus("FAILED");
        settlementRepository.save(settlement);

        Order order = orderRepository.findById(settlement.getOrderId()).orElse(null);
        if (order != null) {
            order.setSettlementStatus("FAILED");
            orderRepository.save(order);
        }

        notificationService.sendNotification(
                settlement.getVendorId(),
                "Payout Failed",
                "Payout failed: " + reason,
                Map.of("type", "payout_failed")
            );
    }

    private void handleRefundProcessed(JSONObject entity) {
        log.info("Refund processed: {}", entity.toString());
    }

    @Transactional
    public void onboardVendor(User vendor) throws Exception {
        log.info("Onboarding vendor: {}", vendor.getId());

        VendorPaymentDetails details = vendorPaymentDetailsRepository.findByUser(vendor)
                .orElse(new VendorPaymentDetails());
        details.setUser(vendor);

        if (details.getRazorpayFundAccountId() != null) {
            log.info("Vendor already onboarded");
            return;
        }

        HttpHeaders headers = getAuthHeaders();

        try {
            // Create Contact
            JSONObject contactReq = new JSONObject();
            contactReq.put("name", vendor.getName());
            contactReq.put("email", vendor.getEmail() != null ? vendor.getEmail() : "");
            contactReq.put("contact", vendor.getPhone());
            contactReq.put("type", "vendor");

            HttpEntity<String> contactEntity = new HttpEntity<>(contactReq.toString(), headers);
            ResponseEntity<String> contactResponse = restTemplate.exchange(
                    "https://api.razorpay.com/v1/contacts", HttpMethod.POST, contactEntity, String.class);

            JSONObject contactJson = new JSONObject(contactResponse.getBody());
            String contactId = contactJson.getString("id");
            details.setRazorpayContactId(contactId);
            log.info("Contact created: {}", contactId);

            // Create Fund Account
            JSONObject bankAccount = new JSONObject();
            bankAccount.put("name", vendor.getAccountHolderName());
            bankAccount.put("account_number", vendor.getBankAccountNumber());
            bankAccount.put("ifsc", vendor.getIfscCode());

            JSONObject fundReq = new JSONObject();
            fundReq.put("contact_id", contactId);
            fundReq.put("account_type", "bank_account");
            fundReq.put("bank_account", bankAccount);

            HttpEntity<String> fundEntity = new HttpEntity<>(fundReq.toString(), headers);
            ResponseEntity<String> fundResponse = restTemplate.exchange(
                    "https://api.razorpay.com/v1/fund_accounts", HttpMethod.POST, fundEntity, String.class);

            JSONObject fundJson = new JSONObject(fundResponse.getBody());
            String fundAccountId = fundJson.getString("id");
            details.setRazorpayFundAccountId(fundAccountId);
            log.info("Fund Account created: {}", fundAccountId);

            vendorPaymentDetailsRepository.save(details);
            log.info("Vendor onboarded successfully");
        } catch (Exception e) {
            log.error("Onboarding failed: {}", e.getMessage());
            throw e;
        }
    }

    public String createPayout(String fundAccountId, double amountInRupees, String orderId) throws Exception {
        log.info("Creating payout: ₹{} for order {}", amountInRupees, orderId);

        JSONObject req = new JSONObject();
        req.put("account_number", razorpayxAccountNumber);
        req.put("fund_account_id", fundAccountId);
        req.put("amount", (int) (amountInRupees * 100));
        req.put("currency", "INR");
        req.put("mode", "IMPS");
        req.put("purpose", "payout");
        req.put("reference_id", "settlement_" + orderId);
        req.put("narration", "AgrowMart Vendor Settlement");

        HttpHeaders headers = getAuthHeaders();
        HttpEntity<String> entity = new HttpEntity<>(req.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.razorpay.com/v1/payouts", HttpMethod.POST, entity, String.class);

        JSONObject payoutJson = new JSONObject(response.getBody());
        String payoutId = payoutJson.getString("id");

        log.info("Payout created: ID={}", payoutId);
        return payoutId;
    }
}