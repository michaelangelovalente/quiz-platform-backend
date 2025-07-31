package com.quizplatform.quiz.business.domain.dto.request;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.quiz.business.domain.enums.QuizDifficultyEnum;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record QuizRequestDto(
        Long id,
        UUID publicId,
        String title,
        String category,
        QuizDifficultyEnum difficulty,
        String description,
        Integer timeLimit,
        Integer passingScore,
        String status,
        String createdBy,
        List<QuestionRequestDto> questions,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) implements BaseDto {
}