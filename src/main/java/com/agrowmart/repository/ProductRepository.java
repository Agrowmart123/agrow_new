//package com.agrowmart.repository;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort.Order;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.data.util.Streamable;
//
//import com.agrowmart.entity.ApprovalStatus;
//import com.agrowmart.entity.Category;
//import com.agrowmart.entity.Product;
//import com.agrowmart.entity.Product.ProductStatus;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface ProductRepository extends JpaRepository<Product, Long> {
//    List<Product> findByMerchantId(Long merchantId);
//    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
//    List<Product> findByStatus(Product.ProductStatus status);
//  //Deepti Kadam
//    List<Product> findAllByOrderByCreatedAtDesc();
//    
//    //Added by Ankita
//    List<Product> findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus status);
//    
//    
//    // ================= ADD FOR SERIAL NO (POINT 6) =================
//
//    // 🔹 Find max serial number for a merchant
//    @Query("select max(p.serialNo) from Product p where p.merchantId = :merchantId")
//    Long findMaxSerialNoByMerchantId(Long merchantId);
//
//    // 🔹 Used after delete to re-order serial numbers
//    List<Product> findByMerchantIdAndStatusOrderBySerialNoAsc(
//            Long merchantId,
//            Product.ProductStatus status
//    );
//    
//    // ✅ REQUIRED FOR PAGINATION (IMPORTANT)
//    Page<Product> findByMerchantIdAndStatus(
//            Long merchantId,
//            Product.ProductStatus status,
//            Pageable pageable
//    );
//	List<Product> findAll(Specification<Product> spec);
//	
//	
//	List<Product> findByMerchantIdAndStatus(
//            Long merchantId,
//            Product.ProductStatus status
//    );
//	
//	List<Product> findByStatusOrderByCreatedAtDesc(Product.ProductStatus status);
//	
//	
//	
//	
//	@Query("""
//		    SELECT p FROM Product p
//		    JOIN User u ON p.merchantId = u.id
//		    WHERE p.status = 'ACTIVE'
//		      AND u.onlineStatus = 'ONLINE'
//		      AND u.profileCompleted = 'YES'
//		""")
//		List<Product> findAllActiveFromOnlineVendors();
//
//		@Query("""
//		    SELECT p FROM Product p
//		    JOIN User u ON p.merchantId = u.id
//		    WHERE p.merchantId = :shopUserId
//		      AND p.status = 'ACTIVE'
//		      AND u.onlineStatus = 'ONLINE'
//		      AND u.profileCompleted = 'YES'
//		""")
//		List<Product> findActiveByShopUserIdAndOnline(@Param("shopUserId") Long shopUserId);
//	
//	
//	
//}


