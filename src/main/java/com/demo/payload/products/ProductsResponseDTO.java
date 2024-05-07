package com.demo.payload.products;

import com.demo.model.ProductEntity;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductsResponseDTO implements Serializable {
    List<ProductEntity> products;
}
