package com.app.backend.global.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class LoginDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginRequest {

        @NonNull
        @NotBlank
        @Email(message = "올바른 형식의 이메일 주소여야 합니다.")
        private String email;

        @NonNull
        @NotBlank
        @Size(min = 8, message = "비밀번호는 최소 8글자 이상이어야 합니다.")
        private String password;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class LoginResponse {
        private String email;
    }
}
