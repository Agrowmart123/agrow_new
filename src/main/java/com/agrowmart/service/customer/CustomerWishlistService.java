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
import com.agrowmart.repository.*;
import com.agrowmart.repository.customer.CustomerWishlistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class CustomerWishlistService {

    private final CustomerWishlistRepository wishlistRepo;
    private final ProductRepository productRepo;
    private final WomenProductRepository womenProductRepo;
    private final VegetableDetailRepository vegRepo;      // Added missing
    private final DairyDetailRepository dairyRepo;        // Added missing
    private final MeatDetailRepository meatRepo;          // Added missing
    private final ShopRepository shopRepo;
    private final UserRepository userRepo;

    public CustomerWishlistService(CustomerWishlistRepository wishlistRepo,
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
        String type = req.productType().toUpperCase();
        if (wishlistRepo.existsByCustomerIdAndProductIdAndProductType(customerId, req.productId(), type)) {
            throw new RuntimeException("Product already in wishlist");
        }
        CustomerWishlist wishlist = new CustomerWishlist(customerId, req.productId(), type);
        wishlistRepo.save(wishlist);
        return buildWishlistProduct(customerId, req.productId(), type);
    }

    public void removeFromWishlist(Long customerId, Long productId, String productType) {
        wishlistRepo.deleteByCustomerIdAndProductIdAndProductType(customerId, productId, productType.toUpperCase());
    }

    public List<WishlistProductDTO> getWishlist(Long customerId) {
        return wishlistRepo.findByCustomerIdOrderByAddedAtDesc(customerId)
                .stream()
                .map(w -> buildWishlistProduct(customerId, w.getProductId(), w.getProductType()))
                .toList();
    }

    public boolean isInWishlist(Long customerId, Long productId, String productType) {
        return wishlistRepo.existsByCustomerIdAndProductIdAndProductType(customerId, productId, productType.toUpperCase());
    }

    private WishlistProductDTO buildWishlistProduct(Long customerId, Long productId, String productType) {
        CustomerWishlist entry = wishlistRepo.findByCustomerIdAndProductIdAndProductType(customerId, productId, productType)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist entry not found"));

        String timeAgo = formatTimeAgo(entry.getAddedAt());

        if ("REGULAR".equals(productType)) {
            Product p = productRepo.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            Shop shop = shopRepo.findById(p.getMerchantId()).orElse(null);
            User vendor = userRepo.findById(p.getMerchantId()).orElse(null);

            String determinedType = determineType(p.getCategory().getName());

            BigDecimal minPrice = getMinPriceFromDetails(p, determinedType);
            BigDecimal maxPrice = getMaxPriceFromDetails(p, determinedType);

            return new WishlistProductDTO(
                    entry.getId(),
                    p.getId(),
                    p.getProductName(),
                    getFirstImage(p.getImagePaths()),
                    shop != null ? shop.getShopName() : "Unknown",
                    vendor != null ? vendor.getBusinessName() : "Unknown",
                    minPrice,
                    maxPrice,
                 
                    p.getCategory().getName(),
                    determinedType,
                    p.getInStock(),
                    entry.getAddedAt(),
                    timeAgo
            );
        } else if ("WOMEN".equals(productType)) {
            WomenProduct wp = womenProductRepo.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Women product not found"));

            User vendor = wp.getSeller();
            Shop shop = shopRepo.findById(vendor.getId()).orElse(null);

            String firstImage = wp.getImageUrlList().isEmpty() ? null : wp.getImageUrlList().get(0);

            return new WishlistProductDTO(
                    entry.getId(),
                    wp.getId(),
                    wp.getName(),
                    firstImage,
                    shop != null ? shop.getShopName() : vendor.getBusinessName(),
                    vendor.getBusinessName(),
                    wp.getMinPrice(),
                    wp.getMaxPrice(),
                  
                    wp.getCategory(),
                    "WOMEN",
                    wp.getStock() > 0,
                    entry.getAddedAt(),
                    timeAgo
            );
        }

        throw new IllegalArgumentException("Invalid product type: " + productType);
    }

    private String getFirstImage(String imagePaths) {
        if (imagePaths == null || imagePaths.isEmpty()) return null;
        return imagePaths.split(",")[0].trim();
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
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
        if (categoryName == null) return "WOMEN";
        String lower = categoryName.toLowerCase();
        if (lower.contains("vegetable")) return "VEGETABLE";
        if (lower.contains("dairy")) return "DAIRY";
        if (lower.contains("meat") || lower.contains("seafood")) return "MEAT";
        return "WOMEN";
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