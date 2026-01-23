package com.agrowmart.repository;

import com.agrowmart.entity.DairyDetail;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DairyDetailRepository extends JpaRepository<DairyDetail, Long> {
	Optional<DairyDetail> findByProductId(Long productId);
	
}