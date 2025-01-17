package com.app.backend.domain.order.controller;

import com.app.backend.domain.order.constant.OrderMessageConstant;
import com.app.backend.domain.order.dto.request.OrderRequest;
import com.app.backend.domain.order.dto.response.OrderResponse;
import com.app.backend.domain.order.exception.OrderException;
import com.app.backend.domain.order.service.OrderService;
import com.app.backend.global.error.exception.ErrorCode;
import com.app.backend.global.rs.RsData;
import com.app.backend.global.security.user.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    private final OrderService orderService;

    @PostMapping
    public RsData<Void> saveOrder(@RequestBody @Valid final OrderRequest orderRequest,
                                  final BindingResult bindingResult,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors())
            throw new OrderException(ErrorCode.INVALID_INPUT_VALUE);

        long userId  = ((CustomUserDetails) userDetails).getUser().getId();
        long orderId = orderService.saveOrder(userId, orderRequest);

        return new RsData<>(true,
                            String.valueOf(HttpStatus.OK.value()),
                            OrderMessageConstant.ORDER_SAVE_SUCCESS);
    }

    @GetMapping("/{id}")
    public RsData<OrderResponse> getOrderById(@PathVariable("id") @Min(1) final Long id,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        long userId = ((CustomUserDetails) userDetails).getUser().getId();

        OrderResponse orderResponse = orderService.getOrderByIdAndUserId(id, userId);

        return new RsData<>(true,
                            String.valueOf(HttpStatus.OK.value()),
                            OrderMessageConstant.ORDER_READ_SUCCESS,
                            orderResponse);
    }

    @PatchMapping("/{id}")
    public RsData<Void> cancelOrder(@PathVariable("id") @Min(1) final Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        long userId = ((CustomUserDetails) userDetails).getUser().getId();

        orderService.updateOrderStatusByUserId(id, userId, "CANCELLED");

        return new RsData<>(true,
                            String.valueOf(HttpStatus.OK.value()),
                            OrderMessageConstant.ORDER_CANCEL_SUCCESS);
    }

}
