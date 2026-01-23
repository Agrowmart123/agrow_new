package com.agrowmart.entity;





import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "vendor_details")

public class VendorPaymentDetails {


	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @OneToOne
	    @JoinColumn(name = "user_id", nullable = false, unique = true)
	    private User user;

	    // Razorpay Onboarding
	    @Column(name = "razorpay_contact_id", length = 50)
	    private String razorpayContactId;

	    @Column(name = "razorpay_fund_account_id", length = 50)
	    private String razorpayFundAccountId;

	    // Wallet & COD
	    @Column(name = "wallet_balance", columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
	    private BigDecimal walletBalance = BigDecimal.ZERO;

	    @Column(name = "cod_outstanding", columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
	    private BigDecimal codOutstanding = BigDecimal.ZERO;

	    @Column(name = "cod_enabled", columnDefinition = "TINYINT(1) DEFAULT 1")
	    private boolean codEnabled = true;

	    private LocalDateTime createdAt = LocalDateTime.now();
	    private LocalDateTime updatedAt = LocalDateTime.now();
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
		public String getRazorpayContactId() {
			return razorpayContactId;
		}
		public void setRazorpayContactId(String razorpayContactId) {
			this.razorpayContactId = razorpayContactId;
		}
		public String getRazorpayFundAccountId() {
			return razorpayFundAccountId;
		}
		public void setRazorpayFundAccountId(String razorpayFundAccountId) {
			this.razorpayFundAccountId = razorpayFundAccountId;
		}
		public BigDecimal getWalletBalance() {
			return walletBalance;
		}
		public void setWalletBalance(BigDecimal walletBalance) {
			this.walletBalance = walletBalance;
		}
		public BigDecimal getCodOutstanding() {
			return codOutstanding;
		}
		public void setCodOutstanding(BigDecimal codOutstanding) {
			this.codOutstanding = codOutstanding;
		}
		public boolean isCodEnabled() {
			return codEnabled;
		}
		public void setCodEnabled(boolean codEnabled) {
			this.codEnabled = codEnabled;
		}
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}
		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}
	}
	