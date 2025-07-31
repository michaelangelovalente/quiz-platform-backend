package com.quizplatform.quiz.business.domain.dto.filter;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.quiz.business.domain.enums.QuizDifficultyEnum;

import java.util.Set;
import java.util.UUID;

public class QuizFilterDto implements BaseDto {
    private Set<String> categories;
    private Set<QuizDifficultyEnum> difficulties;
    private Integer minQuestions;
    private Integer maxQuestions;
    private Set<String> tags;
    private UUID authorPublicId;
    private Boolean hasMultipleAnswers;
    private Integer minPassingScore;

}
