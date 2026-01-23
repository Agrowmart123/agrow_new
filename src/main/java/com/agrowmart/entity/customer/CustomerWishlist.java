package com.agrowmart.entity.customer;

//src/main/java/com/agrowmart/entity/customer/CustomerWishlist.java


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_wishlists",
    uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "product_id", "product_type"}))
public class CustomerWishlist {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(name = "customer_id", nullable = false)
 private Long customerId;

 @Column(name = "product_id", nullable = false)
 private Long productId;

 @Column(name = "product_type", nullable = false, length = 20)
 private String productType; // "REGULAR" or "WOMEN"

 @Column(name = "added_at")
 private LocalDateTime addedAt = LocalDateTime.now();

 // Constructors
 public CustomerWishlist() {}

 public CustomerWishlist(Long customerId, Long productId, String productType) {
     this.customerId = customerId;
     this.productId = productId;
     this.productType = productType;
 }

 // Getters and Setters
 public Long getId() { return id; }
 public void setId(Long id) { this.id = id; }
 public Long getCustomerId() { return customerId; }
 public void setCustomerId(Long customerId) { this.customerId = customerId; }
 public Long getProductId() { return productId; }
 public void setProductId(Long productId) { this.productId = productId; }
 public String getProductType() { return productType; }
 public void setProductType(String productType) { this.productType = productType; }
 public LocalDateTime getAddedAt() { return addedAt; }
 public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}