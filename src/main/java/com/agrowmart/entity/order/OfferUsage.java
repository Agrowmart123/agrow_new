package com.agrowmart.entity.order;


import com.agrowmart.entity.User;
import com.agrowmart.entity.customer.Customer;
import com.agrowmart.entity.order.Order;
import jakarta.persistence.*;

@Entity
@Table(name = "offer_usages")

public class OfferUsage {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne
 private Customer customer;

 public Customer getCustomer() {
	return customer;
}

public void setCustomer(Customer customer) {
	this.customer = customer;
}

@ManyToOne
 private Offer offer;

 @ManyToOne
 private Order order;

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}



public Offer getOffer() {
	return offer;
}

public void setOffer(Offer offer) {
	this.offer = offer;
}

public Order getOrder() {
	return order;
}

public void setOrder(Order order) {
	this.order = order;
}
}