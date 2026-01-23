package com.agrowmart.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.agrowmart.entity.order.Offer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

 List<Offer> findByMerchantIdAndIsActiveTrue(Long merchantId);

 List<Offer> findByMerchantIdAndIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
         Long merchantId, LocalDate today1, LocalDate today2);

 @Query("SELECT o FROM Offer o WHERE o.code = :code AND o.merchant.id = :merchantId " +
        "AND o.isActive = true " +
        "AND :today BETWEEN o.startDate AND o.endDate")
 Optional<Offer> findActiveOfferByCodeAndMerchant(
         @Param("code") String code,
         @Param("merchantId") Long merchantId,
         @Param("today") LocalDate today);
}