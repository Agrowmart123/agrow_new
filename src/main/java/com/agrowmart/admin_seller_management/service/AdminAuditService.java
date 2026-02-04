package com.agrowmart.admin_seller_management.service;

import com.agrowmart.admin_seller_management.entity.AdminAuditLog;
import com.agrowmart.admin_seller_management.repository.AdminAuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminAuditService {

    private final AdminAuditLogRepository repository;

    public AdminAuditService(AdminAuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(
            Long adminId,
            Long vendorId,
            String action,
            String reason,
            String previousStatus,
            String newStatus
    ) {
        repository.save(
                new AdminAuditLog(
                        adminId,
                        vendorId,
                        action,
                        reason,
                        previousStatus,
                        newStatus
                )
        );
    }
}