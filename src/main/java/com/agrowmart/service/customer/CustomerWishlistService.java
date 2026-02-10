//package com.agrowmart.service.customer;
//
//import com.agrowmart.dto.auth.customer.WishlistAddRequest;
//import com.agrowmart.dto.auth.customer.WishlistProductDTO;
//import com.agrowmart.entity.DairyDetail;
//import com.agrowmart.entity.MeatDetail;
//import com.agrowmart.entity.Product;
//import com.agrowmart.entity.Shop;
//import com.agrowmart.entity.User;
//import com.agrowmart.entity.VegetableDetail;
//import com.agrowmart.entity.WomenProduct;
//import com.agrowmart.entity.customer.CustomerWishlist;
//import com.agrowmart.repository.*;
//import com.agrowmart.repository.customer.CustomerWishlistRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//
//@Service
//@Transactional
//public class CustomerWishlistService {
//
//    private final CustomerWishlistRepository wishlistRepo;
//    private final ProductRepository productRepo;
//    private final WomenProductRepository womenProductRepo;
//    private final VegetableDetailRepository vegRepo;      // Added missing
//    private final DairyDetailRepository dairyRepo;        // Added missing
//    private final MeatDetailRepository meatRepo;          // Added missing
//    private final ShopRepository shopRepo;
//    private final UserRepository userRepo;
//
//    public CustomerWishlistService(CustomerWishlistRepository wishlistRepo,
//                                   ProductRepository productRepo,
//                                   WomenProductRepository womenProductRepo,
//                                   VegetableDetailRepository vegRepo,
//                                   DairyDetailRepository dairyRepo,
//                                   MeatDetailRepository meatRepo,
//                                   ShopRepository shopRepo,
//                                   UserRepository userRepo) {
//        this.wishlistRepo = wishlistRepo;
//        this.productRepo = productRepo;
//        this.womenProductRepo = womenProductRepo;
//        this.vegRepo = vegRepo;
//        this.dairyRepo = dairyRepo;
//        this.meatRepo = meatRepo;
//        this.shopRepo = shopRepo;
//        this.userRepo = userRepo;
//    }
//
//    public WishlistProductDTO addToWishlist(Long customerId, WishlistAddRequest req) {
//        String type = req.productType().toUpperCase();
//        if (wishlistRepo.existsByCustomerIdAndProductIdAndProductType(customerId, req.productId(), type)) {
//            throw new RuntimeException("Product already in wishlist");
//        }
//        CustomerWishlist wishlist = new CustomerWishlist(customerId, req.productId(), type);
//        wishlistRepo.save(wishlist);
//        return buildWishlistProduct(customerId, req.productId(), type);
//    }
//
//    public void removeFromWishlist(Long customerId, Long productId, String productType) {
//        wishlistRepo.deleteByCustomerIdAndProductIdAndProductType(customerId, productId, productType.toUpperCase());
//    }
//
//    public List<WishlistProductDTO> getWishlist(Long customerId) {
//        return wishlistRepo.findByCustomerIdOrderByAddedAtDesc(customerId)
//                .stream()
//                .map(w -> buildWishlistProduct(customerId, w.getProductId(), w.getProductType()))
//                .toList();
//    }
//
//    public boolean isInWishlist(Long customerId, Long productId, String productType) {
//        return wishlistRepo.existsByCustomerIdAndProductIdAndProductType(customerId, productId, productType.toUpperCase());
//    }
//
//    private WishlistProductDTO buildWishlistProduct(Long customerId, Long productId, String productType) {
//        CustomerWishlist entry = wishlistRepo.findByCustomerIdAndProductIdAndProductType(customerId, productId, productType)
//                .orElseThrow(() -> new EntityNotFoundException("Wishlist entry not found"));
//
//        String timeAgo = formatTimeAgo(entry.getAddedAt());
//
//        if ("REGULAR".equals(productType)) {
//            Product p = productRepo.findById(productId)
//                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
//
//            Shop shop = shopRepo.findById(p.getMerchantId()).orElse(null);
//            User vendor = userRepo.findById(p.getMerchantId()).orElse(null);
//
//            String determinedType = determineType(p.getCategory().getName());
//
//            BigDecimal minPrice = getMinPriceFromDetails(p, determinedType);
//            BigDecimal maxPrice = getMaxPriceFromDetails(p, determinedType);
//
//            return new WishlistProductDTO(
//                    entry.getId(),
//                    p.getId(),
//                    p.getProductName(),
//                    getFirstImage(p.getImagePaths()),
//                    shop != null ? shop.getShopName() : "Unknown",
//                    vendor != null ? vendor.getBusinessName() : "Unknown",
//                    minPrice,
//                    maxPrice,
//                 
//                    p.getCategory().getName(),
//                    determinedType,
//                    p.getInStock(),
//                    entry.getAddedAt(),
//                    timeAgo
//            );
//        } else if ("WOMEN".equals(productType)) {
//            WomenProduct wp = womenProductRepo.findById(productId)
//                    .orElseThrow(() -> new EntityNotFoundException("Women product not found"));
//
//            User vendor = wp.getSeller();
//            Shop shop = shopRepo.findById(vendor.getId()).orElse(null);
//
//            String firstImage = wp.getImageUrlList().isEmpty() ? null : wp.getImageUrlList().get(0);
//
//            return new WishlistProductDTO(
//                    entry.getId(),
//                    wp.getId(),
//                    wp.getName(),
//                    firstImage,
//                    shop != null ? shop.getShopName() : vendor.getBusinessName(),
//                    vendor.getBusinessName(),
//                    wp.getMinPrice(),
//                    wp.getMaxPrice(),
//                  
//                    wp.getCategory(),
//                    "WOMEN",
//                    wp.getStock() > 0,
//                    entry.getAddedAt(),
//                    timeAgo
//            );
//        }
//
//        throw new IllegalArgumentException("Invalid product type: " + productType);
//    }
//
//    private String getFirstImage(String imagePaths) {
//        if (imagePaths == null || imagePaths.isEmpty()) return null;
//        return imagePaths.split(",")[0].trim();
//    }
//
//    private String formatTimeAgo(LocalDateTime dateTime) {
//        LocalDateTime now = LocalDateTime.now();
//        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
//        long hours = ChronoUnit.HOURS.between(dateTime, now);
//        long days = ChronoUnit.DAYS.between(dateTime, now);
//
//        if (minutes < 60) {
//            return minutes + (minutes == 1 ? " min ago" : " mins ago");
//        }
//        if (hours < 24) {
//            return hours + (hours == 1 ? " hour ago" : " hours ago");
//        }
//        if (days < 7) {
//            return days + (days == 1 ? " day ago" : " days ago");
//        }
//        return "More than a week ago";
//    }
//
//    private String determineType(String categoryName) {
//        if (categoryName == null) return "WOMEN";
//        String lower = categoryName.toLowerCase();
//        if (lower.contains("vegetable")) return "VEGETABLE";
//        if (lower.contains("dairy")) return "DAIRY";
//        if (lower.contains("meat") || lower.contains("seafood")) return "MEAT";
//        return "WOMEN";
//    }
//
//    private BigDecimal getMinPriceFromDetails(Product p, String type) {
//        return switch (type) {
//            case "VEGETABLE" -> vegRepo.findByProductId(p.getId())
//                    .map(VegetableDetail::getMinPrice)
//                    .orElse(BigDecimal.ZERO);
//            case "DAIRY" -> dairyRepo.findByProductId(p.getId())
//                    .map(DairyDetail::getMinPrice)
//                    .orElse(BigDecimal.ZERO);
//            case "MEAT" -> meatRepo.findByProductId(p.getId())
//                    .map(MeatDetail::getMinPrice)
//                    .orElse(BigDecimal.ZERO);
//            default -> BigDecimal.ZERO;
//        };
//    }
//
//    private BigDecimal getMaxPriceFromDetails(Product p, String type) {
//        return switch (type) {
//            case "VEGETABLE" -> vegRepo.findByProductId(p.getId())
//                    .map(VegetableDetail::getMaxPrice)
//                    .orElse(BigDecimal.ZERO);
//            case "DAIRY" -> dairyRepo.findByProductId(p.getId())
//                    .map(DairyDetail::getMaxPrice)
//                    .orElse(BigDecimal.ZERO);
//            case "MEAT" -> meatRepo.findByProductId(p.getId())
//                    .map(MeatDetail::getMaxPrice)
//                    .orElse(BigDecimal.ZERO);
//            default -> BigDecimal.ZERO;
//        };
//    }
//}

