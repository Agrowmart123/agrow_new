package com.agrowmart.service;

import com.agrowmart.dto.auth.product.ProductFilterDTO;
import com.agrowmart.dto.auth.shop.ShopSummaryDTO;
import com.agrowmart.dto.auth.shop.WorkingHourDTO;
import com.agrowmart.dto.auth.women.WomenProductCreateDTO;
import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
import com.agrowmart.entity.ApprovalStatus;
import com.agrowmart.entity.Product.ProductStatus;
import com.agrowmart.entity.Shop;
import com.agrowmart.entity.User;
import com.agrowmart.entity.WomenProduct;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.ForbiddenException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.UserRepository;
import com.agrowmart.repository.WomenProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@Transactional

public class WomenProductService {
	private static final Logger log = LoggerFactory.getLogger(WomenProductService.class);
	
    private final WomenProductRepository productRepo;
    private final UserRepository userRepo;
    private final CloudinaryService cloudinaryService;
    
    public WomenProductService(WomenProductRepository productRepo,UserRepository userRepo,CloudinaryService cloudinaryService ) {
        this.productRepo = productRepo;
        this.userRepo =userRepo;
        this.cloudinaryService=cloudinaryService;
    }
    
    
 // Add in WomenProductService
    private void validateSellerCanModify(Long sellerId) {
    	
        User seller = userRepo.findById(sellerId)
            .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        if (!"ONLINE".equalsIgnoreCase(seller.getOnlineStatus())) {
            throw new BusinessValidationException("You must set your status to ONLINE to manage products.");
        }

        if (!"YES".equals(seller.getProfileCompleted())) {
            throw new BusinessValidationException("Complete your profile first before adding or modifying products.");
        }
    }
    

    
    public WomenProductResponseDTO createProduct(Long sellerId, WomenProductCreateDTO dto, List<MultipartFile> images) throws Exception {
        validateSellerCanModify(sellerId);
        
        User seller = userRepo.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        
        WomenProduct product = new WomenProduct();
        product.setSeller(seller);
        product.setName(dto.name());
        product.setCategory(dto.category());
        product.setDescription(dto.description());
        
        product.setMinPrice(dto.minPrice());
        product.setMaxPrice(dto.maxPrice());
        product.setStock(dto.stock() != null ? dto.stock() : 0);
        product.setUnit(dto.unit());
        product.setIsAvailable(dto.stock() != null && dto.stock() > 0);
        
        product.setIngredients(dto.ingredients());
        product.setShelfLife(dto.shelfLife());
        product.setPackagingType(dto.packagingType());
        product.setProductInfo(dto.productInfo());
        
        // ──── ADD THESE TWO LINES ────
        product.setStatus(ProductStatus.ACTIVE);              // ← Very important!
        product.setApprovalStatus(ApprovalStatus.PENDING);    // ← Already there, just keeping for clarity
        
        // Upload product images
        List<String> uploadedUrls = uploadFiles(images);
        product.setImageUrls(String.join(",", uploadedUrls));
        
        product = productRepo.save(product);
        
        log.info("Women product created → ID: {}, Seller: {}", product.getId(), sellerId);
        return toDTO(product);
    }



    // ========================= GET BY ID =========================
    public WomenProductResponseDTO getProductById(Long id) {
        WomenProduct p = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return toDTO(p);
    }

  
    
    
    
    
 // ========================= GET MY PRODUCTS (Only own products - show all statuses including PENDING)
    public List<WomenProductResponseDTO> getMyProducts(Long sellerId) {
        return productRepo.findBySellerId(sellerId).stream()
                .map(this::toDTO)
                .toList();
    }

    // New: Paginated version for /my (same logic - shows PENDING too)
    public Page<WomenProductResponseDTO> getMyProductsPaginated(Long sellerId, Pageable pageable) {
        Page<WomenProduct> productPage = productRepo.findBySellerId(sellerId, pageable);
        return productPage.map(this::toDTO);
    }

