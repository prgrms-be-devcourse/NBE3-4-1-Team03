package com.app.backend.domain.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.app.backend.domain.order.entity.OrderProduct;
import com.app.backend.global.annotation.RepositoryTest;
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
 * FileName    : OrderProductRepositoryTest
 * Author      : 강찬우
 * Date        : 25. 1. 15.
 * Description :
 */
@ActiveProfiles("test")
@RepositoryTest
@SqlGroup({
        @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS,
             scripts = {"classpath:/db/insert_user.sql", "classpath:/db/insert_product.sql",
                        "classpath:/db/insert_order.sql", "classpath:/db/insert_order_product.sql"}),
        @Sql(executionPhase = ExecutionPhase.AFTER_TEST_CLASS,
             scripts = {"classpath:/db/delete_order_product.sql", "classpath:/db/delete_order.sql",
                        "classpath:/db/delete_product.sql", "classpath:/db/delete_user.sql"})
})
class OrderProductRepositoryTest {

    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private TestEntityManager      em;

    @AfterEach
    void afterEach() {
        em.flush();
        em.clear();
    }

    @ParameterizedTest
    @CsvSource({"1", "2", "3", "4", "5"})
    @DisplayName("findById")
    void findById(final long orderProductId) {
        //Given

        //When
        Optional<OrderProduct> opOrderProduct = orderProductRepository.findById(orderProductId);

        //Then
        assertThat(opOrderProduct).isNotEmpty();
    }

    @ParameterizedTest
    @CsvSource({"6", "7", "8", "9", "10"})
    @DisplayName("findById")
    void findById_unknownId(final long orderProductId) {
        //Given

        //When
        Optional<OrderProduct> opOrderProduct = orderProductRepository.findById(orderProductId);

        //Then
        assertThat(opOrderProduct).isEmpty();
    }

}