package com.agrowmart.entity.AgriProduct;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "agri_pesticide")
@DiscriminatorValue("PESTICIDE")
public class Pesticide extends BaseAgriProduct {
    
    @Column(name = "Pesticidetype")
    private String Pesticidetype;
    
    @Column(name = "PesticideactiveIngredient")
    private String PesticideactiveIngredient;
    
    @Column(name = "Pesticidetoxicity")
    private String Pesticidetoxicity;
    
    @Column(name = "PesticidecibrcNumber")
    private String PesticidecibrcNumber;
    
    @Column(name = "Pesticideformulation")
    private String Pesticideformulation;
 
    public String getPesticidetype() {
        return Pesticidetype;
    }
    
    public void setPesticidetype(String Pesticidetype) {
        this.Pesticidetype = Pesticidetype;
    }
 
    public String getPesticideactiveIngredient() {
        return PesticideactiveIngredient;
    }
    
    public void setPesticideactiveIngredient(String PesticideactiveIngredient) {
        this.PesticideactiveIngredient = PesticideactiveIngredient;
    }
 
    public String getPesticidetoxicity() {
        return Pesticidetoxicity;
    }
    
    public void setPesticidetoxicity(String Pesticidetoxicity) {
        this.Pesticidetoxicity = Pesticidetoxicity;
    }
 
    public String getPesticidecibrcNumber() {
        return PesticidecibrcNumber;
    }
    
    public void setPesticidecibrcNumber(String PesticidecibrcNumber) {
        this.PesticidecibrcNumber = PesticidecibrcNumber;
    }
 
    public String getPesticideformulation() {
        return Pesticideformulation;
    }
    
    public void setPesticideformulation(String Pesticideformulation) {
        this.Pesticideformulation = Pesticideformulation;
    }
}
