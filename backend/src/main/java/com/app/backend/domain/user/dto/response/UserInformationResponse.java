package com.app.backend.domain.user.dto.response;

import com.app.backend.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserInformationResponse {

    private String name;

    private String email;

    private String status;

    private String role;

    private String phone;

    private LocalDateTime created_date;

    private LocalDateTime modified_date;

    public UserInformationResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.status = user.getStatus();
        this.role = user.getRole();
        this.phone = user.getPhone();
        this.created_date = user.getCreatedDate();
        this.modified_date = user.getModifiedDate();
    }

}
