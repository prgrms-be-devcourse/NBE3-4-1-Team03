package com.app.backend.global.rs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@JsonInclude(Include.NON_NULL)
@Getter
@RequiredArgsConstructor
public class RsData<T> {

    @NonNull
    private final Boolean isSuccess;
    @NonNull
    private final String  code;
    @NonNull
    private final String  message;
    private final T       data;

    public RsData(@NonNull final Boolean isSuccess, @NonNull final String code, @NonNull final String message) {
        this(isSuccess, code, message, null);
    }

}
