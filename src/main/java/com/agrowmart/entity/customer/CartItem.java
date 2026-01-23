package com.agrowmart.entity.customer;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")

public class CartItem {

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getNameSnapshot() {
		return nameSnapshot;
	}

	public void setNameSnapshot(String nameSnapshot) {
		this.nameSnapshot = nameSnapshot;
	}

	public String getImageSnapshot() {
		return imageSnapshot;
	}

	public void setImageSnapshot(String imageSnapshot) {
		this.imageSnapshot = imageSnapshot;
	}

	public BigDecimal getPriceAtAdd() {
		return priceAtAdd;
	}

	public void setPriceAtAdd(BigDecimal priceAtAdd) {
		this.priceAtAdd = priceAtAdd;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public LocalDateTime getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(LocalDateTime addedAt) {
		this.addedAt = addedAt;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    private Long productId;

    @Column(length = 20)
    private String productType; // REGULAR, WOMEN, AGRI

    private String nameSnapshot;
    private String imageSnapshot;

    private BigDecimal priceAtAdd;
    private Integer quantity = 1;

    private LocalDateTime addedAt = LocalDateTime.now();
}
