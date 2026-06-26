package com.jtracer.api;

import com.jtracer.api.common.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.jtracer.api")
public class ApiExceptionHandler {

    @ExceptionHandler(NoHealthDataException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHealthData(NoHealthDataException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiErrorResponse.of("NO_HEALTH_DATA", ex.getMessage()));
    }
}
