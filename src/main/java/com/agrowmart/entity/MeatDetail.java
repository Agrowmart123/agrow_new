package com.agrowmart.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "meat_details")

public class MeatDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getKeyFeatures() {
		return keyFeatures;
	}
	public void setKeyFeatures(String keyFeatures) {
		this.keyFeatures = keyFeatures;
	}
	public String getCutType() {
		return cutType;
	}
	public void setCutType(String cutType) {
		this.cutType = cutType;
	}
	public String getServingSize() {
		return servingSize;
	}
	public void setServingSize(String servingSize) {
		this.servingSize = servingSize;
	}
	public String getStorageInstruction() {
		return storageInstruction;
	}
	public void setStorageInstruction(String storageInstruction) {
		this.storageInstruction = storageInstruction;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	public String getEnergy() {
		return energy;
	}
	public void setEnergy(String energy) {
		this.energy = energy;
	}
	public String getDietaryPreference() {
		return dietaryPreference;
	}
	public void setDietaryPreference(String dietaryPreference) {
		this.dietaryPreference = dietaryPreference;
	}
	public boolean isMarinated() {
		return marinated;
	}
	public void setMarinated(boolean marinated) {
		this.marinated = marinated;
	}
	public String getPackagingType() {
		return packagingType;
	}
	public void setPackagingType(String packagingType) {
		this.packagingType = packagingType;
	}
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
	public String getRefundPolicy() {
		return refundPolicy;
	}
	public void setRefundPolicy(String refundPolicy) {
		this.refundPolicy = refundPolicy;
	}

	public String getShelfLife() {
		return shelfLife;
	}
	public void setShelfLife(String shelfLife) {
		this.shelfLife = shelfLife;
	}
	
	@OneToOne
	@JoinColumn(name = "product_id", unique = true, nullable = false)
	
	private Product product;

    private String quantity;
   
    private String brand;
    private String keyFeatures;
    private String cutType;
    private String servingSize;
    private String storageInstruction;

    @Column(name = "usage_instruction")
    private String usage;

    private String energy;
    private String dietaryPreference = "NONVEG";
    private boolean marinated = false;
    private String packagingType;
    private String disclaimer;
    private String refundPolicy;
  
    private String shelfLife;
    
    // Changes :- Ankita ADD MinPrice & MaxPrice
    
    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice;

    @Column(name = "max_price", precision = 10, scale = 2)
    private BigDecimal maxPrice;

	public BigDecimal getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}
	public BigDecimal getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

}
