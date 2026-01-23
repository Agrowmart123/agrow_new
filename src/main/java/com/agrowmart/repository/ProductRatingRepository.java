//package com.agrowmart.repository;
//
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import com.agrowmart.entity.Rating.ProductRating;
//
////public interface ProductRatingRepository
////extends JpaRepository<ProductRating, Long> {
////
////Optional<ProductRating> findByCustomerIdAndProductId(
////    Long customerId, Long productId);
////
////List<ProductRating> findByProductIdOrderByCreatedAtDesc(Long productId);
////
////@Query("SELECT AVG(r.stars) FROM ProductRating r WHERE r.product.id = :productId")
////Double findAverageRating(Long productId);
////
////@Query("""
////SELECT r.stars, COUNT(r)
////FROM ProductRating r
////WHERE r.product.id = :productId
////GROUP BY r.stars
////""")
////List<Object[]> starDistribution(Long productId);
////}
//
//
//public interface ProductRatingRepository extends JpaRepository<ProductRating, Long> {
//
//    // Find by customer + product (normal or women)
//    @Query("SELECT r FROM ProductRating r WHERE r.customer.id = :customerId " +
//           "AND (r.product.id = :productId OR r.womenProduct.id = :womenProductId)")
//    Optional<ProductRating> findByCustomerIdAndProductOrWomenProduct(
//            @Param("customerId") Long customerId,
//            @Param("productId") Long productId,
//            @Param("womenProductId") Long womenProductId);
//
//    // Normal product queries
//    Optional<ProductRating> findByCustomerIdAndProductId(Long customerId, Long productId);
//    List<ProductRating> findByProductIdOrderByCreatedAtDesc(Long productId);
//    @Query("SELECT AVG(r.stars) FROM ProductRating r WHERE r.product.id = :productId")
//    Double findAverageRatingByProductId(@Param("productId") Long productId);
//    @Query("SELECT r.stars, COUNT(r) FROM ProductRating r WHERE r.product.id = :productId GROUP BY r.stars")
//    List<Object[]> starDistributionByProductId(@Param("productId") Long productId);
//
//    // Women product queries
//    Optional<ProductRating> findByCustomerIdAndWomenProductId(Long customerId, Long womenProductId);
//    List<ProductRating> findByWomenProductIdOrderByCreatedAtDesc(Long womenProductId);
//    @Query("SELECT AVG(r.stars) FROM ProductRating r WHERE r.womenProduct.id = :womenProductId")
//    Double findAverageRatingByWomenProductId(@Param("womenProductId") Long womenProductId);
//    @Query("SELECT r.stars, COUNT(r) FROM ProductRating r WHERE r.womenProduct.id = :womenProductId GROUP BY r.stars")
//    List<Object[]> starDistributionByWomenProductId(@Param("womenProductId") Long womenProductId);
//}
//


package com.agrowmart.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.agrowmart.entity.Rating.ProductRating;

public interface ProductRatingRepository extends JpaRepository<ProductRating, Long> {

    // Unified lookup by customer + product (normal OR women)
    @Query("SELECT r FROM ProductRating r " +
           "WHERE r.customer.id = :customerId " +
           "AND (r.product.id = :productId OR r.womenProduct.id = :womenProductId)")
    Optional<ProductRating> findByCustomerIdAndProductOrWomenProduct(
            @Param("customerId") Long customerId,
            @Param("productId") Long productId,
            @Param("womenProductId") Long womenProductId);

    // ──────────────────────────────────────────────
    // NORMAL PRODUCT QUERIES
    // ──────────────────────────────────────────────
    Optional<ProductRating> findByCustomerIdAndProductId(Long customerId, Long productId);

    List<ProductRating> findByProductIdOrderByCreatedAtDesc(Long productId);

    @Query("SELECT AVG(r.stars) FROM ProductRating r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT r.stars, COUNT(r) FROM ProductRating r WHERE r.product.id = :productId GROUP BY r.stars")
    List<Object[]> starDistributionByProductId(@Param("productId") Long productId);

    // Total count for normal product (added - efficient)
    long countByProductId(Long productId);

    // ──────────────────────────────────────────────
    // WOMEN PRODUCT QUERIES
    // ──────────────────────────────────────────────
    Optional<ProductRating> findByCustomerIdAndWomenProductId(Long customerId, Long womenProductId);

    List<ProductRating> findByWomenProductIdOrderByCreatedAtDesc(Long womenProductId);

    @Query("SELECT AVG(r.stars) FROM ProductRating r WHERE r.womenProduct.id = :womenProductId")
    Double findAverageRatingByWomenProductId(@Param("womenProductId") Long womenProductId);

    @Query("SELECT r.stars, COUNT(r) FROM ProductRating r WHERE r.womenProduct.id = :womenProductId GROUP BY r.stars")
    List<Object[]> starDistributionByWomenProductId(@Param("womenProductId") Long womenProductId);

    // Total count for women product (added - efficient)
    long countByWomenProductId(Long womenProductId);
}