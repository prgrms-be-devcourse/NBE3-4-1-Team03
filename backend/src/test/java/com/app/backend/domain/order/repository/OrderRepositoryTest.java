package com.app.backend.domain.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.entity.OrderStatus;
import com.app.backend.global.annotation.RepositoryTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;

/**
 * PackageName : com.app.backend.domain.order.repository
 * FileName    : OrderRepositoryTest
 * Author      : 강찬우
 * Date        : 25. 1. 15.
 * Description :
 */
@ActiveProfiles("test")
@RepositoryTest
@SqlGroup({
        @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS,
             scripts = {"classpath:/db/insert_user.sql", "classpath:/db/insert_order.sql"}),
        @Sql(executionPhase = ExecutionPhase.AFTER_TEST_CLASS,
             scripts = {"classpath:/db/delete_order.sql", "classpath:/db/delete_user.sql"})
})
class OrderRepositoryTest {

    @Autowired
    private OrderRepository   orderRepository;
    @Autowired
    private TestEntityManager em;

    @AfterEach
    void afterEach() {
        em.flush();
        em.clear();
    }

    @ParameterizedTest
    @CsvSource({"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"})
    @DisplayName("findById")
    void findById(final long orderId) {
        //Given

        //When
        Optional<Order> opOrder = orderRepository.findById(orderId);

        //Then
        assertThat(opOrder).isNotEmpty();
        assertThat(opOrder.get().getId()).isEqualTo(orderId);
    }

