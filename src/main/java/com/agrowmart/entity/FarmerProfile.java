package com.agrowmart.entity;


import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "farmer_profiles")
public class FarmerProfile {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 // One FarmerProfile belongs to exactly one User
 @OneToOne
 @JoinColumn(name = "user_id", nullable = false, unique = true)
 private User user;

 @Column(name = "state", length = 100)
 private String state;
 
 

 
 
 
 @Column(name = "bank_name", length = 100)
 private String bankName;

 @Column(name = "account_holder_name", length = 100)
 private String accountHolderName;

 @Column(name = "bank_account_number", length = 50)
 private String bankAccountNumber;

 @Column(name = "ifsc_code", length = 20)
 private String ifscCode;

 @Column(name = "profile_completed", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'false'")
 private String profileCompleted = "false";

 // Constructors
 public FarmerProfile() {}
 public FarmerProfile(User user) {
     this.user = user;
 }

 // Getters & Setters
 public Long getId() { return id; }
 public void setId(Long id) { this.id = id; }

 public User getUser() { return user; }
 public void setUser(User user) { this.user = user; }

 public String getState() { return state; }
 public void setState(String state) { this.state = state; }


 public String getBankName() { return bankName; }
 public void setBankName(String bankName) { this.bankName = bankName; }

 public String getAccountHolderName() { return accountHolderName; }
 public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

 public String getBankAccountNumber() { return bankAccountNumber; }
 public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

 public String getIfscCode() { return ifscCode; }
 public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

 public String getProfileCompleted() { return profileCompleted; }
 public void setProfileCompleted(String profileCompleted) { this.profileCompleted = profileCompleted; }
}