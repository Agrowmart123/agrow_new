// src/main/java/com/agrowmart/service/RatingService.java

package com.agrowmart.service;

import com.agrowmart.dto.auth.rating.RatingCreateRequestDTO;
import com.agrowmart.dto.auth.rating.RatingResponseDTO;
import com.agrowmart.dto.auth.rating.VendorRatingSummaryDTO;
import com.agrowmart.entity.User;
import com.agrowmart.entity.Rating.Rating;
import com.agrowmart.entity.customer.Customer;
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

@Service
@Transactional
public class RatingService {

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

    private void validateStars(Integer stars) {
        if (stars == null || stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }
    }

    @Transactional
    public RatingResponseDTO createOrUpdateRating(Customer customer, RatingCreateRequestDTO req) {
        validateStars(req.stars());

        User vendor = userRepo.findById(req.vendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        String role = vendor.getRole().getName();
        boolean isVendor = role.matches("VEGETABLE|DAIRY|SEAFOODMEAT|WOMEN|FARMER|AGRI");
        if (!isVendor) {
            throw new ForbiddenException("You can only rate vendors");
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
        return mapToResponse(rating);
    }

    @Transactional
    public void deleteRating(Customer customer, Long ratingId) {
        Rating rating = ratingRepo.findByIdAndRaterId(ratingId, customer.getId())
                .orElseThrow(() -> new ForbiddenException("You can only delete your own rating"));
        ratingRepo.delete(rating);
    }

    public VendorRatingSummaryDTO getVendorRatingSummary(Long vendorId) {
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
    }
    
}