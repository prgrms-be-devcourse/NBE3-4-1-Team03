package com.app.backend.global.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * PackageName : com.app.backend.global.config
 * FileName    : TestMailConfig
 * Author      : loadingKKamo21
 * Date        : 25. 1. 19.
 * Description :
 */
@TestConfiguration
public class TestMailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

}
