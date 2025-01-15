package com.app.backend.domain.product.repository;

import com.app.backend.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Page<Product> findByOrderByCreatedDateDesc(Pageable pageable);

    Page<Product> findByOrderByPriceDesc(Pageable pageable);

    Page<Product> findByOrderByNameDesc(Pageable pageable);
}
