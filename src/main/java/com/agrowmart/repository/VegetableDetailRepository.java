package com.agrowmart.repository;

import com.agrowmart.entity.VegetableDetail;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VegetableDetailRepository extends JpaRepository<VegetableDetail, Long> {
	Optional<VegetableDetail> findByProductId(Long productId);
	
}