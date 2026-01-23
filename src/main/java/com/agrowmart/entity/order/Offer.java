package com.agrowmart.entity.order;



import com.agrowmart.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "offers")


public class Offer {

 public enum CustomerGroup {
     ALL, NEW_CUSTOMER, INACTIVE_30_DAYS
 }

 public enum CustomerType {
     ALL, NON_PREMIUM, PREMIUM
 }

 public enum DiscountType {
     PERCENTAGE, FLAT,FREE_PRODUCT
 }

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 //Added by Aakanksha-17/12/25
 @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
 @JoinColumn(name = "offer_price_id")
 private OfferPrice price;


 private String title;  
 @Column(unique = true)// e.g. "Weekend Sale"
 private String code;                     // e.g. "SAVE50"

 @Enumerated(EnumType.STRING)
 private DiscountType discountType = DiscountType.PERCENTAGE;

 private Integer discountPercent;         // 50 for 50%
 private BigDecimal flatDiscount;         // ₹50 off

 private BigDecimal maxDiscountAmount;    // Max ₹200 discount
 private BigDecimal minOrderAmount;       // Min ₹299

 @Enumerated(EnumType.STRING)
 private CustomerGroup customerGroup = CustomerGroup.ALL;

 @Enumerated(EnumType.STRING)
 private CustomerType customerType = CustomerType.ALL;

 private LocalDate startDate;
 private LocalDate endDate;

 private boolean isActive = true;

 @ManyToOne
 @JoinColumn(name = "merchant_id", nullable = false)
 private User merchant;

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

public String getCode() {
	return code;
}

public void setCode(String code) {
	this.code = code;
}

public DiscountType getDiscountType() {
	return discountType;
}

public void setDiscountType(DiscountType discountType) {
	this.discountType = discountType;
}

public Integer getDiscountPercent() {
	return discountPercent;
}

public void setDiscountPercent(Integer discountPercent) {
	this.discountPercent = discountPercent;
}

public BigDecimal getFlatDiscount() {
	return flatDiscount;
}

public void setFlatDiscount(BigDecimal flatDiscount) {
	this.flatDiscount = flatDiscount;
}

public BigDecimal getMaxDiscountAmount() {
	return maxDiscountAmount;
}

public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
	this.maxDiscountAmount = maxDiscountAmount;
}

public BigDecimal getMinOrderAmount() {
	return minOrderAmount;
}

public void setMinOrderAmount(BigDecimal minOrderAmount) {
	this.minOrderAmount = minOrderAmount;
}

public CustomerGroup getCustomerGroup() {
	return customerGroup;
}

public void setCustomerGroup(CustomerGroup customerGroup) {
	this.customerGroup = customerGroup;
}

public CustomerType getCustomerType() {
	return customerType;
}

public void setCustomerType(CustomerType customerType) {
	this.customerType = customerType;
}

public LocalDate getStartDate() {
	return startDate;
}

public void setStartDate(LocalDate startDate) {
	this.startDate = startDate;
}

public LocalDate getEndDate() {
	return endDate;
}

public void setEndDate(LocalDate endDate) {
	this.endDate = endDate;
}

public boolean isActive() {
	return isActive;
}

public void setActive(boolean isActive) {
	this.isActive = isActive;
}

public User getMerchant() {
	return merchant;
}

public void setMerchant(User merchant) {
	this.merchant = merchant;
}

//-------------------
private String freeProductName;
private String freeProductImageUrl;
private String freeProductDescription;
private String freeProductQuantity;           // "500g"
//private BigDecimal freeProductOriginalPrice;   // ₹100
//private BigDecimal freeProductOfferPrice;      // 0 = FREE
private BigDecimal minPurchaseAmount;          // ₹1999
private boolean isFreeGiftOffer = false;       // ← Ye flag add kar do

public String getFreeProductName() {
	return freeProductName;
}

public void setFreeProductName(String freeProductName) {
	this.freeProductName = freeProductName;
}

public String getFreeProductImageUrl() {
	return freeProductImageUrl;
}

public void setFreeProductImageUrl(String freeProductImageUrl) {
	this.freeProductImageUrl = freeProductImageUrl;
}

public String getFreeProductDescription() {
	return freeProductDescription;
}

public void setFreeProductDescription(String freeProductDescription) {
	this.freeProductDescription = freeProductDescription;
}

public String getFreeProductQuantity() {
	return freeProductQuantity;
}

public void setFreeProductQuantity(String freeProductQuantity) {
	this.freeProductQuantity = freeProductQuantity;
}


public BigDecimal getMinPurchaseAmount() {
	return minPurchaseAmount;
}

public void setMinPurchaseAmount(BigDecimal minPurchaseAmount) {
	this.minPurchaseAmount = minPurchaseAmount;
}

public boolean isFreeGiftOffer() {
	return isFreeGiftOffer;
}

public void setFreeGiftOffer(boolean isFreeGiftOffer) {
	this.isFreeGiftOffer = isFreeGiftOffer;
}

public void setOfferPrice(Object object) {
	// TODO Auto-generated method stub
	
}

public OfferPrice getPrice() {
	return price;
}

public void setPrice(OfferPrice price) {
	this.price = price;
}

}