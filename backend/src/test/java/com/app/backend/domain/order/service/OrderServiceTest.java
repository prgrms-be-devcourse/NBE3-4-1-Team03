package com.app.backend.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.app.backend.domain.order.dto.request.OrderProductRequest;
import com.app.backend.domain.order.dto.request.OrderRequest;
import com.app.backend.domain.order.dto.response.OrderResponse;
import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.entity.OrderProduct;
import com.app.backend.domain.order.entity.OrderStatus;
import com.app.backend.domain.order.exception.OrderException;
import com.app.backend.domain.order.repository.OrderProductRepository;
import com.app.backend.domain.order.repository.OrderRepository;
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
import java.util.Comparator;
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
 * FileName    : OrderServiceTest
 * Author      : 강찬우
 * Date        : 25. 1. 16.
 * Description :
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService  orderService;
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private OrderRepository        orderRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private UserRepository         userRepository;
    @Autowired
    private ProductRepository      productRepository;

    @Autowired
    private InitDummyData initDummyData;

    private List<User>    users;
    private List<Product> products;

    @BeforeEach
    void beforeEach() {
        users = initDummyData.createDummyUsers(userRepository, 1);
        products = initDummyData.createDummyProducts(productRepository, 5);
    }

    @AfterEach
    void afterEach() {
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("saveOrder")
    void saveOrder() {
        //Given
        long       customerId = users.get(0).getId();
        List<Long> productIds = products.stream().map(Product::getId).toList();

        List<OrderProductRequest> productInfo = new ArrayList<>();
        for (Long productId : productIds) {
            OrderProductRequest orderProductRequest = new OrderProductRequest(productId, 1);
            productInfo.add(orderProductRequest);
        }
        OrderRequest orderRequest = new OrderRequest(productInfo);

        //When
        long savedOrderId = orderService.saveOrder(customerId, orderRequest);

        //Then
        Order savedOrder = orderRepository.findById(savedOrderId).get();
        User  customer   = users.get(0);

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getCustomer().getId()).isEqualTo(customerId);
        assertThat(savedOrder.getTotalAmount()).isEqualTo(5);
        assertThat(savedOrder.getTotalPrice().compareTo(BigDecimal.valueOf(50000.00)) == 0).isTrue();
        assertThat(savedOrder.getAddress()).isEqualTo(
                "%s %s".formatted(customer.getAddress(), customer.getDetailAddress()));
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.ORDERED);

        List<OrderProduct> savedOrderProducts = savedOrder.getOrderProducts()
                                                          .stream()
                                                          .sorted(Comparator.comparing(OrderProduct::getId))
                                                          .toList();
        List<Product> storedProducts = productRepository.findAllById(productIds)
                                                        .stream()
                                                        .sorted(Comparator.comparing(Product::getId))
                                                        .toList();
        for (int i = 0; i < savedOrderProducts.size(); i++) {
            OrderProduct savedOrderProduct = savedOrderProducts.get(i);

            assertThat(savedOrderProduct).isNotNull();
            assertThat(savedOrderProduct.getOrder().getId()).isEqualTo(savedOrder.getId());
            assertThat(savedOrderProduct.getProduct().getId()).isEqualTo(storedProducts.get(i).getId());
            assertThat(savedOrderProduct.getProductAmount()).isEqualTo(1);
            assertThat(savedOrderProduct.getProductPrice().compareTo(storedProducts.get(i).getPrice()) == 0).isTrue();
            assertThat(savedOrderProduct.getTotalProductPrice())
                    .isEqualTo(storedProducts.get(i).getPrice().multiply(BigDecimal.ONE));
        }
    }

    @Test
    @DisplayName("saveOrder, unknown user id")
    void saveOrder_unknownUserId() {
        //Given
        long       unknownUserId = 1234567890L;
        List<Long> productIds    = products.stream().map(Product::getId).toList();

        List<OrderProductRequest> productInfo = new ArrayList<>();
        for (Long productId : productIds) {
            OrderProductRequest orderProductRequest = new OrderProductRequest(productId, 1);
            productInfo.add(orderProductRequest);
        }
        OrderRequest orderRequest = new OrderRequest(productInfo);

        //When

        //Then
        assertThatThrownBy(() -> orderService.saveOrder(unknownUserId, orderRequest))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("saveOrder, unknown product id")
    void saveOrder_unknownProductId() {
        //Given
        long userId           = users.get(0).getId();
        long unknownProductId = 1234567890L;

        OrderRequest orderRequest = new OrderRequest(List.of(new OrderProductRequest(unknownProductId, 1)));

        //When

        //Then
        assertThatThrownBy(() -> orderService.saveOrder(userId, orderRequest))
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND)
                .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getOrderById")
    void getOrderById() {
        //Given
        User               customer      = users.get(0);
        Order              order         = createDummyOrder(customer, 1).get(0);
        List<OrderProduct> orderProducts = createDummyOrderProducts(order);

        Long orderId = order.getId();
        afterEach();

        //When
        OrderResponse orderResponse = orderService.getOrderById(orderId);

        //Then
        assertThat(orderResponse.getName()).isEqualTo(order.getCustomer().getName());
        assertThat(orderResponse.getOrderNumber()).isEqualTo(order.getOrderNumber());
        assertThat(orderResponse.getTotalAmount()).isEqualTo(order.getTotalAmount());
        assertThat(orderResponse.getTotalPrice().compareTo(order.getTotalPrice()) == 0).isTrue();
        assertThat(orderResponse.getOrderAddress()).isEqualTo(order.getAddress());
        assertThat(orderResponse.getOrderStatus()).isEqualTo(order.getStatus().name());
        for (OrderProduct orderProduct : orderProducts)
            orderResponse.getOrderList().forEach(op -> {
                if (op.getName().equals(orderProduct.getProduct().getName())) {
                    assertThat(op.getPrice().compareTo(orderProduct.getProductPrice()) == 0).isTrue();
                    assertThat(op.getAmount()).isEqualTo(orderProduct.getProductAmount());
                    assertThat(op.getTotalPrice().compareTo(orderProduct.getTotalProductPrice()) == 0).isTrue();
                }
            });
        assertThat(orderResponse.getCreatedDate()).isEqualTo(Ut.Str.localDateTimeToString(order.getCreatedDate()));
    }

    @Test
    @DisplayName("getOrderById, unknown order id")
    void getOrderById_unknownOrderId() {
        //Given
        long unknownOrderId = 1234567890L;

        //When

        //Then
        assertThatThrownBy(() -> orderService.getOrderById(unknownOrderId))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND)
                .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getOrderByIdAndUserId")
    void getOrderByIdAndUserId() {
        //Given
        User               customer      = users.get(0);
        Order              order         = createDummyOrder(customer, 1).get(0);
        List<OrderProduct> orderProducts = createDummyOrderProducts(order);

        Long orderId = order.getId();
        Long userId  = customer.getId();
        afterEach();

        //When
        OrderResponse orderResponse = orderService.getOrderByIdAndUserId(orderId, userId);

        //Then
        assertThat(orderResponse.getName()).isEqualTo(order.getCustomer().getName());
        assertThat(orderResponse.getOrderNumber()).isEqualTo(order.getOrderNumber());
        assertThat(orderResponse.getTotalAmount()).isEqualTo(order.getTotalAmount());
        assertThat(orderResponse.getTotalPrice().compareTo(order.getTotalPrice()) == 0).isTrue();
        assertThat(orderResponse.getOrderAddress()).isEqualTo(order.getAddress());
        assertThat(orderResponse.getOrderStatus()).isEqualTo(order.getStatus().name());
        for (OrderProduct orderProduct : orderProducts)
            orderResponse.getOrderList().forEach(op -> {
                if (op.getName().equals(orderProduct.getProduct().getName())) {
                    assertThat(op.getPrice().compareTo(orderProduct.getProductPrice()) == 0).isTrue();
                    assertThat(op.getAmount()).isEqualTo(orderProduct.getProductAmount());
                    assertThat(op.getTotalPrice().compareTo(orderProduct.getTotalProductPrice()) == 0).isTrue();
                }
            });
        assertThat(orderResponse.getCreatedDate()).isEqualTo(Ut.Str.localDateTimeToString(order.getCreatedDate()));
    }

    @Test
    @DisplayName("getOrderByIdAndUserId, unknown order id")
    void getOrderByIdAndUserId_unknownOrderId() {
        //Given
        long unknownOrderId = 1234567890L;
        Long userId         = users.get(0).getId();
        afterEach();

        //When

        //Then
        final ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;

        assertThatThrownBy(() -> orderService.getOrderByIdAndUserId(unknownOrderId, userId))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getOrderByIdAndUserId, unknown user id")
    void getOrderByIdAndUserId_unknownUserId() {
        //Given
        User  customer = users.get(0);
        Order order    = createDummyOrder(customer, 1).get(0);

        Long orderId       = order.getId();
        Long unknownUserId = 1234567890L;
        afterEach();

        //When

        //Then
        final ErrorCode errorCode = ErrorCode.ORDER_BUYER_MISMATCH;

        assertThatThrownBy(() -> orderService.getOrderByIdAndUserId(orderId, unknownUserId))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getOrderByIdAndUserId, unknown user id and order id")
    void getOrderByIdAndUserId_unknownUserIdAndUnknownOrderId() {
        //Given
        long unknownOrderId = 1234567890L;
        long unknownUserId  = 1234567890L;

        //When

        //Then
        final ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;

        assertThatThrownBy(() -> orderService.getOrderByIdAndUserId(unknownOrderId, unknownUserId))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasMessage(errorCode.getMessage());
    }

    @Test
    @DisplayName("getOrderByOrderNumber")
    void getOrderByOrderNumber() {
        //Given
        User               customer      = users.get(0);
        Order              order         = createDummyOrder(customer, 1).get(0);
        List<OrderProduct> orderProducts = createDummyOrderProducts(order);

        String orderNumber = order.getOrderNumber();
        afterEach();

        //When
        OrderResponse orderResponse = orderService.getOrderByOrderNumber(orderNumber);

        //Then
        assertThat(orderResponse.getName()).isEqualTo(order.getCustomer().getName());
        assertThat(orderResponse.getOrderNumber()).isEqualTo(order.getOrderNumber());
        assertThat(orderResponse.getTotalAmount()).isEqualTo(order.getTotalAmount());
        assertThat(orderResponse.getTotalPrice().compareTo(order.getTotalPrice()) == 0).isTrue();
        assertThat(orderResponse.getOrderAddress()).isEqualTo(order.getAddress());
        assertThat(orderResponse.getOrderStatus()).isEqualTo(order.getStatus().name());
        for (OrderProduct orderProduct : orderProducts)
            orderResponse.getOrderList().forEach(op -> {
                if (op.getName().equals(orderProduct.getProduct().getName())) {
                    assertThat(op.getPrice().compareTo(orderProduct.getProductPrice()) == 0).isTrue();
                    assertThat(op.getAmount()).isEqualTo(orderProduct.getProductAmount());
                    assertThat(op.getTotalPrice().compareTo(orderProduct.getTotalProductPrice()) == 0).isTrue();
                }
            });
        assertThat(orderResponse.getCreatedDate()).isEqualTo(Ut.Str.localDateTimeToString(order.getCreatedDate()));
    }

    @Test
    @DisplayName("getOrderByOrderNumber, unknown order number")
    void getOrderByOrderNumber_unknownOrderNumber() {
        //Given
        String unknownOrderNumber = UUID.randomUUID().toString();

        //When

        //Then
        assertThatThrownBy(() -> orderService.getOrderByOrderNumber(unknownOrderNumber))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND)
                .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getOrdersByUserId")
    void getOrdersByUserId() {
        //Given
        User        customer = users.get(0);
        List<Order> orders   = createDummyOrder(customer, 5);
        orders.forEach(this::createDummyOrderProducts);

        Long userId = customer.getId();
        afterEach();

        //When
        List<OrderResponse> orderResponses = orderService.getOrdersByUserId(userId);

        //Then
        assertThat(orderResponses).hasSize(5);
        for (int i = 0; i < orders.size(); i++) {
            assertThat(orderResponses.get(i).getOrderNumber()).isEqualTo(orders.get(i).getOrderNumber());
            assertThat(orderResponses.get(i).getName()).isEqualTo(orders.get(i).getCustomer().getName());
            assertThat(orderResponses.get(i).getTotalAmount()).isEqualTo(orders.get(i).getTotalAmount());
            assertThat(orderResponses.get(i).getTotalPrice().compareTo(orders.get(i).getTotalPrice()) == 0).isTrue();
            assertThat(orderResponses.get(i).getOrderAddress()).isEqualTo(orders.get(i).getAddress());
            assertThat(orderResponses.get(i).getOrderStatus()).isEqualTo(orders.get(i).getStatus().name());
            assertThat(orderResponses.get(i).getCreatedDate())
                    .isEqualTo(Ut.Str.localDateTimeToString(orders.get(i).getCreatedDate()));
        }
    }

    @Test
    @DisplayName("getOrdersByUserId, unknown user id")
    void getOrdersByUserId_unknownUserId() {
        //Given
        long unknownUserId = 1234567890L;

        //When

        //Then
        assertThatThrownBy(() -> orderService.getOrdersByUserId(unknownUserId))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getOrdersByUserIdAndStatus")
    void getOrdersByUserIdAndStatus() {
        //Given
        User        customer = users.get(0);
        List<Order> orders   = createDummyOrder(customer, 5);
        orders.forEach(this::createDummyOrderProducts);

        Long userId = customer.getId();
        afterEach();

        //When
        List<OrderResponse> orderResponses = orderService.getOrdersByUserIdAndStatus(5, "ORDERED");

        //Then
        assertThat(orderResponses).hasSize(5);
    }

    @Test
    @DisplayName("getOrdersByUserIdAndStatus, unknown user id")
    void getOrdersByUserIdAndStatus_unknownUserId() {
        //Given
        long unknownUserId = 1234567890L;

        //When

        //Then
        assertThatThrownBy(() -> orderService.getOrdersByUserIdAndStatus(unknownUserId, "ORDERED"))
                .isInstanceOf(UserException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getOrdersByUserIdAndStatus, unknown order status")
    void getOrdersByUserIdAndStatus_unknownStatus() {
        //Given
        Long   userId             = users.get(0).getId();
        String unknownOrderStatus = "UNKNOWN";

        //When

        //Then
        assertThatThrownBy(() -> orderService.getOrdersByUserIdAndStatus(userId, unknownOrderStatus))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ORDER_STATUS)
                .hasMessage(ErrorCode.INVALID_ORDER_STATUS.getMessage());
    }

    @Test
    @DisplayName("getAllOrders, List")
    void getAllOrders_typeList() {
        //Given
        List<Order> orders = createDummyOrder(users.get(0), 10);
        afterEach();

        //When
        List<OrderResponse> orderResponses = orderService.getAllOrders();

        //Then
        assertThat(orderResponses).hasSize(orders.size());
        for (int i = 0; i < orders.size(); i++) {
            assertThat(orderResponses.get(i).getOrderNumber()).isEqualTo(orders.get(i).getOrderNumber());
            assertThat(orderResponses.get(i).getName()).isEqualTo(orders.get(i).getCustomer().getName());
            assertThat(orderResponses.get(i).getTotalAmount()).isEqualTo(orders.get(i).getTotalAmount());
            assertThat(orderResponses.get(i).getTotalPrice().compareTo(orders.get(i).getTotalPrice()) == 0).isTrue();
            assertThat(orderResponses.get(i).getOrderAddress()).isEqualTo(orders.get(i).getAddress());
            assertThat(orderResponses.get(i).getOrderStatus()).isEqualTo(orders.get(i).getStatus().name());
            assertThat(orderResponses.get(i).getCreatedDate())
                    .isEqualTo(Ut.Str.localDateTimeToString(orders.get(i).getCreatedDate()));
        }
    }

    @Test
    @DisplayName("getAllOrders, Page")
    void getAllOrders_typePage() {
        //Given
        List<Order> orders      = createDummyOrder(users.get(0), 100);
        Pageable    pageRequest = PageRequest.of(0, 10);
        afterEach();

        //When
        List<OrderResponse> orderResponses = orderService.getAllOrders(pageRequest).getContent();

        //Then
        orders = orders.subList(0, 10);

        assertThat(orderResponses).hasSizeLessThanOrEqualTo(pageRequest.getPageSize());
        for (int i = 0; i < orders.size(); i++) {
            assertThat(orderResponses.get(i).getOrderNumber()).isEqualTo(orders.get(i).getOrderNumber());
            assertThat(orderResponses.get(i).getName()).isEqualTo(orders.get(i).getCustomer().getName());
            assertThat(orderResponses.get(i).getTotalAmount()).isEqualTo(orders.get(i).getTotalAmount());
            assertThat(orderResponses.get(i).getTotalPrice()
                                     .compareTo(orders.get(i).getTotalPrice()) == 0).isTrue();
            assertThat(orderResponses.get(i).getOrderAddress()).isEqualTo(orders.get(i).getAddress());
            assertThat(orderResponses.get(i).getOrderStatus()).isEqualTo(orders.get(i).getStatus().name());
            assertThat(orderResponses.get(i).getCreatedDate())
                    .isEqualTo(Ut.Str.localDateTimeToString(orders.get(i).getCreatedDate()));
        }
    }

    @Test
    @DisplayName("existsByOrderNumber, exists order number")
    void existByOrderNumber() {
        //Given
        String orderNumber = createDummyOrder(users.get(0), 1).get(0).getOrderNumber();

        //When
        boolean flag = orderService.existsByOrderNumber(orderNumber);

        //Then
        assertThat(flag).isTrue();
    }

    @Test
    @DisplayName("existsByOrderNumber, unknown order number")
    void existsByOrderNumber_unknownOrderNumber() {
        //Given
        String orderNumber = UUID.randomUUID().toString();

        //When
        boolean flag = orderService.existsByOrderNumber(orderNumber);

        //Then
        assertThat(flag).isFalse();
    }

    @Test
    @DisplayName("updateOrderStatus, order id and order status")
    void updateOrderStatus_paramOrderIdAndOrderStatus() {
        //Given
        Order  order             = createDummyOrder(users.get(0), 1).get(0);
        long   orderId           = order.getId();
        String beforeOrderStatus = order.getStatus().name();
        afterEach();

        //When
        orderService.updateOrderStatus(orderId, "SHIPPED");

        //Then
        Order updatedOrder = orderRepository.findById(orderId).get();

        assertThat(updatedOrder.getStatus().name()).isNotEqualTo(beforeOrderStatus);
        assertThat(updatedOrder.getStatus().name()).isEqualTo("SHIPPED");
    }

    @Test
    @DisplayName("updateOrderStatus, unknown order id")
    void updateOrderStatus_paramOrderIdAndOrderStatus_unknownOrderId() {
        //Given
        long unknownOrderId = 1234567890L;

        //When

        //Then
        assertThatThrownBy(() -> orderService.updateOrderStatus(unknownOrderId, "SHIPPED"))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND)
                .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("updateOrderStatus, unknown order status")
    void updateOrderStatus_paramOrderIdAndOrderStatus_unknownOrderStatus() {
        //Given
        Order order   = createDummyOrder(users.get(0), 1).get(0);
        long  orderId = order.getId();
        afterEach();

        //When

        //Then
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, "UNKNOWN"))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ORDER_STATUS)
                .hasMessage(ErrorCode.INVALID_ORDER_STATUS.getMessage());
    }

    @Test
    @DisplayName("updateOrderStatus, order number and order status")
    void updateOrderStatus_paramOrderNumberAndOrderStatus() {
        //Given
        Order  order             = createDummyOrder(users.get(0), 1).get(0);
        String orderNumber       = order.getOrderNumber();
        String beforeOrderStatus = order.getStatus().name();
        afterEach();

        //When
        orderService.updateOrderStatus(orderNumber, "SHIPPED");

        //Then
        Order updatedOrder = orderRepository.findByOrderNumber(orderNumber).get();

        assertThat(updatedOrder.getStatus().name()).isNotEqualTo(beforeOrderStatus);
        assertThat(updatedOrder.getStatus().name()).isEqualTo("SHIPPED");
    }

    @Test
    @DisplayName("updateOrderStatus, unknown order number")
    void updateOrderStatus_paramOrderNumberAndOrderStatus_unknownOrderId() {
        //Given
        String orderNumber = UUID.randomUUID().toString();

        //When

        //Then
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderNumber, "SHIPPED"))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND)
                .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("updateOrderStatus, unknown order status")
    void updateOrderStatus_paramOrderNumberAndOrderStatus_unknownOrderStatus() {
        //Given
        Order  order       = createDummyOrder(users.get(0), 1).get(0);
        String orderNumber = order.getOrderNumber();
        afterEach();

        //When

        //Then
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderNumber, "UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("deleteOrderById")
    void deleteOrderById() {
        //Given
        Order              order           = createDummyOrder(users.get(0), 1).get(0);
        List<OrderProduct> orderProducts   = createDummyOrderProducts(order);
        long               orderId         = order.getId();
        List<Long>         orderProductIds = orderProducts.stream().map(OrderProduct::getId).toList();
        afterEach();

        //When
        orderService.deleteOrderById(orderId);

        //Then
        Optional<Order>    opOrder              = orderRepository.findById(orderId);
        List<OrderProduct> deletedOrderProducts = orderProductRepository.findAllById(orderProductIds);

        assertThat(opOrder).isEmpty();
        assertThat(deletedOrderProducts).isEmpty();
    }

    @Test
    @DisplayName("deleteOrderById, unknown order id")
    void deleteOrderById_unknownOrderId() {
        //Given
        long unknownOrderId = 1234567890L;

        //When

        //Then
        assertThatThrownBy(() -> orderService.deleteOrderById(unknownOrderId))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND)
                .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("deleteOrderByOrderNumber")
    void deleteOrderByOrderNumber() {
        //Given
        Order              order           = createDummyOrder(users.get(0), 1).get(0);
        List<OrderProduct> orderProducts   = createDummyOrderProducts(order);
        Long               orderId         = order.getId();
        List<Long>         orderProductIds = orderProducts.stream().map(OrderProduct::getId).toList();
        afterEach();

        //When
        orderService.deleteOrderByOrderNumber(order.getOrderNumber());

        //Then
        Optional<Order>    opOrder              = orderRepository.findByOrderNumber(order.getOrderNumber());
        List<OrderProduct> deletedOrderProducts = orderProductRepository.findAllById(orderProductIds);

        assertThat(opOrder).isEmpty();
        assertThat(deletedOrderProducts).isEmpty();
    }

    @Test
    @DisplayName("deleteOrderByOrderNumber, unknown order number")
    void deleteOrderByOrderNumber_unknownOrderNumber() {
        //Given
        String orderNumber = UUID.randomUUID().toString();

        //When

        //Then
        assertThatThrownBy(() -> orderService.deleteOrderByOrderNumber(orderNumber))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND)
                .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }

    //==================== 내부 메서드 ====================//

    private List<Order> createDummyOrder(final User customer, final int size) {
        return initDummyData.createDummyOrders(orderRepository, size, customer);
    }

    private List<OrderProduct> createDummyOrderProducts(final Order order) {
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Product product : products) {
            OrderProduct orderProduct = initDummyData.createDummyOrderProduct(orderProductRepository, product, order);
            orderProducts.add(orderProduct);
        }
        return orderProducts;
    }

}