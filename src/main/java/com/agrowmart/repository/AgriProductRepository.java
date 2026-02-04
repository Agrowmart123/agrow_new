package com.agrowmart.repository;
 
import com.agrowmart.entity.User;
import com.agrowmart.entity.AgriProduct.BaseAgriProduct;
import com.agrowmart.entity.AgriProduct.BaseAgriProduct.ApprovalStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface AgriProductRepository extends JpaRepository<BaseAgriProduct, Long> {
 
    List<BaseAgriProduct> findByVendor(User vendor);
 
    Optional<BaseAgriProduct> findByIdAndVendor(Long id, User vendor);
 
    @Query("SELECT p FROM BaseAgriProduct p WHERE LOWER(p.AgriproductName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.Agridescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BaseAgriProduct> search(@Param("keyword") String keyword);

	long countByVendor(User user);
	
	List<BaseAgriProduct> findByVisibleToCustomersTrue();

	@Query("SELECT p FROM BaseAgriProduct p " +
	       "WHERE p.visibleToCustomers = true " +
	       "AND (LOWER(p.AgriproductName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	       "     OR LOWER(p.Agridescription) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<BaseAgriProduct> searchVisible(@Param("keyword") String keyword);
	
	// ────────────────────────────────────────────────
    // Admin / approval related queries
    // ────────────────────────────────────────────────
    List<BaseAgriProduct> findByApprovalStatus(ApprovalStatus status);

    @Query("SELECT p FROM BaseAgriProduct p WHERE p.approvalStatus = 'PENDING'")
    List<BaseAgriProduct> findAllPending();

    @Query("SELECT p FROM BaseAgriProduct p WHERE p.approvalStatus = 'REJECTED'")
    List<BaseAgriProduct> findAllRejected();

    @Query("SELECT p FROM BaseAgriProduct p WHERE p.approvalStatus = 'APPROVED'")
    List<BaseAgriProduct> findAllApproved();

 // Add this to AgriProductRepository.java
    List<BaseAgriProduct> findByVendorAndApprovalStatus(User vendor, ApprovalStatus status);
    // Optional: count pending for dashboard
    long countByApprovalStatus(ApprovalStatus status);
}
