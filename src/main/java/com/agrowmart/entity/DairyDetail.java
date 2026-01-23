package com.agrowmart.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "dairy_details")

public class DairyDetail {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← THIS WAS MISSING!
    private Long id;

	
	@OneToOne
	@JoinColumn(name = "product_id", unique = true, nullable = false)
	
	private Product product;
	
    private String quantity;
  
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
	public String getIngredients() {
		return ingredients;
	}
	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}
	public String getPackagingType() {
		return packagingType;
	}
	public void setPackagingType(String packagingType) {
		this.packagingType = packagingType;
	}
	public String getProductInformation() {
		return productInformation;
	}
	public void setProductInformation(String productInformation) {
		this.productInformation = productInformation;
	}
	public String getUsageInformation() {
		return usageInformation;
	}
	public void setUsageInformation(String usageInformation) {
		this.usageInformation = usageInformation;
	}
	public String getDietaryPreference() {
		return dietaryPreference;
	}
	public void setDietaryPreference(String dietaryPreference) {
		this.dietaryPreference = dietaryPreference;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getStorage() {
		return storage;
	}
	public void setStorage(String storage) {
		this.storage = storage;
	}

	public String getShelfLife() {
		return shelfLife;
	}
	public void setShelfLife(String shelfLife) {
		this.shelfLife = shelfLife;
	}
	private String brand;
    private String ingredients;
    private String packagingType;
    private String productInformation;
    private String usageInformation;
    private String dietaryPreference = "VEG";
    private String unit;
    private String storage;
   
    private String shelfLife;
}