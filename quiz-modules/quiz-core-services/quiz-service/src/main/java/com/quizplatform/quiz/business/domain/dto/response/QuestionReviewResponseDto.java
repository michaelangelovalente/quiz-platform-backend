package com.quizplatform.quiz.business.domain.dto.response;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.quiz.business.domain.enums.QuestionTypeEnum;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record QuestionReviewResponseDto(
        Long id,
        String text,
        QuestionTypeEnum type,
        List<String> options,
        List<String> correctAnswer,
        Integer points,
        String explanation,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) implements BaseDto {
}