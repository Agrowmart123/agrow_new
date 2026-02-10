package com.agrowmart.service;

import com.agrowmart.dto.auth.order.CreateOrderRequest;
import com.agrowmart.dto.auth.order.PaymentResponse;
import com.agrowmart.entity.User;
import com.agrowmart.entity.order.Order;
import com.agrowmart.exception.AuthExceptions.AuthenticationFailedException;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.ForbiddenException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;

    public PaymentService(RazorpayClient razorpayClient, OrderRepository orderRepository) {
        this.razorpayClient = razorpayClient;
        this.orderRepository = orderRepository;
    }

    public PaymentResponse createPaymentOrder(User customer, CreateOrderRequest request) {

        // 1. Auth check
        if (customer == null) {
            throw new AuthenticationFailedException("Customer must be authenticated to create payment order");
        }

        // 2. Input validation
        if (request == null || request.orderId() == null || request.amount() == null) {
            throw new BusinessValidationException("Order ID and amount are required");
        }

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Payment amount must be greater than zero");
        }

        // 3. Fetch order
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + request.orderId()));

        // 4. Ownership check
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new ForbiddenException("You can only create payment for your own orders");
        }

        // 5. Payment mode check
        if (!"ONLINE".equalsIgnoreCase(order.getPaymentMode())) {
            throw new BusinessValidationException("Payment order creation is only allowed for ONLINE payment mode");
        }

        // 6. Status check (prevent duplicate / wrong state)
        if (!"PENDING".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new BusinessValidationException("Payment order can only be created for orders in PENDING status");
        }

        // 7. Extra safety: amount should match order total
        BigDecimal expectedAmount = order.getTotalPrice();
        if (expectedAmount == null || expectedAmount.compareTo(request.amount()) != 0) {
            log.warn("Amount mismatch → Order ID: {}, expected: {}, requested: {}", 
                     request.orderId(), expectedAmount, request.amount());
            throw new BusinessValidationException("Payment amount does not match order total");
        }

        try {
            JSONObject razorpayOrderRequest = new JSONObject();
            // Safe BigDecimal → paise conversion
            BigDecimal amountInPaise = request.amount().multiply(BigDecimal.valueOf(100));
            razorpayOrderRequest.put("amount", amountInPaise.intValueExact()); // safe, throws if overflow
            razorpayOrderRequest.put("currency", "INR");
            razorpayOrderRequest.put("receipt", "order_" + order.getId());

            // Helpful notes for Razorpay dashboard
            JSONObject notes = new JSONObject();
            notes.put("orderId", order.getId());
            notes.put("customerId", customer.getId());
            notes.put("customerName", customer.getName() != null ? customer.getName() : "Unknown");
            razorpayOrderRequest.put("notes", notes);

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(razorpayOrderRequest);

            log.info("Razorpay order created → Razorpay ID: {}, Amount: ₹{}", 
                     razorpayOrder.get("id"), request.amount());

            return new PaymentResponse(
                    razorpayOrder.get("id"),
                    request.amount(),
                    "INR"
            );

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed for order ID: {}", request.orderId(), e);
            throw new BusinessValidationException("Failed to create Razorpay payment order: " + e.getMessage());
        } catch (ArithmeticException e) {
            log.error("Amount too large/small for order ID: {}", request.orderId(), e);
            throw new BusinessValidationException("Invalid payment amount (too large or too small)");
        } catch (Exception e) {
            log.error("Unexpected error creating Razorpay order for order ID: {}", request.orderId(), e);
            throw new BusinessValidationException("Payment service error. Please try again later.");
        }
    }
}