package com.agrowmart.admin_seller_management.repository;

import com.agrowmart.admin_seller_management.entity.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminAuditLogRepository
        extends JpaRepository<AdminAuditLog, Long> {

    List<AdminAuditLog> findByVendorIdOrderByCreatedAtDesc(Long vendorId);
}