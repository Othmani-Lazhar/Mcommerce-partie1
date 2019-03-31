package com.ecommerce.microcommerce.model;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
//@JsonIgnoreProperties(value = {"prixAchat"})
//@JsonFilter("monFiltreDynamique")
@Entity
public class Product {

    @Id
    @GeneratedValue
    private int id;

    @Length(min=3, max=20, message = "Nom trop long ou trop court. Et oui messages sont plus stylés que ceux de Spring" )
    private String nom;

    private int prix;
    //information que nous ne souhaitons pas exposer
    private int prixAchat;

    @Transient
    private int marge;


    @PostLoad
    public void calculmarge(){
        this.marge=this.prix-this.prixAchat;
    }
}