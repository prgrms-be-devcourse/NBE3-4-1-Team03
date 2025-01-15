package com.app.backend.domain.order.repository;

import com.app.backend.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName : com.app.backend.domain.order.repository
 * FileName    : OrderRepository
 * Author      : 강찬우
 * Date        : 25. 1. 14.
 * Description :
 */
public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
}
