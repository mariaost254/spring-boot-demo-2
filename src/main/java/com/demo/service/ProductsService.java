package com.demo.service;

import com.demo.model.ProductEntity;
import com.demo.payload.products.*;
import com.demo.repositories.ProductsRepository;
import com.demo.utils.exceptions.ProductsServiceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private final String PRODUCTS_URL = "https://dummyjson.com/products/";

    private static final String PRODUCT_CACHE_PREFIX = "product:";

    private static final long CACHE_TIMEOUT = 10;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private Lock registryLock;

    @Autowired
    private final RedisTemplate<String, ProductEntity> redisTemplateProducts;

    public ProductDTO getById(Integer id) {
        ProductEntity product = getProductById(id);
        if(product == null){
            ProductDTO u = restTemplate.getForObject(PRODUCTS_URL+id.toString(), ProductDTO.class);
            if(u == null){
                throw new ProductsServiceException("Product not found");
            }
            product = ProductEntity.builder()
                    .id(u.getId())
                    .brand(u.getBrand())
                    .category(u.getCategory())
                    .description(u.getDescription())
                    .discountPercentage(u.getDiscountPercentage())
                    .price(u.getPrice())
                    .rating(u.getRating())
                    .stock(u.getStock())
                    .thumbnail(u.getThumbnail())
                    .title(u.getTitle())
                    .build();
            ProductEntity saved = productsRepository.save(product);
            cacheProduct(saved);
        }

        return ProductDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPercentage(product.getDiscountPercentage())
                .rating(product.getRating())
                .stock(product.getStock())
                .brand(product.getBrand())
                .category(product.getCategory())
                .thumbnail(product.getThumbnail())
                .build();
    }

    @Cacheable("products")
    @Transactional
    public ProductsResponseDTO getAllProducts(){
        List<ProductEntity> products = new ArrayList<>();
        products = productsRepository.findAll();
        if(products.isEmpty() || products.size() > Objects.requireNonNull(redisTemplateProducts.opsForList().size("products"))) {
            registryLock.lock();
            try {
            ProductsAPIResponse result = restTemplate.getForObject(PRODUCTS_URL, ProductsAPIResponse.class);
            if (result != null) {
                products = result.getProducts()
                        .stream()
                        .filter(d -> getProductById(d.getId()) == null)
                        .map(u -> ProductEntity.builder()
                                .id(u.getId())
                                .brand(u.getBrand())
                                .category(u.getCategory())
                                .description(u.getDescription())
                                .discountPercentage(u.getDiscountPercentage())
                                .price(u.getPrice())
                                .rating(u.getRating())
                                .stock(u.getStock())
                                .thumbnail(u.getThumbnail())
                                .title(u.getTitle())
                                .build())
                        .collect(Collectors.toList());
            }
            productsRepository.saveAll(products);
            products.forEach(this::cacheProduct);
            } finally {
                registryLock.unlock();
            }
        }
        return ProductsResponseDTO.builder().products(productsRepository.findAll()).build();
    }


    public GenericResponse saveProduct(ProductRequest u) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductRequest> request = new HttpEntity<>(u, headers);
        ResponseEntity<Object> response = restTemplate.exchange(PRODUCTS_URL+"add", HttpMethod.POST, request, Object.class);
        if(response.getStatusCode().equals(HttpStatus.OK)) {
            ProductEntity productEntity = ProductEntity.builder()
                    .brand(u.getBrand())
                    .category(u.getCategory())
                    .description(u.getDescription())
                    .discountPercentage(u.getDiscountPercentage())
                    .price(u.getPrice())
                    .rating(u.getRating())
                    .stock(u.getStock())
                    .thumbnail(u.getThumbnail())
                    .title(u.getTitle())
                    .build();
            ProductEntity saved = productsRepository.save(productEntity);
            cacheProduct(saved);
            return new GenericResponse("Success");
        }
        throw new ProductsServiceException("Product not found");
    }


    public GenericResponse updateProduct(ProductRequest u) {
        Optional<ProductEntity> productOptional = productsRepository.findById(u.getId());
        if (productOptional.isPresent()) {
            ProductEntity productEntity = productOptional.get();
            productEntity.setBrand(u.getBrand());
            productEntity.setCategory(u.getCategory());
            productEntity.setDescription(u.getDescription());
            productEntity.setDiscountPercentage(u.getDiscountPercentage());
            productEntity.setPrice(u.getPrice());
            productEntity.setRating(u.getRating());
            productEntity.setStock(u.getStock());
            productEntity.setThumbnail(u.getThumbnail());
            productEntity.setTitle(u.getTitle());
            productsRepository.save(productEntity);
            cacheProduct(productEntity);
            return new GenericResponse("Success");
        }
        throw new ProductsServiceException("Product not found");
    }

    public GenericResponse deleteById(Integer id) {
        if(productsRepository.existsById(id)){
            productsRepository.deleteById(id);
            evictProductCache(id);
            return new GenericResponse("Success");
        }
        throw new ProductsServiceException("Product not found");
    }

    private ProductEntity getProductById(Integer id) {
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        ProductEntity cachedProduct = redisTemplateProducts.opsForValue().get(cacheKey);

        if (cachedProduct != null) {
            return cachedProduct;
        }

        ProductEntity product = productsRepository.findById(id).orElse(null);
        if (product != null) {
            redisTemplateProducts.opsForValue().set(cacheKey, product, 10, TimeUnit.MINUTES);
        }
        return product;
    }

    private void cacheProduct(ProductEntity product) {
        String cacheKey = PRODUCT_CACHE_PREFIX + product.getId();
        redisTemplateProducts.opsForValue().set(cacheKey, product, 10, TimeUnit.MINUTES);
    }

    private void evictProductCache(Integer id) {
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        redisTemplateProducts.delete(cacheKey);
    }
}
