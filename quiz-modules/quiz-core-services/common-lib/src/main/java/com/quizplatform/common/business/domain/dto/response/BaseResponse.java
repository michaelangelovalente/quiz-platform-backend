package com.quizplatform.common.business.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quizplatform.common.business.domain.BaseDto;

import java.time.Instant;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseResponse<DTO extends BaseDto>(
        boolean success,
        DTO data,
        String message,
        String errorCode,
        Instant timestamp
) {

    public BaseResponse {
        if (Objects.isNull(timestamp)) {
            timestamp = Instant.now();
        }
    }

    public static <DTO extends BaseDto> BaseResponse<DTO> success(DTO data) {
        return new BaseResponse<>(true, data, null, null, Instant.now());
    }

    public static <DTO extends BaseDto> BaseResponse<DTO> error(String message) {
        return new BaseResponse<>(false, null, message, "ERROR", Instant.now());
    }

    public static <DTO extends BaseDto> BaseResponse<DTO> error(String errorCode, String message) {
        return new BaseResponse<>(false, null, message, errorCode, Instant.now());
    }

    public static <DTO extends BaseDto> BaseResponse<DTO> notFound(String resource) {
        return error("NOT_FOUND", String.format("Resource not found: %s", resource));
    }

    public static <DTO extends BaseDto> BaseResponse<DTO> badRequest(String message) {
        return error("BAD_REQUEST", message);
    }

    public static <DTO extends BaseDto> BaseResponse<DTO> internal(String message) {
        return error("INTERNAL_ERROR", message);
    }

    public static BaseResponse<GenericResponseDto> successEmpty() {
        return new BaseResponse<>(true, null, null, null, Instant.now());
    }

    public static BaseResponse<GenericResponseDto> successEmpty(String message) {
        return new BaseResponse<>(true, null, message, null, Instant.now());
    }

    public static BaseResponse<GenericResponseDto> deleted() {
        return successEmpty("Resource deleted successfully");
    }

    public static BaseResponse<GenericResponseDto> deleted(String resource) {
        return successEmpty(String.format("%s deleted successfully", resource));
    }



    public static BaseResponse<GenericResponseDto> count(Long count) {
        return successLong(count, String.format("Count: %d", count));
    }

    public static BaseResponse<GenericResponseDto> deleteAll(Long count) {
        return successLong(count, String.format("%d records deleted successfully", count));
    }


    public static BaseResponse<GenericResponseDto> exists(Boolean exists) {
        return successBoolean(exists);
    }

    public static BaseResponse<GenericResponseDto> successGeneric(Object value) {
        return success(new GenericResponseDto(value));
    }

    public static BaseResponse<GenericResponseDto> successGeneric(Object value, String message) {
        return new BaseResponse<>(true, new GenericResponseDto(value), message, null, Instant.now());
    }

    // --------- Helper methods ----------
    private static BaseResponse<GenericResponseDto> successLong(Long value, String message) {
        return new BaseResponse<>(true, new GenericResponseDto(value), message, null, Instant.now());
    }

    private static BaseResponse<GenericResponseDto> successBoolean(Boolean value) {
        return success(new GenericResponseDto(value));
    }

    private static BaseResponse<GenericResponseDto> successLong(Long value) {
        return success(new GenericResponseDto(value));
    }
}