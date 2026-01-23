//package com.agrowmart.repository;
//
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.agrowmart.entity.OtpCode;
//import com.agrowmart.enums.OtpPurpose;
//
//import java.util.Optional;
//
//public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
//    Optional<OtpCode> findTopByPhoneAndPurposeAndUsedFalseOrderByCreatedAtDesc(String phone, OtpPurpose purpose);
//}