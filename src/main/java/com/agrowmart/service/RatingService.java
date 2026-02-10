// src/main/java/com/agrowmart/service/RatingService.java

package com.agrowmart.service;

import com.agrowmart.dto.auth.rating.RatingCreateRequestDTO;
import com.agrowmart.dto.auth.rating.RatingResponseDTO;
import com.agrowmart.dto.auth.rating.VendorRatingSummaryDTO;
import com.agrowmart.entity.User;
import com.agrowmart.entity.Rating.Rating;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.exception.AuthExceptions.AuthenticationFailedException;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.ForbiddenException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.repository.RatingRepository;
import com.agrowmart.repository.UserRepository;
import com.agrowmart.repository.customer.CustomerRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@Transactional
public class RatingService {

	private static final Logger log = LoggerFactory.getLogger(ProductRatingService.class);
	
	
    private final RatingRepository ratingRepo;
    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;

    public RatingService(RatingRepository ratingRepo,
                         UserRepository userRepo,
                         CustomerRepository customerRepo) {
        this.ratingRepo = ratingRepo;
        this.userRepo = userRepo;
        this.customerRepo = customerRepo;
    }



    @Transactional
    public RatingResponseDTO createOrUpdateRating(Customer customer, RatingCreateRequestDTO req) {
    	if (customer == null) {
            throw new AuthenticationFailedException("Customer must be authenticated to rate a vendor");
        }
    	validateStars(req.stars());
    	if (req.vendorId() == null) {
            throw new BusinessValidationException("Vendor ID is required");
        }
        User vendor = userRepo.findById(req.vendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        String role = vendor.getRole().getName();
        boolean isVendor = role.matches("VEGETABLE|DAIRY|SEAFOODMEAT|WOMEN|FARMER|AGRI");
        if (!isVendor) {
        	throw new BusinessValidationException("You can only rate registered vendors");
        }

        Rating rating = ratingRepo.findByRaterIdAndRatedId(customer.getId(), vendor.getId())
                .orElse(new Rating());

        rating.setRater(customer);
        rating.setRated(vendor);
        rating.setStars(req.stars());
        rating.setFeedback(req.feedback() != null ? req.feedback().trim() : null);

        if (rating.getId() == null) {
            rating.setCreatedAt(new Date());
        }
        rating.setUpdatedAt(new Date());

        rating = ratingRepo.save(rating);
        log.info("Rating {} {} stars for vendor {} by customer {}", 
                rating.getId(), rating.getStars(), vendor.getId(), customer.getId());
        return mapToResponse(rating);
    }

    @Transactional
    public void deleteRating(Customer customer, Long ratingId) {
    	if (customer == null) {
            throw new AuthenticationFailedException("Customer must be authenticated to delete rating");
        }
    	Rating rating = ratingRepo.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with ID: " + ratingId));

        if (!rating.getRater().getId().equals(customer.getId())) {
            throw new ForbiddenException("You can only delete your own rating");
        }
        ratingRepo.delete(rating);
        log.info("Rating {} deleted by customer {}", ratingId, customer.getId());
    }

    public VendorRatingSummaryDTO getVendorRatingSummary(Long vendorId) {
    	if (vendorId == null) {
            throw new BusinessValidationException("Vendor ID is required");
        }
    	User vendor = userRepo.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));
    	
        Double avg = ratingRepo.findAverageRatingByVendorId(vendorId);
        Long total = ratingRepo.findTotalRatingsByVendorId(vendorId);
        List<Rating> reviews = ratingRepo.findByRatedIdOrderByCreatedAtDesc(vendorId);

        Map<Integer, Long> starCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) starCounts.put(i, 0L);

        List<Object[]> distribution = ratingRepo.findStarDistributionByVendorId(vendorId);
        for (Object[] row : distribution) {
            int stars = (Integer) row[0];
            long count = (Long) row[1];
            starCounts.put(stars, count);
        }

        List<RatingResponseDTO> reviewDTOs = reviews.stream()
                .map(this::mapToResponse)
                .toList();

        double average = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
        long totalRatings = total != null ? total : 0;

        return new VendorRatingSummaryDTO(average, totalRatings, starCounts, reviewDTOs);
    }

    private RatingResponseDTO mapToResponse(Rating r) {
        String customerName = r.getRater() != null ? r.getRater().getFullName() : "Anonymous";
        return new RatingResponseDTO(
                r.getId(),
                r.getStars(),
                r.getFeedback(),
                customerName,
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
    
    public List<VendorRatingSummaryDTO> getTopRatedVendors(int limit) {
        Pageable pageable = (Pageable) PageRequest.of(0, limit);
        return ratingRepo.findTopRatedVendorsLimited(pageable)
            .stream()
            .map(row -> {
                Long vendorId = (Long) row[0];
                return getVendorRatingSummary(vendorId);
            })
            .toList();
    }// ──────────────────────────────────────────────
    // VALIDATION & MAPPING
    // ──────────────────────────────────────────────
    private void validateStars(Integer stars) {
        if (stars == null || stars < 1 || stars > 5) {
            throw new BusinessValidationException("Stars must be between 1 and 5");
        }
    }
    
}