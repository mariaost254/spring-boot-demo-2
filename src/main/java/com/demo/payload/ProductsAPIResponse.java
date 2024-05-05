package com.demo.payload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductsAPIResponse {
    private List<ProductDTO> products;
    private int total;
    private int skip;
    private int limit;

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ProductDTO{
        private int id;
        private String title;
        private String description;
        private double price;
        private double discountPercentage;
        private double rating;
        private int stock;
        private String brand;
        private String category;
        private String thumbnail;
        private List<String> images;
    }
}
