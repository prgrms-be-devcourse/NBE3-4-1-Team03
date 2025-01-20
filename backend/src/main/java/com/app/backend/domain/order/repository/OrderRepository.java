package com.app.backend.domain.order.repository;

import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    Optional<Order> findByIdAndCustomer_Id(Long id, Long customerId);

    List<Order> findByCustomer_Id(Long id);

    List<Order> findByCustomer_IdAndStatus(Long id, OrderStatus status);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findAllByStatusAndCreatedDateLessThanEqual(OrderStatus status, LocalDateTime dateTime);

    boolean existsByOrderNumber(String orderNumber);

    void deleteByOrderNumber(String orderNumber);

}
