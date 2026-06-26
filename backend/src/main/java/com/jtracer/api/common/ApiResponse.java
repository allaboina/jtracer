package com.jtracer.api.common;

import java.time.Instant;

public record ApiResponse<T>(boolean success, Instant timestamp, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, Instant.now(), data);
    }
}
