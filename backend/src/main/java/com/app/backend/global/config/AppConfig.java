package com.app.backend.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public static String getSiteFrontUrl() {
        return "http://localhost:3000";
    }

}
