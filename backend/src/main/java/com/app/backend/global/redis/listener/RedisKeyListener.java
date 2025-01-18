package com.app.backend.global.redis.listener;

import com.app.backend.domain.product.service.ProductService;
import com.app.backend.global.redis.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisKeyListener implements MessageListener {
    private final ProductService productService;
    private final RedisRepository redisRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        if(!message.toString().startsWith("order-")) return;
        String expiredKey = message.toString().split("-")[1];
        Long product_id = Long.parseLong(expiredKey.split("_")[1]);
        Integer amount = (Integer) redisRepository.get("orderValue-"+expiredKey);
//        Integer a = productService.findById(product_id).getStock();
        productService.restoreStock(product_id,amount);
//        Integer b = productService.findById(product_id).getStock();
//        System.out.printf("처리전 : %d%n", a);
//        System.out.printf("처리후 : %d%n", b);
    }
}