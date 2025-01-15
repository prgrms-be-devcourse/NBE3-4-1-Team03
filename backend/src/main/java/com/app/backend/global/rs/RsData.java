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
    private final String  message;
    @NonNull
    private final String  code;
    private final T       data;

    public RsData(final boolean isSuccess, final String message, final String code) {
        this(isSuccess, message, code, null);
    }

}
