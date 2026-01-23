package com.agrowmart.repository.customer;

//src/main/java/com/agrowmart/repository/customer/CustomerWishlistRepository.java


import com.agrowmart.entity.customer.CustomerWishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerWishlistRepository extends JpaRepository<CustomerWishlist, Long> {

 List<CustomerWishlist> findByCustomerIdOrderByAddedAtDesc(Long customerId);

 Optional<CustomerWishlist> findByCustomerIdAndProductIdAndProductType(
         Long customerId, Long productId, String productType);

 boolean existsByCustomerIdAndProductIdAndProductType(
         Long customerId, Long productId, String productType);

 void deleteByCustomerIdAndProductIdAndProductType(
         Long customerId, Long productId, String productType);
}