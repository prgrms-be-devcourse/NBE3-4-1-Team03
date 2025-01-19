package com.app.backend.domain.order.repository;

import com.app.backend.domain.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName : com.app.backend.domain.order.repository
 * FileName    : OrderProductRepository
 * Author      : loadingKKamo21
 * Date        : 25. 1. 15.
 * Description :
 */
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long>, OrderProductRepositoryCustom {
}
