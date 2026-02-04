package com.agrowmart.service;

import com.agrowmart.dto.auth.product.*;
import com.agrowmart.dto.auth.shop.ShopSummaryDTO;
import com.agrowmart.entity.*;
import com.agrowmart.entity.Product.ProductStatus;
import com.agrowmart.exception.ForbiddenException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    
    private final VegetableDetailRepository vegRepo;
    private final DairyDetailRepository dairyRepo;
    private final MeatDetailRepository meatRepo;
    
    private final CloudinaryService cloudinary;
     private final ShopRepository shopRepo;
    private final   UserRepository userRepo;
    
    public ProductService(ProductRepository productRepo, CategoryRepository categoryRepo,

    		VegetableDetailRepository vegRepo,DairyDetailRepository dairyRepo,MeatDetailRepository

    		meatRepo,   CloudinaryService cloudinary,ShopRepository shopRepo,
    		UserRepository userRepo
    		   
    		
    		) {

this.productRepo = productRepo;

this.categoryRepo = categoryRepo;

this.vegRepo=vegRepo;

this.dairyRepo=dairyRepo;

this.meatRepo=meatRepo;

this.cloudinary = cloudinary;

this.shopRepo =shopRepo;
this.userRepo=userRepo;


}

 // Add near the top of ProductService class
    private void validateVendorCanModify(Long merchantId) {
        User vendor = userRepo.findById(merchantId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        if (!"ONLINE".equalsIgnoreCase(vendor.getOnlineStatus())) {
            throw new ForbiddenException("You must be ONLINE to add, update or delete products. Update your status first.");
        }

        if (!"YES".equals(vendor.getProfileCompleted())) {
            throw new ForbiddenException("Please complete your profile 100% before managing products.");
        }
    }
    
    
    
    // ===================== CREATE =====================
    public ProductResponseDTO create(ProductCreateDTO dto, Long merchantId) throws Exception {

    	validateVendorCanModify(merchantId);
    	
    	
    	// After fetching category
    	Category category = categoryRepo.findById(dto.categoryId())
    	        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    	String type = determineProductType(category);  // ← pass full category object
    	
    	
        Product product = new Product();
        product.setMerchantId(merchantId);
        product.setProductName(dto.productName());
        product.setShortDescription(dto.shortDescription());
        product.setCategory(category);
        
        // ===== Stock logic (changed - Ankita) =====
        
        product.setStockQuantity(
                dto.stockQuantity() != null ? dto.stockQuantity() : 0.0
        );
        
        if (dto.stockQuantity() != null) {
            product.setStockQuantity(dto.stockQuantity());
            product.setInStock(dto.stockQuantity() > 0);
        }
        
        product.setInStock(dto.stockQuantity() != null && dto.stockQuantity() > 0);

        List<String> imageUrls = uploadImages(dto.images());
        product.setImagePaths(String.join(",", imageUrls));
        
     // ===== SERIAL NO (ADD) =====
        Long maxSerial = productRepo.findMaxSerialNoByMerchantId(merchantId);
        Long nextSerialNo = (maxSerial == null ? 1 : maxSerial + 1);
        product.setSerialNo(nextSerialNo);


        product = productRepo.save(product); // ID generated

        Object details = createDetailsEntity(dto, product, type);
     // 9️⃣ Seller → Shop
        User seller = userRepo.findById(merchantId).orElse(null);
        Shop shop = seller != null ? seller.getShop() : null;

        ShopSummaryDTO shopDTO = (shop == null) ? null : new ShopSummaryDTO(
                shop.getId(),
                shop.getShopName(),
                shop.getShopPhoto(),
                shop.getShopAddress(),
                shop.getShopType()
        );

        // 🔟 Final response
        return new ProductResponseDTO(
                product.getId(),
                product.getProductName(),
                product.getShortDescription(),
                product.getStatus().name(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                imageUrls,
                merchantId,
                type,
                details,
                product.getInStock() ? "In Stock" : "Out of Stock",
                product.getSerialNo(),
                shopDTO
        );
    
        
    }
   
 // ===================== UPDATE - FINAL & FULL =====================
    public ProductResponseDTO update(Long productId, ProductUpdateDTO dto, Long merchantId) throws Exception {
        validateVendorCanModify(merchantId);

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getMerchantId().equals(merchantId)) {
            throw new ForbiddenException("You can only update your own products");
        }

        // 1. Update basic product fields (null-safe)
        Optional.ofNullable(dto.productName()).ifPresent(product::setProductName);
        Optional.ofNullable(dto.shortDescription()).ifPresent(product::setShortDescription);
        
     // ================= STOCK UPDATE (🔥 FIX HERE 🔥) =================
        if (dto.stockQuantity() != null) {
            product.setStockQuantity(dto.stockQuantity());

            if (dto.stockQuantity() > 0) {
                product.setInStock(true);
            } else {
                product.setStockQuantity(0.0);
                product.setInStock(false);
            }
        }

        // Category change (if provided)
        String type;
        if (dto.categoryId() != null) {
            Category newCategory = categoryRepo.findById(dto.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(newCategory);
            type = determineProductType(newCategory);
        } else {
            type = determineProductType(product.getCategory());
        }

        // 2. Image handling — FULL REPLACE if new images are sent
        if (dto.images() != null && !dto.images().isEmpty()) {
            // Delete old images (best effort — don't fail if delete fails)
            getImageList(product.getImagePaths()).forEach(oldUrl -> {
                try {
                    cloudinary.delete(oldUrl);
                } catch (Exception e) {
                    System.err.println("Failed to delete old image: " + oldUrl);
                }
            });

            // Upload and replace with new images
            List<String> newImageUrls = uploadImages(dto.images());
            product.setImagePaths(String.join(",", newImageUrls));
        }

        // 3. Save core product entity (name, description, category, images)
        product = productRepo.save(product);

        // 4. MOST IMPORTANT: Update the detail entity (Vegetable/Dairy/Meat)
        updateDetailsEntity(dto, product.getId(), type);

        // 5. Fetch fresh updated details for response
        Object updatedDetails = fetchDetailsEntity(product.getId(), type);

        return toResponseDto(product);}
        // 6. Build fresh response with updated data
      


    
    
    
    private void updateDetailsEntity(ProductUpdateDTO dto, Long productId, String type) {
        switch (type) {
            case "VEGETABLE" -> vegRepo.findByProductId(productId).ifPresent(v -> {
                Optional.ofNullable(dto.vegWeight()).ifPresent(v::setWeight);
                Optional.ofNullable(dto.vegUnit()).ifPresent(v::setUnit);
                Optional.ofNullable(dto.vegMinPrice()).ifPresent(v::setMinPrice);
                Optional.ofNullable(dto.vegMaxPrice()).ifPresent(v::setMaxPrice);
                Optional.ofNullable(dto.vegDisclaimer()).ifPresent(v::setDisclaimer);
             
                Optional.ofNullable(dto.shelfLife()).ifPresent(v::setShelfLife);
                vegRepo.save(v);
            });

            case "DAIRY" -> dairyRepo.findByProductId(productId).ifPresent(d -> {
                Optional.ofNullable(dto.dairyQuantity()).ifPresent(d::setQuantity);
//                Optional.ofNullable(dto.dairyPrice()).ifPresent(d::setPrice);
                Optional.ofNullable(dto.dairyMinPrice()).ifPresent(d::setMinPrice);
                Optional.ofNullable(dto.dairyMaxPrice()).ifPresent(d::setMaxPrice);

               
                Optional.ofNullable(dto.dairyBrand()).ifPresent(d::setBrand);
                Optional.ofNullable(dto.dairyIngredients()).ifPresent(d::setIngredients);
                Optional.ofNullable(dto.dairyPackagingType()).ifPresent(d::setPackagingType);
                Optional.ofNullable(dto.dairyProductInfo()).ifPresent(d::setProductInformation);
                Optional.ofNullable(dto.dairyUsageInfo()).ifPresent(d::setUsageInformation);
                Optional.ofNullable(dto.dairyUnit()).ifPresent(d::setUnit);
                Optional.ofNullable(dto.dairyStorage()).ifPresent(d::setStorage);
              
                Optional.ofNullable(dto.shelfLife()).ifPresent(d::setShelfLife);
                dairyRepo.save(d);
            });

            case "MEAT" -> meatRepo.findByProductId(productId).ifPresent(m -> {
                Optional.ofNullable(dto.meatQuantity()).ifPresent(m::setQuantity);
//                Optional.ofNullable(dto.meatPrice()).ifPresent(m::setPrice);
                
                // Changes :- Ankita 
                Optional.ofNullable(dto.meatMinPrice()).ifPresent(m::setMinPrice);
                Optional.ofNullable(dto.meatMaxPrice()).ifPresent(m::setMaxPrice);

                Optional.ofNullable(dto.meatBrand()).ifPresent(m::setBrand);
                Optional.ofNullable(dto.meatKeyFeatures()).ifPresent(m::setKeyFeatures);
                Optional.ofNullable(dto.meatCutType()).ifPresent(m::setCutType);
                Optional.ofNullable(dto.meatServingSize()).ifPresent(m::setServingSize);
                Optional.ofNullable(dto.meatStorageInstruction()).ifPresent(m::setStorageInstruction);
                Optional.ofNullable(dto.meatUsage()).ifPresent(m::setUsage);
                Optional.ofNullable(dto.meatEnergy()).ifPresent(m::setEnergy);
                Optional.ofNullable(dto.meatMarinated()).ifPresent(m::setMarinated);
                Optional.ofNullable(dto.meatPackagingType()).ifPresent(m::setPackagingType);
                Optional.ofNullable(dto.meatDisclaimer()).ifPresent(m::setDisclaimer);
                Optional.ofNullable(dto.meatRefundPolicy()).ifPresent(m::setRefundPolicy);
              
                Optional.ofNullable(dto.shelfLife()).ifPresent(m::setShelfLife);
                meatRepo.save(m);
            });
        }
    }
    
    
    
    // ===================== HELPER: Fetch Details Correctly =====================
    private Object fetchDetailsEntity(Long productId, String type) {
        return switch (type) {
            case "VEGETABLE" -> vegRepo.findByProductId(productId).orElse(null);
            case "DAIRY"     -> dairyRepo.findByProductId(productId).orElse(null);
            case "MEAT"      -> meatRepo.findByProductId(productId).orElse(null);
            default          -> null;
        };
    }


    private List<String> uploadImages(List<MultipartFile> files) throws Exception {
        if (files == null || files.isEmpty()) return List.of();
        return files.stream()
                .map(file -> {
                    try {
                        return cloudinary.upload(file);
                    } catch (Exception e) {
                        throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
                    }
                })
                .toList();
    }

    private List<String> getImageList(String imagePaths) {
        if (!StringUtils.hasText(imagePaths)) return new ArrayList<>();
        return Arrays.stream(imagePaths.split(","))
                .filter(StringUtils::hasText)
                .toList();
    }


    
    private String determineProductType(Category category) {
        if (category == null) return "GENERAL";

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
            // Add more roots if needed later (women-root, etc.)
            current = current.getParent();
        }
        return "GENERAL";
    }
  

    // ===================== SEARCH =====================
 
    public Page<ProductResponseDTO> search(ProductSearchDTO filter) {
        Pageable pageable = PageRequest.of(
                Optional.ofNullable(filter.page()).orElse(0),
                Optional.ofNullable(filter.size()).orElse(20),
                Sort.by("createdAt").descending()
        );

        Specification<Product> spec = Specification.where(null);

        if (StringUtils.hasText(filter.name())) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("productName")), "%" + filter.name().toLowerCase() + "%"));
        }
        if (filter.categoryId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), filter.categoryId()));
        }
        if (StringUtils.hasText(filter.status())) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), Product.ProductStatus.valueOf(filter.status())));
        }

        // THIS IS THE ONLY CORRECT WAY WHEN METHOD REFERENCE DOESN'T WORK
        Page<Product> productPage = productRepo.findAll(spec, pageable);

        return productPage.map(product -> toResponseDto(product));
    }

 // ===================== MAPPER (🔥 SINGLE SOURCE OF TRUTH 🔥) =====================
    public ProductResponseDTO toResponseDto(Product p) {

        List<String> images = getImageList(p.getImagePaths());
        String type = determineProductType(p.getCategory());
        Object details = fetchDetailsEntity(p.getId(), type);

        User seller = userRepo.findById(p.getMerchantId()).orElse(null);
        Shop shop = seller != null ? seller.getShop() : null;

        ShopSummaryDTO shopDTO = shop == null ? null : new ShopSummaryDTO(
                shop.getId(),
                shop.getShopName(),
                shop.getShopPhoto(),
                shop.getShopAddress(),
                shop.getShopType()
        );

        return new ProductResponseDTO(
                p.getId(),
                p.getProductName(),
                p.getShortDescription(),
                p.getStatus().name(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                images,
                p.getMerchantId(),
                type,
                details,
                p.getInStock() ? "In Stock" : "Out of Stock",
                p.getSerialNo(),
                shopDTO
        );
    }

    
    public List<ProductResponseDTO> getVendorProducts(Long merchantId) {
        return productRepo.findByMerchantId(merchantId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }


    // ===================== HELPERS =====================


    private Object createDetailsEntity(ProductCreateDTO dto, Product product, String type) {
        return switch (type) {
            case "VEGETABLE" -> {
                VegetableDetail v = new VegetableDetail();
                v.setProduct(product);
                v.setWeight(dto.vegWeight());
                v.setUnit(dto.vegUnit());
                v.setMinPrice(dto.vegMinPrice());
                v.setMaxPrice(dto.vegMaxPrice());
                v.setDisclaimer(dto.vegDisclaimer());
              
                v.setShelfLife(dto.shelfLife());
                yield vegRepo.save(v);
            }
            case "DAIRY" -> {
                DairyDetail d = new DairyDetail();
                d.setProduct(product);
                d.setQuantity(dto.dairyQuantity());
         
                d.setBrand(dto.dairyBrand());
                d.setIngredients(dto.dairyIngredients());
                d.setPackagingType(dto.dairyPackagingType());
                d.setProductInformation(dto.dairyProductInfo());
                d.setUsageInformation(dto.dairyUsageInfo());
                d.setUnit(dto.dairyUnit());
                d.setStorage(dto.dairyStorage());
//                d.setPrice(dto.dairyPrice());
                d.setMinPrice(dto.dairyMinPrice());   // ✅
                d.setMaxPrice(dto.dairyMaxPrice());   // ✅

              
                d.setShelfLife(dto.shelfLife());
                yield dairyRepo.save(d);
                
            }
            case "MEAT" -> {
                MeatDetail m = new MeatDetail();
                m.setProduct(product);
                m.setQuantity(dto.meatQuantity());
             
                m.setBrand(dto.meatBrand());
                m.setKeyFeatures(dto.meatKeyFeatures());
                m.setCutType(dto.meatCutType());
                m.setServingSize(dto.meatServingSize());
                m.setStorageInstruction(dto.meatStorageInstruction());
                m.setUsage(dto.meatUsage());
                m.setEnergy(dto.meatEnergy());
                m.setMarinated(dto.meatMarinated());
                m.setPackagingType(dto.meatPackagingType());
                m.setDisclaimer(dto.meatDisclaimer());
                m.setRefundPolicy(dto.meatRefundPolicy());
               
                m.setMinPrice(dto.meatMinPrice());   // ✅
                m.setMaxPrice(dto.meatMaxPrice());   // ✅

             
                m.setShelfLife(dto.shelfLife());
                yield meatRepo.save(m);
            }
            default -> null;
        };
    }
    

    public VendorProductPaginatedResponse getVendorProductsPaginated(Long merchantId, int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("serialNo").ascending());
        Page<Product> productPage = productRepo.findByMerchantId(merchantId, pageable);  // Changed - no status filter
        List<ProductResponseDTO> products = productPage.getContent().stream().map(this::toResponseDto).toList();
        return new VendorProductPaginatedResponse(
                products,
                productPage.getNumber(),
                productPage.getTotalPages(),
                productPage.getTotalElements(),
                productPage.getSize()
        );
    }

    

    public List<ProductResponseDTO> getAllActiveProducts() {
        return productRepo
                .findByStatusAndApprovalStatus(
                        Product.ProductStatus.ACTIVE,
                        ApprovalStatus.APPROVED
                )
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    

 // ===================== FILTERING - FULLY WORKING (Regular + Women Products) =====================
 // ===================== FILTERING - FULLY WORKING (Only Regular Products) =====================
    public List<ProductResponseDTO> getFilteredProducts(ProductFilterDTO filter) {
        Specification<Product> spec = ProductSpecifications.isActive();

        // Category filter
        if (filter.categories() != null && !filter.categories().isEmpty()) {
            spec = spec.and(ProductSpecifications.inCategories(filter.categories()));
        }

        // In Stock filter
        if (filter.inStock() != null && filter.inStock()) {
            spec = spec.and(ProductSpecifications.isInStock(true));
        }

        List<Product> products = productRepo.findAll(spec);

        // Sorting
        if (filter.sortBy() != null) {
            boolean ascending = filter.sortBy().endsWith("_low_high");
            products = products.stream()
                    .sorted((p1, p2) -> {
                        return switch (filter.sortBy()) {
                            case "price_low_high", "price_high_low" ->
                                FilterHelper.comparePrice(getMinPrice(p1), getMinPrice(p2), ascending);
                          //  case "rating_low_high", "rating_high_low" ->
                            //    FilterHelper.compareRating(p1.getAverageRating(), p2.getAverageRating(), ascending);
                            default -> 0;
                        };
                    })
                    .toList();
        }

        return products.stream()
                .map(this::toResponseDto)
                .toList();
    }
    
    private BigDecimal getMinPrice(Product p) {
    	String type = determineProductType(p.getCategory());
        return switch (type) {
            case "VEGETABLE" -> vegRepo.findByProductId(p.getId())
                    .map(VegetableDetail::getMinPrice).orElse(BigDecimal.ZERO);
            case "DAIRY" -> dairyRepo.findByProductId(p.getId())
                    .map(DairyDetail::getMinPrice).orElse(BigDecimal.ZERO);
            case "MEAT" -> meatRepo.findByProductId(p.getId())
                    .map(MeatDetail::getMinPrice).orElse(BigDecimal.ZERO);
            default -> BigDecimal.ZERO;
        };
    }

    
    public ProductResponseDTO getPublicProductById(Long productId) throws Exception {
        Product product = productRepo.findApprovedProductById(productId)
                .orElseThrow(() -> new Exception("Product not found or not approved"));
        return toResponseDto(product);
    }

    
    

 // ================= PUBLIC - RECENT PRODUCTS =================
    public List<ProductResponseDTO> getRecentlyAddedPublicProducts(int limit) {
        return productRepo.findAllActiveFromOnlineVendors()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .map(this::toResponseDto)
                .toList();
    }

    

    public List<ProductResponseDTO> getProductsByShop(Long shopUserId) {
        return productRepo
                .findByMerchantIdAndStatusAndApprovalStatus(
                        shopUserId,
                        ProductStatus.ACTIVE,
                        ApprovalStatus.APPROVED
                )
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    
    // 21 Jan 
    
    // ================= ADMIN ONLY =================
    // (approval filter NOT required here)

    public List<ProductResponseDTO> getMerchantProductsForAdmin(Long merchantId) {
        return productRepo.findByMerchantId(merchantId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }
    
    // 21 Jan 
    
    
    
    
 //------- status
 // In ProductService.java (add this new method)
    public ProductResponseDTO updateStatus(Long productId, String newStatusStr, Long merchantId) throws Exception {
        validateVendorCanModify(merchantId);

        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getMerchantId().equals(merchantId)) {
            throw new ForbiddenException("You can only update your own products");
        }

        ProductStatus newStatus;
        try {
            newStatus = ProductStatus.valueOf(newStatusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: must be ACTIVE or INACTIVE");
        }

        // Always update even if same (idempotent)
        product.setStatus(newStatus);
        product = productRepo.save(product);

        return toResponseDto(product);
    }
    
  //Deepti kadam
    // ===================== ADMIN METHODS =====================
 // ===================== ADMIN: FULL PRODUCTS WITH DETAILS =====================
    public List<ProductResponseDTO> getAllProductsForAdminDTO() {
        return productRepo.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponseDto)   // ⭐ DETAILS HERE
                .toList();
    }
    
    

 // ADMIN: View product details
 // ===================== ADMIN: VIEW PRODUCT WITH FULL DETAILS =====================
    public ProductResponseDTO getProductByIdForAdminDTO(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toResponseDto(product); // ⭐ THIS FIXES EVERYTHING
    }
    
    @Transactional
    public void delete(Long productId, Long merchantId) {
        validateVendorCanModify(merchantId);

        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getMerchantId().equals(merchantId)) {
            throw new ForbiddenException("You can only delete your own products");
        }

        // Critical protection – vendors cannot delete approved products
        if (product.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new ForbiddenException(
                "Approved products cannot be deleted by vendors. " +
                "Only admin can permanently remove approved/live products. " +
                "Contact support if needed."
            );
        }

        // Step 1: Determine product type (VEGETABLE / DAIRY / MEAT)
        String type = determineProductType(product.getCategory());

        // Step 2: Delete the child detail record first (this fixes the FK error)
        deleteDetailsEntity(productId, type);

        // Step 3: Clean Cloudinary images
        if (product.getImagePaths() != null && !product.getImagePaths().isBlank()) {
            Arrays.stream(product.getImagePaths().split(","))
                  .filter(url -> url != null && !url.trim().isEmpty())
                  .map(this::extractPublicId)
                  .forEach(publicId -> {
                      try {
                          cloudinary.delete(publicId);
                      } catch (Exception e) {
                          System.err.println("Failed to delete image " + publicId);
                      }
                  });
        }

        // Step 4: HARD DELETE the product itself
        productRepo.delete(product);

        System.out.println("Vendor " + merchantId + " HARD-DELETED pending product ID: " + productId);

        // Optional: reorder serial numbers if needed
        // reorderVendorProductsSerialNumbers(merchantId);
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
    
    private void deleteDetailsEntity(Long productId, String type) {
        switch (type) {
            case "VEGETABLE" -> vegRepo.findByProductId(productId).ifPresent(vegRepo::delete);
            case "DAIRY" -> dairyRepo.findByProductId(productId).ifPresent(dairyRepo::delete);
            case "MEAT" -> meatRepo.findByProductId(productId).ifPresent(meatRepo::delete);
        }
        
    }
}