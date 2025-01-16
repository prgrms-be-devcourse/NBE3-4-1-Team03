package com.app.backend.domain.user.dto.request;

import com.app.backend.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class UserInfoModifyRequest {

    @NonNull
    @NotBlank
    @Length(min = 2, max = 10)
    private String name;

    @NonNull
    @NotBlank(message = "주소는 필수 입력값입니다")
    private String address;

    @NonNull
    @NotBlank(message = "주소는 필수 입력값입니다")
    private String detailAddress;

    @NonNull
    @NotBlank
    @Pattern(regexp = "^(010\\d{8}|011\\d{7})$",
            message = "전화번호는 대시없이 입력하셔야 합니다.")
    private String phone;

    public UserInfoModifyRequest(User user) {
        this.name = user.getName();
        this.address = user.getAddress();
        this.detailAddress = user.getDetailAddress();
        this.phone = user.getPhone();
    }

}
