
package com.agrowmart.admin_product_management;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.agrowmart.dto.auth.product.PendingProductListDTO;
import com.agrowmart.dto.auth.product.ProductResponseDTO;
import com.agrowmart.dto.auth.shop.ShopSummaryDTO;
import com.agrowmart.dto.auth.women.PendingWomenProductDTO;
import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
import com.agrowmart.entity.ApprovalStatus;
import com.agrowmart.entity.Category;
import com.agrowmart.entity.Product;
import com.agrowmart.entity.User;
import com.agrowmart.entity.WomenProduct;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.DairyDetailRepository;
import com.agrowmart.repository.MeatDetailRepository;
import com.agrowmart.repository.ProductRepository;
import com.agrowmart.repository.UserRepository;
import com.agrowmart.repository.VegetableDetailRepository;
import com.agrowmart.repository.WomenProductRepository;
import com.agrowmart.service.CloudinaryService;
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
    private final CloudinaryService cloudinaryService;
    private final VegetableDetailRepository vegRepo;
    private final DairyDetailRepository dairyRepo;
    private final MeatDetailRepository meatRepo;
    private final UserRepository userRepository;
    
    
    public AdminProductService(
            ProductRepository productRepository,
            WomenProductRepository womenProductRepository,
            ProductService productService,
            WomenProductService womenProductService,
            CloudinaryService cloudinaryService,
        	VegetableDetailRepository vegRepo,DairyDetailRepository dairyRepo,MeatDetailRepository

    		meatRepo,
    		UserRepository userRepository
            ) {

        this.productRepository = productRepository;
        this.womenProductRepository = womenProductRepository;
        this.productService = productService;
        this.womenProductService = womenProductService;
         this.cloudinaryService =cloudinaryService;
         this.vegRepo=vegRepo;

         this.dairyRepo=dairyRepo;

         this.meatRepo=meatRepo;
         this.userRepository=userRepository;
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
   

        return productService.toResponseDto(productRepository.save(product));
    }

    public Map<String, Object> rejectProduct(Long productId, String reason) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Optional: only allow reject if still PENDING
        if (product.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Only PENDING products can be rejected");
        }

        product.setApprovalStatus(ApprovalStatus.REJECTED);
        product.setStatus(Product.ProductStatus.INACTIVE);
        product.setRejectionReason(reason != null && !reason.trim().isEmpty() 
            ? reason.trim() 
            : "No specific reason provided by admin");

        productRepository.save(product);

        return Map.of(
            "message", "Product rejected successfully",
            "productId", productId,
            "reason", product.getRejectionReason()
        );
    }
    
    private void deleteDetailsEntity(Long productId, String type) {
        switch (type) {
            case "VEGETABLE" -> vegRepo.findByProductId(productId).ifPresent(vegRepo::delete);
            case "DAIRY" -> dairyRepo.findByProductId(productId).ifPresent(dairyRepo::delete);
            case "MEAT" -> meatRepo.findByProductId(productId).ifPresent(meatRepo::delete);
        }
    }
    
    private String determineProductType(Category category) {
        if (category == null) {
            return "GENERAL";
        }
        Category current = category;
        while (current != null) {
            String slug = current.getSlug();
            if ("vegetable-root".equals(slug)) {
                return "VEGETABLE";
            }
            if ("dairy-root".equals(slug)) {
                return "DAIRY";
            }
            if ("seafoodmeat-root".equals(slug)) {
                return "MEAT";
            }
            current = current.getParent();
        }
        return "GENERAL";
    }
    
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        // Step 1: Get type – use the CORRECT method name
        String type = determineProductType(product.getCategory());

        // Step 2: Delete child detail first – this removes vegetable_details / dairy_details / meat_details
        deleteDetailsEntity(productId, type);

        // Step 3: Clean images
        if (product.getImagePaths() != null && !product.getImagePaths().isBlank()) {
            Arrays.stream(product.getImagePaths().split(","))
                  .filter(url -> url != null && !url.trim().isEmpty())
                  .map(this::extractPublicId)
                  .forEach(publicId -> {
                      try {
                          cloudinaryService.delete(publicId);
                      } catch (Exception ignored) {}
                  });
        }

        // Step 4: HARD DELETE the product
        productRepository.delete(product);

        System.out.println("ADMIN HARD-DELETED product ID: " + productId + " (type: " + type + ")");
    }
    // ✅ RESTORE PRODUCT
    public ProductResponseDTO restoreProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setStatus(Product.ProductStatus.ACTIVE);

        return productService.toResponseDto(productRepository.save(product));
    }

    // ────────────── Women Products ──────────────

    public List<PendingWomenProductDTO> getPendingWomenProducts() {
        return womenProductRepository
                .findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus.PENDING)
                .stream()
                .peek(wp -> {
                    // Force lazy loading (only if inside @Transactional)
                    if (wp.getSeller() != null) {
                        wp.getSeller().getShop();
                    }
                })
                .map(this::toPendingWomenDTO)
                .toList();
    }

    public WomenProductResponseDTO approveWomenProduct(Long id) {
        WomenProduct product = womenProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));

        product.setApprovalStatus(ApprovalStatus.APPROVED);
        product.setIsAvailable(true);
  
        return womenProductService.toDTO(womenProductRepository.save(product));
    }

    public Map<String, Object> rejectWomenProduct(Long id, String reason) {
        WomenProduct product = womenProductRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));

        if (product.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Only PENDING products can be rejected");
        }

        product.setApprovalStatus(ApprovalStatus.REJECTED);
        product.setIsAvailable(false);
        product.setRejectionReason(reason != null && !reason.trim().isEmpty() 
            ? reason.trim() 
            : "No specific reason provided by admin");

        womenProductRepository.save(product);

        return Map.of(
            "message", "Women product rejected successfully",
            "productId", id,
            "reason", product.getRejectionReason()
        );
    }

    /**
     * ADMIN ONLY: Permanently delete (hard delete) any women's product,
     * including APPROVED ones. Record is completely removed from database.
     */
    @Transactional
    public void deleteWomenProduct(Long id) {
        WomenProduct product = womenProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Women product not found with ID: " + id));


        // Step 1: Clean up Cloudinary images (very important!)
        if (product.getImageUrls() != null && !product.getImageUrls().isBlank()) {
            Arrays.stream(product.getImageUrls().split(","))
                  .filter(url -> url != null && !url.trim().isEmpty())
                  .map(this::extractPublicId)  // your existing helper method
                  .forEach(publicId -> {
                      try {
                          cloudinaryService.delete(publicId);
                          System.out.println("Deleted Cloudinary image during admin delete: " + publicId);
                      } catch (Exception e) {
                          System.err.println("Failed to delete image " + publicId + ": " + e.getMessage());
                      }
                  });
        }

        // Step 2: HARD DELETE - remove the row completely from database
        womenProductRepository.delete(product);

        System.out.println("ADMIN HARD-DELETED women product ID: " + id + " (was " + product.getApprovalStatus() + ")");
    }

    
    
    
    
    // ✅ RESTORE
    public WomenProductResponseDTO restoreWomenProduct(Long id) {
        WomenProduct product = womenProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));

    
        product.setIsAvailable(true);

        return womenProductService.toDTO(womenProductRepository.save(product));
    }

 // ────────────── Mapping Helpers ──────────────
    private PendingProductListDTO toPendingProductDTO(Product p) {
        User seller = p.getMerchantId() != null
                ? userRepository.findById(p.getMerchantId()).orElse(null)
                : null;

        var shop = seller != null ? seller.getShop() : null;

        ShopSummaryDTO shopDTO = shop == null ? null : new ShopSummaryDTO(
                shop.getId(),
                shop.getShopName(),
                shop.getShopPhoto(),
                shop.getShopAddress(),
                shop.getShopType()
        );

        return new PendingProductListDTO(
                p.getId(),
                p.getProductName(),
                seller != null ? seller.getName() : "Unknown Seller",
                p.getCategory() != null ? p.getCategory().getName() : "N/A",
                p.getApprovalStatus() != null ? p.getApprovalStatus().name() : "PENDING",   // fixed
                p.getCreatedAt() != null ? p.getCreatedAt().toString() : "N/A",             // fixed
                getImageList(p.getImagePaths()),
                p.getShortDescription(),
                getProductType(p.getCategory()),
                shopDTO
        );
    }


    private PendingWomenProductDTO toPendingWomenDTO(WomenProduct p) {

        User seller = p.getSeller();
        var shop = seller != null ? seller.getShop() : null;

        ShopSummaryDTO shopDTO = shop == null ? null : new ShopSummaryDTO(
                shop.getId(),
                shop.getShopName(),
                shop.getShopPhoto(),
                shop.getShopAddress(),
                shop.getShopType()
        );

        return new PendingWomenProductDTO(
                p.getId(),
                p.getUuid(),
                p.getName(),
                seller != null ? seller.getId() : null,
                seller != null ? seller.getName() : "Unknown Seller",
                p.getCategory(), // String category is OK
                p.getApprovalStatus() != null ? p.getApprovalStatus().name() : "PENDING",
                p.getImageUrlList(),
                p.getMinPrice(),
                p.getMaxPrice(),
                p.getStock(),
                p.getIsAvailable(),
                p.getCreatedAt() != null ? p.getCreatedAt().toString() : "N/A",
                null,
                shopDTO
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
    
    private String extractPublicId(String url) {
        if (url == null || url.isEmpty()) return null;
        try {
            String noExt = url.substring(0, url.lastIndexOf('.'));
            return noExt.substring(noExt.lastIndexOf('/') + 1);
        } catch (Exception e) {
            return null;
        }
    }
    
}