package com.jtracer.api.common;

import java.time.Instant;

public record ApiErrorResponse(boolean success, Instant timestamp, ApiErrorBody error) {

    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(false, Instant.now(), new ApiErrorBody(code, message));
    }

    public record ApiErrorBody(String code, String message) {}
}
