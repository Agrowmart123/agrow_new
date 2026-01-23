//package com.agrowmart.admin_product_management;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.agrowmart.dto.auth.product.PendingProductListDTO;
//import com.agrowmart.dto.auth.product.ProductApprovalRequest;
//import com.agrowmart.dto.auth.women.PendingWomenProductDTO;
//import com.agrowmart.dto.auth.women.WomenProductApprovalRequest;
//import com.agrowmart.entity.Product;
//import com.agrowmart.entity.WomenProduct;
//import com.agrowmart.service.ProductService;
//import com.agrowmart.service.WomenProductService;
////✅ ADD THIS
//import org.springframework.web.bind.annotation.RequestBody;
//import jakarta.validation.Valid;
//
//import java.util.*;
//@RestController
//@RequestMapping("/api/admin")
//@PreAuthorize("hasAuthority('ADMIN')")
//public class AdminProductController {
//
//    private final ProductService productService;
//    private final WomenProductService womenProductService;
//    private final AdminProductService adminProductService;
//
//    public AdminProductController(ProductService productService,
//                                  WomenProductService womenProductService,AdminProductService adminProductService) {
//        this.productService = productService;
//        this.womenProductService = womenProductService;
//        this.adminProductService = adminProductService;
//    }
//
//    // ===================== ADMIN: ALL PRODUCTS =====================
// // ===================== ADMIN: ALL PRODUCTS =====================
//    @GetMapping("/products")
//    public ResponseEntity<Map<String, Object>> getAllProductsForAdmin() {
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("vendorProducts", productService.getAllProductsForAdminDTO());
//        response.put("womenProducts", womenProductService.getAllProductsForAdmin());
//
//        return ResponseEntity.ok(response);
//    }
//
//    // ===================== ADMIN: VIEW PRODUCT DETAILS =====================
//    @GetMapping("/products/{type}/{id}")
//    public ResponseEntity<?> viewProductDetails(
//            @PathVariable String type,
//            @PathVariable Long id) {
//
//        if ("vendor".equalsIgnoreCase(type)) {
//            return ResponseEntity.ok(
//                    productService.getProductByIdForAdminDTO(id)
//            );
//        }
//
//        if ("women".equalsIgnoreCase(type)) {
//            return ResponseEntity.ok(
//                    womenProductService.getProductByIdForAdmin(id)
//            );
//        }
//
//        return ResponseEntity.badRequest().body("Invalid product type");
//    }
//    
// // ────────────── Regular Products ──────────────
//
//    @GetMapping("/products/pending")
//    public ResponseEntity<List<PendingProductListDTO>> getPendingProducts() {
//        return ResponseEntity.ok(adminProductService.getPendingProducts());
//    }
//
////    @PatchMapping("/products/{id}/approval")
////    public ResponseEntity<?> handleProductApproval(
////            @PathVariable Long id,
////            @RequestBody @Valid ProductApprovalRequest request) {
////
////        String action = request.action().toUpperCase();
////        String reason = request.rejectionReason() != null ? request.rejectionReason() : "No reason provided";
////
////        return switch (action) {
////            case "APPROVE" -> ResponseEntity.ok(adminProductService.approveProduct(id));
////            case "REJECT" -> ResponseEntity.ok(adminProductService.rejectProduct(id, reason));
////            case "DELETE" -> {
////                adminProductService.deleteProduct(id);
////                yield ResponseEntity.noContent().build();
////            }
////            default -> ResponseEntity.badRequest()
////                    .body(Map.of("error", "Invalid action. Use: APPROVE, REJECT, DELETE"));
////        };
////    }
//    @PatchMapping("/products/{id}/approval")
//    public ResponseEntity<?> handleProductApproval(
//            @PathVariable Long id,
//            @RequestBody ProductApprovalRequest request) {
//
//        String actionRaw = request.action();
//
//        if (actionRaw == null || actionRaw.trim().isEmpty()) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", "Action is required (APPROVE, REJECT or DELETE)"));
//        }
//
//        String action = actionRaw.trim().toUpperCase();
//
//        String reason = request.rejectionReason() != null 
//                        ? request.rejectionReason() 
//                        : "No reason provided";
//
//        return switch (action) {
//            case "APPROVE" -> ResponseEntity.ok(adminProductService.approveProduct(id));
//            case "REJECT" -> ResponseEntity.ok(adminProductService.rejectProduct(id, reason));
//            case "DELETE" -> {
//                adminProductService.deleteProduct(id);
//                yield ResponseEntity.noContent().build();
//            }
//            default -> ResponseEntity.badRequest()
//                    .body(Map.of("error", "Invalid action. Use: APPROVE, REJECT, DELETE"));
//        };
//    }
//
//    // ────────────── Women Products ──────────────
//
//    @GetMapping("/women-products/pending")
//    public ResponseEntity<List<PendingWomenProductDTO>> getPendingWomenProducts() {
//        return ResponseEntity.ok(adminProductService.getPendingWomenProducts());
//    }
//
//    @PatchMapping("/women-products/{id}/approval")
//    public ResponseEntity<?> handleWomenProductApproval(
//            @PathVariable Long id,
//            @RequestBody @Valid WomenProductApprovalRequest request) {
//
//        String action = request.action().toUpperCase();
//        String reason = request.rejectionReason() != null ? request.rejectionReason() : "No reason provided";
//
//        return switch (action) {
//            case "APPROVE" -> ResponseEntity.ok(adminProductService.approveWomenProduct(id));
//            case "REJECT" -> ResponseEntity.ok(adminProductService.rejectWomenProduct(id, reason));
//            case "DELETE" -> {
//                adminProductService.deleteWomenProduct(id);
//                yield ResponseEntity.noContent().build();
//            }
//            default -> ResponseEntity.badRequest()
//                    .body(Map.of("error", "Invalid action. Use: APPROVE, REJECT, DELETE"));
//        };
//    }
//    
// // ────────────── Regular Products - Separate Endpoints ──────────────
//
// // Reject 
// @PatchMapping("/products/{id}/reject")
// public ResponseEntity<?> rejectProduct(
//         @PathVariable Long id,
//         @RequestBody @Valid ProductApprovalRequest request) {  // किंवा फक्त reason साठी स्वतंत्र DTO
//
//     String reason = request.rejectionReason() != null ? request.rejectionReason() : "No reason provided";
//     return ResponseEntity.ok(adminProductService.rejectProduct(id, reason));
// }
//
// // Delete 
// @DeleteMapping("/products/{id}")
// public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
//     adminProductService.deleteProduct(id);
//     return ResponseEntity.noContent().build();
// }
// 
////Women Products - Separate Endpoints
//
//@PatchMapping("/women-products/{id}/reject")
//public ResponseEntity<?> rejectWomenProduct(
//      @PathVariable Long id,
//      @RequestBody @Valid WomenProductApprovalRequest request) {
//
//  String reason = request.rejectionReason() != null ? request.rejectionReason() : "No reason provided";
//  return ResponseEntity.ok(adminProductService.rejectWomenProduct(id, reason));
//}
//
//@DeleteMapping("/women-products/{id}")
//public ResponseEntity<?> deleteWomenProduct(@PathVariable Long id) {
//  adminProductService.deleteWomenProduct(id);
//  return ResponseEntity.noContent().build();
//}
//
//}
package com.agrowmart.admin_product_management;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrowmart.dto.auth.product.PendingProductListDTO;
import com.agrowmart.dto.auth.product.ProductApprovalRequest;
import com.agrowmart.dto.auth.women.PendingWomenProductDTO;
import com.agrowmart.dto.auth.women.WomenProductApprovalRequest;
import com.agrowmart.entity.Product;
import com.agrowmart.entity.WomenProduct;
import com.agrowmart.service.ProductService;
import com.agrowmart.service.WomenProductService;
//✅ ADD THIS
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import java.util.*;
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminProductController {

    private final ProductService productService;
    private final WomenProductService womenProductService;
    private final AdminProductService adminProductService;

    public AdminProductController(ProductService productService,
                                  WomenProductService womenProductService,AdminProductService adminProductService) {
        this.productService = productService;
        this.womenProductService = womenProductService;
        this.adminProductService = adminProductService;
    }

    // ===================== ADMIN: ALL PRODUCTS =====================
 // ===================== ADMIN: ALL PRODUCTS =====================
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProductsForAdmin() {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("vendorProducts", productService.getAllProductsForAdminDTO());
        response.put("womenProducts", womenProductService.getAllProductsForAdmin());

        return ResponseEntity.ok(response);
    }

    // ===================== ADMIN: VIEW PRODUCT DETAILS =====================
    @GetMapping("/products/{type}/{id}")
    public ResponseEntity<?> viewProductDetails(
            @PathVariable String type,
            @PathVariable Long id) {

        if ("vendor".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(
                    productService.getProductByIdForAdminDTO(id)
            );
        }

        if ("women".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(
                    womenProductService.getProductByIdForAdmin(id)
            );
        }

        return ResponseEntity.badRequest().body("Invalid product type");
    }
    
 // ────────────── Regular Products ──────────────

    @GetMapping("/products/pending")
    public ResponseEntity<List<PendingProductListDTO>> getPendingProducts() {
        return ResponseEntity.ok(adminProductService.getPendingProducts());
    }

//    @PatchMapping("/products/{id}/approval")
//    public ResponseEntity<?> handleProductApproval(
//            @PathVariable Long id,
//            @RequestBody @Valid ProductApprovalRequest request) {
//
//        String action = request.action().toUpperCase();
//        String reason = request.rejectionReason() != null ? request.rejectionReason() : "No reason provided";
//
//        return switch (action) {
//            case "APPROVE" -> ResponseEntity.ok(adminProductService.approveProduct(id));
//            case "REJECT" -> ResponseEntity.ok(adminProductService.rejectProduct(id, reason));
//            case "DELETE" -> {
//                adminProductService.deleteProduct(id);
//                yield ResponseEntity.noContent().build();
//            }
//            default -> ResponseEntity.badRequest()
//                    .body(Map.of("error", "Invalid action. Use: APPROVE, REJECT, DELETE"));
//        };
//    }
    @PatchMapping("/products/{id}/approval")
    public ResponseEntity<?> handleProductApproval(
            @PathVariable Long id,
            @RequestBody ProductApprovalRequest request) {

        String actionRaw = request.action();

        if (actionRaw == null || actionRaw.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Action is required (APPROVE, REJECT or DELETE)"));
        }

        String action = actionRaw.trim().toUpperCase();

        String reason = request.rejectionReason() != null 
                        ? request.rejectionReason() 
                        : "No reason provided";

        return switch (action) {
            case "APPROVE" -> ResponseEntity.ok(adminProductService.approveProduct(id));
            case "REJECT" -> ResponseEntity.ok(adminProductService.rejectProduct(id, reason));
            case "DELETE" -> {
                adminProductService.deleteProduct(id);
                yield ResponseEntity.noContent().build();
            }
            default -> ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid action. Use: APPROVE, REJECT, DELETE"));
        };
    }

    // ────────────── Women Products ──────────────

    @GetMapping("/women-products/pending")
    public ResponseEntity<List<PendingWomenProductDTO>> getPendingWomenProducts() {
        return ResponseEntity.ok(adminProductService.getPendingWomenProducts());
    }

    @PatchMapping("/women-products/{id}/approval")
    public ResponseEntity<?> handleWomenProductApproval(
            @PathVariable Long id,
            @RequestBody @Valid WomenProductApprovalRequest request) {

        String action = request.action().toUpperCase();
        String reason = request.rejectionReason() != null ? request.rejectionReason() : "No reason provided";

        return switch (action) {
            case "APPROVE" -> ResponseEntity.ok(adminProductService.approveWomenProduct(id));
            case "REJECT" -> ResponseEntity.ok(adminProductService.rejectWomenProduct(id, reason));
            case "DELETE" -> {
                adminProductService.deleteWomenProduct(id);
                yield ResponseEntity.noContent().build();
            }
            default -> ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid action. Use: APPROVE, REJECT, DELETE"));
        };
    }
    
 // ────────────── Regular Products - Separate Endpoints ──────────────

 // Reject 
 @PatchMapping("/products/{id}/reject")
 public ResponseEntity<?> rejectProduct(
         @PathVariable Long id,
         @RequestBody @Valid ProductApprovalRequest request) {  // किंवा फक्त reason साठी स्वतंत्र DTO

     String reason = request.rejectionReason() != null ? request.rejectionReason() : "No reason provided";
     return ResponseEntity.ok(adminProductService.rejectProduct(id, reason));
 }

 // Delete 
 @DeleteMapping("/products/{id}")
 public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
     adminProductService.deleteProduct(id);
     return ResponseEntity.noContent().build();
 }
 
//Women Products - Separate Endpoints

@PatchMapping("/women-products/{id}/reject")
public ResponseEntity<?> rejectWomenProduct(
      @PathVariable Long id,
      @RequestBody @Valid WomenProductApprovalRequest request) {

  String reason = request.rejectionReason() != null ? request.rejectionReason() : "No reason provided";
  return ResponseEntity.ok(adminProductService.rejectWomenProduct(id, reason));
}

@DeleteMapping("/women-products/{id}")
public ResponseEntity<?> deleteWomenProduct(@PathVariable Long id) {
  adminProductService.deleteWomenProduct(id);
  return ResponseEntity.noContent().build();
}
// restore 

@PatchMapping("/products/{id}/restore")
public ResponseEntity<?> restoreProduct(@PathVariable Long id) {
    return ResponseEntity.ok(adminProductService.restoreProduct(id));
}

@PatchMapping("/women-products/{id}/restore")
public ResponseEntity<?> restoreWomenProduct(@PathVariable Long id) {
    return ResponseEntity.ok(adminProductService.restoreWomenProduct(id));
}

}