package com.agrowmart.service.customer;

import com.agrowmart.dto.auth.customer.WishlistAddRequest;
import com.agrowmart.dto.auth.customer.WishlistProductDTO;
import com.agrowmart.entity.DairyDetail;
import com.agrowmart.entity.MeatDetail;
import com.agrowmart.entity.Product;
import com.agrowmart.entity.Shop;
import com.agrowmart.entity.User;
import com.agrowmart.entity.VegetableDetail;
import com.agrowmart.entity.WomenProduct;
import com.agrowmart.entity.customer.CustomerWishlist;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.*;
import com.agrowmart.repository.customer.CustomerWishlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class CustomerWishlistService {

    private static final Logger log = LoggerFactory.getLogger(CustomerWishlistService.class);

    private final CustomerWishlistRepository wishlistRepo;
    private final ProductRepository productRepo;
    private final WomenProductRepository womenProductRepo;
    private final VegetableDetailRepository vegRepo;
    private final DairyDetailRepository dairyRepo;
    private final MeatDetailRepository meatRepo;
    private final ShopRepository shopRepo;
    private final UserRepository userRepo;

    public CustomerWishlistService(
            CustomerWishlistRepository wishlistRepo,
            ProductRepository productRepo,
            WomenProductRepository womenProductRepo,
            VegetableDetailRepository vegRepo,
            DairyDetailRepository dairyRepo,
            MeatDetailRepository meatRepo,
            ShopRepository shopRepo,
            UserRepository userRepo) {
        this.wishlistRepo = wishlistRepo;
        this.productRepo = productRepo;
        this.womenProductRepo = womenProductRepo;
        this.vegRepo = vegRepo;
        this.dairyRepo = dairyRepo;
        this.meatRepo = meatRepo;
        this.shopRepo = shopRepo;
        this.userRepo = userRepo;
    }

    public WishlistProductDTO addToWishlist(Long customerId, WishlistAddRequest req) {
        if (req == null || req.productId() == null || req.productType() == null) {
            log.warn("Add to wishlist failed - invalid request for customer ID: {}", customerId);
            throw new BusinessValidationException("Product ID and product type are required");
        }

        String type = req.productType().trim().toUpperCase();
        log.info("Add to wishlist attempt - Customer ID: {}, Product ID: {}, Type: {}", customerId, req.productId(), type);

        if (wishlistRepo.existsByCustomerIdAndProductIdAndProductType(customerId, req.productId(), type)) {
            log.warn("Duplicate wishlist add attempt - Customer ID: {}, Product ID: {}, Type: {}", customerId, req.productId(), type);
            throw new BusinessValidationException("This product is already in your wishlist");
        }

        CustomerWishlist wishlist = new CustomerWishlist(customerId, req.productId(), type);
        wishlistRepo.save(wishlist);

        log.info("Product added to wishlist - Wishlist ID: {}, Customer ID: {}", wishlist.getId(), customerId);

        return buildWishlistProduct(customerId, req.productId(), type);
    }

    public void removeFromWishlist(Long customerId, Long productId, String productType) {
        if (productId == null || productType == null) {
            log.warn("Remove from wishlist failed - missing parameters - Customer ID: {}", customerId);
            throw new BusinessValidationException("Product ID and type are required to remove from wishlist");
        }

        String type = productType.trim().toUpperCase();
        log.info("Remove from wishlist - Customer ID: {}, Product ID: {}, Type: {}", customerId, productId, type);

        wishlistRepo.deleteByCustomerIdAndProductIdAndProductType(customerId, productId, type);
        log.debug("Product removed from wishlist successfully - Customer ID: {}, Product ID: {}", customerId, productId);
    }

    public List<WishlistProductDTO> getWishlist(Long customerId) {
        if (customerId == null) {
            log.warn("Get wishlist failed - null customer ID");
            throw new BusinessValidationException("Customer ID is required");
        }

        log.debug("Fetching wishlist for customer ID: {}", customerId);

        return wishlistRepo.findByCustomerIdOrderByAddedAtDesc(customerId)
                .stream()
                .map(w -> buildWishlistProduct(customerId, w.getProductId(), w.getProductType()))
                .toList();
    }

    public boolean isInWishlist(Long customerId, Long productId, String productType) {
        if (customerId == null || productId == null || productType == null) {
            return false;
        }

        String type = productType.trim().toUpperCase();
        boolean exists = wishlistRepo.existsByCustomerIdAndProductIdAndProductType(customerId, productId, type);
        log.debug("Wishlist check - Customer ID: {}, Product ID: {}, Type: {}, Exists: {}", customerId, productId, type, exists);
        return exists;
    }

    private WishlistProductDTO buildWishlistProduct(Long customerId, Long productId, String productType) {
        String type = productType.trim().toUpperCase();

        CustomerWishlist entry = wishlistRepo.findByCustomerIdAndProductIdAndProductType(customerId, productId, type)
                .orElseThrow(() -> {
                    log.warn("Wishlist entry not found - Customer ID: {}, Product ID: {}, Type: {}", customerId, productId, type);
                    return new ResourceNotFoundException("Wishlist entry not found for this product");
                });

        String timeAgo = formatTimeAgo(entry.getAddedAt());

        if ("REGULAR".equals(type)) {
            Product p = productRepo.findById(productId)
                    .orElseThrow(() -> {
                        log.warn("Regular product not found during wishlist build - Product ID: {}", productId);
                        return new ResourceNotFoundException("Product not found");
                    });

            Shop shop = p.getMerchantId() != null ? shopRepo.findById(p.getMerchantId()).orElse(null) : null;
            User vendor = p.getMerchantId() != null ? userRepo.findById(p.getMerchantId()).orElse(null) : null;

            String determinedType = determineType(p.getCategory() != null ? p.getCategory().getName() : null);
            BigDecimal minPrice = getMinPriceFromDetails(p, determinedType);
            BigDecimal maxPrice = getMaxPriceFromDetails(p, determinedType);

            return new WishlistProductDTO(
                    entry.getId(),
                    p.getId(),
                    p.getProductName(),
                    getFirstImage(p.getImagePaths()),
                    shop != null ? shop.getShopName() : "Unknown Shop",
                    vendor != null ? vendor.getBusinessName() : "Unknown Vendor",
                    minPrice,
                    maxPrice,
                    p.getCategory() != null ? p.getCategory().getName() : "Uncategorized",
                    determinedType,
                    p.getInStock(),
                    entry.getAddedAt(),
                    timeAgo
            );
        } else if ("WOMEN".equals(type)) {
            WomenProduct wp = womenProductRepo.findById(productId)
                    .orElseThrow(() -> {
                        log.warn("Women product not found during wishlist build - Product ID: {}", productId);
                        return new ResourceNotFoundException("Women product not found");
                    });

            User vendor = wp.getSeller();
            Shop shop = vendor != null ? shopRepo.findById(vendor.getId()).orElse(null) : null;

            String firstImage = wp.getImageUrlList() != null && !wp.getImageUrlList().isEmpty()
                    ? wp.getImageUrlList().get(0)
                    : null;

            return new WishlistProductDTO(
                    entry.getId(),
                    wp.getId(),
                    wp.getName(),
                    firstImage,
                    shop != null ? shop.getShopName() : (vendor != null ? vendor.getBusinessName() : "Unknown"),
                    vendor != null ? vendor.getBusinessName() : "Unknown Vendor",
                    wp.getMinPrice(),
                    wp.getMaxPrice(),
                    wp.getCategory(),
                    "WOMEN",
                    wp.getStock() > 0,
                    entry.getAddedAt(),
                    timeAgo
            );
        }

        log.warn("Invalid product type during wishlist build: {}", type);
        throw new BusinessValidationException("Invalid product type: " + type);
    }

    private String getFirstImage(String imagePaths) {
        if (imagePaths == null || imagePaths.isBlank()) {
            return null;
        }
        String[] paths = imagePaths.split(",");
        return paths.length > 0 ? paths[0].trim() : null;
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown time";

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (minutes < 60) {
            return minutes + (minutes == 1 ? " min ago" : " mins ago");
        }
        if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }
        if (days < 7) {
            return days + (days == 1 ? " day ago" : " days ago");
        }
        return "More than a week ago";
    }

    private String determineType(String categoryName) {
        if (categoryName == null) return "GENERAL";

        String lower = categoryName.toLowerCase();
        if (lower.contains("vegetable") || lower.contains("root")) return "VEGETABLE";
        if (lower.contains("dairy")) return "DAIRY";
        if (lower.contains("meat") || lower.contains("seafood")) return "MEAT";
        return "GENERAL";
    }

    private BigDecimal getMinPriceFromDetails(Product p, String type) {
        return switch (type) {
            case "VEGETABLE" -> vegRepo.findByProductId(p.getId())
                    .map(VegetableDetail::getMinPrice)
                    .orElse(BigDecimal.ZERO);
            case "DAIRY" -> dairyRepo.findByProductId(p.getId())
                    .map(DairyDetail::getMinPrice)
                    .orElse(BigDecimal.ZERO);
            case "MEAT" -> meatRepo.findByProductId(p.getId())
                    .map(MeatDetail::getMinPrice)
                    .orElse(BigDecimal.ZERO);
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal getMaxPriceFromDetails(Product p, String type) {
        return switch (type) {
            case "VEGETABLE" -> vegRepo.findByProductId(p.getId())
                    .map(VegetableDetail::getMaxPrice)
                    .orElse(BigDecimal.ZERO);
            case "DAIRY" -> dairyRepo.findByProductId(p.getId())
                    .map(DairyDetail::getMaxPrice)
                    .orElse(BigDecimal.ZERO);
            case "MEAT" -> meatRepo.findByProductId(p.getId())
                    .map(MeatDetail::getMaxPrice)
                    .orElse(BigDecimal.ZERO);
            default -> BigDecimal.ZERO;
        };
    }
}