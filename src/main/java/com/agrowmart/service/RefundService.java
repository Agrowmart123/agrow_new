package com.agrowmart.service;


import com.agrowmart.entity.order.Order;
import com.agrowmart.entity.order.Payment;
import com.agrowmart.entity.User;
import com.agrowmart.repository.OrderRepository;
import com.agrowmart.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class RefundService {
    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    public RefundService(RazorpayClient razorpayClient,
                         OrderRepository orderRepository,
                         PaymentRepository paymentRepository,
                         NotificationService notificationService) {
        this.razorpayClient = razorpayClient;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void initiateRefund(String orderId, User actor) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        // Authorization: Only merchant or admin
        if (!order.getMerchant().getId().equals(actor.getId()) &&
            !"ADMIN".equals(actor.getRole().getName())) {
            throw new RuntimeException("Not authorized to refund this order");
        }
        if (!"ONLINE".equals(order.getPaymentMode())) {
            throw new IllegalStateException("Refund only allowed for ONLINE payments");
        }
        if (!"SUCCESS".equals(order.getPaymentStatus())) {
            throw new IllegalStateException("Cannot refund non-successful payment");
        }
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment record not found"));
        try {
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", (int) (order.getTotalPrice().doubleValue() * 100)); // full refund in paise
            refundRequest.put("speed", "optimum"); // or "normal"
            Refund refund = razorpayClient.payments.refund(payment.getRazorpayPaymentId(), refundRequest);
            // Update order status
            order.setPaymentStatus("REFUND_INITIATED");
            orderRepository.save(order);
            // Notify customer
            notificationService.sendNotification(
                    order.getCustomer().getId(),
                    "Refund Initiated",
                    "Refund of ₹" + order.getTotalPrice() + " initiated for Order #" + orderId +
                    ". Expected in 3-7 days depending on bank.",
                    Map.of("type", "refund_initiated", "orderId", orderId)
            );
        } catch (RazorpayException e) {
            throw new RuntimeException("Refund failed: " + e.getMessage());
        }
    }
}