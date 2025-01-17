package com.app.backend.domain.user.controller;

import com.app.backend.domain.order.constant.OrderMessageConstant;
import com.app.backend.domain.order.dto.response.OrderResponse;
import com.app.backend.domain.order.service.OrderService;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.global.error.exception.ErrorCode;
import com.app.backend.global.rs.RsData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PackageName : com.app.backend.domain.user.controller
 * FileName    : AdminController
 * Author      : 강찬우
 * Date        : 25. 1. 17.
 * Description :
 */
@RestController
@RequestMapping(value = "/api/v1/admin",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public RsData<List<OrderResponse>> getAllOrders(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("userDetails = " + userDetails);
        if (!userDetails.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")))   //관리자 권한 확인
            throw new UserException(ErrorCode.HANDLE_ACCESS_DENIED);

        List<OrderResponse> allOrders = orderService.getAllOrders();

        return new RsData<>(true,
                            String.valueOf(HttpStatus.OK.value()),
                            OrderMessageConstant.ORDER_LIST_READ_SUCCESS,
                            allOrders);
    }

}
