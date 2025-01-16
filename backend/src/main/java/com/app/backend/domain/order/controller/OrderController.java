package com.app.backend.domain.order.controller;

import com.app.backend.domain.order.constant.OrderMessageConstant;
import com.app.backend.domain.order.dto.request.OrderRequest;
import com.app.backend.domain.order.dto.response.OrderResponse;
import com.app.backend.domain.order.exception.OrderException;
import com.app.backend.domain.order.service.OrderService;
import com.app.backend.global.error.exception.ErrorCode;
import com.app.backend.global.rs.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/orders",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService                orderService;
    private final ValidationAutoConfiguration validationAutoConfiguration;

    @PostMapping
    public ResponseEntity<RsData<Void>> saveOrder(@RequestBody @Valid final OrderRequest orderRequest, final
    BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new OrderException(ErrorCode.INVALID_INPUT_VALUE);

        //TODO: 스프링 시큐리티 회원 ID 추출

        long customerId = 1L;
        long orderId    = orderService.saveOrder(customerId, orderRequest);
        return ResponseEntity.ok(new RsData<>(true,
                                              String.valueOf(HttpStatus.OK.value()),
                                              OrderMessageConstant.ORDER_SAVE_SUCCESS));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RsData<OrderResponse>> getOrderById(@PathVariable("id") @Valid @Min(1) final Long id) {
        //TODO: 주문 조회 시 인증된 회원(로그인된 상태)이 본인의 주문 정보만 조회 가능하도록

        OrderResponse orderResponse = orderService.getOrderById(id);
        return ResponseEntity.ok(new RsData<>(true,
                                              String.valueOf(HttpStatus.OK.value()),
                                              OrderMessageConstant.ORDER_READ_SUCCESS,
                                              orderResponse));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RsData<Void>> cancelOrder(@PathVariable("id") @Valid @Min(1) final Long id) {
        //TODO: 주문 조회 시 인증된 회원(로그인된 상태)이 본인의 주문 정보만 취소 가능하도록

        orderService.updateOrderStatus(id, "CANCELLED");
        return ResponseEntity.ok(new RsData<>(true,
                                              String.valueOf(HttpStatus.OK.value()),
                                              OrderMessageConstant.ORDER_CANCEL_SUCCESS));
    }

}
