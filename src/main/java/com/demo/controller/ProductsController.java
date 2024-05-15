package com.demo.controller;

import com.demo.payload.products.*;
import com.demo.service.ProductsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsService productsService;

    @GetMapping
    public ResponseEntity<ProductsResponseDTO> getAllProducts(){
        return ResponseEntity.ok(productsService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Integer id){
        return ResponseEntity.ok(productsService.getById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<GenericResponse> addProduct(@Valid @RequestBody ProductRequest productRequest){
        return ResponseEntity.ok(productsService.saveProduct(productRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<GenericResponse> updateProduct(@Valid @RequestBody ProductRequest productRequest){
        return ResponseEntity.ok(productsService.updateProduct(productRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<GenericResponse> deleteById(@PathVariable Integer id){
        return ResponseEntity.ok(productsService.deleteById(id));
    }

    //TODO add pagination
}
