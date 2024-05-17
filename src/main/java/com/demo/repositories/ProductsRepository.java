package com.demo.repositories;

import com.demo.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

@Repository
public interface ProductsRepository extends JpaRepository<ProductEntity, Integer>,
        PagingAndSortingRepository<ProductEntity, Integer> {
}
