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
import java.util.List;
import java.util.Random;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class InitDummyData {

    private final Random RANDOM = new Random();

    public List<User> createDummyUsers(final UserRepository userRepository, final int size) {
        int count = (int) userRepository.count();

        List<User> users = new ArrayList<>();
        for (int i = 1 + count; i <= size + count; i++) {
            String userStr = "user" + String.format("%0" + String.valueOf(size + count).length() + "d", i);
            User user = User.builder()
                            .email(userStr + "@mail.com")
                            .password(userStr)
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

    public List<Product> createDummyProducts(final ProductRepository productRepository, final int size) {
        int count = (int) productRepository.count();

        List<Product> products = new ArrayList<>();
        for (int i = 1 + count; i <= size + count; i++) {
            String productStr = "product" + String.format("%0" + String.valueOf(size + count).length() + "d", i);
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

    public List<Order> createDummyOrders(final OrderRepository orderRepository, int size, final User customer) {
        int count = (int) orderRepository.count();

        List<Order> orders = new ArrayList<>();
        for (int i = 1 + count; i <= size + count; i++) {
            String orderStr = "order" + String.format("%0" + String.valueOf(size + count).length() + "d", i);
            Order order = Order.of(customer, orderStr, 3, BigDecimal.valueOf(50000),
                                   "%s %s".formatted(customer.getAddress(), customer.getDetailAddress()));
            orderRepository.save(order);
            orders.add(order);
        }

        return orders;
    }

    public OrderProduct createDummyOrderProduct(final OrderProductRepository orderProductRepository,
                                                final Product product, final Order order) {
        OrderProduct orderProduct = OrderProduct.of(order, product, RANDOM.nextInt(9) + 1, product.getPrice());
        orderProductRepository.save(orderProduct);
        return orderProduct;
    }

}
