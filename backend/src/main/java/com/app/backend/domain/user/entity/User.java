package com.app.backend.domain.user.entity;

import com.app.backend.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_email", unique = true, nullable = false)
    @Email(message = "")
    private String email;

    @Column(name = "user_password", nullable = false)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "user_address", nullable = false)
    private String address;

    @Column(name = "user_address_detail", nullable = false)
    private String detailAddress;

    @Column(name = "user_phone", nullable = false)
    private String phone;

    @Column(name = "user_status", nullable = false)
    private String status = "ACTIVATED";

    @Column(name = "user_role", nullable = false)
    private String role = "ROLE_USER";

    public void modifyInfo(String name, String address, String detailAddress, String phone) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
        if (detailAddress != null) this.detailAddress = detailAddress;
        if (phone != null) this.phone = phone;
    }

    public void changePassword(String password) {
        this.password = password;
    }

}
