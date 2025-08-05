package com.quizplatform.quiz.business.domain.dto.response;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.quiz.business.domain.enums.QuizDifficultyEnum;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record QuizResponseDto(
        UUID publicId,
        String title,
        String category,
        QuizDifficultyEnum difficulty,
        String description,
        Integer timeLimit,
        Integer passingScore,
        // ist contains DTOs that HIDE the correct answers
        List<QuestionResponseDto> questions,
        OffsetDateTime createdAt
) implements BaseDto {
}