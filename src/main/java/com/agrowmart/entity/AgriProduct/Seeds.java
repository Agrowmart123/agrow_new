package com.agrowmart.entity.AgriProduct;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "agri_seeds")
@DiscriminatorValue("SEEDS")
public class Seeds extends BaseAgriProduct {
    
    @Column(name = "SeedscropType")
    private String SeedscropType;
    
    @Column(name = "Seedsvariety")
    private String Seedsvariety;
    
    @Column(name = "seedClass")
    private String seedClass;
    
    @Column(name = "SeedsgerminationPercentage")
    private Double SeedsgerminationPercentage;
    
    @Column(name = "SeedsphysicalPurityPercentage")
    private Double SeedsphysicalPurityPercentage;
    
    @Column(name = "SeedslotNumber")
    private String SeedslotNumber;
 
    public String getSeedscropType() {
        return SeedscropType;
    }
    
    public void setSeedscropType(String SeedscropType) {
        this.SeedscropType = SeedscropType;
    }
 
    public String getSeedsvariety() {
        return Seedsvariety;
    }
    
    public void setSeedsvariety(String Seedsvariety) {
        this.Seedsvariety = Seedsvariety;
    }
 
    public String getSeedClass() {
        return seedClass;
    }
    
    public void setSeedClass(String seedClass) {
        this.seedClass = seedClass;
    }
 
    public Double getSeedsgerminationPercentage() {
        return SeedsgerminationPercentage;
    }
    
    public void setSeedsgerminationPercentage(Double SeedsgerminationPercentage) {
        this.SeedsgerminationPercentage = SeedsgerminationPercentage;
    }
 
    public Double getSeedsphysicalPurityPercentage() {
        return SeedsphysicalPurityPercentage;
    }
    
    public void setSeedsphysicalPurityPercentage(Double SeedsphysicalPurityPercentage) {
        this.SeedsphysicalPurityPercentage = SeedsphysicalPurityPercentage;
    }
 
    public String getSeedslotNumber() {
        return SeedslotNumber;
    }
    
    public void setSeedslotNumber(String SeedslotNumber) {
        this.SeedslotNumber = SeedslotNumber;
    }
}
