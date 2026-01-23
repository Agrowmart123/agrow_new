//package com.agrowmart.admin_product_management;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.stereotype.Service;
//
//import com.agrowmart.dto.auth.product.PendingProductListDTO;
//import com.agrowmart.dto.auth.product.ProductResponseDTO;
//import com.agrowmart.dto.auth.women.PendingWomenProductDTO;
//import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
//import com.agrowmart.entity.ApprovalStatus;
//import com.agrowmart.entity.Category;
//import com.agrowmart.entity.Product;
//import com.agrowmart.entity.WomenProduct;
//import com.agrowmart.exception.ResourceNotFoundException;
//import com.agrowmart.repository.ProductRepository;
//import com.agrowmart.repository.WomenProductRepository;
//import com.agrowmart.service.CloudinaryService;
//import com.agrowmart.service.ProductService;
//import com.agrowmart.service.WomenProductService;
//
//import jakarta.transaction.Transactional;
//
//@Service
//@Transactional
//public class AdminProductService {
//
//    private final ProductRepository productRepository;
//    private final WomenProductRepository womenProductRepository;
//    private final CloudinaryService cloudinaryService;
//    private final ProductService productService;           // for mapping
//    private final WomenProductService womenProductService; // for mapping
//
//    public AdminProductService(
//            ProductRepository productRepository,
//            WomenProductRepository womenProductRepository,
//            CloudinaryService cloudinaryService,
//            ProductService productService,
//            WomenProductService womenProductService) {
//
//        this.productRepository = productRepository;
//        this.womenProductRepository = womenProductRepository;
//        this.cloudinaryService = cloudinaryService;
//        this.productService = productService;
//        this.womenProductService = womenProductService;
//    }
//
//    // ────────────── Regular Products ──────────────
//    public List<PendingProductListDTO> getPendingProducts() {
//        return productRepository.findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus.PENDING)
//                .stream()
//                .map(this::toPendingProductDTO)
//                .toList();
//    }
//
//    public ProductResponseDTO approveProduct(Long productId) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
//
//        product.setApprovalStatus(ApprovalStatus.APPROVED);
//        product.setStatus(Product.ProductStatus.ACTIVE);
//        product = productRepository.save(product);
//
//        return productService.toResponseDto(product); // reuse existing mapper
//    }
//
//    public Map<String, Object> rejectProduct(Long productId, String reason) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
//
//        product.setApprovalStatus(ApprovalStatus.REJECTED);
//        product.setStatus(Product.ProductStatus.INACTIVE);
//        product = productRepository.save(product);
//
//        return Map.of(
//                "message", "Product rejected successfully",
//                "productId", productId,
//                "reason", reason != null ? reason : "No reason provided"
//        );
//    }
//
//    public void deleteProduct(Long productId) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
//        
//        // Reuse existing delete logic (including images cleanup)
//        productService.delete(productId, product.getMerchantId());
//    }
//
//    // ────────────── Women Products ──────────────
//    public List<PendingWomenProductDTO> getPendingWomenProducts() {
//        return womenProductRepository.findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus.PENDING)
//                .stream()
//                .map(this::toPendingWomenDTO)
//                .toList();
//    }
//
//    public WomenProductResponseDTO approveWomenProduct(Long id) {
//        WomenProduct product = womenProductRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));
//
//        product.setApprovalStatus(ApprovalStatus.APPROVED);
//        product.setIsAvailable(true);
//        product = womenProductRepository.save(product);
//
//        return womenProductService.toDTO(product);    }
//
//    public Map<String, Object> rejectWomenProduct(Long id, String reason) {
//        WomenProduct product = womenProductRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));
//
//        product.setApprovalStatus(ApprovalStatus.REJECTED);
//        product.setIsAvailable(false);
//        product = womenProductRepository.save(product);
//
//        return Map.of(
//                "message", "Women product rejected successfully",
//                "productId", id,
//                "reason", reason != null ? reason : "No reason provided"
//        );
//    }
//
//    public void deleteWomenProduct(Long id) {
//        WomenProduct product = womenProductRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));
//
//        womenProductService.deleteProduct(product.getSeller().getId(), id);
//    }
//
//    // ────────────── Mapping Helpers ──────────────
//    private PendingProductListDTO toPendingProductDTO(Product p) {
//        return new PendingProductListDTO(
//                p.getId(),
//                p.getProductName(),
//                "Merchant " + p.getMerchantId(),
//                p.getCategory().getName(),
//                p.getCreatedAt() != null ? p.getCreatedAt().toString() : "N/A",
//                getImageList(p.getImagePaths()),
//                p.getShortDescription(),
//                getProductType(p.getCategory()) // fixed: added helper method
//        );
//    }
//    private PendingWomenProductDTO toPendingWomenDTO(WomenProduct p) {
//        return new PendingWomenProductDTO(
//            p.getId(),
//            p.getUuid(),                          // UUID of the product
//            p.getName(),
//            p.getSeller().getId(),
//            p.getSeller().getName(),
//            p.getCategory(),                      // String if category is String, else p.getCategory().getName()
//            p.getApprovalStatus() != null ? p.getApprovalStatus().name() : "PENDING",
//            p.getImageUrlList(),
//            p.getMinPrice(),
//            p.getMaxPrice(),
//            p.getStock(),
//            p.getIsAvailable(),
//            p.getCreatedAt() != null ? p.getCreatedAt().toString() : "N/A",
//            null                                  // rejectionReason, pass null if not rejected
//        );
//    }
//
//
//    private List<String> getImageList(String paths) {
//        if (paths == null || paths.isBlank()) return List.of();
//        return Arrays.asList(paths.split(","));
//    }
//
//    // ------------------ NEW HELPER ------------------
//    private String getProductType(Category category) {
//        if (category == null) return "Unknown";
//        String name = category.getName().toLowerCase();
//        if (name.contains("women")) return "Women";
//        if (name.contains("men")) return "Men";
//        return "Other";
//        
//        
//    }
//}
package com.agrowmart.admin_product_management;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.agrowmart.dto.auth.product.PendingProductListDTO;
import com.agrowmart.dto.auth.product.ProductResponseDTO;
import com.agrowmart.dto.auth.women.PendingWomenProductDTO;
import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
import com.agrowmart.entity.ApprovalStatus;
import com.agrowmart.entity.Category;
import com.agrowmart.entity.Product;
import com.agrowmart.entity.WomenProduct;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.ProductRepository;
import com.agrowmart.repository.WomenProductRepository;
import com.agrowmart.service.ProductService;
import com.agrowmart.service.WomenProductService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AdminProductService {

    private final ProductRepository productRepository;
    private final WomenProductRepository womenProductRepository;
    private final ProductService productService;
    private final WomenProductService womenProductService;

    public AdminProductService(
            ProductRepository productRepository,
            WomenProductRepository womenProductRepository,
            ProductService productService,
            WomenProductService womenProductService) {

        this.productRepository = productRepository;
        this.womenProductRepository = womenProductRepository;
        this.productService = productService;
        this.womenProductService = womenProductService;
    }

    // ────────────── Regular Products ──────────────

    public List<PendingProductListDTO> getPendingProducts() {
        return productRepository
                .findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus.PENDING)
                .stream()
                .map(this::toPendingProductDTO)
                .toList();
    }

    public ProductResponseDTO approveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setApprovalStatus(ApprovalStatus.APPROVED);
        product.setStatus(Product.ProductStatus.ACTIVE);
        product.setDeleted(false);

        return productService.toResponseDto(productRepository.save(product));
    }

    public Map<String, Object> rejectProduct(Long productId, String reason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setApprovalStatus(ApprovalStatus.REJECTED);
        product.setStatus(Product.ProductStatus.INACTIVE);

        productRepository.save(product);

        return Map.of(
                "message", "Product rejected successfully",
                "productId", productId,
                "reason", reason != null ? reason : "No reason provided"
        );
    }

    // ✅ SOFT DELETE (NO HARD DELETE)
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setDeleted(true);
        product.setStatus(Product.ProductStatus.INACTIVE);

        productRepository.save(product);
    }

    // ✅ RESTORE PRODUCT
    public ProductResponseDTO restoreProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setDeleted(false);
        product.setStatus(Product.ProductStatus.ACTIVE);

        return productService.toResponseDto(productRepository.save(product));
    }

    // ────────────── Women Products ──────────────

    public List<PendingWomenProductDTO> getPendingWomenProducts() {
        return womenProductRepository
                .findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus.PENDING)
                .stream()
                .map(this::toPendingWomenDTO)
                .toList();
    }

    public WomenProductResponseDTO approveWomenProduct(Long id) {
        WomenProduct product = womenProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));

        product.setApprovalStatus(ApprovalStatus.APPROVED);
        product.setIsAvailable(true);
        product.setDeleted(false);

        return womenProductService.toDTO(womenProductRepository.save(product));
    }

    public Map<String, Object> rejectWomenProduct(Long id, String reason) {
        WomenProduct product = womenProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));

        product.setApprovalStatus(ApprovalStatus.REJECTED);
        product.setIsAvailable(false);

        womenProductRepository.save(product);

        return Map.of(
                "message", "Women product rejected successfully",
                "productId", id,
                "reason", reason != null ? reason : "No reason provided"
        );
    }

    // ✅ SOFT DELETE
    public void deleteWomenProduct(Long id) {
        WomenProduct product = womenProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));

        product.setDeleted(true);
        product.setIsAvailable(false);

        womenProductRepository.save(product);
    }

    // ✅ RESTORE
    public WomenProductResponseDTO restoreWomenProduct(Long id) {
        WomenProduct product = womenProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));

        product.setDeleted(false);
        product.setIsAvailable(true);

        return womenProductService.toDTO(womenProductRepository.save(product));
    }

    // ────────────── Mapping Helpers ──────────────

    private PendingProductListDTO toPendingProductDTO(Product p) {
        return new PendingProductListDTO(
                p.getId(),
                p.getProductName(),
                "Merchant " + p.getMerchantId(),
                p.getCategory().getName(),
                p.getCreatedAt() != null ? p.getCreatedAt().toString() : "N/A",
                getImageList(p.getImagePaths()),
                p.getShortDescription(),
                getProductType(p.getCategory())
        );
    }

    private PendingWomenProductDTO toPendingWomenDTO(WomenProduct p) {
        return new PendingWomenProductDTO(
                p.getId(),
                p.getUuid(),
                p.getName(),
                p.getSeller().getId(),
                p.getSeller().getName(),
                p.getCategory(),
                p.getApprovalStatus() != null ? p.getApprovalStatus().name() : "PENDING",
                p.getImageUrlList(),
                p.getMinPrice(),
                p.getMaxPrice(),
                p.getStock(),
                p.getIsAvailable(),
                p.getCreatedAt() != null ? p.getCreatedAt().toString() : "N/A",
                null
        );
    }

    private List<String> getImageList(String paths) {
        if (paths == null || paths.isBlank()) return List.of();
        return Arrays.asList(paths.split(","));
    }

    private String getProductType(Category category) {
        if (category == null) return "Unknown";
        String name = category.getName().toLowerCase();
        if (name.contains("women")) return "Women";
        if (name.contains("men")) return "Men";
        return "Other";
    }
}