    // ========================= GET ALL PRODUCTS (PUBLIC) - ONLY APPROVED
    public List<WomenProductResponseDTO> getAllWomenProducts() {
        return productRepo.findByApprovalStatus(ApprovalStatus.APPROVED)  // ← ONLY APPROVED
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ========================= GET ALL ACTIVE (PUBLIC) - ONLY APPROVED + AVAILABLE
    public List<WomenProductResponseDTO> getAllActiveWomenProducts() {
        return productRepo.findByApprovalStatusAndIsAvailableTrue(ApprovalStatus.APPROVED)  // ← ONLY APPROVED + available
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ========================= RECENTLY ADDED (PUBLIC) - ONLY APPROVED
    public List<WomenProductResponseDTO> getRecentlyAddedWomenProducts(int limit) {
        return productRepo.findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus.APPROVED)  // ← ONLY APPROVED
                .stream()
                .limit(limit)
                .map(this::toDTO)
                .toList();
    }

    // ========================= FILTERED (PUBLIC) - ONLY APPROVED
    public List<WomenProductResponseDTO> getFilteredProducts(ProductFilterDTO filter) {
        // Start with ONLY APPROVED products
        List<WomenProduct> products = productRepo.findByApprovalStatus(ApprovalStatus.APPROVED);

        // Apply other filters...
        if (filter.categories() != null && !filter.categories().isEmpty()) {
            products = products.stream()
                    .filter(p -> filter.categories().contains(p.getCategory()))
                    .toList();
        }
        if (filter.inStock() != null && filter.inStock()) {
            products = products.stream()
                    .filter(p -> p.getStock() != null && p.getStock() > 0)
                    .toList();
        }
        // Sorting...
        if (filter.sortBy() != null) {
            boolean ascending = filter.sortBy().endsWith("_low_high");
            products = products.stream()
                    .sorted((p1, p2) -> {
                        int cmp = p1.getMinPrice().compareTo(p2.getMinPrice());
                        return ascending ? cmp : -cmp;
                    })
                    .toList();
        }
        return products.stream()
                .map(this::toDTO)
                .toList();
    }
    

    
 // ========================= UPDATE PRODUCT =========================
    public WomenProductResponseDTO updateProduct(Long sellerId, Long productId, WomenProductCreateDTO dto, List<MultipartFile> newImages) throws Exception {
        validateSellerCanModify(sellerId);
        
        WomenProduct product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new SecurityException("You can only update your own products");
        }

        // Update only if new value is provided (null-safe)
        if (dto.name() != null && !dto.name().trim().isEmpty()) {
            product.setName(dto.name().trim());
        }

        // FIX for unit (NOT NULL in DB) – only update if provided
        if (dto.unit() != null && !dto.unit().trim().isEmpty()) {
            product.setUnit(dto.unit().trim());
        }
        // If unit is null/empty → keep old value (do NOT set null)

        if (dto.category() != null && !dto.category().trim().isEmpty()) {
            product.setCategory(dto.category().trim());
        }

        if (dto.description() != null) {
            product.setDescription(dto.description());
        }

        if (dto.minPrice() != null) {
            product.setMinPrice(dto.minPrice());
        }
        if (dto.maxPrice() != null) {
            product.setMaxPrice(dto.maxPrice());
        }
        if (dto.stock() != null) {
            product.setStock(dto.stock());
            product.setIsAvailable(dto.stock() > 0);
        }

        // Optional fields – preserve old value if not sent
        product.setIngredients(dto.ingredients() != null ? dto.ingredients() : product.getIngredients());
        product.setShelfLife(dto.shelfLife() != null ? dto.shelfLife() : product.getShelfLife());
        product.setPackagingType(dto.packagingType() != null ? dto.packagingType() : product.getPackagingType());
        product.setProductInfo(dto.productInfo() != null ? dto.productInfo() : product.getProductInfo());

     // ──── IMAGE HANDLING – FINAL SAFE LOGIC ────
        List<String> finalImageUrls = new ArrayList<>();

        if (newImages != null && !newImages.isEmpty() && newImages.stream().anyMatch(file -> !file.isEmpty())) {
            // Case 1: User sent at least one valid new image → REPLACE ALL
        	  System.out.println("New images received for product {}. Deleting old images..."+ productId);

            // Delete ALL old images from Cloudinary (only if they exist)
            if (product.getImageUrls() != null && !product.getImageUrls().isBlank()) {
                Arrays.stream(product.getImageUrls().split(","))
                        .filter(url -> url != null && !url.trim().isEmpty())
                        .map(this::extractPublicId)
                        .forEach(publicId -> {
                            try {
                                cloudinaryService.delete(publicId);
                                System.out.println("Deleted old Cloudinary image: {}"+ publicId);
                            } catch (Exception e) {
                            	 System.out.println("Failed to delete old Cloudinary image {}: {}. Continuing..."+ publicId + e.getMessage());
                            }
                        });
            }

            // Upload only the new valid images
            List<String> uploadedUrls = uploadFiles(newImages);
            finalImageUrls.addAll(uploadedUrls);
            System.out.println("Uploaded {} new images for product {}"+ uploadedUrls.size() + productId);

        } else {
            // Case 2: No new images (or empty files) → KEEP ALL OLD IMAGES
        	 System.out.println("No new images sent for product {}. Keeping existing images."+ productId);

            if (product.getImageUrls() != null && !product.getImageUrls().isBlank()) {
                finalImageUrls = Arrays.asList(product.getImageUrls().split(","));
            }
        }

        // Set final image URLs (empty string if no images)
        product.setImageUrls(finalImageUrls.isEmpty() ? null : String.join(",", finalImageUrls));
        product.setUpdatedAt(LocalDateTime.now());

        product = productRepo.save(product);
        log.info("Women product updated → ID: {}, Seller: {}", productId, sellerId);
        return toDTO(product);
    }

