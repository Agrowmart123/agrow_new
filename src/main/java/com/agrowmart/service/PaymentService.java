package com.agrowmart.service;


import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.agrowmart.dto.auth.order.CreateOrderRequest;
import com.agrowmart.dto.auth.order.PaymentResponse;
import com.agrowmart.entity.User;
import com.agrowmart.entity.order.Order;
import com.agrowmart.repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;

    public PaymentService(RazorpayClient razorpayClient, OrderRepository orderRepository) {
        this.razorpayClient = razorpayClient;
        this.orderRepository = orderRepository;
    }

    public PaymentResponse createPaymentOrder(User customer, CreateOrderRequest request) throws RazorpayException {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"ONLINE".equals(order.getPaymentMode())) {
            throw new IllegalStateException("Payment creation only for ONLINE orders");
        }

        JSONObject razorpayOrderRequest = new JSONObject();
        razorpayOrderRequest.put("amount", (int)(request.amount() * 100)); // paise
        razorpayOrderRequest.put("currency", "INR");
        razorpayOrderRequest.put("receipt", "order_" + order.getId());

        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(razorpayOrderRequest);

        return new PaymentResponse(
                razorpayOrder.get("id"),
                request.amount(),
                "INR"
        );
    }
}