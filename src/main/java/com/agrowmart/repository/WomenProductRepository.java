package com.agrowmart.repository;

import com.agrowmart.dto.auth.women.WomenProductResponseDTO;
import com.agrowmart.entity.ApprovalStatus;
import com.agrowmart.entity.Product.ProductStatus;
import com.agrowmart.entity.WomenProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WomenProductRepository extends JpaRepository<WomenProduct, Long> {

    List<WomenProduct> findBySellerId(Long sellerId);
    List<WomenProduct> findByCategory(String category);
    List<WomenProduct> findByIsAvailableTrue();

    // Added by Ankita
    List<WomenProduct> findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus status);
//    List<WomenProductResponseDTO> findByStatus(ProductStatus status);

    // Only APPROVED products (public visible)
    List<WomenProduct> findByApprovalStatus(ApprovalStatus approvalStatus);

    // APPROVED + Available (active products)
    List<WomenProduct> findByApprovalStatusAndIsAvailableTrue(ApprovalStatus approvalStatus);

    Page<WomenProduct> findBySellerId(Long sellerId, Pageable pageable);

    List<WomenProduct> findAllByOrderByCreatedAtDesc();

    @Query("SELECT wp FROM WomenProduct wp JOIN wp.seller u " +
           "WHERE u.onlineStatus = 'ONLINE' AND u.profileCompleted = 'YES'")
    List<WomenProduct> findAllFromOnlineSellers();

    @Query("""
        SELECT w FROM WomenProduct w
        JOIN User u ON w.seller = u
        JOIN Shop s ON s.user = u
        WHERE w.id = :id
          AND w.approvalStatus = 'APPROVED'
          AND w.isAvailable = true
          AND s.isApproved = true
    """)
    Optional<WomenProduct> findApprovedWomenProductForOrder(@Param("id") Long id);

    // ──── THIS IS THE ONLY SAFE VERSION ────
    Optional<WomenProduct> findByIdAndSeller_IdAndStatusAndApprovalStatus(
            Long id,
            Long sellerId,
            ProductStatus status,
            ApprovalStatus approvalStatus
    );
    @Query("""
            SELECT wp FROM WomenProduct wp
            LEFT JOIN FETCH wp.seller s
            LEFT JOIN FETCH s.shop sh
            WHERE wp.approvalStatus = :status
            ORDER BY wp.createdAt DESC
        """)
        List<WomenProduct> findAllByApprovalStatusWithSellerAndShop(@Param("status") ApprovalStatus status);

        // Your existing method for single product
        @Query("""
            SELECT wp FROM WomenProduct wp
            JOIN FETCH wp.seller s
            JOIN FETCH s.shop sh
            WHERE wp.id = :id
        """)
        Optional<WomenProduct> findByIdWithSellerAndShop(@Param("id") Long id);


        @Query("""
        	    SELECT wp FROM WomenProduct wp
        	    LEFT JOIN FETCH wp.seller s
        	    LEFT JOIN FETCH s.shop sh
        	    ORDER BY wp.createdAt DESC
        	""")
        	List<WomenProduct> findAllWithSellerAndShopOrderByCreatedAtDesc();

    // ──── NO MORE DeletedFalse method below this line ────
}