package com.demo.service;

import com.demo.model.ProductEntity;
import com.demo.payload.GenericResponse;
import com.demo.payload.ProductRequest;
import com.demo.payload.ProductsAPIResponse;
import com.demo.payload.ProductsResponseDTO;
import com.demo.repositories.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private final String PRODUCTS_URL = "https://dummyjson.com/products/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductsRepository productsRepository;

    public ResponseEntity<ProductsResponseDTO> getAllProducts() {
        RestTemplate restTemplate = new RestTemplate();
        ProductsAPIResponse result = restTemplate.getForObject(PRODUCTS_URL, ProductsAPIResponse.class);
        List<ProductEntity> products = new ArrayList<>();
        if(result != null) {
            products = result.getProducts()
                    .stream()
                    .map(u -> ProductEntity.builder()
                            .id(u.getId())
                            .brand(u.getBrand())
                            .category(u.getCategory())
                            .description(u.getDescription())
                            .discountPercentage(u.getDiscountPercentage())
                            .images(u.getImages())
                            .price(u.getPrice())
                            .rating(u.getRating())
                            .stock(u.getStock())
                            .thumbnail(u.getThumbnail())
                            .title(u.getTitle())
                            .build())
                    .collect(Collectors.toList());
        }
        productsRepository.saveAll(products); //move to AOP later
        return new ResponseEntity<ProductsResponseDTO>(ProductsResponseDTO.builder()
                .products(products)
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<GenericResponse> saveProduct(ProductRequest u) {
        ProductEntity productEntity = ProductEntity.builder()
                .brand(u.getBrand())
                .category(u.getCategory())
                .description(u.getDescription())
                .discountPercentage(u.getDiscountPercentage())
                .images(u.getImages())
                .price(u.getPrice())
                .rating(u.getRating())
                .stock(u.getStock())
                .thumbnail(u.getThumbnail())
                .title(u.getTitle())
                .build();
        productsRepository.save(productEntity);
        return new ResponseEntity<GenericResponse>(GenericResponse.builder().msg("Success").build(), HttpStatus.OK);
    }

    public ResponseEntity<GenericResponse> updateProduct(ProductRequest u) {
        Optional<ProductEntity> productOptional = productsRepository.findById(u.getId());
        if (productOptional.isPresent()) {
            ProductEntity productEntity = productOptional.get();
            productEntity.setBrand(u.getBrand());
            productEntity.setCategory(u.getCategory());
            productEntity.setDescription(u.getDescription());
            productEntity.setDiscountPercentage(u.getDiscountPercentage());
            productEntity.setImages(u.getImages());
            productEntity.setPrice(u.getPrice());
            productEntity.setRating(u.getRating());
            productEntity.setStock(u.getStock());
            productEntity.setThumbnail(u.getThumbnail());
            productEntity.setTitle(u.getTitle());
            productsRepository.save(productEntity);
            return ResponseEntity.ok(GenericResponse.builder().msg("Success").build());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().msg("Product not found").build());
    }

    public ResponseEntity<?> getById(Integer id) {
        Optional<ProductEntity> product = productsRepository.findById(id);
        if(product.isPresent()){
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(GenericResponse.builder().msg("Success").build(), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<GenericResponse> deleteById(Integer id) {
        if(productsRepository.existsById(id)){
            productsRepository.deleteById(id);
            return ResponseEntity.ok(GenericResponse.builder().msg("Success").build());
        }
        return new ResponseEntity<>(GenericResponse.builder().msg("Product not found").build(), HttpStatus.NOT_FOUND);
    }
}
