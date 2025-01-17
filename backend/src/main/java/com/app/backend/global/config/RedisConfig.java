package com.app.backend.global.config;

import com.app.backend.global.redis.listener.RedisKeyListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
            RedisKeyListener listener) {
        System.out.println("Checking Redis Connection...");
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        System.out.println("Checking Redis Connection...");
        container.setConnectionFactory(connectionFactory);
        System.out.println("Checking Redis Connection...");
        // 만료 이벤트 리스너 추가
        container.addMessageListener(listener, new ChannelTopic("__keyevent@0__:expired"));
        System.out.println("Checking Redis Connection...");
        return container;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
