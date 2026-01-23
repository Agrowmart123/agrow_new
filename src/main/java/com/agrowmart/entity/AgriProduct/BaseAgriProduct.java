package com.agrowmart.entity.AgriProduct;

import jakarta.persistence.*;
import com.agrowmart.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agri_products")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "Agricategory", discriminatorType = DiscriminatorType.STRING)
public abstract class BaseAgriProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AgriproductName", nullable = false)
    private String AgriproductName;

    @Column(name = "Agridescription", columnDefinition = "TEXT")
    private String Agridescription;

    @Column(name = "Agriprice", precision = 10, scale = 2, nullable = false)
    private BigDecimal Agriprice;

    @Column(name = "Agriunit", nullable = false)
    private String Agriunit;

    @Column(name = "Agriquantity", nullable = false)
    private Integer Agriquantity;

    // CRITICAL FIX: Use @Lob for storing JSON array of image URLs
    @Lob
    @Column(name = "AgriimageUrl", columnDefinition = "LONGTEXT")
    private String AgriimageUrl;

    @Column(name = "AgribrandName")
    private String AgribrandName;

    @Column(name = "AgripackagingType")
    private String AgripackagingType;

    @Column(name = "AgrilicenseNumber")
    private String AgrilicenseNumber;

    @Column(name = "AgrilicenseType")
    private String AgrilicenseType;

    @Lob
    @Column(name = "AgrilicenseImageUrl", columnDefinition = "LONGTEXT")
    private String AgrilicenseImageUrl;

    @Column(name = "AgribatchNumber")
    private String AgribatchNumber;

    @Column(name = "AgrimanufacturerName")
    private String AgrimanufacturerName;

    @Column(name = "AgrimanufacturingDate")
    private LocalDate AgrimanufacturingDate;

    @Column(name = "AgriexpiryDate")
    private LocalDate AgriexpiryDate;

    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "fromAdmin")
    private Boolean fromAdmin = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AgrivendorId", nullable = false)
    private User vendor;

    // Jackson ObjectMapper – thread-safe if properly configured
    private static final ObjectMapper mapper = new ObjectMapper();

    // ====================== Getters & Setters ======================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAgriproductName() { return AgriproductName; }
    public void setAgriproductName(String AgriproductName) { this.AgriproductName = AgriproductName; }

    public String getAgridescription() { return Agridescription; }
    public void setAgridescription(String Agridescription) { this.Agridescription = Agridescription; }

    public BigDecimal getAgriprice() { return Agriprice; }
    public void setAgriprice(BigDecimal Agriprice) { this.Agriprice = Agriprice; }

    public String getAgriunit() { return Agriunit; }
    public void setAgriunit(String Agriunit) { this.Agriunit = Agriunit; }

    public Integer getAgriquantity() { return Agriquantity; }
    public void setAgriquantity(Integer Agriquantity) { this.Agriquantity = Agriquantity; }

    public String getAgribrandName() { return AgribrandName; }
    public void setAgribrandName(String AgribrandName) { this.AgribrandName = AgribrandName; }

    public String getAgripackagingType() { return AgripackagingType; }
    public void setAgripackagingType(String AgripackagingType) { this.AgripackagingType = AgripackagingType; }

    public String getAgrilicenseNumber() { return AgrilicenseNumber; }
    public void setAgrilicenseNumber(String AgrilicenseNumber) { this.AgrilicenseNumber = AgrilicenseNumber; }

    public String getAgrilicenseType() { return AgrilicenseType; }
    public void setAgrilicenseType(String AgrilicenseType) { this.AgrilicenseType = AgrilicenseType; }

    public String getAgrilicenseImageUrl() { return AgrilicenseImageUrl; }
    public void setAgrilicenseImageUrl(String AgrilicenseImageUrl) { this.AgrilicenseImageUrl = AgrilicenseImageUrl; }

    public String getAgribatchNumber() { return AgribatchNumber; }
    public void setAgribatchNumber(String AgribatchNumber) { this.AgribatchNumber = AgribatchNumber; }

    public String getAgrimanufacturerName() { return AgrimanufacturerName; }
    public void setAgrimanufacturerName(String AgrimanufacturerName) { this.AgrimanufacturerName = AgrimanufacturerName; }

    public LocalDate getAgrimanufacturingDate() { return AgrimanufacturingDate; }
    public void setAgrimanufacturingDate(LocalDate AgrimanufacturingDate) { this.AgrimanufacturingDate = AgrimanufacturingDate; }

    public LocalDate getAgriexpiryDate() { return AgriexpiryDate; }
    public void setAgriexpiryDate(LocalDate AgriexpiryDate) { this.AgriexpiryDate = AgriexpiryDate; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public Boolean getFromAdmin() { return fromAdmin; }
    public void setFromAdmin(Boolean fromAdmin) { this.fromAdmin = fromAdmin; }

    public User getVendor() { return vendor; }
    public void setVendor(User vendor) { this.vendor = vendor; }

    @Transient
    public String getAgricategory() {
        if (this instanceof Fertilizer) return "FERTILIZER";
        if (this instanceof Seeds) return "SEEDS";
        if (this instanceof Pesticide) return "PESTICIDE";
        if (this instanceof Pipe) return "PIPE";
        return null;
    }

    // ====================== JSON List<String> Helpers ======================

    /**
     * Get image URLs as List<String> – used in service layer
     */
    @Transient
    public List<String> getAgriImageUrls() {
        if (AgriimageUrl == null || AgriimageUrl.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(AgriimageUrl, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            // Log this in production, but for now return empty to avoid crash
            System.err.println("Failed to parse AgriimageUrl JSON: " + AgriimageUrl);
            return new ArrayList<>();
        }
    }

    /**
     * Set image URLs from List<String> – automatically converts to JSON
     */
    public void setAgriImageUrls(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            this.AgriimageUrl = null;
        } else {
            try {
                this.AgriimageUrl = mapper.writeValueAsString(urls);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize image URLs to JSON", e);
            }
        }
    }
    
 //----------------
    
    @Column(name = "visible_to_customers", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean visibleToCustomers = true;

    public boolean isVisibleToCustomers() {
        return visibleToCustomers;
    }

    public void setVisibleToCustomers(boolean visible) {
        this.visibleToCustomers = visible;
    }
}