package com.app.backend.global.redis.listener;

import com.app.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisKeyDeletionListener implements MessageListener {
    private final ProductService productService;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = new String(message.getBody());
        System.out.println("키가 삭제됬어요: " + key);

        System.out.println("재고를 다시 돌려놓습니다.: " + key);
    }
}
