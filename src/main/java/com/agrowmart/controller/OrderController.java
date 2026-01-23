
//package com.agrowmart.controller;
//
//import com.agrowmart.dto.auth.order.*;
//import com.agrowmart.entity.customer.Customer;
//import com.agrowmart.entity.User;
//import com.agrowmart.service.OrderService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/orders")
//public class OrderController {
//
//    private final OrderService orderService;
//
//    public OrderController(OrderService orderService) {
//        this.orderService = orderService;
//    }
//
//    // Customer places order
//    @PostMapping("/create")
//    @PreAuthorize("hasAuthority('CUSTOMER')")
//    public ResponseEntity<OrderResponseDTO> createOrder(
//            @AuthenticationPrincipal Customer customer,
//            @Valid @RequestBody OrderRequestDTO request) {
//        return ResponseEntity.status(201).body(orderService.createOrder(customer, request));
//    }
//
//    // Customer views their orders
//    @GetMapping("/my")
//    @PreAuthorize("hasAuthority('CUSTOMER')")
//    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(@AuthenticationPrincipal Customer customer) {
//        return ResponseEntity.ok(orderService.getCustomerOrders(customer));
//    }
//
//    // Vendor sees pending orders
//    @GetMapping("/vendor/pending")
//    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
//    public ResponseEntity<List<OrderResponseDTO>> getPendingOrders(@AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.getVendorPendingOrders(vendor));
//    }
//
//    // Vendor accepts order
//    @PostMapping("/accept/{orderId}")
//    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
//    public ResponseEntity<OrderResponseDTO> acceptOrder(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.acceptOrder(orderId, vendor));
//    }
//
//    // Vendor rejects order
//    @PostMapping("/reject/{orderId}")
//    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
//    public ResponseEntity<OrderResponseDTO> rejectOrder(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.rejectOrder(orderId, vendor));
//    }
//
//    // Vendor marks delivered
//    @PostMapping("/delivered/{orderId}")
//    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
//    public ResponseEntity<OrderResponseDTO> markDelivered(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.markAsDelivered(orderId, vendor));
//    }
//
//    // Vendor sees all orders
//    @GetMapping("/vendor/all")
//    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
//    public ResponseEntity<List<OrderResponseDTO>> getAllVendorOrders(@AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.getAllVendorOrders(vendor));
//    }
//
//    // Customer cancels order
//    @PostMapping("/cancel/{orderId}")
//    @PreAuthorize("hasAuthority('CUSTOMER')")
//    public ResponseEntity<OrderResponseDTO> cancelOrder(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal Customer customer,
//            @Valid @RequestBody OrderCancelRequestDTO request) {
//        return ResponseEntity.ok(orderService.cancelOrderByCustomer(orderId, customer, request.reason()));
//    }
//
//    // Vendor cancels order
//    @PostMapping("/vendor/cancel/{orderId}")
//    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
//    public ResponseEntity<OrderResponseDTO> vendorCancelOrder(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User vendor,
//            @Valid @RequestBody OrderCancelRequestDTO request) {
//        return ResponseEntity.ok(orderService.cancelOrderByVendor(orderId, vendor, request.reason()));
//    }
//
//    // Vendor confirms COD collected
//    @PostMapping("/cod-collected/{orderId}")
//    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
//    public ResponseEntity<OrderResponseDTO> confirmCodCollected(
//            @PathVariable String orderId,
//            @AuthenticationPrincipal User vendor) {
//        return ResponseEntity.ok(orderService.confirmCodCollected(orderId, vendor));
//    }
//}



package com.agrowmart.controller;