    @ParameterizedTest
    @CsvSource({"11", "12", "13", "14", "15", "16", "17", "18", "19", "20"})
    @DisplayName("findById, unknown order id")
    void findById_unknownId(final long orderId) {
        //Given

        //When
        Optional<Order> opOrder = orderRepository.findById(orderId);

        //Then
        assertThat(opOrder).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"1", "2", "3", "4", "5"})
    @DisplayName("findByCustomer_Id")
    void findByCustomer_Id(final long userId) {
        //Given

        //When
        List<Order> orders = orderRepository.findByCustomer_Id(userId);

        //Then
        assertThat(orders).hasSize(2);
        assertThat(orders.stream().allMatch(o -> o.getCustomer().getId() == userId)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"6", "7", "8", "9", "10"})
    @DisplayName("findByCustomer_Id, unknown customer id")
    void findByCustomer_Id_unknownCustomerId(final long userId) {
        //Given

        //When
        List<Order> orders = orderRepository.findByCustomer_Id(userId);

        //Then
        assertThat(orders).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"1, ORDERED", "2, DELIVERED", "3, ORDERED", "4, CANCELLED", "5, SHIPPED"})
    @DisplayName("findByCustomer_IdAndStatus")
    void findByCustomer_IdAndStatus(final long userId, final OrderStatus status) {
        //Given

        //When
        List<Order> orders = orderRepository.findByCustomer_IdAndStatus(userId, status);

        //Then
        assertThat(orders).hasSize(1);
    }

    @ParameterizedTest
    @CsvSource({"6, ORDERED", "7, DELIVERED", "8, ORDERED", "9, CANCELLED", "10, SHIPPED"})
    @DisplayName("findByCustomer_IdAndStatus, unknown customer id")
    void findByCustomer_IdAndStatus_unknownId(final long userId, final OrderStatus status) {
        //Given

        //When
        List<Order> orders = orderRepository.findByCustomer_IdAndStatus(userId, status);

        //Then
        assertThat(orders).isEmpty();
    }

    @ParameterizedTest
    @CsvSource(
            {"20250101ABCDE1", "20250101ABCDE2", "20250101ABCDE3", "20250101ABCDE4", "20250101ABCDE5", "20250101ABCDE6",
             "20250101ABCDE7", "20250101ABCDE8", "20250101ABCDE9", "20250101ABCDE10"})
    @DisplayName("findByOrderNumber")
    void findByOrderNumber(final String orderNumber) {
        //Given

        //When
        Optional<Order> opOrder = orderRepository.findByOrderNumber(orderNumber);

        //Then
        assertThat(opOrder).isNotEmpty();
        assertThat(opOrder.get().getOrderNumber()).isEqualTo(orderNumber);
    }

    @ParameterizedTest
    @CsvSource({"UNKNOWN20250101ABCDE1", "UNKNOWN20250101ABCDE2", "UNKNOWN20250101ABCDE3", "UNKNOWN20250101ABCDE4",
                "UNKNOWN20250101ABCDE5", "UNKNOWN20250101ABCDE6", "UNKNOWN20250101ABCDE7", "UNKNOWN20250101ABCDE8",
                "UNKNOWN20250101ABCDE9", "UNKNOWN20250101ABCDE10"})
    @DisplayName("findByOrderNumber, unknown order number")
    void findByOrderNumber_unknownOrderNumber(final String orderNumber) {
        //Given

        //When
        Optional<Order> opOrder = orderRepository.findByOrderNumber(orderNumber);

        //Then
        assertThat(opOrder).isEmpty();
    }

    @ParameterizedTest
    @CsvSource(
            {"20250101ABCDE1", "20250101ABCDE2", "20250101ABCDE3", "20250101ABCDE4", "20250101ABCDE5", "20250101ABCDE6",
             "20250101ABCDE7", "20250101ABCDE8", "20250101ABCDE9", "20250101ABCDE10"})
    @DisplayName("existsByOrderNumber")
    void existsByOrderNumber(final String orderNumber) {
        //Given

        //When
        boolean flag = orderRepository.existsByOrderNumber(orderNumber);

        //Then
        assertThat(flag).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"UNKNOWN20250101ABCDE1", "UNKNOWN20250101ABCDE2", "UNKNOWN20250101ABCDE3", "UNKNOWN20250101ABCDE4",
                "UNKNOWN20250101ABCDE5", "UNKNOWN20250101ABCDE6", "UNKNOWN20250101ABCDE7", "UNKNOWN20250101ABCDE8",
                "UNKNOWN20250101ABCDE9", "UNKNOWN20250101ABCDE10"})
    @DisplayName("existsByOrderNumber, unknown order number")
    void existsByOrderNumber_unknownOrderNumber(final String orderNumber) {
        //Given

        //When
        boolean flag = orderRepository.existsByOrderNumber(orderNumber);

        //Then
        assertThat(flag).isFalse();
    }

    @ParameterizedTest
    @CsvSource(
            {"20250101ABCDE1", "20250101ABCDE2", "20250101ABCDE3", "20250101ABCDE4", "20250101ABCDE5", "20250101ABCDE6",
             "20250101ABCDE7", "20250101ABCDE8", "20250101ABCDE9", "20250101ABCDE10"})
    @DisplayName("deleteByOrderNumber")
    void deleteByOrderNumber(final String orderNumber) {
        //Given

        //When
        orderRepository.deleteByOrderNumber(orderNumber);

        //Then
        Long  orderId = Long.valueOf(orderNumber.substring("20250101ABCDE".length()));
        Order order   = em.find(Order.class, orderId);

        assertThat(order).isNull();
    }

    @ParameterizedTest
    @CsvSource({"UNKNOWN20250101ABCDE1", "UNKNOWN20250101ABCDE2", "UNKNOWN20250101ABCDE3", "UNKNOWN20250101ABCDE4",
                "UNKNOWN20250101ABCDE5", "UNKNOWN20250101ABCDE6", "UNKNOWN20250101ABCDE7", "UNKNOWN20250101ABCDE8",
                "UNKNOWN20250101ABCDE9", "UNKNOWN20250101ABCDE10"})
    @DisplayName("deleteByOrderNumber, unknown order number")
    void deleteByOrderNumber_unknownOrderNumber(final String orderNumber) {
        //Given

        //When
        orderRepository.deleteByOrderNumber(orderNumber);

        //Then
        Long  orderId = Long.valueOf(orderNumber.substring("UNKNOWN20250101ABCDE".length()));
        Order order   = em.find(Order.class, orderId);

        assertThat(order).isNotNull();
    }

}