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
import com.app.backend.domain.order.repository.OrderRepository;
import com.app.backend.domain.order.service.OrderService;
import com.app.backend.domain.user.entity.User;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * PackageName : com.app.backend.domain.order.controller
 * FileName    : OrderControllerTest
 * Author      : 강찬우
 * Date        : 25. 1. 16.
 * Description :
 */
//@MockBean(JpaMetamodelMappingContext.class)
//NOTE: @MockBean(JpaMetamodelMappingContext.class) -> Spring Boot 3.4.0 이후 사용되지 않음, 제거 예정
//@EnableJpaAuditing을 Application에 적용 시 JPA metamodel 에러가 발생하며, 해결을 위해 @EnableJpaAuditing을 따로 Config 클래스로
//분리 후 적용 필요
//@Import(TestConfig.class)
@WebMvcTest(OrderController.class)
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
        when(orderService.getOrderById(anyLong())).thenReturn(orderResponse);
        doNothing().when(orderService).updateOrderStatus(anyLong(), anyString());
    }

    //TODO: 시큐리티 내 인증 객체 확인 후 로직 수행 테스트 필요, 추후 회원 도메인 설계 확인 후 시큐리티 항목 추가

    @Test
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
                     .andExpect(result -> assertThat(
                             result.getResolvedException() instanceof MethodArgumentNotValidException)
                     )
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @Test
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
                     .andExpect(result -> assertThat(
                             result.getResolvedException() instanceof MethodArgumentNotValidException)
                     )
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @Test
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
                             result.getResolvedException() instanceof HandlerMethodValidationException)
                     )
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @Test
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
                             result.getResolvedException() instanceof HandlerMethodValidationException)
                     )
                     .andExpect(content().json(objectMapper.writeValueAsString(rsData)))
                     .andDo(print());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public Validator validator() {
            return new LocalValidatorFactoryBean();
        }

        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }

    }

}