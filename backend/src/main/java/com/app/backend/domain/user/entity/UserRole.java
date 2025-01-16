package com.app.backend.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_ADMIN("관리자"),
    ROLE_USER("일반 사용자");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public boolean isAdmin() {
        return this == ROLE_ADMIN;
    }

    public static UserRole getDefaultRole() {
        return ROLE_USER;
    }

}
