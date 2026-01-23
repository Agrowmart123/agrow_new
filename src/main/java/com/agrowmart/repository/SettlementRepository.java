package com.agrowmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrowmart.entity.order.Settlement;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

	// Find all settlements with a specific status (PENDING, PROCESSING, PAID, FAILED)
    List<Settlement> findByStatus(String status);

    // Find settlement by Razorpay payout ID (critical for webhook updates)
    Optional<Settlement> findByRazorpayPayoutId(String razorpayPayoutId);

    // Find all pending settlements for admin dashboard
    List<Settlement> findByStatusOrderByCreatedAtDesc(String status);

    // Find settlements by vendor ID (for vendor history)
    List<Settlement> findByVendorIdOrderByCreatedAtDesc(Long vendorId);

    // Find settlements by order ID
    Optional<Settlement> findByOrderId(String orderId);
}