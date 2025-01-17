package com.app.backend.global.initdata;

import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.repository.OrderRepository;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.repository.ProductRepository;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    private final UserRepository    userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository   orderRepository;
    private final PasswordEncoder   passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    void init() throws InterruptedException {
        List<User>    dummyUsers    = createDummyUsers(5);
        List<Product> dummyProducts = createDummyProducts(10);
        List<Order>   dummyOrders   = createDummyOrders(20, dummyUsers);
    }

    private List<User> createDummyUsers(final int size) throws InterruptedException {
        if (userRepository.count() > 0) return List.of();

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Thread.sleep(1);
            String userStr = "user" + String.format("%0" + String.valueOf("size").length() + "d", i);
            User user = User.builder()
                            .email(userStr + "@mail.com")
                            .password(passwordEncoder.encode(userStr))
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

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Thread.sleep(1);
            String productStr = "product" + String.format("%0" + String.valueOf("size").length() + "d", i);
            Product product = Product.builder()
                                     .name(productStr)
                                     .description(productStr + " description")
                                     .image(null)
                                     .price(BigDecimal.valueOf(10000))
                                     .stock(10)
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
            String orderStr = "order" + String.format("%0" + String.valueOf("size").length() + "d", i);
            User   customer = users.get(RANDOM.nextInt(users.size()));
            Order order = Order.of(customer, orderStr, 3, BigDecimal.valueOf(50000),
                                   "%s %s".formatted(customer.getAddress(), customer.getDetailAddress()));
            orderRepository.save(order);
            orders.add(order);
        }

        return orders;
    }

}
