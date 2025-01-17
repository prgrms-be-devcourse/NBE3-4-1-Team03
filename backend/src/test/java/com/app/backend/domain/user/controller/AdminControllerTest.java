package com.app.backend.domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.app.backend.domain.order.constant.OrderMessageConstant;
import com.app.backend.domain.order.dto.response.OrderResponse;
import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.service.OrderService;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.global.error.exception.ErrorCode;
import com.app.backend.global.rs.RsData;
import com.app.backend.global.util.ReflectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * PackageName : com.app.backend.domain.user.controller
 * FileName    : AdminControllerTest
 * Author      : 강찬우
 * Date        : 25. 1. 17.
 * Description :
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @MockitoBean
    private OrderService orderService;
    @Autowired
    private MockMvc      mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private OrderResponse orderResponse;

    @BeforeEach
    void beforeEach() {
        User customer = User.builder()
                            .email("user@mail.com")
                            .password("user")
                            .name("user")
                            .address("user address")
                            .detailAddress("user detail address")
                            .phone("01000000000")
                            .status("ACTIVATE")
                            .role("ROLE_USER")
                            .build();

        Order order = Order.of(customer, "orderNumber", 1, BigDecimal.valueOf(10000.00),
                               "%s %s".formatted(customer.getAddress(), customer.getDetailAddress()));
        ReflectionUtil.setPrivateFieldValue(Order.class, order, "createdDate", LocalDateTime.now());

        orderResponse = OrderResponse.of(order);

        when(orderService.getAllOrders()).thenReturn(List.of(orderResponse));
    }

    @Test
    @DisplayName("getAllOrders")
    void getAllOrders() throws Exception {
        //Given

        //When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/admin/orders")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                              .with(user("admin").roles("ADMIN")));

        //Then
        RsData<Object> rsData = new RsData<>(true,
                                             String.valueOf(HttpStatus.OK.value()),
                                             OrderMessageConstant.ORDER_LIST_READ_SUCCESS,
                                             List.of(orderResponse));

        resultActions.andExpect(status().isOk())
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @Test
    @DisplayName("getAllOrders, unauthorized")
    void getAllOrders_unauthorized() throws Exception {
        //Given

        //When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/admin/orders")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                              .with(user("user").roles("USER")));

        //Then
        resultActions.andExpect(status().isForbidden())
                     .andExpect(result -> {
                         assertThat(result.getResolvedException() instanceof UserException).isTrue();
                         final ErrorCode errorCode = ErrorCode.HANDLE_ACCESS_DENIED;
                         assertThat(((UserException) result.getResolvedException()).getErrorCode())
                                 .isEqualTo(errorCode);
                         assertThat(result.getResolvedException().getMessage()).isEqualTo(errorCode.getMessage());
                     })
                     .andDo(print());
    }

    @Test
    @DisplayName("getAllOrders, no authentication")
    void getAllOrders_noAuthentication() throws Exception {
        //Given

        //When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/admin/orders")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE));

        //Then
        resultActions.andExpect(status().isBadRequest())
                     .andDo(print());
    }

}