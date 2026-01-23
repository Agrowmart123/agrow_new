package com.agrowmart.repository;

//src/main/java/com/agrowmart/repository/OfferUsageRepository.java

import com.agrowmart.entity.User;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.order.Offer;
import com.agrowmart.entity.order.OfferUsage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfferUsageRepository extends JpaRepository<OfferUsage, Long> {
 boolean existsByCustomerAndOffer(Customer customer, Offer offer);
 Optional<OfferUsage> findByCustomerAndOffer(Customer customer, Offer offer);
}