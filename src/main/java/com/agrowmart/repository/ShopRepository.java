package com.agrowmart.repository;


import com.agrowmart.entity.Shop;
import com.agrowmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.awt.print.Pageable;
import java.util.*;

public interface ShopRepository extends JpaRepository<Shop, Long> {
 Optional<Shop> findByUser(User user);
 Optional<Shop> findByUserId(Long userId);
 boolean existsByUser(User user);
 
 
//MOST POPULAR SHOPS - by number of delivered orders
//In ShopRepository.java — REPLACE BOTH METHODS WITH THESE

@Query("""
  SELECT s FROM Shop s
  WHERE s.isApproved = true AND s.isActive = true
  ORDER BY (
      SELECT COUNT(o) FROM Order o
      WHERE o.merchant.id = s.user.id
      AND o.status = 'DELIVERED'
  ) DESC
  """)
List<Shop> findPopularShops();

@Query("""
  SELECT s FROM Shop s
  WHERE s.isApproved = true AND s.isActive = true
  ORDER BY (
      SELECT COUNT(o) FROM Order o
      WHERE o.merchant.id = s.user.id
      AND o.status = 'DELIVERED'
  ) DESC
  """)
List<Shop> findPopularShops(Pageable pageable);
}