package com.app.backend.global.initdata;

import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.entity.OrderProduct;
import com.app.backend.domain.order.repository.OrderProductRepository;
import com.app.backend.domain.order.repository.OrderRepository;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.repository.ProductRepository;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class BaseInitData {

    private static final Random RANDOM = new Random();

    private final UserRepository         userRepository;
    private final ProductRepository      productRepository;
    private final OrderRepository        orderRepository;
    private final PasswordEncoder        passwordEncoder;
    private final OrderProductRepository orderProductRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    void init() throws InterruptedException {
        List<User>         dummyUsers         = createDummyUsers(5);
        List<Product>      dummyProducts      = createDummyProducts(10);
        List<Order>        dummyOrders        = createDummyOrders(20, dummyUsers);
        List<OrderProduct> dummyOrderProducts = createDummyOrderProducts(30, dummyOrders, dummyProducts);
    }

    private List<User> createDummyUsers(final int size) throws InterruptedException {
        if (userRepository.count() > 0) return List.of();

        List<User> users = new ArrayList<>();

        String adminStr = "admin";
        User admin = User.builder()
                .email(adminStr + "@mail.com")
                .password(passwordEncoder.encode(adminStr+"password"))
                .name(adminStr)
                .address(adminStr + " address")
                .detailAddress(adminStr + " detail address")
                .phone("01000000000")
                .status("ACTIVATED")
                .role("ROLE_ADMIN")
                .build();
        userRepository.save(admin);

        for (int i = 1; i <= size; i++) {
            Thread.sleep(1);
            String userStr = "user" + String.format("%0" + String.valueOf(size).length() + "d", i);
            User user = User.builder()
                    .email(userStr + "@mail.com")
                    .password(passwordEncoder.encode(userStr+"password"))
                    .name(userStr)
                    .address(userStr + " address")
                    .detailAddress(userStr + " detail address")
                    .phone("01000000000")
                    .status("ACTIVATED")
                    .role("ROLE_USER")
                    .build();
            userRepository.save(user);
            users.add(user);
        }

        return users;
    }

    private List<Product> createDummyProducts(final int size) throws InterruptedException {
        if (productRepository.count() > 0) return List.of();

        // 상품 이름 배열
        String[] productNames = {
                "에티오피아 예가체프", "콜롬비아 수프리모", "케냐 AA",
                "과테말라 안티구아", "브라질 산토스", "모닝 블렌드",
                "리치 블렌드", "스위트 블렌드", "다크 로스트 블렌드",
                "디카페인 콜롬비아"
        };

        // 가격 배열
        BigDecimal[] prices = {
                BigDecimal.valueOf(15000), BigDecimal.valueOf(14000), BigDecimal.valueOf(18000),
                BigDecimal.valueOf(16000), BigDecimal.valueOf(12000), BigDecimal.valueOf(13000),
                BigDecimal.valueOf(14500), BigDecimal.valueOf(15000), BigDecimal.valueOf(13500),
                BigDecimal.valueOf(16000)
        };

        // 상품 설명 배열
        String[] descriptions = {
                "플로럴 향과 시트러스 노트가 특징인 고급 싱글 오리진 원두.",
                "밸런스 좋은 맛과 캐러멜 향이 돋보이는 콜롬비아 대표 원두.",
                "상큼한 과일 맛과 높은 산미가 매력적인 프리미엄 원두.",
                "초콜릿과 견과류 향이 어우러진 과테말라산 고품질 원두.",
                "부드럽고 묵직한 맛을 자랑하는 브라질 대표 원두.",
                "아침에 어울리는 깔끔한 맛의 블렌드 원두.",
                "풍부한 바디감과 깊은 향이 특징인 고급 블렌드.",
                "달콤하고 부드러운 맛을 강조한 인기 블렌드 원두.",
                "짙은 로스팅 향과 묵직한 맛을 가진 다크 로스트 블렌드.",
                "부드럽고 산뜻한 맛을 제공하는 디카페인 원두."
        };

        // 상품 생성
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Thread.sleep(1);
            String productName = productNames[i % productNames.length];
            BigDecimal price = prices[i % prices.length];
            String description = descriptions[i % descriptions.length];

            Product product = Product.builder()
                    .name(productName)
                    .description(description)
                    .image(null)
                    .price(price)
                    .stock(100)
                    .status(true)
                    .build();
            productRepository.save(product);
            products.add(product);
        }

        return products;
    }

    private List<Order> createDummyOrders(final int size, final List<User> users) throws InterruptedException {
        if (orderRepository.count() > 0) return List.of();

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Thread.sleep(1);
            String orderStr = "order" + String.format("%0" + String.valueOf(size).length() + "d", i);
            User   customer = users.get(RANDOM.nextInt(users.size()));
            Order order = Order.of(customer, orderStr, 3, BigDecimal.valueOf(50000),
                                   "%s %s".formatted(customer.getAddress(), customer.getDetailAddress()));
            orderRepository.save(order);
            orders.add(order);
        }

        return orders;
    }

    private List<OrderProduct> createDummyOrderProducts(final int size, final List<Order> orders,
                                                        final List<Product> products) throws InterruptedException {
        if (orderProductRepository.count() > 0) return List.of();

        List<OrderProduct> orderProducts = new ArrayList<>();
        Map<Order, BigDecimal> orderTotalPriceMap = new HashMap<>();

        for (int i = 0; i < size; i++) {
            Thread.sleep(1);
            Product product = products.get(RANDOM.nextInt(products.size()));
            Order order = orders.get(RANDOM.nextInt(orders.size()));

            int quantity = RANDOM.nextInt(9) + 1;
            BigDecimal productTotalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));

            OrderProduct orderProduct = OrderProduct.of(order, product, quantity, product.getPrice());
            orderProductRepository.save(orderProduct);
            orderProducts.add(orderProduct);

            orderTotalPriceMap.put(order,
                    orderTotalPriceMap.getOrDefault(order, BigDecimal.ZERO).add(productTotalPrice));
        }

        for (Map.Entry<Order, BigDecimal> entry : orderTotalPriceMap.entrySet()) {
            Order order = entry.getKey();
            BigDecimal totalPrice = entry.getValue();
            order.updateTotalPrice(totalPrice);
            orderRepository.save(order);
        }

        return orderProducts;
    }

}
