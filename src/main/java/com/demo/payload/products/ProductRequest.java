package com.demo.payload.products;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @Nullable
    private Integer id;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @NonNull
    private Double price;
    @NonNull
    private Double discountPercentage;
    @NonNull
    private Double rating;
    @NonNull
    private Integer stock;
    @NonNull
    private String brand;
    @NonNull
    private String category;
    @NonNull
    private String thumbnail;
    private List<String> images;
}
