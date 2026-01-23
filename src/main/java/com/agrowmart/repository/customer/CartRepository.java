package com.agrowmart.repository.customer;

import com.agrowmart.entity.customer.Cart;
import com.agrowmart.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomer(Customer customer);

    Optional<Cart> findByCustomerId(Long customerId);
}
