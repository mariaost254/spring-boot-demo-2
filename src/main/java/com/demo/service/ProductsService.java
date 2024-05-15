package com.demo.service;

import com.demo.model.ProductEntity;
import com.demo.payload.products.*;
import com.demo.repositories.ProductsRepository;
import com.demo.utils.exceptions.ProductsServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable("products")
    public ProductsResponseDTO getAllProducts() {
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
        productsRepository.saveAll(products); //TODO - custom caching - with db and api -> fetch from db
        return ProductsResponseDTO.builder().products(products).build();
    }

    //TODO chache evict, put ...
    public GenericResponse saveProduct(ProductRequest u) {
        //TODO add dummy save to exernal api and evict cache here
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
        return new GenericResponse("Success");
    }

    public GenericResponse updateProduct(ProductRequest u) {
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
            return new GenericResponse("Success");
        }
        throw new ProductsServiceException("Product not found");
    }

    public ProductDTO getById(Integer id) {
        Optional<ProductEntity> product = productsRepository.findById(id);
        if(product.isPresent()){
            return ProductDTO.builder()
                    .id(product.get().getId())
                    .brand(product.get().getBrand())
                    .build();
        }
        throw new ProductsServiceException("Product not found");
    }

    public GenericResponse deleteById(Integer id) {
        if(productsRepository.existsById(id)){
            productsRepository.deleteById(id);
            return new GenericResponse("Success");
        }
        throw new ProductsServiceException("Product not found");
    }
}
