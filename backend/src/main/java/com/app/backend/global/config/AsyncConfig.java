package com.app.backend.global.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * PackageName : com.app.backend.global.config
 * FileName    : AsyncConfig
 * Author      : loadingKKamo21
 * Date        : 25. 1. 18.
 * Description :
 */
@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean
    public Executor mailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MailTask-");
        executor.setKeepAliveSeconds(60);
        executor.initialize();
        return executor;
    }

}
