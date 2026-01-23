package com.agrowmart.entity.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.agrowmart.entity.Product;
import com.agrowmart.entity.WomenProduct;
import com.agrowmart.entity.AgriProduct.BaseAgriProduct;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "women_product_id", nullable = true)
    private WomenProduct womenProduct;
    
    public BaseAgriProduct getAgriProduct() {
		return agriProduct;
	}

	public void setAgriProduct(BaseAgriProduct agriProduct) {
		this.agriProduct = agriProduct;
	}
	// ===================== AGRI PRODUCT =====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agri_product_id")
    private BaseAgriProduct agriProduct;

    private int quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalPrice;

    // ──────────────────────────────────────────────
    // HELPER METHODS - Very important!
    // ──────────────────────────────────────────────
    public boolean isWomenProduct() {
        return womenProduct != null;
    }

    public Object getRealProduct() {
        return womenProduct != null ? womenProduct : product;
    }

    public Long getRealProductId() {
        if (womenProduct != null) return womenProduct.getId();
        if (product != null) return product.getId();
        return null;
    }

    public String getDisplayName() {
        if (womenProduct != null) return womenProduct.getName();
        if (product != null) return product.getProductName();
        return "Unknown Product";
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public WomenProduct getWomenProduct() { return womenProduct; }
    public void setWomenProduct(WomenProduct womenProduct) { this.womenProduct = womenProduct; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}