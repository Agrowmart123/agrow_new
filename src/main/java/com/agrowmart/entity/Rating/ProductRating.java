package com.agrowmart.entity.Rating;



import com.agrowmart.entity.Product;
import com.agrowmart.entity.WomenProduct;
import com.agrowmart.entity.customer.Customer;
import jakarta.persistence.*;
import java.util.Date;
@Entity
@Table(
    name = "product_ratings",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customer_id", "product_id"}),
        @UniqueConstraint(columnNames = {"customer_id", "women_product_id"})
    }
)
public class ProductRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Now nullable – for women products this will be null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    // Now nullable – for normal products this will be null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "women_product_id", nullable = true)
    private WomenProduct womenProduct;

    @Column(nullable = false)
    private int stars;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public WomenProduct getWomenProduct() {
		return womenProduct;
	}

	public void setWomenProduct(WomenProduct womenProduct) {
		this.womenProduct = womenProduct;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

    // ... all getters & setters ...

}