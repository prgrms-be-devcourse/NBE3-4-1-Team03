package com.app.backend.domain.user.dto.request;


import com.app.backend.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequest {

    @NonNull
    @NotBlank
    @Email(message = "올바른 형식의 이메일 주소여야 합니다")
    private String email;

    @NonNull
    @NotBlank
    @Size(min = 8, message = "비밀번호는 최소 8글자 이상이어야 합니다.")
    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*",
            message = "비밀번호에는 1개 이상의 특수문자가 포함되어야 합니다.")
    private String password;

    @NonNull
    @NotBlank
    @Length(min = 2, max = 10)
    private String name;

    @NonNull
    @NotBlank(message = "주소는 필수 입력값입니다")
    private String address;

    @NonNull
    @NotBlank(message = "상세주소는 필수 입력값입니다")
    private String detailAddress;

    @NonNull
    @NotBlank
    @Pattern(regexp = "^(010\\d{8}|011\\d{7})$",
            message = "전화번호는 대시없이 입력하셔야 합니다.")
    private String phone;

    public UserSignupRequest(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.address = user.getAddress();
        this.detailAddress = user.getDetailAddress();
        this.phone = user.getPhone();
    }

}
