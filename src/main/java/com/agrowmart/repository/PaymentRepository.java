package com.agrowmart.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.agrowmart.entity.order.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);
    Optional<Payment> findByOrderId(String  orderId);
}