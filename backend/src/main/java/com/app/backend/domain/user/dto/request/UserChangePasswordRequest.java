package com.app.backend.domain.user.dto.request;

import com.app.backend.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class UserChangePasswordRequest {

    @NonNull
    @NotBlank
    @Email(message = "올바른 형식의 이메일 주소여야 합니다")
    private String email;

    @NonNull
    @NotBlank
    @Size(min = 8, message = "비밀번호는 최소 8글자 이상이어야 합니다.")
    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*",
            message = "비밀번호에는 1개 이상의 특수문자가 포함되어야 합니다.")
    private String newPassword;

}
