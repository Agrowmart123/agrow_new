package com.agrowmart.entity.order;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long vendorId;

    @Column(nullable = false)
    private Double payoutAmount;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String  orderId) {
		this.orderId = orderId;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	public Double getPayoutAmount() {
		return payoutAmount;
	}

	public void setPayoutAmount(Double payoutAmount) {
		this.payoutAmount = payoutAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRazorpayPayoutId() {
		return razorpayPayoutId;
	}

	public void setRazorpayPayoutId(String razorpayPayoutId) {
		this.razorpayPayoutId = razorpayPayoutId;
	}

	public LocalDateTime getPayoutDate() {
		return payoutDate;
	}

	public void setPayoutDate(LocalDateTime payoutDate) {
		this.payoutDate = payoutDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Column(nullable = false)
    private String status = "PENDING"; // PENDING, PAID, FAILED

    private String razorpayPayoutId;

    private LocalDateTime payoutDate;

    private LocalDateTime createdAt = LocalDateTime.now();
}