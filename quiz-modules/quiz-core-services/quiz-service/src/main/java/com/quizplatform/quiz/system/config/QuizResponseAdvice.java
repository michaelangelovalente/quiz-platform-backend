package com.quizplatform.quiz.system.config;

import com.quizplatform.common.business.domain.dto.response.BaseResponse;
import com.quizplatform.common.system.config.BaseControllerResponseAdvice;
import com.quizplatform.quiz.business.domain.dto.response.QuizResponseDto;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1) // Higher priority than base
public class QuizResponseAdvice extends BaseControllerResponseAdvice {

    @Override
    protected void customizeResponse(
            BaseResponse<?> body,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        // Quiz-specific customizations
        if (request.getURI().getPath().contains("/publish") && body.success()) {
            response.setStatusCode(HttpStatus.ACCEPTED); // 202 for async publish
        }

        // Add quiz-specific headers
        if (body.data() instanceof QuizResponseDto quizResponse) {
            response.getHeaders().add("X-Quiz-Category", quizResponse.category());
        }
    }
}