//package com.agrowmart.repository;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import com.agrowmart.entity.ApprovalStatus;
//import com.agrowmart.entity.Product;
//import com.agrowmart.entity.Product.ProductStatus;
//
//public interface ProductRepository extends JpaRepository<Product, Long> {
//
//    // ================= BASIC / ADMIN =================
//
//    List<Product> findByMerchantId(Long merchantId);
//
//    List<Product> findByMerchantIdAndStatus(
//            Long merchantId,
//            ProductStatus status
//    );
//    
// // Pending / Approved / Rejected Products
//    List<Product> findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus status);
//
//
//    Page<Product> findByMerchantIdAndStatus(
//            Long merchantId,
//            ProductStatus status,
//            Pageable pageable
//    );
//
//    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
//
//    List<Product> findAll(Specification<Product> spec);
//
//    List<Product> findAllByOrderByCreatedAtDesc();
//
//    // ================= SERIAL NO LOGIC =================
//
//    @Query("select max(p.serialNo) from Product p where p.merchantId = :merchantId")
//    Long findMaxSerialNoByMerchantId(@Param("merchantId") Long merchantId);
//
//    List<Product> findByMerchantIdAndStatusOrderBySerialNoAsc(
//            Long merchantId,
//            ProductStatus status
//    );
//
//    // ================= ✅ PUBLIC SAFE METHODS =================
//    // 🔒 Always ACTIVE + APPROVED
//
//    // For listing all pending products
//    List<Product> findByStatusAndApprovalStatus(ProductStatus status, ApprovalStatus approvalStatus);
//
//    List<Product> findByStatusAndApprovalStatusOrderByCreatedAtDesc(
//            ProductStatus status,
//            ApprovalStatus approvalStatus
//    );
//
//    List<Product> findByMerchantIdAndStatusAndApprovalStatus(
//            Long merchantId,
//            ProductStatus status,
//            ApprovalStatus approvalStatus
//    );
//
//    Page<Product> findByStatusAndApprovalStatus(
//            ProductStatus status,
//            ApprovalStatus approvalStatus,
//            Pageable pageable
//    );
//
//    // ================= ONLINE + PROFILE COMPLETED =================
//
//    @Query("""
//        SELECT p FROM Product p
//        JOIN User u ON p.merchantId = u.id
//        WHERE p.status = 'ACTIVE'
//          AND p.approvalStatus = 'APPROVED'
//          AND u.onlineStatus = 'ONLINE'
//          AND u.profileCompleted = 'YES'
//    """)
//    List<Product> findAllActiveFromOnlineVendors();
//
//    @Query("""
//        SELECT p FROM Product p
//        JOIN User u ON p.merchantId = u.id
//        WHERE p.merchantId = :shopUserId
//          AND p.status = 'ACTIVE'
//          AND p.approvalStatus = 'APPROVED'
//          AND u.onlineStatus = 'ONLINE'
//          AND u.profileCompleted = 'YES'
//    """)
//    List<Product> findActiveByShopUserIdAndOnline(
//            @Param("shopUserId") Long shopUserId
//    );
//    
//    @Query("""
//    	    SELECT p FROM Product p
//    	    JOIN User u ON p.merchantId = u.id
//    	    WHERE p.id = :productId
//    	      AND p.status = 'ACTIVE'
//    	      AND p.approvalStatus = 'APPROVED'
//    	      AND u.onlineStatus = 'ONLINE'
//    	      AND u.profileCompleted = 'YES'
//    	""")
//    	Optional<Product> findApprovedProductById(@Param("productId") Long productId);
//
//    @Query("""
//    	    SELECT p FROM Product p
//    	    JOIN User u ON p.merchantId = u.id
//    	    JOIN Shop s ON s.user = u
//    	    WHERE p.id = :id
//    	      AND p.approvalStatus = 'APPROVED'
//    	      AND p.status = 'ACTIVE'
//    	      AND s.isApproved = true
//    	""")
//    	Optional<Product> findApprovedProductForOrder(@Param("id") Long id);
//    
//    Optional<Product> findByIdAndMerchantIdAndStatusAndApprovalStatus(
//            Long id,
//            Long merchantId,
//            ProductStatus status,
//            ApprovalStatus approvalStatus
//    );
// 
//    // Add this new method
//    Page<Product> findByMerchantId(Long merchantId, Pageable pageable);  // No status filter - all products
//
//
//} 
package com.agrowmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.agrowmart.entity.ApprovalStatus;
import com.agrowmart.entity.Product;
import com.agrowmart.entity.Product.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // ================= BASIC / ADMIN =================
    // 🔓 Admin can see deleted also

    List<Product> findByMerchantId(Long merchantId);

    Page<Product> findByMerchantId(Long merchantId, Pageable pageable);

    List<Product> findByMerchantIdAndStatus(
            Long merchantId,
            ProductStatus status
    );

    
    List<Product> findByMerchantIdAndStatusAndDeletedFalseOrderBySerialNoAsc(
            Long merchantId,
            ProductStatus status
    );

    Page<Product> findByMerchantIdAndStatus(
            Long merchantId,
            ProductStatus status,
            Pageable pageable
    );

    List<Product> findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus status);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    List<Product> findAll(Specification<Product> spec);

    List<Product> findAllByOrderByCreatedAtDesc();

    // ================= SERIAL NO LOGIC =================

    @Query("select max(p.serialNo) from Product p where p.merchantId = :merchantId")
    Long findMaxSerialNoByMerchantId(@Param("merchantId") Long merchantId);

    List<Product> findByMerchantIdAndStatusOrderBySerialNoAsc(
            Long merchantId,
            ProductStatus status
    );

    // ================= ✅ PUBLIC / VENDOR SAFE =================
    // 🔒 Always NOT DELETED + ACTIVE + APPROVED
    
    List<Product> findByMerchantIdAndDeletedFalse(Long merchantId);

    Page<Product> findByMerchantIdAndDeletedFalse(Long merchantId, Pageable pageable);


    List<Product> findByStatusAndApprovalStatusAndDeletedFalse(
            ProductStatus status,
            ApprovalStatus approvalStatus
    );

    List<Product> findByStatusAndApprovalStatusAndDeletedFalseOrderByCreatedAtDesc(
            ProductStatus status,
            ApprovalStatus approvalStatus
    );

    List<Product> findByMerchantIdAndStatusAndApprovalStatusAndDeletedFalse(
            Long merchantId,
            ProductStatus status,
            ApprovalStatus approvalStatus
    );

    Page<Product> findByStatusAndApprovalStatusAndDeletedFalse(
            ProductStatus status,
            ApprovalStatus approvalStatus,
            Pageable pageable
    );

    Optional<Product> findByIdAndMerchantIdAndStatusAndApprovalStatusAndDeletedFalse(
            Long id,
            Long merchantId,
            ProductStatus status,
            ApprovalStatus approvalStatus
    );

    // ================= ONLINE + PROFILE COMPLETED =================

    @Query("""
        SELECT p FROM Product p
        JOIN User u ON p.merchantId = u.id
        WHERE p.status = 'ACTIVE'
          AND p.approvalStatus = 'APPROVED'
          AND p.deleted = false
          AND u.onlineStatus = 'ONLINE'
          AND u.profileCompleted = 'YES'
    """)
    List<Product> findAllActiveFromOnlineVendors();

    @Query("""
        SELECT p FROM Product p
        JOIN User u ON p.merchantId = u.id
        WHERE p.merchantId = :shopUserId
          AND p.status = 'ACTIVE'
          AND p.approvalStatus = 'APPROVED'
          AND p.deleted = false
          AND u.onlineStatus = 'ONLINE'
          AND u.profileCompleted = 'YES'
    """)
    List<Product> findActiveByShopUserIdAndOnline(
            @Param("shopUserId") Long shopUserId
    );
    
    

    @Query("""
        SELECT p FROM Product p
        JOIN User u ON p.merchantId = u.id
        WHERE p.id = :productId
          AND p.status = 'ACTIVE'
          AND p.approvalStatus = 'APPROVED'
          AND p.deleted = false
          AND u.onlineStatus = 'ONLINE'
          AND u.profileCompleted = 'YES'
    """)
    Optional<Product> findApprovedProductById(@Param("productId") Long productId);

    @Query("""
        SELECT p FROM Product p
        JOIN User u ON p.merchantId = u.id
        JOIN Shop s ON s.user = u
        WHERE p.id = :id
          AND p.approvalStatus = 'APPROVED'
          AND p.status = 'ACTIVE'
          AND p.deleted = false
          AND s.isApproved = true
    """)
    Optional<Product> findApprovedProductForOrder(@Param("id") Long id);

}
