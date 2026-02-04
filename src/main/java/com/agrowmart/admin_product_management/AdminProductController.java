package com.agrowmart.admin_product_management;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.*;

import com.agrowmart.dto.auth.product.PendingProductListDTO;
import com.agrowmart.dto.auth.product.ProductApprovalRequest;
import com.agrowmart.dto.auth.women.PendingWomenProductDTO;
import com.agrowmart.dto.auth.women.WomenProductApprovalRequest;
import com.agrowmart.service.ProductService;
import com.agrowmart.service.WomenProductService;


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminProductController {

    private final ProductService productService;
    private final WomenProductService womenProductService;
    private final AdminProductService adminProductService;

    public AdminProductController(
            ProductService productService,
            WomenProductService womenProductService,
            AdminProductService adminProductService) {
        this.productService = productService;
        this.womenProductService = womenProductService;
        this.adminProductService = adminProductService;
    }

    // ──────────────────────────────────────────────
    //  ALL PRODUCTS (for admin dashboard)
    // ──────────────────────────────────────────────
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProductsForAdmin() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("vendorProducts", productService.getAllProductsForAdminDTO());
        response.put("womenProducts", womenProductService.getAllProductsForAdmin());
        return ResponseEntity.ok(response);
    }

    // ──────────────────────────────────────────────
    //  VIEW SINGLE PRODUCT DETAILS
    // ──────────────────────────────────────────────
    @GetMapping("/products/{type}/{id}")
    public ResponseEntity<?> viewProductDetails(
            @PathVariable String type,
            @PathVariable Long id) {

        if ("vendor".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(productService.getProductByIdForAdminDTO(id));
        }
        if ("women".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(womenProductService.getProductByIdForAdmin(id));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid product type. Use 'vendor' or 'women'."
        ));
    }

    // ──────────────────────────────────────────────
    //  PENDING PRODUCTS (lists)
    // ──────────────────────────────────────────────
    @GetMapping("/products/pending")
    public ResponseEntity<List<PendingProductListDTO>> getPendingProducts() {
        return ResponseEntity.ok(adminProductService.getPendingProducts());
    }

    @GetMapping("/women-products/pending")
    public ResponseEntity<List<PendingWomenProductDTO>> getPendingWomenProducts() {
        return ResponseEntity.ok(adminProductService.getPendingWomenProducts());
    }

    // ──────────────────────────────────────────────
    //  UNIFIED APPROVAL ENDPOINT (Regular Products)
    // ──────────────────────────────────────────────
    @PatchMapping("/products/{id}/approval")
    public ResponseEntity<?> handleProductApproval(
            @PathVariable Long id,
            @RequestBody @Valid ProductApprovalRequest request) {

        String action = (request.action() != null)
                ? request.action().trim().toUpperCase()
                : "";

        String reason = (request.rejectionReason() != null)
                ? request.rejectionReason().trim()
                : null;

        // Mandatory rejection reason check
        if ("REJECT".equals(action)) {
            if (reason == null || reason.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Rejection reason is required when rejecting a product"
                ));
            }
        }

        return switch (action) {
            case "APPROVE" -> ResponseEntity.ok(adminProductService.approveProduct(id));
            case "REJECT"  -> ResponseEntity.ok(adminProductService.rejectProduct(id, reason));
            case "DELETE"  -> {
                adminProductService.deleteProduct(id);
                yield ResponseEntity.noContent().build();
            }
            default -> ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid action. Allowed: APPROVE, REJECT, DELETE"
            ));
        };
    }

    // ──────────────────────────────────────────────
    //  UNIFIED APPROVAL ENDPOINT (Women Products)
    // ──────────────────────────────────────────────
    @PatchMapping("/women-products/{id}/approval")
    public ResponseEntity<?> handleWomenProductApproval(
            @PathVariable Long id,
            @RequestBody @Valid WomenProductApprovalRequest request) {

        String action = (request.action() != null)
                ? request.action().trim().toUpperCase()
                : "";

        String reason = (request.rejectionReason() != null)
                ? request.rejectionReason().trim()
                : null;

        // Mandatory rejection reason check
        if ("REJECT".equals(action)) {
            if (reason == null || reason.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Rejection reason is required when rejecting a women product"
                ));
            }
        }

        return switch (action) {
            case "APPROVE" -> ResponseEntity.ok(adminProductService.approveWomenProduct(id));
            case "REJECT"  -> ResponseEntity.ok(adminProductService.rejectWomenProduct(id, reason));
            case "DELETE"  -> {
                adminProductService.deleteWomenProduct(id);
                yield ResponseEntity.noContent().build();
            }
            default -> ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid action. Allowed: APPROVE, REJECT, DELETE"
            ));
        };
    }

    // ──────────────────────────────────────────────
    //  SEPARATE REJECT ENDPOINTS (alternative / legacy support)
    // ──────────────────────────────────────────────
    @PatchMapping("/products/{id}/reject")
    public ResponseEntity<?> rejectProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductApprovalRequest request) {

        String reason = (request.rejectionReason() != null)
                ? request.rejectionReason().trim()
                : null;

        if (reason == null || reason.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Rejection reason is required"
            ));
        }

        return ResponseEntity.ok(adminProductService.rejectProduct(id, reason));
    }

    @PatchMapping("/women-products/{id}/reject")
    public ResponseEntity<?> rejectWomenProduct(
            @PathVariable Long id,
            @RequestBody @Valid WomenProductApprovalRequest request) {

        String reason = (request.rejectionReason() != null)
                ? request.rejectionReason().trim()
                : null;

        if (reason == null || reason.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Rejection reason is required"
            ));
        }

        return ResponseEntity.ok(adminProductService.rejectWomenProduct(id, reason));
    }

    // ──────────────────────────────────────────────
    //  SEPARATE DELETE ENDPOINTS
    // ──────────────────────────────────────────────
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/women-products/{id}")
    public ResponseEntity<Void> deleteWomenProduct(@PathVariable Long id) {
        adminProductService.deleteWomenProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    //  RESTORE DELETED PRODUCTS
    // ──────────────────────────────────────────────
    @PatchMapping("/products/{id}/restore")
    public ResponseEntity<?> restoreProduct(@PathVariable Long id) {
        return ResponseEntity.ok(adminProductService.restoreProduct(id));
    }

    @PatchMapping("/women-products/{id}/restore")
    public ResponseEntity<?> restoreWomenProduct(@PathVariable Long id) {
        return ResponseEntity.ok(adminProductService.restoreWomenProduct(id));
    }
}