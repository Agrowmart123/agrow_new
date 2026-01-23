//package com.agrowmart.service;
//
//
//
//import com.agrowmart.dto.auth.rating.ProductRatingCreateRequestDTO;
//import com.agrowmart.dto.auth.rating.ProductRatingResponseDTO;
//import com.agrowmart.dto.auth.rating.ProductRatingSummaryDTO;
//import com.agrowmart.entity.Product;
//import com.agrowmart.entity.Rating.ProductRating;
//import com.agrowmart.entity.customer.Customer;
//import com.agrowmart.exception.ForbiddenException;
//import com.agrowmart.exception.ResourceNotFoundException;
//import com.agrowmart.repository.ProductRatingRepository;
//import com.agrowmart.repository.ProductRepository;
//import com.agrowmart.repository.OrderRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//
//@Service
//@Transactional
//public class ProductRatingService {
//
//    private final ProductRatingRepository ratingRepo;
//    private final ProductRepository productRepo;
//    private final OrderRepository orderRepo;
//
//    public ProductRatingService(ProductRatingRepository ratingRepo,
//                                ProductRepository productRepo,
//                                OrderRepository orderRepo) {
//        this.ratingRepo = ratingRepo;
//        this.productRepo = productRepo;
//        this.orderRepo = orderRepo;
//    }
//
//    // CREATE OR UPDATE PRODUCT RATING
//    public ProductRatingResponseDTO createOrUpdateProductRating(
//            Customer customer,
//            ProductRatingCreateRequestDTO req) {
//
//        validateStars(req.stars());
//
//        Product product = productRepo.findById(req.productId())
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
//
//        // Optional but recommended: rate only purchased products
//        boolean purchased = orderRepo
//                .existsByCustomerIdAndProductId(customer.getId(), product.getId());
//
//        if (!purchased) {
//            throw new ForbiddenException("You can rate only purchased products");
//        }
//
//        ProductRating rating = ratingRepo
//                .findByCustomerIdAndProductId(customer.getId(), product.getId())
//                .orElse(new ProductRating());
//
//        rating.setCustomer(customer);
//        rating.setProduct(product);
//        rating.setStars(req.stars());
//        rating.setFeedback(req.feedback());
//        rating.setUpdatedAt(new Date());
//
//        if (rating.getId() == null) {
//            rating.setCreatedAt(new Date());
//        }
//
//        ratingRepo.save(rating);
//
//        return mapToResponse(rating);
//    }
//
//    // DELETE OWN RATING
//    public void deleteRating(Customer customer, Long ratingId) {
//
//        ProductRating rating = ratingRepo.findById(ratingId)
//                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));
//
//        if (!rating.getCustomer().getId().equals(customer.getId())) {
//            throw new ForbiddenException("You can delete only your own rating");
//        }
//
//        ratingRepo.delete(rating);
//    }
//
//    // GET PRODUCT RATING SUMMARY
//    @Transactional(readOnly = true)
//    public ProductRatingSummaryDTO getProductRatingSummary(Long productId) {
//
//        productRepo.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
//
//        Double avg = ratingRepo.findAverageRating(productId);
//        List<ProductRating> ratings =
//                ratingRepo.findByProductIdOrderByCreatedAtDesc(productId);
//
//        Map<Integer, Long> starCounts = new HashMap<>();
//        for (int i = 1; i <= 5; i++) starCounts.put(i, 0L);
//
//        List<Object[]> distribution = ratingRepo.starDistribution(productId);
//        for (Object[] row : distribution) {
//            starCounts.put((Integer) row[0], (Long) row[1]);
//        }
//
//        List<ProductRatingResponseDTO> reviews = ratings.stream()
//                .map(this::mapToResponse)
//                .toList();
//
//        double average = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
//
//        return new ProductRatingSummaryDTO(
//                average,
//                ratings.size(),
//                starCounts,
//                reviews
//        );
//    }
//
//    // HELPERS
//    private void validateStars(Integer stars) {
//        if (stars == null || stars < 1 || stars > 5) {
//            throw new IllegalArgumentException("Stars must be between 1 and 5");
//        }
//    }
//
//    private ProductRatingResponseDTO mapToResponse(ProductRating rating) {
//        return new ProductRatingResponseDTO(
//                rating.getId(),
//                rating.getStars(),
//                rating.getFeedback(),
//                rating.getCustomer().getFullName(),
//                rating.getCreatedAt()
//        );
//    }
//}

