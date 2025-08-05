package com.quizplatform.common.business.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseListResponse<T>(
    boolean success,
    List<T> data,
    String message,
    String errorCode,
    PageInfo pageInfo,
    Instant timestamp
) {
    
    public record PageInfo(
        int page,
        int size,
        long totalElements,
        int totalPages
    ) {
        public static PageInfo of(int page, int size, long totalElements) {
            int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
            return new PageInfo(page, size, totalElements, Math.max(totalPages, 1));
        }
    }
    
    public BaseListResponse {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        if (data == null) {
            data = List.of();
        }
    }
    
    public static <T> BaseListResponse<T> success(List<T> data, int page, int size, long totalElements) {
        return new BaseListResponse<>(
            true, 
            data, 
            null, 
            null, 
            PageInfo.of(page, size, totalElements), 
            Instant.now()
        );
    }
    
    public static <T> BaseListResponse<T> success(List<T> data) {
        return new BaseListResponse<>(true, data, null, null, null, Instant.now());
    }
    public static <T> BaseListResponse<T> error(String message) {
        return new BaseListResponse<>(false, List.of(), message, "ERROR", null, Instant.now());
    }
    
    public static <T> BaseListResponse<T> error(String errorCode, String message) {
        return new BaseListResponse<>(false, List.of(), message, errorCode, null, Instant.now());
    }
}