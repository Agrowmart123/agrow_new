package com.agrowmart.admin_seller_management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_audit_logs")
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;
    private Long vendorId;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}

	public String getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Column(nullable = false)
    private String action;   // APPROVE, REJECT, DELETE, RESTORE, BLOCK, UNBLOCK

    @Column(length = 500)
    private String reason;

    private String previousStatus;
    private String newStatus;

    private LocalDateTime createdAt;

    // ===== constructor =====
    public AdminAuditLog() {}

    public AdminAuditLog(
            Long adminId,
            Long vendorId,
            String action,
            String reason,
            String previousStatus,
            String newStatus
    ) {
        this.adminId = adminId;
        this.vendorId = vendorId;
        this.action = action;
        this.reason = reason;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.createdAt = LocalDateTime.now();
    }

    // getters & setters
}
