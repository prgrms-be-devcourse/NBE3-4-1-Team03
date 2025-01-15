package com.app.backend.domain.product.repository;

import com.app.backend.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Long, Product> {

}
