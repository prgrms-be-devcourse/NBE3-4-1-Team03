package com.app.backend.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.app.backend.domain.order.dto.request.PaymentRequest;
import com.app.backend.domain.order.dto.response.PaymentResponse;
import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.entity.Payment;
import com.app.backend.domain.order.entity.PaymentStatus;
import com.app.backend.domain.order.exception.OrderException;
import com.app.backend.domain.order.exception.PaymentException;
import com.app.backend.domain.order.repository.OrderRepository;
import com.app.backend.domain.order.repository.PaymentRepository;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.repository.ProductRepository;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.repository.UserRepository;
import com.app.backend.global.error.exception.ErrorCode;
import com.app.backend.global.initdata.InitDummyData;
import com.app.backend.standard.util.Ut;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackageName : com.app.backend.domain.order.service
 * FileName    : PaymentServiceTest
 * Author      : loadingKKamo21
 * Date        : 25. 1. 18.
 * Description :
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;
    @PersistenceContext
    private EntityManager  em;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository   orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository    userRepository;

    @Autowired
    private InitDummyData initDummyData;

    private List<User>    users;
    private List<Product> products;
    private List<Order>   orders;

    @BeforeEach
    void beforeEach() {
        users = initDummyData.createDummyUsers(userRepository, 1);
        orders = initDummyData.createDummyOrders(orderRepository, 1, users.get(0));
        products = initDummyData.createDummyProducts(productRepository, 5);
    }

    @AfterEach
    void afterEach() {
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("savePayment")
    void savePayment() {
        //Given
        long customerId = users.get(0).getId();
        long orderId    = orders.get(0).getId();

        String         paymentUid     = UUID.randomUUID().toString().replace("-", "");
        PaymentRequest paymentRequest = new PaymentRequest(paymentUid, "CREDIT_CARD", BigDecimal.valueOf(10000.00));

        //When
        long savedPaymentId = paymentService.savePayment(customerId, orderId, paymentRequest);

        //Then
        Payment savedPayment = paymentRepository.findById(savedPaymentId).get();

        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getId()).isNotNull();
        assertThat(savedPayment.getOrder().getId()).isEqualTo(orderId);
        assertThat(savedPayment.getCustomer().getId()).isEqualTo(customerId);
        assertThat(savedPayment.getPaymentUid()).isEqualTo(paymentUid);
        assertThat(savedPayment.getMethod().name()).isEqualTo(paymentRequest.getMethod());
        assertThat(savedPayment.getPaidAmount().compareTo(paymentRequest.getPaidAmount()) == 0).isTrue();
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("savePayment, unknown user id")
    void savePayment_unknownUserId() {
        //Given
        long customerId = 1234567890L;
        long orderId    = orders.get(0).getId();

        String         paymentUid     = UUID.randomUUID().toString().replace("-", "");
        PaymentRequest paymentRequest = new PaymentRequest(paymentUid, "CREDIT_CARD", BigDecimal.valueOf(10000.00));

        //When

        //Then
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.savePayment(customerId, orderId, paymentRequest))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("savePayment, unknown order id")
    void savePayment_unknownOrderId() {
        //Given
        long customerId     = users.get(0).getId();
        long unknownOrderId = 1234567890L;

        String         paymentUid     = UUID.randomUUID().toString().replace("-", "");
        PaymentRequest paymentRequest = new PaymentRequest(paymentUid, "CREDIT_CARD", BigDecimal.valueOf(10000.00));

        //When

        //Then
        ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.savePayment(customerId, unknownOrderId, paymentRequest))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("savePayment, unknown user id and unknown order id")
    void savePayment_unknownUserIdAndUnknownOrderId() {
        //Given
        long customerId = 1234567890L;
        long orderId    = 1234567890L;

        //When

        String         paymentUid     = UUID.randomUUID().toString().replace("-", "");
        PaymentRequest paymentRequest = new PaymentRequest(paymentUid, "CREDIT_CARD", BigDecimal.valueOf(10000.00));

        //Then
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.savePayment(customerId, orderId, paymentRequest))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getPaymentById")
    void getPaymentById() {
        //Given
        User    customer = users.get(0);
        Payment payment  = createDummyPayments(orders, customer).get(0);

        Long paymentId = payment.getId();
        afterEach();

        //When
        PaymentResponse paymentResponse = paymentService.getPaymentById(paymentId);

        //Then
        assertThat(paymentResponse.getName()).isEqualTo(payment.getCustomer().getName());
        assertThat(paymentResponse.getOrderNumber()).isEqualTo(payment.getOrder().getOrderNumber());
        assertThat(paymentResponse.getPaymentUid()).isEqualTo(payment.getPaymentUid());
        assertThat(paymentResponse.getMethod()).isEqualTo(payment.getMethod().name());
        assertThat(paymentResponse.getPaidAmount().compareTo(payment.getPaidAmount()) == 0).isTrue();
        assertThat(paymentResponse.getCreatedDate()).isEqualTo(Ut.Str.localDateTimeToString(payment.getCreatedDate()));
    }

    @Test
    @DisplayName("getPaymentById, unknown payment id")
    void getPaymentById_unknownPaymentId() {
        //Given
        long unknownPaymentId = 1234567890L;

        //When

        //Then
        ErrorCode errorCode = ErrorCode.PAYMENT_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.getPaymentById(unknownPaymentId))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getPaymentByPaymentUid")
    void getPaymentByPaymentUid() {
        //Given
        User    customer = users.get(0);
        Payment payment  = createDummyPayments(orders, customer).get(0);

        String paymentUid = payment.getPaymentUid();
        afterEach();

        //When
        PaymentResponse paymentResponse = paymentService.getPaymentByPaymentUid(paymentUid);

        //Then
        assertThat(paymentResponse.getName()).isEqualTo(payment.getCustomer().getName());
        assertThat(paymentResponse.getOrderNumber()).isEqualTo(payment.getOrder().getOrderNumber());
        assertThat(paymentResponse.getPaymentUid()).isEqualTo(payment.getPaymentUid());
        assertThat(paymentResponse.getMethod()).isEqualTo(payment.getMethod().name());
        assertThat(paymentResponse.getPaidAmount().compareTo(payment.getPaidAmount()) == 0).isTrue();
        assertThat(paymentResponse.getCreatedDate()).isEqualTo(Ut.Str.localDateTimeToString(payment.getCreatedDate()));
    }

    @Test
    @DisplayName("getPaymentByPaymentUid, unknown payment uid")
    void getPaymentByPaymentUid_unknownPaymentUid() {
        //Given
        String paymentUid = UUID.randomUUID().toString();

        //When

        //Then
        ErrorCode errorCode = ErrorCode.PAYMENT_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.getPaymentByPaymentUid(paymentUid))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getPaymentByOrderId")
    void getPaymentByOrderId() {
        //Given
        User    customer = users.get(0);
        Payment payment  = createDummyPayments(orders, customer).get(0);

        Long orderId = orders.get(0).getId();
        afterEach();

        //When
        PaymentResponse paymentResponse = paymentService.getPaymentByOrderId(orderId);

        //Then
        assertThat(paymentResponse.getName()).isEqualTo(payment.getCustomer().getName());
        assertThat(paymentResponse.getOrderNumber()).isEqualTo(payment.getOrder().getOrderNumber());
        assertThat(paymentResponse.getPaymentUid()).isEqualTo(payment.getPaymentUid());
        assertThat(paymentResponse.getMethod()).isEqualTo(payment.getMethod().name());
        assertThat(paymentResponse.getPaidAmount().compareTo(payment.getPaidAmount()) == 0).isTrue();
        assertThat(paymentResponse.getCreatedDate()).isEqualTo(Ut.Str.localDateTimeToString(payment.getCreatedDate()));
    }

    @Test
    @DisplayName("getPaymentByOrderId, unknown order id")
    void getPaymentByOrderId_unknownOrderId() {
        //Given
        long unknownOrderId = 1234567890L;

        //When

        //Then
        ErrorCode errorCode = ErrorCode.PAYMENT_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.getPaymentByOrderId(unknownOrderId))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getPaymentByIdAndOrderId")
    void getPaymentByIdAndOrderId() {
        //Given
        User    customer = users.get(0);
        Payment payment  = createDummyPayments(orders, customer).get(0);

        Long paymentId = payment.getId();
        Long orderId   = orders.get(0).getId();
        afterEach();

        //When
        PaymentResponse paymentResponse = paymentService.getPaymentByIdAndOrderId(paymentId, orderId);

        //Then
        assertThat(paymentResponse.getName()).isEqualTo(payment.getCustomer().getName());
        assertThat(paymentResponse.getOrderNumber()).isEqualTo(payment.getOrder().getOrderNumber());
        assertThat(paymentResponse.getPaymentUid()).isEqualTo(payment.getPaymentUid());
        assertThat(paymentResponse.getMethod()).isEqualTo(payment.getMethod().name());
        assertThat(paymentResponse.getPaidAmount().compareTo(payment.getPaidAmount()) == 0).isTrue();
        assertThat(paymentResponse.getCreatedDate()).isEqualTo(Ut.Str.localDateTimeToString(payment.getCreatedDate()));
    }

    @Test
    @DisplayName("getPaymentByIdAndOrderId, unknown payment id")
    void getPaymentByIdAndOrderId_unknownPaymentId() {
        //Given
        long unknownPaymentId = 1234567890L;
        Long orderId          = orders.get(0).getId();
        afterEach();

        //When

        //Then
        ErrorCode errorCode = ErrorCode.PAYMENT_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.getPaymentByIdAndOrderId(unknownPaymentId, orderId))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getPaymentByIdAndOrderId, unknown order id")
    void getPaymentByIdAndOrderId_unknownOrderId() {
        //Given
        User    customer = users.get(0);
        Payment payment  = createDummyPayments(orders, customer).get(0);

        Long paymentId      = payment.getId();
        long unknownOrderId = 1234567890L;
        afterEach();

        //When

        //Then
        ErrorCode errorCode = ErrorCode.PAYMENT_ORDER_MISMATCH;

        assertThatThrownBy(() -> paymentService.getPaymentByIdAndOrderId(paymentId, unknownOrderId))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getPaymentByIdAndOrderId, unknown payment id and unknown order id")
    void getPaymentByIdAndOrderId_unknownPaymentIdAndUnknownOrderId() {
        //Given
        long unknownPaymentId = 1234567890L;
        long unknownOrderId   = 1234567890L;

        //When

        //Then
        ErrorCode errorCode = ErrorCode.PAYMENT_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.getPaymentByIdAndOrderId(unknownPaymentId, unknownOrderId))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getPaymentsByUserId")
    void getPaymentsByUserId() {
        //Given
        User customer = users.get(0);
        orders.addAll(createDummyOrder(customer, 9));
        List<Payment> payments = createDummyPayments(orders, customer);

        Long userId = customer.getId();
        afterEach();

        //When
        List<PaymentResponse> paymentResponses = paymentService.getPaymentsByUserId(userId);

        //Then
        assertThat(paymentResponses).hasSize(10);
        for (int i = 0; i < payments.size(); i++) {
            assertThat(paymentResponses.get(i).getName()).isEqualTo(payments.get(i).getCustomer().getName());
            assertThat(paymentResponses.get(i).getOrderNumber()).isEqualTo(payments.get(i).getOrder().getOrderNumber());
            assertThat(paymentResponses.get(i).getPaymentUid()).isEqualTo(payments.get(i).getPaymentUid());
            assertThat(paymentResponses.get(i).getMethod()).isEqualTo(payments.get(i).getMethod().name());
            assertThat(paymentResponses.get(i).getPaidAmount().compareTo(payments.get(i).getPaidAmount()) == 0)
                    .isTrue();
            assertThat(paymentResponses.get(i).getCreatedDate())
                    .isEqualTo(Ut.Str.localDateTimeToString(payments.get(i).getCreatedDate()));
        }
    }

    @Test
    @DisplayName("getPaymentsByUserId, unknown user id")
    void getPaymentsByUserId_unknownUserId() {
        //Given
        long unknownUserId = 1234567890L;

        //When

        //Then
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.getPaymentsByUserId(unknownUserId))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getPaymentsByUserIdAndPaymentStatus")
    void getPaymentsByUserIdAndPaymentStatus() {
        //Given
        User customer = users.get(0);
        orders.addAll(createDummyOrder(customer, 9));
        List<Payment> payments = createDummyPayments(orders, customer);

        Long userId = customer.getId();
        afterEach();

        //When
        List<PaymentResponse> paymentResponses = paymentService.getPaymentsByUserIdAndPaymentStatus(userId, "SUCCESS");

        //Then
        assertThat(paymentResponses).hasSize(10);
        for (int i = 0; i < payments.size(); i++) {
            assertThat(paymentResponses.get(i).getName()).isEqualTo(payments.get(i).getCustomer().getName());
            assertThat(paymentResponses.get(i).getOrderNumber()).isEqualTo(payments.get(i).getOrder().getOrderNumber());
            assertThat(paymentResponses.get(i).getPaymentUid()).isEqualTo(payments.get(i).getPaymentUid());
            assertThat(paymentResponses.get(i).getMethod()).isEqualTo(payments.get(i).getMethod().name());
            assertThat(paymentResponses.get(i).getPaidAmount().compareTo(payments.get(i).getPaidAmount()) == 0)
                    .isTrue();
            assertThat(paymentResponses.get(i).getCreatedDate())
                    .isEqualTo(Ut.Str.localDateTimeToString(payments.get(i).getCreatedDate()));
        }
    }

    @Test
    @DisplayName("getPaymentsByUserIdAndPaymentStatus, unknown user id")
    void getPaymentsByUserIdAndPaymentStatus_unknownUserId() {
        //Given
        long unknownUserId = 1234567890L;

        //When

        //Then
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.getPaymentsByUserIdAndPaymentStatus(unknownUserId, "SUCCESS"))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getPaymentsByUserIdAndPaymentStatus, unknown payment status")
    void getPaymentsByUserIdAndPaymentStatus_unknownPaymentStatus() {
        //Given
        User customer = users.get(0);
        orders.addAll(createDummyOrder(customer, 9));
        List<Payment> payments = createDummyPayments(orders, customer);

        Long userId = customer.getId();
        afterEach();

        //When

        //Then
        ErrorCode errorCode = ErrorCode.INVALID_PAYMENT_STATUS;

        assertThatThrownBy(() -> paymentService.getPaymentsByUserIdAndPaymentStatus(userId, "UNKNOWN"))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getPaymentsByUserIdAndPaymentStatus, unknown user id and unknown payment status")
    void getPaymentsByUserIdAndPaymentStatus_unknownUserIdAndPaymentStatus() {
        //Given
        long unknownUserId = 1234567890L;

        //When

        //Then
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.getPaymentsByUserIdAndPaymentStatus(unknownUserId, "UNKNOWN"))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getAllPayments, List")
    void getAllPayments_typeList() {
        //Given
        User customer = users.get(0);
        orders.addAll(createDummyOrder(customer, 9));
        List<Payment> payments = createDummyPayments(orders, customer);
        afterEach();

        //When
        List<PaymentResponse> paymentResponses = paymentService.getAllPayments();

        //Then
        assertThat(paymentResponses).hasSize(payments.size());
        for (int i = 0; i < payments.size(); i++) {
            assertThat(paymentResponses.get(i).getName()).isEqualTo(payments.get(i).getCustomer().getName());
            assertThat(paymentResponses.get(i).getOrderNumber()).isEqualTo(payments.get(i).getOrder().getOrderNumber());
            assertThat(paymentResponses.get(i).getPaymentUid()).isEqualTo(payments.get(i).getPaymentUid());
            assertThat(paymentResponses.get(i).getMethod()).isEqualTo(payments.get(i).getMethod().name());
            assertThat(paymentResponses.get(i).getPaidAmount().compareTo(payments.get(i).getPaidAmount()) == 0)
                    .isTrue();
            assertThat(paymentResponses.get(i).getCreatedDate())
                    .isEqualTo(Ut.Str.localDateTimeToString(payments.get(i).getCreatedDate()));
        }
    }

    @Test
    @DisplayName("getAllPayments, Page")
    void getAllPayments_typePage() {
        //Given
        User customer = users.get(0);
        orders.addAll(createDummyOrder(customer, 99));
        List<Payment> payments    = createDummyPayments(orders, customer);
        Pageable      pageRequest = PageRequest.of(0, 10);
        afterEach();

        //When
        List<PaymentResponse> paymentResponses = paymentService.getAllPayments(pageRequest).getContent();

        //Then
        payments = payments.subList(0, 10);

        assertThat(paymentResponses).hasSizeLessThanOrEqualTo(pageRequest.getPageSize());
        for (int i = 0; i < payments.size(); i++) {
            assertThat(paymentResponses.get(i).getName()).isEqualTo(payments.get(i).getCustomer().getName());
            assertThat(paymentResponses.get(i).getOrderNumber()).isEqualTo(payments.get(i).getOrder().getOrderNumber());
            assertThat(paymentResponses.get(i).getPaymentUid()).isEqualTo(payments.get(i).getPaymentUid());
            assertThat(paymentResponses.get(i).getMethod()).isEqualTo(payments.get(i).getMethod().name());
            assertThat(paymentResponses.get(i).getPaidAmount().compareTo(payments.get(i).getPaidAmount()) == 0)
                    .isTrue();
            assertThat(paymentResponses.get(i).getCreatedDate())
                    .isEqualTo(Ut.Str.localDateTimeToString(payments.get(i).getCreatedDate()));
        }
    }

    @Test
    @DisplayName("existsByPaymentUid")
    void existsByPaymentUid() {
        //Given
        User    customer = users.get(0);
        Payment payment  = createDummyPayments(orders, customer).get(0);

        String paymentUid = payment.getPaymentUid();
        afterEach();

        //When
        boolean flag = paymentService.existsByPaymentUid(paymentUid);

        //Then
        assertThat(flag).isTrue();
    }

    @Test
    @DisplayName("existsByPaymentUid, unknown payment uid")
    void existsByPaymentUid_unknownPaymentUid() {
        //Given
        String paymentUid = UUID.randomUUID().toString();

        //When
        boolean flag = paymentService.existsByPaymentUid(paymentUid);

        //Then
        assertThat(flag).isFalse();
    }

    @Test
    @DisplayName("updatePaymentStatus")
    void updatePaymentStatus_paramPaymentIdAndPaymentStatus() {
        //Given
        User    customer            = users.get(0);
        Payment payment             = createDummyPayments(orders, customer).get(0);
        long    paymentId           = payment.getId();
        String  beforePaymentStatus = payment.getStatus().name();
        afterEach();

        //When
        paymentService.updatePaymentStatus(paymentId, "FAILED");

        //Then
        Payment updatedPayment = paymentRepository.findById(paymentId).get();

        assertThat(updatedPayment.getStatus().name()).isNotEqualTo(beforePaymentStatus);
        assertThat(updatedPayment.getStatus().name()).isEqualTo("FAILED");
    }

    @Test
    @DisplayName("updatePaymentStatus, unknown payment id")
    void updatePaymentStatus_paramPaymentIdAndPaymentStatus_unknownPaymentId() {
        //Given
        long unknownPaymentId = 1234567890L;

        //When

        //Then
        ErrorCode errorCode = ErrorCode.PAYMENT_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.updatePaymentStatus(unknownPaymentId, "FAILED"))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("updatePaymentStatus, unknown payment status")
    void updatePaymentStatus_paramPaymentIdAndPaymentStatus_unknownPaymentStatus() {
        //Given
        User    customer  = users.get(0);
        Payment payment   = createDummyPayments(orders, customer).get(0);
        long    paymentId = payment.getId();
        afterEach();

        //When

        //Then
        ErrorCode errorCode = ErrorCode.INVALID_PAYMENT_STATUS;

        assertThatThrownBy(() -> paymentService.updatePaymentStatus(paymentId, "UNKNOWN"))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("updatePaymentStatusByUserId")
    void updatePaymentStatusByUserId() {
        //Given
        User    customer            = users.get(0);
        Payment payment             = createDummyPayments(orders, customer).get(0);
        long    paymentId           = payment.getId();
        long    userId              = customer.getId();
        String  beforePaymentStatus = payment.getStatus().name();
        afterEach();

        //When
        paymentService.updatePaymentStatusByUserId(paymentId, userId, "FAILED");

        //Then
        Payment updatedPayment = paymentRepository.findById(paymentId).get();

        assertThat(updatedPayment.getStatus().name()).isNotEqualTo(beforePaymentStatus);
        assertThat(updatedPayment.getStatus().name()).isEqualTo("FAILED");
    }


    @Test
    @DisplayName("updatePaymentStatusByUserId, unknown user id")
    void updatePaymentStatusByUserId_unknownUserId() {
        //Given
        User    customer      = users.get(0);
        Payment payment       = createDummyPayments(orders, customer).get(0);
        long    paymentId     = payment.getId();
        long    unknownUserId = 1234567890L;
        afterEach();

        //When

        //Then
        ErrorCode errorCode = ErrorCode.PAYMENT_BUYER_MISMATCH;

        assertThatThrownBy(() -> paymentService.updatePaymentStatusByUserId(paymentId, unknownUserId, "FAILED"))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("updatePaymentStatusByUserId, unknown payment status")
    void updatePaymentStatusByUserId_unknownPaymentStatus() {
        //Given
        User    customer  = users.get(0);
        Payment payment   = createDummyPayments(orders, customer).get(0);
        long    paymentId = payment.getId();
        long    userId    = customer.getId();
        afterEach();

        //When

        //Then
        ErrorCode errorCode = ErrorCode.INVALID_PAYMENT_STATUS;

        assertThatThrownBy(() -> paymentService.updatePaymentStatusByUserId(paymentId, userId, "UNKNOWN"))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("deletePaymentById")
    void deletePaymentById() {
        //Given
        User    customer  = users.get(0);
        Payment payment   = createDummyPayments(orders, customer).get(0);
        long    paymentId = payment.getId();
        afterEach();

        //When
        paymentService.deletePaymentById(paymentId);

        //Then
        Optional<Payment> opPayment = paymentRepository.findById(paymentId);

        assertThat(opPayment).isEmpty();
    }

    @Test
    @DisplayName("deletePaymentById, unknown payment id")
    void deletePaymentById_unknownPaymentId() {
        //Given
        long unknownPaymentId = 1234567890L;

        //When

        //Then
        ErrorCode errorCode = ErrorCode.PAYMENT_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.deletePaymentById(unknownPaymentId))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("deleteByPaymentUid")
    void deleteByPaymentUid() {
        //Given
        User    customer   = users.get(0);
        Payment payment    = createDummyPayments(orders, customer).get(0);
        String  paymentUid = payment.getPaymentUid();
        afterEach();

        //When
        paymentService.deletePaymentByPaymentUid(paymentUid);

        //Then
        Optional<Payment> opPayment = paymentRepository.findByPaymentUid(paymentUid);

        assertThat(opPayment).isEmpty();
    }

    @Test
    @DisplayName("deleteByPaymentUid, unknown payment uid")
    void deleteByPaymentUid_unknownPaymentUid() {
        //Given
        String paymentUid = UUID.randomUUID().toString();

        //When

        //Then
        ErrorCode errorCode = ErrorCode.PAYMENT_NOT_FOUND;

        assertThatThrownBy(() -> paymentService.deletePaymentByPaymentUid(paymentUid))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    //==================== 내부 메서드 ====================//

    private List<Payment> createDummyPayments(final List<Order> orders, final User customer) {
        List<Payment> payments = new ArrayList<>();
        for (Order order : orders) {
            Payment payment = initDummyData.createDummyPayment(paymentRepository, order, customer);
            payments.add(payment);
        }
        return payments;
    }

    private List<Order> createDummyOrder(final User customer, final int size) {
        return initDummyData.createDummyOrders(orderRepository, size, customer);
    }

}