    // ========================= HELPER METHODS =========================

    private List<String> uploadFiles(List<MultipartFile> files) throws Exception {
        List<String> urls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    urls.add(cloudinaryService.upload(file));
                }
            }
        }
        return urls;
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

    
    public WomenProductResponseDTO toDTO(WomenProduct p) {

        // ---------- IMAGE URL PARSING ----------
        List<String> imageList = new ArrayList<>();
        if (p.getImageUrls() != null && !p.getImageUrls().isBlank()) {
            imageList = Arrays.asList(p.getImageUrls().split(","));
        }

        // ---------- SELLER & SHOP ----------
        User seller = p.getSeller();
        Shop shop = seller != null ? seller.getShop() : null;

        ShopSummaryDTO shopDTO = null;

        if (shop != null) {
        	 Boolean open = isShopOpenFromWorkingHours(shop);

            shopDTO = new ShopSummaryDTO(
                    shop.getId(),                          // Long shopId
                    shop.getShopName(),                    // String shopName
                    shop.getShopPhoto(),                   // String shopPhoto
                    shop.getShopAddress(),                 // String shopAddress
                    shop.getShopType(),                    // String shopType
                    extractCity(shop.getShopAddress()),    // String city
                    extractPincode(shop.getShopAddress()), // String pincode
                    0.0,                                   // Double rating
                    open                                   // Boolean open
            );
        }

        
        
        // ---------- DTO MAPPING (ORDER MATTERS!) ----------
        return new WomenProductResponseDTO(
                p.getId(),                       // Long id
                p.getUuid(),                     // String uuid

                seller != null ? seller.getId() : null,              // Long sellerId
                seller != null ? seller.getName() : "Unknown Seller",// String sellerName

                p.getName(),                     // String name
                p.getCategory(),                 // String category
                p.getDescription(),              // String description
                p.getApprovalStatus(),           // ApprovalStatus status

                p.getMinPrice(),                 // BigDecimal minPrice
                p.getMaxPrice(),                 // BigDecimal maxPrice
                p.getStock(),                    // Integer stock
                p.getUnit(),                     // String unit

                imageList,                       // List<String> imageUrls
                p.getIsAvailable(),              // Boolean isAvailable
                p.getCreatedAt(),                // LocalDateTime createdAt

                // ===== EXTRA FIELDS =====
                p.getIngredients(),              // String ingredients
                p.getShelfLife(),                // String shelfLife
                p.getPackagingType(),            // String packagingType
                p.getProductInfo(),              // String productInfo

                shopDTO                          // ShopSummaryDTO
        );
    }

    
    
    
    
    

    private Boolean isShopOpenFromWorkingHours(Shop shop) {
        if (shop == null || shop.getWorkingHoursJson() == null || shop.getWorkingHoursJson().trim().isEmpty()) {
            System.out.println("Shop " + (shop != null ? shop.getId() : "null") + " has no working hours JSON");
            return false;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<WorkingHourDTO> hours = mapper.readValue(
                    shop.getWorkingHoursJson(),
                    new TypeReference<List<WorkingHourDTO>>() {}
            );

            if (hours.isEmpty()) {
                System.out.println("Shop " + shop.getId() + " has empty working hours list");
                return false;
            }

            String today = LocalDate.now(ZoneId.of("Asia/Kolkata"))
                    .getDayOfWeek()
                    .name();  // MONDAY, TUESDAY, ...

            LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));

            // Debug info (you can switch to proper logger later)
            System.out.println("Shop ID: " + shop.getId() +
                    " | Today: " + today +
                    " | Current time: " + now);

            System.out.println("Working hours JSON: " + shop.getWorkingHoursJson());

            for (WorkingHourDTO h : hours) {
                String storedDay = h.getDay();
                if (storedDay == null || storedDay.trim().isEmpty()) {
                    continue;
                }

                String normalizedDay = storedDay.trim().toUpperCase();

                System.out.println("  Checking day: " + storedDay + " → normalized: " + normalizedDay);

                if (today.equals(normalizedDay)) {
                    String openStr = h.getOpen();
                    String closeStr = h.getClose();

                    if (openStr == null || closeStr == null || openStr.trim().isEmpty() || closeStr.trim().isEmpty()) {
                        System.out.println("  Invalid time format for " + normalizedDay);
                        continue;
                    }

                    LocalTime openTime = LocalTime.parse(openStr.trim());
                    LocalTime closeTime = LocalTime.parse(closeStr.trim());

                    System.out.println("  → Found matching day: " + normalizedDay +
                            " | Open: " + openTime +
                            " | Close: " + closeTime +
                            " | Now: " + now);

                    // Normal opening hours (open < close)
                    if (closeTime.isAfter(openTime)) {
                        boolean isOpen = !now.isBefore(openTime) && !now.isAfter(closeTime);
                        System.out.println("  → Normal hours → isOpen = " + isOpen);
                        return isOpen;
                    }
                    // Overnight hours (e.g. 22:00 – 06:00)
                    else {
                        boolean isOpen = !now.isBefore(openTime) || !now.isAfter(closeTime);
                        System.out.println("  → Overnight hours → isOpen = " + isOpen);
                        return isOpen;
                    }
                }
            }

            System.out.println("No matching day found for shop " + shop.getId() + " (today is " + today + ")");
            return false;

        } catch (JsonProcessingException e) {
            System.err.println("Invalid JSON format for shop " + shop.getId() + ": " + e.getMessage());
            return false;
        } catch (DateTimeParseException e) {
            System.err.println("Time parsing error for shop " + shop.getId() + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error checking shop " + shop.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    
    
    

    public WomenProductResponseDTO updateWomenProductStatus(Long productId, boolean isActive, Long sellerId) {
        validateSellerCanModify(sellerId);

        WomenProduct product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("You can only update status of your own products");
        }

        // Optional: skip if no change (idempotent)
        if (product.getIsAvailable() != isActive) {
            product.setIsAvailable(isActive);
            product.setUpdatedAt(LocalDateTime.now());
            product = productRepo.save(product);
        }

        return toDTO(product);
    }
    public WomenProductResponseDTO updateProductApproval(
            Long productId,
            ApprovalStatus status
    ) {
        WomenProduct product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setApprovalStatus(status);

        if (status == ApprovalStatus.APPROVED) {
            product.setIsAvailable(true);
        } else {
            product.setIsAvailable(false);
        }

        product.setUpdatedAt(LocalDateTime.now());
        productRepo.save(product);

        return toDTO(product);
    }
    
  //Deepti Kadam
    // ===================== ADMIN METHODS =====================
    public List<WomenProductResponseDTO> getAllProductsForAdmin() {
        return productRepo.findAllWithSellerAndShopOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .toList();
    }


       
   //-----------------------    
       public void deleteProduct(Long sellerId, Long productId) {
    	    validateSellerCanModify(sellerId);

    	    WomenProduct product = productRepo.findById(productId)
    	            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

    	    if (!product.getSeller().getId().equals(sellerId)) {
    	        throw new ForbiddenException("You can only delete your own products");
    	    }

    	    // Critical: Block delete of APPROVED products
    	    if (product.getApprovalStatus() == ApprovalStatus.APPROVED) {
    	        throw new ForbiddenException(
    	            "Approved products cannot be deleted by vendors. " +
    	            "Only admin can remove approved/live products."
    	        );
    	    }

    	    // Optional: Extra safety - block if product has any orders
    	    // if (orderItemRepository.existsByWomenProductId(productId)) {
    	    //     throw new ForbiddenException("Cannot delete - product is part of orders");
    	    // }

    	    // HARD DELETE - remove from database completely
    	    productRepo.delete(product);

    	    // Optional: Clean Cloudinary images
    	    if (product.getImageUrls() != null && !product.getImageUrls().isBlank()) {
    	        Arrays.stream(product.getImageUrls().split(","))
    	              .filter(url -> url != null && !url.trim().isEmpty())
    	              .map(this::extractPublicId)
    	              .forEach(publicId -> {
    	                  try {
    	                      cloudinaryService.delete(publicId);
    	                  } catch (Exception e) {
    	                      // log error, don't fail
    	                  }
    	              });
    	    }

    	}
       
       
       public WomenProductResponseDTO getProductByIdForAdmin(Long id) {
    	    WomenProduct product = productRepo.findByIdWithSellerAndShop(id)
    	            .orElseThrow(() -> new ResourceNotFoundException("Women product not found"));

    	    return toDTO(product); // ✅ THIS includes ShopSummaryDTO
    	}
       
       
       
       
       private boolean isShopOpen(LocalTime opensAt, LocalTime closesAt) {

   	    if (opensAt == null || closesAt == null) {
   	        return false;
   	    }

   	    LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));

   	    // Normal same-day shop (09:00 → 18:00)
   	    if (closesAt.isAfter(opensAt)) {
   	        return !now.isBefore(opensAt) && !now.isAfter(closesAt);
   	    }

   	    // Overnight shop (20:00 → 02:00)
   	    return !now.isBefore(opensAt) || !now.isAfter(closesAt);
   	}


      // ===================== ADDRESS HELPERS =====================
      private String extractCity(String address) {
          if (address == null || address.isBlank()) return null;
          return address;
      }

      private String extractPincode(String address) {
          return null;
      }
}
