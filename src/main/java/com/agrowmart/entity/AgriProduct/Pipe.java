package com.agrowmart.entity.AgriProduct;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "agri_pipe")
@DiscriminatorValue("PIPE")
public class Pipe extends BaseAgriProduct {
    
    @Column(name = "Pipetype")
    private String Pipetype;
    
    @Column(name = "Pipesize")
    private String Pipesize;
    
    @Column(name = "Pipelength")
    private Double Pipelength;
    
    @Column(name = "PipebisNumber")
    private String PipebisNumber;
 
    public String getPipetype() {
        return Pipetype;
    }
    
    public void setPipetype(String Pipetype) {
        this.Pipetype = Pipetype;
    }
 
    public String getPipesize() {
        return Pipesize;
    }
    
    public void setPipesize(String Pipesize) {
        this.Pipesize = Pipesize;
    }
 
    public Double getPipelength() {
        return Pipelength;
    }
    
    public void setPipelength(Double Pipelength) {
        this.Pipelength = Pipelength;
    }
 
    public String getPipebisNumber() {
        return PipebisNumber;
    }
    
    public void setPipebisNumber(String PipebisNumber) {
        this.PipebisNumber = PipebisNumber;
    }
}
