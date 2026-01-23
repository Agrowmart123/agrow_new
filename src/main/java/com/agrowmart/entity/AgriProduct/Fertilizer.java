package com.agrowmart.entity.AgriProduct;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "agri_fertilizer")
@DiscriminatorValue("FERTILIZER")
public class Fertilizer extends BaseAgriProduct {
    
    @Column(name = "fertilizerType")
    private String fertilizerType;
    
    @Column(name = "nutrientComposition")
    private String nutrientComposition;
    
    @Column(name = "fcoNumber")
    private String fcoNumber;
 
    public String getFertilizerType() {
        return fertilizerType;
    }
    
    public void setFertilizerType(String fertilizerType) {
        this.fertilizerType = fertilizerType;
    }
 
    public String getNutrientComposition() {
        return nutrientComposition;
    }
    
    public void setNutrientComposition(String nutrientComposition) {
        this.nutrientComposition = nutrientComposition;
    }
 
    public String getFcoNumber() {
        return fcoNumber;
    }
    
    public void setFcoNumber(String fcoNumber) {
        this.fcoNumber = fcoNumber;
    }
}
