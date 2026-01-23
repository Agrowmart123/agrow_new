package com.agrowmart.repository;


import com.agrowmart.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("SELECT s FROM Subscription s " +
           "WHERE s.active = true " +
           "AND s.expiryDate < :now")
    List<Subscription> findAllExpiredActiveSubscriptions(LocalDateTime now);
}