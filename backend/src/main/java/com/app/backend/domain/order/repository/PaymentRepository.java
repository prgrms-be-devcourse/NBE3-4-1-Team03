package com.app.backend.domain.order.repository;

import com.app.backend.domain.order.entity.Payment;
import com.app.backend.domain.order.entity.PaymentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName : com.app.backend.domain.order.repository
 * FileName    : PaymentRepository
 * Author      : 강찬우
 * Date        : 25. 1. 17.
 * Description :
 */
public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {

    Optional<Payment> findByPaymentUid(String paymentUid);

    Optional<Payment> findByOrder_Id(Long id);

    List<Payment> findByCustomer_Id(Long id);

    List<Payment> findByCustomer_IdAndStatus(Long id, PaymentStatus status);

    boolean existsByPaymentUid(String paymentUid);

    void deleteByPaymentUid(String paymentUid);

}
