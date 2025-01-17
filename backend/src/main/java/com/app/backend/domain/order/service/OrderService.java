package com.app.backend.domain.order.service;

import com.app.backend.domain.order.dto.request.OrderProductRequest;
import com.app.backend.domain.order.dto.request.OrderRequest;
import com.app.backend.domain.order.dto.response.OrderResponse;
import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.entity.OrderProduct;
import com.app.backend.domain.order.entity.OrderStatus;
import com.app.backend.domain.order.exception.OrderException;
import com.app.backend.domain.order.repository.OrderProductRepository;
import com.app.backend.domain.order.repository.OrderRepository;
import com.app.backend.domain.order.util.OrderUtil;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.exception.ProductException;
import com.app.backend.domain.product.repository.ProductRepository;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.repository.UserRepository;
import com.app.backend.global.error.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository        orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository      productRepository;
    private final UserRepository         userRepository;

    /**
     * 주문 저장
     *
     * @param userId       - 회원 ID
     * @param orderRequest - 주문 요청 DTO
     * @return 주문 ID
     */
    @Transactional
    public long saveOrder(final long userId, final OrderRequest orderRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        //TODO: 회원 상태(userStatus)에 따른 로직 수행 여부

        List<OrderProductRequest> productInfo = orderRequest.getProductInfo();

        List<Long>    productIds = productInfo.stream().map(OrderProductRequest::getProductId).toList();
        List<Product> products   = productRepository.findAllById(productIds);

        if (productIds.size() != products.size())   //주문 요청 정보의 제품 ID 중 존재하지 않은 제품 ID가 포함된 경우
            throw new ProductException(ErrorCode.PRODUCT_NOT_FOUND);

        Map<Long, Integer>    productAmountMap = getProductAmountMap(productInfo);  //주문 제품 ID-주문 개수 맵
        int                   orderAmount      = productAmountMap.values().stream().mapToInt(i -> i).sum(); //주문 제품 총 개수
        Map<Long, BigDecimal> productPriceMap  = getProductPriceMap(products);   //주문 제품 ID-주문 가격 맵

        BigDecimal totalPrice = getTotalPrice(productPriceMap, productAmountMap);   //주문 제품 총 합계 가격

        String orderNumber = getNewOrderNumber();   //주문 번호 생성

        Order order = orderRepository.save(
                Order.of(user, orderNumber, orderAmount, totalPrice,
                         "%s %s".formatted(user.getAddress(), user.getDetailAddress()))
        );    //주문(Order) 엔티티 저장

        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Long productId : productIds) {
            Integer    amount  = productAmountMap.get(productId);
            BigDecimal price   = productPriceMap.get(productId);
            Product    product = products.stream().filter(p -> p.getId().equals(productId)).findFirst().get();

            OrderProduct orderProduct = OrderProduct.of(order, product, amount, price);
            orderProducts.add(orderProduct);
            //TODO: 제품 주문 정보 저장과 함께 제품 재고 수량 감소 연산 추가 필요
//            orderProductRepository.save(orderProduct);
//            orderProduct.getProduct().reduceStock(amount);
            //TODO: 재고가 주문 수량보다 작다면? -> throw new ProductException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        orderProductRepository.saveAll(orderProducts);  //주문 제품(OrderProduct) 엔티티 저장

        return order.getId();
    }

    /**
     * 주문 ID로 주문 정보 단건 조회
     *
     * @param orderId - 주문 ID
     * @return 주문 정보 응답(OrderResponse)
     */
    public OrderResponse getOrderById(final long orderId) {
        Order order = orderRepository.findById(orderId)
                                     .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));
        return OrderResponse.of(order);
    }

    /**
     * 주문 ID, 회원 ID로 주문 정보 단건 조회
     * 해당 주문에 대한 구매 회원이 아닐 경우 예외
     *
     * @param orderId - 주문 ID
     * @param userId  - 회원 ID
     * @return
     */
    public OrderResponse getOrderByIdAndUserId(final long orderId, final long userId) {
        Order order = orderRepository.findById(orderId)
                                     .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getCustomer().getId().equals(userId))
            return OrderResponse.of(order);

        throw new OrderException(ErrorCode.ORDER_BUYER_MISMATCH);
    }

    /**
     * 주문 번호로 주문 정보 단건 조회
     *
     * @param orderNumber - 주문 번호
     * @return 주문 정보 응답(OrderResponse)
     */
    public OrderResponse getOrderByOrderNumber(final String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                                     .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));
        return OrderResponse.of(order);
    }

    /**
     * 회원 ID로 주문 정보 다건 조회
     *
     * @param userId - 회원 ID
     * @return 주문 정보 응답(OrderResponse) 목록
     */
    public List<OrderResponse> getOrdersByUserId(final long userId) {
        if (userRepository.findById(userId).isEmpty())
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        List<Order> orders = orderRepository.findByCustomer_Id(userId);
        return orders.stream().map(OrderResponse::of).toList();
    }

    /**
     * 회원 ID, 주문 상태로 주문 정보 다건 조회
     *
     * @param userId      - 회원 ID
     * @param orderStatus - 주문 상태 문자열: ORDERED, SHIPPED, DELIVERED, CANCELLED
     * @return 주문 정보 응답(OrderResponse) 목록
     */
    public List<OrderResponse> getOrdersByUserIdAndStatus(final long userId, final String orderStatus) {
        if (userRepository.findById(userId).isEmpty())
            throw new UserException(ErrorCode.USER_NOT_FOUND);

        validOrderStatus(orderStatus);

        List<Order> orders = orderRepository.findByCustomer_IdAndStatus(userId, OrderStatus.valueOf(orderStatus));
        return orders.stream().map(OrderResponse::of).toList();
    }

    /**
     * 모든 주문 정보 조회(List)
     *
     * @return 주문 정보 응답(OrderResponse) 목록
     */
    public List<OrderResponse> getAllOrders() {
        //NOTE: 관리자 권한 계정 대상
        return orderRepository.findAll().stream().map(OrderResponse::of).toList();
    }

    /**
     * 모든 주문 정보 조회(Page)
     *
     * @param pageable - 페이징 객체
     * @return 주문 정보 응답(OrderResponse) 페이징 객체
     */
    public Page<OrderResponse> getAllOrders(final Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderResponse::of);
    }

    /**
     * 주문 번호 검증
     *
     * @param orderNumber - 주문 번호
     * @return 주문 번호에 해당하는 주문 엔티티 존재 여부
     */
    public boolean existsByOrderNumber(final String orderNumber) {
        return orderRepository.existsByOrderNumber(orderNumber);
    }

    /**
     * 주문 상태 변경
     *
     * @param orderId     - 주문 ID
     * @param orderStatus - 변경할 주문 상태 문자열: ORDERED, SHIPPED, DELIVERED, CANCELLED
     */
    @Transactional
    public void updateOrderStatus(final long orderId, final String orderStatus) {
        Order order = orderRepository.findById(orderId)
                                     .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

        validOrderStatus(orderStatus);

        order.updateOrderStatus(OrderStatus.valueOf(orderStatus));
    }

    /**
     * 주문 상태 변경
     *
     * @param orderId     - 주문 ID
     * @param userId      - 회원 ID
     * @param orderStatus - 변경할 주문 상태 문자열: ORDERED, SHIPPED, DELIVERED, CANCELLED
     */
    @Transactional
    public void updateOrderStatusByUserId(final long orderId, final long userId, final String orderStatus) {
        Order order = orderRepository.findById(orderId)
                                     .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getCustomer().getId().equals(userId)) {
            validOrderStatus(orderStatus);

            order.updateOrderStatus(OrderStatus.valueOf(orderStatus));

            return;
        }

        throw new OrderException(ErrorCode.ORDER_BUYER_MISMATCH);
    }

    /**
     * 주문 상태 변경
     *
     * @param orderNumber - 주문 번호
     * @param orderStatus - 변경할 주문 상태 문자열: ORDERED, SHIPPED, DELIVERED, CANCELLED
     */
    @Transactional
    public void updateOrderStatus(final String orderNumber, final String orderStatus) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                                     .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));
        order.updateOrderStatus(OrderStatus.valueOf(orderStatus));
    }

    /**
     * 주문 삭제
     *
     * @param orderId - 주문 ID
     */
    @Transactional
    public void deleteOrderById(final long orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return;
        }
        throw new OrderException(ErrorCode.ORDER_NOT_FOUND);
    }

    /**
     * 주문 삭제
     *
     * @param orderNumber - 주문 번호
     */
    @Transactional
    public void deleteOrderByOrderNumber(final String orderNumber) {
        if (orderRepository.existsByOrderNumber(orderNumber)) {
            orderRepository.deleteByOrderNumber(orderNumber);
            return;
        }
        throw new OrderException(ErrorCode.ORDER_NOT_FOUND);
    }

    //==================== 내부 메서드 ====================//

    /**
     * 제품 ID - 가격 맵 추출
     *
     * @param products - 제품 엔티티 목록
     * @return 제품 ID - 가격 맵
     */
    private Map<Long, BigDecimal> getProductPriceMap(final List<Product> products) {
        return products.stream().collect(Collectors.toMap(Product::getId, Product::getPrice));
    }

    /**
     * 제품 ID - 개수 맵 추출
     *
     * @param productInfo - 주문 제품 정보 목록
     * @return 주문 제품 ID - 주문 개수 맵
     */
    private Map<Long, Integer> getProductAmountMap(final List<OrderProductRequest> productInfo) {
        return productInfo.stream()
                          .collect(Collectors.toMap(OrderProductRequest::getProductId, OrderProductRequest::getAmount));
    }

    /**
     * 주문 제품 당 가격 x 주문 제품 개수 합계
     *
     * @param productPriceMap  - 제품 ID - 가격 맵
     * @param productAmountMap - 제품 ID - 개수 맵
     * @return 총 주문 가격
     */
    private BigDecimal getTotalPrice(final Map<Long, BigDecimal> productPriceMap,
                                     final Map<Long, Integer> productAmountMap) {
        return productPriceMap.entrySet().stream()
                              .map(entry -> {
                                  Long       productId = entry.getKey();
                                  BigDecimal price     = entry.getValue();
                                  Integer    amount    = productAmountMap.getOrDefault(productId, 0);
                                  //TODO: 주문 ID는 존재하지만 개수가 없는 경우...?
                                  return price.multiply(BigDecimal.valueOf(amount));
                              }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 데이터 베이스에 존재하지 않는(Unique) 주문 번호 생성
     *
     * @return 생성된 주문 번호
     */
    private String getNewOrderNumber() {
        String orderNumber;
        do {
            orderNumber = OrderUtil.generateOrderNumber();
        } while (orderRepository.existsByOrderNumber(orderNumber));
        return orderNumber;
    }

    /**
     * 주문 상태 문자열 검증
     *
     * @param orderStatus - 주문 상태 문자열
     * @return 주문 상태 enum 존재 여부
     */
    private boolean validOrderStatus(final String orderStatus) {
        if (Arrays.stream(OrderStatus.values()).anyMatch(os -> os.name().equals(orderStatus)))
            return true;
        throw new OrderException(ErrorCode.INVALID_ORDER_STATUS);
    }

}
