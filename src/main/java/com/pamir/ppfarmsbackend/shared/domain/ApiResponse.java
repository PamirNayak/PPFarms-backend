package com.pamir.ppfarmsbackend.shared.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private String path;
    private String traceId;
    private T data;
    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public static <T> ApiResponse<T> success(T data) {
        String currentTraceId = org.slf4j.MDC.get("traceId");
        return ApiResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message("Operation completed successfully")
                .traceId(currentTraceId)
                .data(data)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        String currentTraceId = org.slf4j.MDC.get("traceId");
        return ApiResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message(message)
                .traceId(currentTraceId)
                .data(data)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        String currentTraceId = org.slf4j.MDC.get("traceId");
        return ApiResponse.<T>builder()
                .success(false)
                .code("ERROR")
                .message(message)
                .traceId(currentTraceId)
                .data(null)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String code, String path) {
        String currentTraceId = org.slf4j.MDC.get("traceId");
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .path(path)
                .traceId(currentTraceId)
                .data(null)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String code, String path, T data) {
        String currentTraceId = org.slf4j.MDC.get("traceId");
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .path(path)
                .traceId(currentTraceId)
                .data(data)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
