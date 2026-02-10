
package com.agrowmart.service;

import com.agrowmart.dto.auth.rating.ProductRatingCreateRequestDTO;
import com.agrowmart.dto.auth.rating.ProductRatingResponseDTO;
import com.agrowmart.dto.auth.rating.ProductRatingSummaryDTO;
import com.agrowmart.entity.Product;
import com.agrowmart.entity.WomenProduct;
import com.agrowmart.entity.Rating.ProductRating;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.exception.AuthExceptions.AuthenticationFailedException;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.ForbiddenException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.OrderRepository;
import com.agrowmart.repository.ProductRatingRepository;
import com.agrowmart.repository.ProductRepository;
import com.agrowmart.repository.WomenProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@Transactional
public class ProductRatingService {

	private static final Logger log = LoggerFactory.getLogger(ProductRatingService.class);
	
    private final ProductRatingRepository ratingRepo;
    private final ProductRepository productRepo;
    private final WomenProductRepository womenProductRepo;
    private final OrderRepository orderRepo;

    public ProductRatingService(
            ProductRatingRepository ratingRepo,
            ProductRepository productRepo,
            WomenProductRepository womenProductRepo,
            OrderRepository orderRepo) {
        this.ratingRepo = ratingRepo;
        this.productRepo = productRepo;
        this.womenProductRepo = womenProductRepo;
        this.orderRepo = orderRepo;
    }

    /**
     * Create or update rating - fully supports BOTH normal and women products
     */
    public ProductRatingResponseDTO createOrUpdateProductRating(
            Customer customer,
            ProductRatingCreateRequestDTO req) {
    	if (customer == null) {
            throw new AuthenticationFailedException("Customer must be authenticated to rate a product");
        }

        validateStars(req.stars());

        Long productId = req.productId();
        if (productId == null) {
            throw new BusinessValidationException("Product ID is required");
        }
        
        // 1. Try to find as normal product
        Product normalProduct = productRepo.findById(productId).orElse(null);
        WomenProduct womenProduct = null;
        String productType = null;

        if (normalProduct != null) {
            productType = "NORMAL";
        } else {
            // 2. Try as women product
            womenProduct = womenProductRepo.findById(productId).orElse(null);
            if (womenProduct == null) {
                throw new ResourceNotFoundException("Product not found with ID: " + productId);
            }
            productType = "WOMEN";
        }

        // 3. Check if customer actually purchased this product
        boolean hasPurchased;
        if ("NORMAL".equals(productType)) {
            hasPurchased = orderRepo.existsByCustomerIdAndProductId(customer.getId(), productId);
        } else {
            hasPurchased = orderRepo.existsByCustomerIdAndWomenProductId(customer.getId(), productId);
        }

        if (!hasPurchased) {
            throw new ForbiddenException("You can only rate products you have purchased");
        }

        // 4. Find existing rating or create new
        ProductRating rating = ratingRepo.findByCustomerIdAndProductOrWomenProduct(
                customer.getId(),
                normalProduct != null ? normalProduct.getId() : null,
                womenProduct != null ? womenProduct.getId() : null
        ).orElse(new ProductRating());

        // 5. Set values properly
        rating.setCustomer(customer);
        rating.setProduct(normalProduct);         // null for women → now allowed
        rating.setWomenProduct(womenProduct);     // null for normal → now allowed
        rating.setStars(req.stars());
        rating.setFeedback(req.feedback());
        rating.setUpdatedAt(new Date());

        if (rating.getId() == null) {
            rating.setCreatedAt(new Date());
        }

        // 6. Save - now works because columns are nullable
        ratingRepo.save(rating);
        log.info("Rating {} for product ID: {} by customer ID: {}", 
                rating.getId(), productId, customer.getId());
        return mapToResponse(rating);
    }

    /**
     * Get rating summary - works perfectly for BOTH types
     */
    @Transactional(readOnly = true)
    public ProductRatingSummaryDTO getProductRatingSummary(Long productId) {
    	if (productId == null) {
            throw new BusinessValidationException("Product ID is required");
        }
    	
        Product normalProduct = productRepo.findById(productId).orElse(null);
        if (normalProduct != null) {
            return buildSummary(productId, true);
        }

        WomenProduct womenProduct = womenProductRepo.findById(productId).orElse(null);
        if (womenProduct != null) {
            return buildSummary(productId, false);
        }

        throw new ResourceNotFoundException("Product not found with ID: " + productId);
    }

    private ProductRatingSummaryDTO buildSummary(Long productId, boolean isNormalProduct) {
        Double avg;
        List<ProductRating> ratings;
        long totalReviews;

        if (isNormalProduct) {
            avg = ratingRepo.findAverageRatingByProductId(productId);
            ratings = ratingRepo.findByProductIdOrderByCreatedAtDesc(productId);
            totalReviews = ratingRepo.countByProductId(productId);
        } else {
            avg = ratingRepo.findAverageRatingByWomenProductId(productId);
            ratings = ratingRepo.findByWomenProductIdOrderByCreatedAtDesc(productId);
            totalReviews = ratingRepo.countByWomenProductId(productId);
        }

        Map<Integer, Long> starCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) starCounts.put(i, 0L);

        List<Object[]> distribution = isNormalProduct
                ? ratingRepo.starDistributionByProductId(productId)
                : ratingRepo.starDistributionByWomenProductId(productId);

        for (Object[] row : distribution) {
            Integer star = (Integer) row[0];
            Long count = (Long) row[1];
            starCounts.put(star, count);
        }

        List<ProductRatingResponseDTO> reviews = ratings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        double average = (avg != null) ? Math.round(avg * 10.0) / 10.0 : 0.0;

        return new ProductRatingSummaryDTO(
                average,
                totalReviews,           // Now using correct count
                starCounts,
                reviews
        );
    }

    public void deleteRating(Customer customer, Long ratingId) {
    	if (customer == null) {
            throw new AuthenticationFailedException("Customer must be authenticated to delete rating");
        }
        ProductRating rating = ratingRepo.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        if (!rating.getCustomer().getId().equals(customer.getId())) {
            throw new ForbiddenException("You can only delete your own rating");
        }

        ratingRepo.delete(rating);
        log.info("Rating {} deleted by customer ID: {}", ratingId, customer.getId());
    }

    private void validateStars(Integer stars) {
        if (stars == null || stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }
    }

    private ProductRatingResponseDTO mapToResponse(ProductRating rating) {
        return new ProductRatingResponseDTO(
                rating.getId(),
                rating.getStars(),
                rating.getFeedback(),
                rating.getCustomer().getFullName(),
                rating.getCreatedAt()
        );
    }
}