//
//package com.agrowmart.service;
//
//import com.agrowmart.dto.auth.rating.ProductRatingCreateRequestDTO;
//import com.agrowmart.dto.auth.rating.ProductRatingResponseDTO;
//import com.agrowmart.dto.auth.rating.ProductRatingSummaryDTO;
//import com.agrowmart.entity.Product;
//import com.agrowmart.entity.WomenProduct;
//import com.agrowmart.entity.Rating.ProductRating;
//import com.agrowmart.entity.customer.Customer;
//import com.agrowmart.exception.ForbiddenException;
//import com.agrowmart.exception.ResourceNotFoundException;
//import com.agrowmart.repository.OrderRepository;
//import com.agrowmart.repository.ProductRatingRepository;
//import com.agrowmart.repository.ProductRepository;
//import com.agrowmart.repository.WomenProductRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class ProductRatingService {
//
//    private final ProductRatingRepository ratingRepo;
//    private final ProductRepository productRepo;
//    private final WomenProductRepository womenProductRepo;
//    private final OrderRepository orderRepo;
//
//    public ProductRatingService(
//            ProductRatingRepository ratingRepo,
//            ProductRepository productRepo,
//            WomenProductRepository womenProductRepo,
//            OrderRepository orderRepo) {
//        this.ratingRepo = ratingRepo;
//        this.productRepo = productRepo;
//        this.womenProductRepo = womenProductRepo;
//        this.orderRepo = orderRepo;
//    }
//
//    /**
//     * Create or update rating - works for BOTH normal products and women products
//     */
//    public ProductRatingResponseDTO createOrUpdateProductRating(
//            Customer customer,
//            ProductRatingCreateRequestDTO req) {
//
//        validateStars(req.stars());
//
//        Long productId = req.productId();
//
//        // 1. Try to find as normal product
//        Product normalProduct = productRepo.findById(productId).orElse(null);
//        WomenProduct womenProduct = null;
//        String productType = null;
//
//        if (normalProduct != null) {
//            productType = "NORMAL";
//        } else {
//            // 2. Try as women product
//            womenProduct = womenProductRepo.findById(productId).orElse(null);
//            if (womenProduct == null) {
//                throw new ResourceNotFoundException("Product not found with ID: " + productId);
//            }
//            productType = "WOMEN";
//        }
//
//        // 3. Check if customer actually purchased this product
//        boolean hasPurchased;
//        if ("NORMAL".equals(productType)) {
//            hasPurchased = orderRepo.existsByCustomerIdAndProductId(customer.getId(), productId);
//        } else {
//            hasPurchased = orderRepo.existsByCustomerIdAndWomenProductId(customer.getId(), productId);
//        }
//
//        if (!hasPurchased) {
//            throw new ForbiddenException("You can only rate products you have purchased");
//        }
//
//        // 4. Find existing rating or create new
//        ProductRating rating = ratingRepo.findByCustomerIdAndProductOrWomenProduct(
//                customer.getId(),
//                normalProduct != null ? normalProduct.getId() : null,
//                womenProduct != null ? womenProduct.getId() : null
//        ).orElse(new ProductRating());
//
//        // 5. Set values
//        rating.setCustomer(customer);
//        rating.setProduct(normalProduct);
//        rating.setWomenProduct(womenProduct);
//        rating.setStars(req.stars());
//        rating.setFeedback(req.feedback());
//        rating.setUpdatedAt(new Date());
//
//        if (rating.getId() == null) {
//            rating.setCreatedAt(new Date());
//        }
//
//        ratingRepo.save(rating);
//
//        return mapToResponse(rating);
//    }
//
//    /**
//     * Get rating summary - works for BOTH types
//     */
//    @Transactional(readOnly = true)
//    public ProductRatingSummaryDTO getProductRatingSummary(Long productId) {
//        // Check if it's a normal product
//        Product normalProduct = productRepo.findById(productId).orElse(null);
//        if (normalProduct != null) {
//            return buildSummary(productId, true);
//        }
//
//        // Check if it's a women product
//        WomenProduct womenProduct = womenProductRepo.findById(productId).orElse(null);
//        if (womenProduct != null) {
//            return buildSummary(productId, false);
//        }
//
//        throw new ResourceNotFoundException("Product not found with ID: " + productId);
//    }
//
//    private ProductRatingSummaryDTO buildSummary(Long productId, boolean isNormalProduct) {
//        Double avg;
//        List<ProductRating> ratings;
//
//        if (isNormalProduct) {
//            avg = ratingRepo.findAverageRatingByProductId(productId);
//            ratings = ratingRepo.findByProductIdOrderByCreatedAtDesc(productId);
//        } else {
//            avg = ratingRepo.findAverageRatingByWomenProductId(productId);
//            ratings = ratingRepo.findByWomenProductIdOrderByCreatedAtDesc(productId);
//        }
//
//        Map<Integer, Long> starCounts = new HashMap<>();
//        for (int i = 1; i <= 5; i++) starCounts.put(i, 0L);
//
//        List<Object[]> distribution = isNormalProduct
//                ? ratingRepo.starDistributionByProductId(productId)
//                : ratingRepo.starDistributionByWomenProductId(productId);
//
//        for (Object[] row : distribution) {
//            starCounts.put((Integer) row[0], (Long) row[1]);
//        }
//
//        List<ProductRatingResponseDTO> reviews = ratings.stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//
//        double average = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
//
//        return new ProductRatingSummaryDTO(
//                average,
//                ratings.size(),
//                starCounts,
//                reviews
//        );
//    }
//
//    public void deleteRating(Customer customer, Long ratingId) {
//        ProductRating rating = ratingRepo.findById(ratingId)
//                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));
//
//        if (!rating.getCustomer().getId().equals(customer.getId())) {
//            throw new ForbiddenException("You can only delete your own rating");
//        }
//
//        ratingRepo.delete(rating);
//    }
//
//    private void validateStars(Integer stars) {
//        if (stars == null || stars < 1 || stars > 5) {
//            throw new IllegalArgumentException("Stars must be between 1 and 5");
//        }
//    }
//
//    private ProductRatingResponseDTO mapToResponse(ProductRating rating) {
//        return new ProductRatingResponseDTO(
//                rating.getId(),
//                rating.getStars(),
//                rating.getFeedback(),
//                rating.getCustomer().getFullName(),
//                rating.getCreatedAt()
//        );
//    }
//}

package com.agrowmart.service;

import com.agrowmart.dto.auth.rating.ProductRatingCreateRequestDTO;
import com.agrowmart.dto.auth.rating.ProductRatingResponseDTO;
import com.agrowmart.dto.auth.rating.ProductRatingSummaryDTO;
import com.agrowmart.entity.Product;
import com.agrowmart.entity.WomenProduct;
import com.agrowmart.entity.Rating.ProductRating;
import com.agrowmart.entity.customer.Customer;
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

@Service
@Transactional
public class ProductRatingService {

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

        validateStars(req.stars());

        Long productId = req.productId();

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

        return mapToResponse(rating);
    }

    /**
     * Get rating summary - works perfectly for BOTH types
     */
    @Transactional(readOnly = true)
    public ProductRatingSummaryDTO getProductRatingSummary(Long productId) {
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
        ProductRating rating = ratingRepo.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        if (!rating.getCustomer().getId().equals(customer.getId())) {
            throw new ForbiddenException("You can only delete your own rating");
        }

        ratingRepo.delete(rating);
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
