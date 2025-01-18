package com.app.backend.domain.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.app.backend.domain.order.entity.Payment;
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
 * FileName    : PaymentRepositoryTest
 * Author      : 강찬우
 * Date        : 25. 1. 18.
 * Description :
 */
@ActiveProfiles("test")
@RepositoryTest
@SqlGroup({
        @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS,
             scripts = {"classpath:/db/insert_user.sql", "classpath:/db/insert_order.sql",
                        "classpath:/db/insert_payment.sql"}),
        @Sql(executionPhase = ExecutionPhase.AFTER_TEST_CLASS,
             scripts = {"classpath:/db/delete_payment.sql", "classpath:/db/delete_order.sql",
                        "classpath:/db/delete_user.sql"})
})
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;
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
    void findById(final long paymentId) {
        //Given

        //When
        Optional<Payment> opPayment = paymentRepository.findById(paymentId);

        //Then
        assertThat(opPayment).isNotEmpty();
    }

    @ParameterizedTest
    @CsvSource({"11", "12", "13", "14", "15", "16", "17", "18", "19", "20"})
    @DisplayName("findById, unknown payment id")
    void findById_unknownId(final long paymentId) {
        //Given

        //When
        Optional<Payment> opPayment = paymentRepository.findById(paymentId);

        //Then
        assertThat(opPayment).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"paymentuid_1", "paymentuid_2", "paymentuid_3", "paymentuid_4", "paymentuid_5", "paymentuid_6",
                "paymentuid_7", "paymentuid_8", "paymentuid_9", "paymentuid_10"})
    @DisplayName("findByPaymentUid")
    void findByPaymentUid(final String paymentUid) {
        //Given

        //When
        Optional<Payment> opPayment = paymentRepository.findByPaymentUid(paymentUid);

        //Then
        assertThat(opPayment).isNotEmpty();
    }

    @ParameterizedTest
    @CsvSource({"unknownpaymentuid_1", "unknownpaymentuid_2", "unknownpaymentuid_3", "unknownpaymentuid_4",
                "unknownpaymentuid_5", "unknownpaymentuid_6", "unknownpaymentuid_7", "unknownpaymentuid_8",
                "unknownpaymentuid_9", "unknownpaymentuid_10"})
    @DisplayName("findByPaymentUid, unknown payment uid")
    void findByPaymentUid_unknownPaymentUid(final String paymentUid) {
        //Given

        //When
        Optional<Payment> opPayment = paymentRepository.findByPaymentUid(paymentUid);

        //Then
        assertThat(opPayment).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"})
    @DisplayName("findByOrder_Id")
    void findByOrder_Id(final long orderId) {
        //Given

        //When
        Optional<Payment> opPayment = paymentRepository.findByOrder_Id(orderId);

        //Then
        assertThat(opPayment).isNotEmpty();
    }

    @ParameterizedTest
    @CsvSource({"11", "12", "13", "14", "15", "16", "17", "18", "19", "20"})
    @DisplayName("findByOrder_Id")
    void findByOrder_Id_unknownOrderId(final long orderId) {
        //Given

        //When
        Optional<Payment> opPayment = paymentRepository.findByOrder_Id(orderId);

        //Then
        assertThat(opPayment).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"1", "2", "3", "4", "5"})
    @DisplayName("findByCustomer_Id")
    void findByCustomer_Id(final long customerId) {
        //Given

        //When
        List<Payment> payments = paymentRepository.findByCustomer_Id(customerId);

        //Then
        assertThat(payments).hasSize(2);
        assertThat(payments.stream().allMatch(p -> p.getCustomer().getId() == customerId)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"6", "7", "8", "9", "10"})
    @DisplayName("findByCustomer_Id")
    void findByCustomer_Id_unknownCustomerId(final long customerId) {
        //Given

        //When
        List<Payment> payments = paymentRepository.findByCustomer_Id(customerId);

        //Then
        assertThat(payments).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"paymentuid_1", "paymentuid_2", "paymentuid_3", "paymentuid_4", "paymentuid_5", "paymentuid_6",
                "paymentuid_7", "paymentuid_8", "paymentuid_9", "paymentuid_10"})
    @DisplayName("existsByPaymentUid")
    void existsByPaymentUid(final String paymentUid) {
        //Given

        //When
        boolean flag = paymentRepository.existsByPaymentUid(paymentUid);

        //Then
        assertThat(flag).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"unknownpaymentuid_1", "unknownpaymentuid_2", "unknownpaymentuid_3", "unknownpaymentuid_4",
                "unknownpaymentuid_5", "unknownpaymentuid_6", "unknownpaymentuid_7", "unknownpaymentuid_8",
                "unknownpaymentuid_9", "unknownpaymentuid_10"})
    @DisplayName("existsByPaymentUid, unknown payment uid")
    void existsByPaymentUid_unknownPaymentUid(final String paymentUid) {
        //Given

        //When
        boolean flag = paymentRepository.existsByPaymentUid(paymentUid);

        //Then
        assertThat(flag).isFalse();
    }

    @ParameterizedTest
    @CsvSource({"paymentuid_1", "paymentuid_2", "paymentuid_3", "paymentuid_4", "paymentuid_5", "paymentuid_6",
                "paymentuid_7", "paymentuid_8", "paymentuid_9", "paymentuid_10"})
    @DisplayName("deleteByPaymentUid")
    void deleteByPaymentUid(final String paymentUid) {
        //Given

        //When
        paymentRepository.deleteByPaymentUid(paymentUid);

        //Then
        Long    paymentId = Long.valueOf(paymentUid.substring("paymentuid_".length()));
        Payment payment   = em.find(Payment.class, paymentId);

        assertThat(payment).isNull();
    }

    @ParameterizedTest
    @CsvSource({"unknownpaymentuid_1", "unknownpaymentuid_2", "unknownpaymentuid_3", "unknownpaymentuid_4",
                "unknownpaymentuid_5", "unknownpaymentuid_6", "unknownpaymentuid_7", "unknownpaymentuid_8",
                "unknownpaymentuid_9", "unknownpaymentuid_10"})
    @DisplayName("deleteByPaymentUid, unknown payment uid")
    void deleteByPaymentUid_unknownPaymentUid(final String paymentUid) {
        //Given

        //When
        paymentRepository.deleteByPaymentUid(paymentUid);

        //Then
        Long    paymentId = Long.valueOf(paymentUid.substring("unknownpaymentuid_".length()));
        Payment payment   = em.find(Payment.class, paymentId);

        assertThat(payment).isNotNull();
    }

}