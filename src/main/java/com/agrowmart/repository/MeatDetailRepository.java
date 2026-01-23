package com.agrowmart.repository;

import com.agrowmart.entity.MeatDetail;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeatDetailRepository extends JpaRepository<MeatDetail, Long> {
	Optional<MeatDetail> findByProductId(Long productId);
}