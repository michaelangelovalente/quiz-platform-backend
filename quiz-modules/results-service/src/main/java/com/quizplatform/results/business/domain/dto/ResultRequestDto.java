package com.quizplatform.results.business.domain.dto;

import com.quizplatform.results.business.domain.enums.ResultStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResultRequestDto {
    @NotNull
    private Long sessionId;

    @NotNull
    private Double score;

    @NotBlank
    private ResultStatusEnum status;
}
