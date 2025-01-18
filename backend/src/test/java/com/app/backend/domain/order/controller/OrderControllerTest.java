package com.app.backend.domain.order.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.app.backend.domain.order.constant.OrderMessageConstant;
import com.app.backend.domain.order.dto.request.OrderProductRequest;
import com.app.backend.domain.order.dto.request.OrderRequest;
import com.app.backend.domain.order.dto.response.OrderResponse;
import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.exception.OrderException;
import com.app.backend.domain.order.repository.OrderRepository;
import com.app.backend.domain.order.service.OrderService;
import com.app.backend.domain.user.entity.User;
import com.app.backend.global.annotation.CustomWithMockAdmin;
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
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * PackageName : com.app.backend.domain.order.controller
 * FileName    : OrderControllerTest
 * Author      : loadingKKamo21
 * Date        : 25. 1. 16.
 * Description :
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @MockitoBean
    private OrderService    orderService;
    @MockitoBean
    private OrderRepository orderRepository;
    @Autowired
    private MockMvc         mockMvc;
    @Autowired
    private ObjectMapper    objectMapper;

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

        when(orderService.saveOrder(anyLong(), any(OrderRequest.class))).thenReturn(1L);
        when(orderService.getOrderByIdAndUserId(anyLong(), anyLong())).thenReturn(orderResponse);
        doNothing().when(orderService).updateOrderStatus(anyLong(), anyString());
    }

    @Test
    @CustomWithMockAdmin
    @DisplayName("saveOrder")
    void saveOrder() throws Exception {
        //Given
        OrderRequest orderRequest = new OrderRequest(
                List.of(new OrderProductRequest(1L, 1), new OrderProductRequest(2L, 2))
        );

        //When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/orders")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                              .content(objectMapper.writeValueAsString(orderRequest)));

        //Then
        RsData<Void> rsData = new RsData<>(true,
                                           String.valueOf(HttpStatus.OK.value()),
                                           OrderMessageConstant.ORDER_SAVE_SUCCESS);

        resultActions.andExpect(status().isOk())
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @Test
    @CustomWithMockAdmin
    @DisplayName("saveOrder, unknown product id")
    void saveOrder_unknownProductId() throws Exception {
        //Given
        OrderRequest orderRequest = new OrderRequest(
                List.of(new OrderProductRequest(-1234567890L, 1), new OrderProductRequest(2L, 2))
        );

        //When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/orders")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                              .content(objectMapper.writeValueAsString(orderRequest)));

        //Then
        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        RsData<Object> rsData = new RsData<>(false,
                                             errorCode.getCode(),
                                             errorCode.getMessage());

        resultActions.andExpect(status().isBadRequest())
                     .andExpect(result -> {
                         assertThat(result.getResolvedException() instanceof OrderException).isTrue();
                         assertThat(result.getResolvedException().getMessage()).isEqualTo(errorCode.getMessage());
                     })
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @Test
    @CustomWithMockAdmin
    @DisplayName("saveOrder, unknown amount")
    void saveOrder_unknownAmount() throws Exception {
        //Given
        OrderRequest orderRequest = new OrderRequest(
                List.of(new OrderProductRequest(1L, -1), new OrderProductRequest(2L, 2))
        );

        //When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/orders")
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                              .content(objectMapper.writeValueAsString(orderRequest)));

        //Then
        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        RsData<Object> rsData = new RsData<>(false,
                                             errorCode.getCode(),
                                             errorCode.getMessage());

        resultActions.andExpect(status().isBadRequest())
                     .andExpect(result -> {
                         assertThat(result.getResolvedException() instanceof OrderException).isTrue();
                         assertThat(result.getResolvedException().getMessage()).isEqualTo(errorCode.getMessage());
                     })
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @Test
    @CustomWithMockAdmin
    @DisplayName("getOrderById")
    void getOrderById() throws Exception {
        //Given

        //When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/orders/{id}", 1)
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                              .param("id", "1"));

        //Then
        RsData<OrderResponse> rsData = new RsData<>(true,
                                                    String.valueOf(HttpStatus.OK.value()),
                                                    OrderMessageConstant.ORDER_READ_SUCCESS,
                                                    orderResponse);

        resultActions.andExpect(status().isOk())
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @Test
    @CustomWithMockAdmin
    @DisplayName("getOrderById, unknown id")
    void getOrderById_unknownId() throws Exception {
        //Given

        //When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/orders/{id}", -1234567890)
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE));

        //Then
        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        RsData<Object> rsData = new RsData<>(false,
                                             errorCode.getCode(),
                                             errorCode.getMessage());

        resultActions.andExpect(status().isBadRequest())
                     .andExpect(result -> assertThat(
                             result.getResolvedException() instanceof HandlerMethodValidationException
                     ).isTrue())
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @Test
    @CustomWithMockAdmin
    @DisplayName("cancelOrder")
    void cancelOrder() throws Exception {
        //Given

        //When
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/orders/{id}", 1)
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE));

        //Then
        RsData<OrderResponse> rsData = new RsData<>(true,
                                                    String.valueOf(HttpStatus.OK.value()),
                                                    OrderMessageConstant.ORDER_CANCEL_SUCCESS);

        resultActions.andExpect(status().isOk())
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @Test
    @CustomWithMockAdmin
    @DisplayName("cancelOrder, unknown id")
    void cancelOrder_unknownId() throws Exception {
        //Given

        //When
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/orders/{id}", -1234567890)
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE));

        //Then
        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        RsData<Object> rsData = new RsData<>(false,
                                             errorCode.getCode(),
                                             errorCode.getMessage());

        resultActions.andExpect(status().isBadRequest())
                     .andExpect(result -> assertThat(
                             result.getResolvedException() instanceof HandlerMethodValidationException
                     ).isTrue())
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

}