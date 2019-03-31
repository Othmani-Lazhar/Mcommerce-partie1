package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;

import com.ecommerce.microcommerce.model.PrixMargeResponseDTO;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OTHMANI Lazhar
 * on 28/03/2019
 */
@Api( description="API pour des opérations CRUD sur les produits.")
@RestController
public class ProductController {


    @Autowired
    private ProductDao productDao;

    //Récupérer la liste des produits
    @ApiOperation(value = "Récupérer la liste des produits triés par ordre alphabétique")
    @RequestMapping(value = "/Produits", method = RequestMethod.GET)
    public MappingJacksonValue listeProduits() {

        List<Product> produits = productDao.findByOrderByNomAsc();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
        produitsFiltres.setFilters(listDeNosFiltres);
        return produitsFiltres;
    }

    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value="/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id)
    {
        if (!productDao.findById(id).isPresent())
            throw new ProduitIntrouvableException("Product Not Exist with this id : "+id);

        return productDao.findById(id).get();
    }

    //ajouter un produit
    @PostMapping(value = "/Produits")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {

        if (product.getPrix()==0)
            throw new ProduitGratuitException("Le prix de vente ne doit pas vaut 0 !");

        Product productAdded =  productDao.save(product);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }


    @GetMapping(value = "test/produits/{prix}")
    public List<Product> searchByPrice(@PathVariable int prix) {

        return productDao.chercherUnProduitCher(prix);
    }


    @DeleteMapping (value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id) {

        productDao.deleteById(id);
    }


    @PutMapping (value = "/Produits")
    public void updateProduit(@Valid @RequestBody Product product) {

        if (product.getPrix()==0)
            throw new ProduitGratuitException("Le prix de vente ne doit pas vaut 0 !");
        productDao.save(product);
    }



    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère la marge du prix d'un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping (value = "/AdminProduits")
    public   List<PrixMargeResponseDTO> calculerMargeProduit() {

        List<Product> produits = productDao.findAll();

        List<PrixMargeResponseDTO> responseDTOList= produits.stream().map(product -> PrixMargeResponseDTO.builder().product(product).marge(product.getPrix()-product.getPrixAchat()).build()).collect(Collectors.toList());

        return responseDTOList;

    }


}
