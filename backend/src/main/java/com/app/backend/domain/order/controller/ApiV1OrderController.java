package com.app.backend.domain.order.controller;

import com.app.backend.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PackageName : com.app.backend.domain.order.controller
 * FileName    : ApiV1OrderController
 * Author      : 강찬우
 * Date        : 25. 1. 14.
 * Description :
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class ApiV1OrderController {

    private final OrderService orderService;

}
