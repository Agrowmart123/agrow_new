package com.agrowmart.repository;


import com.agrowmart.entity.VendorPaymentDetails;
import com.agrowmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorPaymentDetailsRepository extends JpaRepository<VendorPaymentDetails, Long> {
    Optional<VendorPaymentDetails> findByUser(User user);
    Optional<VendorPaymentDetails> findByUserId(Long userId);
}