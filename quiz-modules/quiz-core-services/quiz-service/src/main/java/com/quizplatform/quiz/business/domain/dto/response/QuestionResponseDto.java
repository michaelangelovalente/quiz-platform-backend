package com.quizplatform.quiz.business.domain.dto.response;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.quiz.business.domain.enums.QuestionTypeEnum;
import lombok.Builder;

import java.util.List;

@Builder
public record QuestionResponseDto(
        Long id,
        String text,
        QuestionTypeEnum type,
        List<String> options,
        Integer points
) implements BaseDto {
}