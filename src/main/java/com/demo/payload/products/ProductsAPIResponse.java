package com.demo.payload.products;

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
}
