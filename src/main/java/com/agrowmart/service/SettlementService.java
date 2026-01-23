package com.agrowmart.service;

import com.agrowmart.entity.VendorPaymentDetails;
import com.agrowmart.entity.order.Order;
import com.agrowmart.entity.order.Settlement;
import com.agrowmart.repository.OrderRepository;
import com.agrowmart.repository.SettlementRepository;
import com.agrowmart.repository.VendorPaymentDetailsRepository;
import com.razorpay.RazorpayException;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class SettlementService {
    private final OrderRepository orderRepository;
    private final VendorPaymentDetailsRepository vendorPaymentDetailsRepository;
    private final SettlementRepository settlementRepository;
    private final RazorpayService razorpayService;

    public SettlementService(
            OrderRepository orderRepository,
            VendorPaymentDetailsRepository vendorPaymentDetailsRepository,
            SettlementRepository settlementRepository,
            RazorpayService razorpayService) {
        this.orderRepository = orderRepository;
        this.vendorPaymentDetailsRepository = vendorPaymentDetailsRepository;
        this.settlementRepository = settlementRepository;
        this.razorpayService = razorpayService;
    }

    /**
     * Daily cron job: Runs every day at 2:00 AM
     * Processes settlements for orders delivered 7+ days ago
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void processDailySettlements() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        List<Order> eligibleOrders = orderRepository.findEligibleForSettlement(cutoff);
        for (Order order : eligibleOrders) {
            // Only process if payment successful and order delivered
            if (!"SUCCESS".equals(order.getPaymentStatus()) ||
                !"DELIVERED".equals(order.getStatus())) {
                continue;
            }
            VendorPaymentDetails details = vendorPaymentDetailsRepository
                    .findByUser(order.getMerchant())
                    .orElse(null);
            if (details == null || details.getRazorpayFundAccountId() == null) {
                // Vendor not onboarded for payouts
                continue;
            }
            BigDecimal commission = order.getTotalPrice().multiply(BigDecimal.valueOf(0.10)); // 10%
            BigDecimal payoutAmount = order.getTotalPrice().subtract(commission);
            try {
                String payoutId = razorpayService.createPayout(
                        details.getRazorpayFundAccountId(),
                        payoutAmount.doubleValue(),
                        order.getId()
                );
                Settlement settlement = new Settlement();
                settlement.setOrderId(order.getId());
                settlement.setVendorId(order.getMerchant().getId());
                settlement.setPayoutAmount(payoutAmount.doubleValue());
                settlement.setRazorpayPayoutId(payoutId);
                settlement.setStatus("PROCESSING");
                settlement.setPayoutDate(LocalDateTime.now());
                settlementRepository.save(settlement);
                order.setSettlementStatus("PROCESSING");
                orderRepository.save(order);
            } catch (RazorpayException e) {
                System.err.println("Razorpay payout failed for order " + order.getId() + ": " + e.getMessage());
                e.printStackTrace();
                // Optionally notify admin
            } catch (Exception e) {
                System.err.println("Unexpected error during settlement for order " + order.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // ==================== ADMIN METHODS ====================
    public List<Settlement> getPendingSettlements() {
        return settlementRepository.findByStatus("PENDING");
    }

    public List<Settlement> getFailedSettlements() {
        return settlementRepository.findByStatus("FAILED");
    }

    public VendorPaymentDetails getVendorWallet(Long vendorId) {
        return vendorPaymentDetailsRepository.findByUserId(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor wallet not found"));
    }

    @Transactional
    public void manualPayout(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("Settlement not found"));
        Order order = orderRepository.findById(settlement.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        VendorPaymentDetails details = vendorPaymentDetailsRepository
                .findByUser(order.getMerchant())
                .orElseThrow(() -> new RuntimeException("Vendor not onboarded for payouts"));
        BigDecimal payoutAmount = BigDecimal.valueOf(settlement.getPayoutAmount());
        String payoutId;
        try {
            payoutId = razorpayService.createPayout(
                    details.getRazorpayFundAccountId(),
                    payoutAmount.doubleValue(),
                    order.getId()
            );
            settlement.setRazorpayPayoutId(payoutId);
            settlement.setStatus("PROCESSING");
            settlement.setPayoutDate(LocalDateTime.now());
            settlementRepository.save(settlement);
            order.setSettlementStatus("PROCESSING");
            orderRepository.save(order);
        } catch (RazorpayException e) {
            throw new RuntimeException("Manual payout failed due to Razorpay error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during manual payout: " + e.getMessage(), e);
        }
    }
}