package com.agrowmart.entity.order;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "offer_prices")
public class OfferPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal originalPrice;

    @Column(nullable = false)
    private BigDecimal offerPrice; // 0 = FREE

    @Column(nullable = false)
    private boolean free;

    /* ================= VALIDATION ================= */
    @PrePersist
    @PreUpdate
    private void validatePrice() {

        if (originalPrice == null || offerPrice == null) {
            throw new IllegalStateException("Price cannot be null");
        }

        if (offerPrice.compareTo(originalPrice) >= 0) {
            throw new IllegalStateException(
                "Offer price must always be LESS than original price"
            );
        }

        if (free && offerPrice.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException(
                "Free offer must have offerPrice = 0"
            );
        }
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}

	public BigDecimal getOfferPrice() {
		return offerPrice;
	}

	public void setOfferPrice(BigDecimal offerPrice) {
		this.offerPrice = offerPrice;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

    // getters & setters
}
