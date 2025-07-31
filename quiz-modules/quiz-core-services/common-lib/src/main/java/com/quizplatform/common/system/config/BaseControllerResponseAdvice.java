package com.quizplatform.common.system.config;

import com.quizplatform.common.business.domain.dto.response.BaseResponse;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

@RestControllerAdvice
public abstract class BaseControllerResponseAdvice implements ResponseBodyAdvice<BaseResponse<?>> {

    @Override
    public boolean supports(MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return BaseResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public BaseResponse<?> beforeBodyWrite(
            BaseResponse<?> body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response) {

        if (Objects.nonNull( body) && !body.success()) {
            mapErrorToHttpStatus(body, response);
        } else if (request.getMethod().equals(HttpMethod.POST)) {
            response.setStatusCode(HttpStatus.CREATED);
        }

        // Custom resp
        customizeResponse(body, request, response);

        return body;
    }

    protected void mapErrorToHttpStatus(BaseResponse<?> body, ServerHttpResponse response) {
        HttpStatus status = switch (body.errorCode()) {
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED;
            case "FORBIDDEN" -> HttpStatus.FORBIDDEN;
            case "CONFLICT" -> HttpStatus.CONFLICT;
            case "INTERNAL_ERROR" -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };
        response.setStatusCode(status);
    }

    // Hook for service-specific customization (If need to modify resp based on specific microserv)
    protected abstract void customizeResponse(
            BaseResponse<?> body,
            ServerHttpRequest request,
            ServerHttpResponse response
    );
}