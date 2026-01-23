package com.agrowmart.entity.Rating;



import jakarta.persistence.*;
import java.util.Date;

import com.agrowmart.entity.User;
import com.agrowmart.entity.customer.Customer;

@Entity
@Table(name = "ratings", uniqueConstraints = {
 @UniqueConstraint(columnNames = {"rater_id", "rated_id"}) // One rating per customer per vendor
})
public class Rating {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "rater_id", nullable = false)
 private Customer rater; // Customer

 public Customer getRater() {
	return rater;
}
public void setRater(Customer rater) {
	this.rater = rater;
}
@ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "rated_id", nullable = false)
 private User rated; // Vendor

 @Column(nullable = false)
 private int stars; // Must be 1-5

 @Column(columnDefinition = "TEXT")
 private String feedback;

 @Column(name = "created_at", nullable = false, updatable = false)
 @Temporal(TemporalType.TIMESTAMP)
 private Date createdAt = new Date();

 @Column(name = "updated_at")
 @Temporal(TemporalType.TIMESTAMP)
 private Date updatedAt;

 // Getters and Setters
 public Long getId() { return id; }
 public void setId(Long id) { this.id = id; }

 public User getRated() { return rated; }
 public void setRated(User rated) { this.rated = rated; }
 public int getStars() { return stars; }
 public void setStars(int stars) { this.stars = stars; }
 public String getFeedback() { return feedback; }
 public void setFeedback(String feedback) { this.feedback = feedback; }
 public Date getCreatedAt() { return createdAt; }
 public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
 public Date getUpdatedAt() { return updatedAt; }
 public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}