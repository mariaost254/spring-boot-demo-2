package com.demo.payload.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProductDTO implements Serializable {
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
