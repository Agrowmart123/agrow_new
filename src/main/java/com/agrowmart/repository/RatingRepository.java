package com.agrowmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.awt.print.Pageable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agrowmart.entity.Rating.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

 List<Rating> findByRatedIdOrderByCreatedAtDesc(Long ratedId);

 Optional<Rating> findByIdAndRaterId(Long id, Long raterId);

 Optional<Rating> findByRaterIdAndRatedId(Long raterId, Long ratedId);

 @Query("SELECT COALESCE(AVG(r.stars), 0.0) FROM Rating r WHERE r.rated.id = :vendorId")
 Double findAverageRatingByVendorId(Long vendorId);

 @Query("SELECT COALESCE(COUNT(r), 0L) FROM Rating r WHERE r.rated.id = :vendorId")
 Long findTotalRatingsByVendorId(Long vendorId);

 @Query("SELECT r.stars, COUNT(r) FROM Rating r WHERE r.rated.id = :vendorId GROUP BY r.stars ORDER BY r.stars DESC")
 List<Object[]> findStarDistributionByVendorId(Long vendorId);
 
 
 @Query("""
		    SELECT r.rated, AVG(r.stars) as avgRating, COUNT(r) as reviewCount
		    FROM Rating r
		    GROUP BY r.rated
		    HAVING COUNT(r) >= 1
		    ORDER BY avgRating DESC, reviewCount DESC
		    """)
		List<Object[]> findTopRatedVendors();

		// Or with limit
		@Query("""
		    SELECT r.rated, AVG(r.stars), COUNT(r)
		    FROM Rating r
		    GROUP BY r.rated
		    HAVING COUNT(r) >= 3
		    ORDER BY AVG(r.stars) DESC, COUNT(r) DESC
		    """)
		List<Object[]> findTopRatedVendorsLimited(Pageable pageable);
}