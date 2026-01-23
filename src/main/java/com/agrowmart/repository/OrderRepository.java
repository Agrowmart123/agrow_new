//package com.agrowmart.repository;
//
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import com.agrowmart.entity.User;
//import com.agrowmart.entity.customer.Customer;
//import com.agrowmart.entity.order.Order;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//public interface OrderRepository extends JpaRepository<Order, String > {
//
//	    List<Order> findByCustomer(Customer customer);
//	    List<Order> findByMerchantAndStatus(User merchant, Order.OrderStatus status);
//	    Optional<User> findByCustomerOrderByCreatedAtDesc(Customer customer);
//	    boolean existsByCustomerAndMerchantAndCreatedAtAfter(Customer customer, User merchant, LocalDateTime thirtyDaysAgo);
//	  
//	    boolean existsByCustomerAndMerchant(Customer customer, User merchant);
//	    List<Order> findByMerchantOrderByCreatedAtDesc(User merchant);
//	    List<Order> findByMerchant(User merchant);
//	    
//
//	    @Query("SELECT o FROM Order o WHERE o.status = 'DELIVERED' AND o.settlementStatus = 'PENDING' AND o.createdAt <= :cutoff")
//	    List<Order> findEligibleForSettlement(@Param("cutoff") LocalDateTime cutoff);
//	    
//	    
//	    
//	    
//	 
//	    // ✅ Check if a customer purchased a specific product (DELIVERED only)
//	    @Query("""
//	        SELECT COUNT(o) > 0
//	        FROM Order o
//	        JOIN o.items i
//	        WHERE o.customer.id = :customerId
//	          AND i.product.id = :productId
//	          AND o.status = com.agrowmart.entity.order.Order.OrderStatus.DELIVERED
//	    """)
//	    boolean existsByCustomerIdAndProductId(
//	            @Param("customerId") Long customerId,
//	            @Param("productId") Long productId
//	    );
//	}

package com.agrowmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.agrowmart.entity.User;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.order.Order;
import com.agrowmart.entity.order.Order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByCustomer(Customer customer);

  //  List<Order> findByMerchantAndStatus(User merchant, Order.OrderStatus status);

    Optional<User> findByCustomerOrderByCreatedAtDesc(Customer customer);

    boolean existsByCustomerAndMerchantAndCreatedAtAfter(Customer customer, User merchant, LocalDateTime thirtyDaysAgo);

    boolean existsByCustomerAndMerchant(Customer customer, User merchant);

    List<Order> findByMerchantOrderByCreatedAtDesc(User merchant);

    List<Order> findByMerchant(User merchant);

    @Query("SELECT o FROM Order o WHERE o.status = 'DELIVERED' AND o.settlementStatus = 'PENDING' AND o.createdAt <= :cutoff")
    List<Order> findEligibleForSettlement(@Param("cutoff") LocalDateTime cutoff);

    // Check if a customer purchased a specific product (DELIVERED only)
    @Query("""
        SELECT COUNT(o) > 0
        FROM Order o
        JOIN o.items i
        WHERE o.customer.id = :customerId
          AND i.product.id = :productId
          AND o.status = com.agrowmart.entity.order.Order.OrderStatus.DELIVERED
    """)
    boolean existsByCustomerIdAndProductId(
            @Param("customerId") Long customerId,
            @Param("productId") Long productId
    );

    // ✅ Check if a customer purchased a specific WomenProduct (DELIVERED only)
    // Use the same query, pass the WomenProduct's ID
 // CORRECTED check for WOMEN products (must use women_product_id column!)
    @Query("""
        SELECT COUNT(oi) > 0
        FROM Order o
        JOIN o.items oi
        WHERE o.customer.id = :customerId
          AND oi.womenProduct.id = :womenProductId
          AND o.status = com.agrowmart.entity.order.Order.OrderStatus.DELIVERED
    """)
    boolean existsByCustomerIdAndWomenProductId(
            @Param("customerId") Long customerId,
            @Param("womenProductId") Long womenProductId
    );
    
 // In OrderRepository.java
    List<Order> findByMerchantAndStatusIn(User merchant, List<Order.OrderStatus> statuses);

	List<Order> findByMerchantAndStatus(User vendor, OrderStatus scheduled);

	Optional<User> findByCustomerAndStatusIn(Customer customer, List<OrderStatus> activeStatuses);
    
}
