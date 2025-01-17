package com.app.backend.global.security.dto;

import lombok.*;

public class LoginDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class LoginResponse {
        private String email;
    }
}
