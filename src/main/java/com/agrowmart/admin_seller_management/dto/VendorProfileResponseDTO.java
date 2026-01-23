//package com.agrowmart.admin_seller_management.dto;
//
//import com.agrowmart.admin_seller_management.enums.AccountStatus;
//import java.time.LocalDateTime;
//
//public class VendorProfileResponseDTO {
//
//    private Long id;
//    private String name;
//    private String email;
//    private String phone;
//    private String businessName;
//    private String address;
//    private String photoUrl;
//
//    private AccountStatus accountStatus;
//    private String statusReason;
//
//    private DocumentsDTO documents;
//    private ShopDetailsDTO shop;
//
//    // ================= GETTERS & SETTERS =================
//
//    public ShopDetailsDTO getShop() {
//		return shop;
//	}
//
//	public void setShop(ShopDetailsDTO shop) {
//		this.shop = shop;
//	}
//
//	public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getBusinessName() {
//        return businessName;
//    }
//
//    public void setBusinessName(String businessName) {
//        this.businessName = businessName;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getPhotoUrl() {
//        return photoUrl;
//    }
//
//    public void setPhotoUrl(String photoUrl) {
//        this.photoUrl = photoUrl;
//    }
//
//    public AccountStatus getAccountStatus() {
//        return accountStatus;
//    }
//
//    public void setAccountStatus(AccountStatus accountStatus) {
//        this.accountStatus = accountStatus;
//    }
//
//    public String getStatusReason() {
//        return statusReason;
//    }
//
//    public void setStatusReason(String statusReason) {
//        this.statusReason = statusReason;
//    }
//
//    public DocumentsDTO getDocuments() {
//        return documents;
//    }
//
//    public void setDocuments(DocumentsDTO documents) {
//        this.documents = documents;
//    }
//}

package com.agrowmart.admin_seller_management.dto;

import com.agrowmart.admin_seller_management.enums.AccountStatus;
import java.time.LocalDateTime;

public class VendorProfileResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String businessName;
    private String address;
    private String photoUrl;

    private AccountStatus accountStatus;
    private String statusReason;

    private DocumentsDTO documents;
    private ShopDetailsDTO shop;

    // ================= GETTERS & SETTERS =================

    public ShopDetailsDTO getShop() {
		return shop;
	}

	public void setShop(ShopDetailsDTO shop) {
		this.shop = shop;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public DocumentsDTO getDocuments() {
        return documents;
    }

    public void setDocuments(DocumentsDTO documents) {
        this.documents = documents;
    }
}