import com.agrowmart.dto.auth.order.*;
import com.agrowmart.entity.User;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.order.Order;
import com.agrowmart.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ──────────────────────────────────────────────
    // Customer APIs
    // ──────────────────────────────────────────────

    /**
     * Customer places a new order
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @AuthenticationPrincipal Customer customer,
            @Valid @RequestBody OrderRequestDTO request) {
        return ResponseEntity.status(201).body(orderService.createOrder(customer, request));
    }

    /**
     * Customer views their own orders
     */
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(
            @AuthenticationPrincipal Customer customer) {
        return ResponseEntity.ok(orderService.getCustomerOrders(customer));
    }

    /**
     * Customer cancels their own order
     */
    @PostMapping("/cancel/{orderId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal Customer customer,
            @Valid @RequestBody OrderCancelRequestDTO request) {
        return ResponseEntity.ok(orderService.cancelOrderByCustomer(orderId, customer, request.reason()));
    }

    // ──────────────────────────────────────────────
    // Vendor (Merchant) APIs
    // ──────────────────────────────────────────────

    /**
     * Vendor sees pending orders (new orders waiting for acceptance)
     */
    @GetMapping("/vendor/pending")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<List<OrderResponseDTO>> getPendingOrders(
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.getVendorPendingOrders(vendor));
    }

    /**
     * Vendor sees all their orders (any status)
     */
    @GetMapping("/vendor/all")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<List<OrderResponseDTO>> getAllVendorOrders(
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.getAllVendorOrders(vendor));
    }

    
    
    @GetMapping("/vendor/scheduled")
    @PreAuthorize("hasAnyAuthority('VEGETABLE','DAIRY','SEAFOODMEAT','WOMEN','FARMER','AGRI')")
    public ResponseEntity<Object> getVendorScheduledOrders(
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.getVendorScheduledOrders(vendor));
    }
    
    /**
     * Vendor accepts an order
     */
    @PostMapping("/accept/{orderId}")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> acceptOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.acceptOrder(orderId, vendor));
    }

    /**
     * Vendor rejects an order
     */
    @PostMapping("/reject/{orderId}")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> rejectOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.rejectOrder(orderId, vendor));
    }

    /**
     * Vendor marks order as ready (important decision point)
     * - For self-delivery → goes to OUT_FOR_DELIVERY
     * - For delivery partner → generates pickup QR & notifies delivery boys
     */
    @PostMapping("/{orderId}/ready")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> markOrderReady(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.markOrderReady(orderId, vendor));
    }

    /**
     * Vendor generates / regenerates pickup QR code token
     * (Only needed for DELIVERY_PARTNER mode)
     */
    @PostMapping("/{orderId}/generate-pickup-qr")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> generatePickupQR(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.generateVendorPickupQR(orderId, vendor));
    }

    /**
     * Vendor cancels their accepted/pending order
     */
    @PostMapping("/vendor/cancel/{orderId}")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> vendorCancelOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor,
            @Valid @RequestBody OrderCancelRequestDTO request) {
        return ResponseEntity.ok(orderService.cancelOrderByVendor(orderId, vendor, request.reason()));
    }

    /**
     * Vendor confirms cash collected (COD orders only)
     */
    @PostMapping("/cod-collected/{orderId}")
    @PreAuthorize("hasAnyAuthority('VEGETABLE', 'DAIRY', 'SEAFOODMEAT', 'WOMEN', 'FARMER', 'AGRI')")
    public ResponseEntity<OrderResponseDTO> confirmCodCollected(
            @PathVariable String orderId,
            @AuthenticationPrincipal User vendor) {
        return ResponseEntity.ok(orderService.confirmCodCollected(orderId, vendor));
    }

    
    

    // ──────────────────────────────────────────────
    // Delivery Partner APIs
    // ──────────────────────────────────────────────

    /**
     * Delivery Partner scans QR code
     * - VENDOR_PICKUP: pickup from vendor shop
     * - USER_DELIVERY: delivery confirmation to customer
     */
    @PostMapping("/{orderId}/scan")
    @PreAuthorize("hasAuthority('DELIVERY')")
    public ResponseEntity<OrderResponseDTO> scanQrCode(
            @PathVariable String orderId,
            @Valid @RequestBody ScanRequestDTO scanRequest,
            @AuthenticationPrincipal User deliveryPartner) {
        return ResponseEntity.ok(orderService.scanToken(orderId, scanRequest, deliveryPartner));
    }
    
    

    // Quick status check
    @GetMapping("/{orderId}/status")
    @PreAuthorize("hasAnyAuthority('CUSTOMER','VEGETABLE','DAIRY','SEAFOODMEAT','WOMEN','FARMER','AGRI','DELIVERY')")
    public ResponseEntity<OrderStatusResponseDTO> getOrderStatus(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderStatus(orderId));
    }
}
