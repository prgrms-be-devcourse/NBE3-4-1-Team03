package com.app.backend.domain.order.service;

import com.app.backend.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackageName : com.app.backend.domain.order.service
 * FileName    : OrderService
 * Author      : 강찬우
 * Date        : 25. 1. 14.
 * Description :
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

//    public OrderResponse getOrderById(final long id) {
//        Order order = orderRepository.findById(id)
//                                     .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));
//        return from(order);
//    }

    //==================== 내부 메서드 ====================//

//    private static Order toEntity(final OrderRequest dto) {
//        return Order.of(null, null, 0, null, null, null);
//    }
//
//    private static OrderResponse from(final Order order) {
//        return OrderResponse.from(order);
//    }

}
