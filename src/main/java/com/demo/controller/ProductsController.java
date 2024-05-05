package com.demo.controller;

import com.demo.payload.GenericResponse;
import com.demo.payload.ProductRequest;
import com.demo.payload.ProductsResponseDTO;
import com.demo.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        return productsService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id){
        return productsService.getById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<GenericResponse> addProduct(@Validated @RequestBody ProductRequest productRequest){
        return productsService.saveProduct(productRequest);
    }

    @PutMapping("/update")
    public ResponseEntity<GenericResponse> updateProduct(@Validated @RequestBody ProductRequest productRequest){
        return productsService.updateProduct(productRequest);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<GenericResponse> deleteById(@PathVariable Integer id){
        return productsService.deleteById(id);
    }
}
