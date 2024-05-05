package com.demo.payload;

import com.demo.model.ProductEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductsResponseDTO {
    List<ProductEntity> products;
}
