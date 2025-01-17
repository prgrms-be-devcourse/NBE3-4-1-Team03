package com.app.backend.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserStatus {

    ACTIVATED("계정 활성화 상태"),
    DELETED("계정 삭제 상태");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public boolean isActivated() {
        return this == ACTIVATED;
    }

    public static UserStatus getDefaultStatus() {
        return ACTIVATED;
    }

}
