package com.funchive.authserver.common.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class ResponseBody<T> implements Serializable {
    private String code;
    private String message;
    private T data;

    public static <T> ResponseBody<T> of(String code, String message) {
        return ResponseBody.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ResponseBody<T> of(String code, String message, T data) {
        return ResponseBody.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseBody<T> of(String message, T data) {
        return ResponseBody.<T>builder()
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseBody<T> of(T data) {
        return ResponseBody.<T>builder()
                .code("SUCCESS")
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ResponseBody<T> ok() {
        return ResponseBody.<T>builder()
                .code("SUCCESS")
                .message("Success")
                .data(null)
                .build();
    }
}
