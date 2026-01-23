package com.agrowmart.admin_seller_management.dto;

import java.time.LocalTime;

public class ShopDetailsDTO {

    private Long id;
    private String shopName;
    private String shopType;
    private String shopAddress;
    private String description;
    private String workingHours;

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getShopType() {
		return shopType;
	}
	public void setShopType(String shopType) {
		this.shopType = shopType;
	}
	public String getShopAddress() {
		return shopAddress;
	}
	public void setShopAddress(String shopAddress) {
		this.shopAddress = shopAddress;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getWorkingHours() {
		return workingHours;
	}
	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}
	public String getShopLicense() {
		return shopLicense;
	}
	public void setShopLicense(String shopLicense) {
		this.shopLicense = shopLicense;
	}
	public String getShopPhoto() {
		return shopPhoto;
	}
	public void setShopPhoto(String shopPhoto) {
		this.shopPhoto = shopPhoto;
	}
	public String getShopCoverPhoto() {
		return shopCoverPhoto;
	}
	public void setShopCoverPhoto(String shopCoverPhoto) {
		this.shopCoverPhoto = shopCoverPhoto;
	}
	public String getShopLicensePhoto() {
		return shopLicensePhoto;
	}
	public void setShopLicensePhoto(String shopLicensePhoto) {
		this.shopLicensePhoto = shopLicensePhoto;
	}
	public LocalTime getOpensAt() {
		return opensAt;
	}
	public void setOpensAt(LocalTime opensAt) {
		this.opensAt = opensAt;
	}
	public LocalTime getClosesAt() {
		return closesAt;
	}
	public void setClosesAt(LocalTime closesAt) {
		this.closesAt = closesAt;
	}
	public boolean isApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	private String shopLicense;
    private String shopPhoto;
    private String shopCoverPhoto;
    private String shopLicensePhoto;

    private LocalTime opensAt;
    private LocalTime closesAt;
    private boolean approved;
    private boolean active;
    
    private String vendorType;
    // getters & setters

	public String getVendorType() {
		return vendorType;
	}
	public void setVendorType(String vendorType) {
		this.vendorType = vendorType;
	}
	
	private String gstCertificateNumber;

	public String getGstCertificateNumber() {
		return gstCertificateNumber;
	}
	public void setGstCertificateNumber(String gstCertificateNumber) {
		this.gstCertificateNumber = gstCertificateNumber;
	}

    // getters & setters
}
