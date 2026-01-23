package com.agrowmart.entity.order;


import jakarta.persistence.*;

@Entity
@Table(name = "vendor_wallets")
public class VendorWallet {
    @Id
    private Long vendorId;

    private Double balance = 0.0;

    private Double codOutstanding = 0.0;  // Total COD amount pending collection

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getCodOutstanding() {
		return codOutstanding;
	}

	public void setCodOutstanding(Double codOutstanding) {
		this.codOutstanding = codOutstanding;
	}
}