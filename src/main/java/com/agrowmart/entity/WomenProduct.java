package com.agrowmart.entity;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.agrowmart.entity.Product.ProductStatus;

@Entity
@Table(name = "women_products")

public class WomenProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid = java.util.UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private String name;  

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice;

    @Column(name = "max_price", precision = 10, scale = 2)
    private BigDecimal maxPrice;

    private Integer stock = 0;

    @Column(nullable = false)
    private String unit;

    @Column(name = "image_urls", length = 2000)
    private String imageUrls;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public User getSeller() {
		return seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(String imageUrls) {
		this.imageUrls = imageUrls;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	private Boolean isAvailable = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private String country = "India";

    // Helper methods for imageUrls
    public List<String> getImageUrlList() {
        if (imageUrls == null || imageUrls.isEmpty()) return new ArrayList<>();
        return Arrays.asList(imageUrls.split(","));
    }

    public void setImageUrlList(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            this.imageUrls = null;
        } else {
            this.imageUrls = String.join(",", urls);
        }
    }
    
 // ============ NEW FIELDS ============
    @Column(length = 500)
    private String ingredients;        // e.g., "Aloe Vera, Coconut Oil, Vitamin E"

    @Column(length = 100)
    private String shelfLife;          // e.g., "12 months", "2 years"

    @Column(length = 100)
    private String packagingType;      // e.g., "Glass Bottle", "Plastic Pouch"

    @Column(columnDefinition = "TEXT")
    private String productInfo;        // Detailed product information

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public String getShelfLife() {
		return shelfLife;
	}

	public void setShelfLife(String shelfLife) {
		this.shelfLife = shelfLife;
	}

	public String getPackagingType() {
		return packagingType;
	}

	public void setPackagingType(String packagingType) {
		this.packagingType = packagingType;
	}

	public String getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(String productInfo) {
		this.productInfo = productInfo;
	}
	
	// admin 
	
		@Enumerated(EnumType.STRING)
	    @Column(name = "approval_status", nullable = false)
	    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
		
		

	    public ApprovalStatus getApprovalStatus() {
	        return approvalStatus;
	    }

	    public void setApprovalStatus(ApprovalStatus approvalStatus) {
	        this.approvalStatus = approvalStatus;
	    }
	    
	 // ──────────────────────────────────────────────
	    // THIS IS THE IMPORTANT CHANGE
	    // ──────────────────────────────────────────────
	    @Enumerated(EnumType.STRING)
	    @Column(name = "status")
	    private ProductStatus status = ProductStatus.ACTIVE;   // ← ADD = ProductStatus.ACTIVE here

	    // Keep your existing getters/setters
	    public ProductStatus getStatus() {
	        return status;
	    }

	    public void setStatus(ProductStatus status) {
	        this.status = status;
	    }
	 // Restore 
	    
	    @Column(name = "is_deleted")
	    private Boolean deleted = false;

	    public Boolean getDeleted() {
	        return deleted;
	    }

	    public void setDeleted(Boolean deleted) {
	        this.deleted = deleted;
	    }
	    
}

	    