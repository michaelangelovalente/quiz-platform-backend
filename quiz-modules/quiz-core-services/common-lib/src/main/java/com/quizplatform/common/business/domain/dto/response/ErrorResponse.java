package com.quizplatform.common.business.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Objects;

/**
 * SError response for API error handling.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    String errorCode,
    String message,
    String path,
    int statusCode,
    Instant timestamp
) {
    
    public ErrorResponse {
        if (Objects.isNull(timestamp)) {
            timestamp = Instant.now();
        }
    }
    
    public static ErrorResponse of(String errorCode, String message, int statusCode) {
        return new ErrorResponse(errorCode, message, null, statusCode, Instant.now());
    }

    // TODO: REPLACE WITH COMMON EXCEPTION CODES
    public static ErrorResponse badRequest(String message) {
        return of("BAD_REQUEST", message, 400);
    }
    
    public static ErrorResponse notFound(String resource) {
        return of("NOT_FOUND", String.format("Resource not found: %s", resource), 404);
    }
    
    public static ErrorResponse internal(String message) {
        return of("INTERNAL_ERROR", message, 500);
    }
    
    // With path for request context
    public ErrorResponse withPath(String path) {
        return new ErrorResponse(errorCode, message, path, statusCode, timestamp);
    }
}