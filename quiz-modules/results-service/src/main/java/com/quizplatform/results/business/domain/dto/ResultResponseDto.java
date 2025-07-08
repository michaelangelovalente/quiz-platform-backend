package com.quizplatform.results.business.domain.dto;

import com.quizplatform.results.business.domain.enums.ResultStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultResponseDto {
    @NotNull
    private Long sessionId;

    @NotNull
    private Double score;

    @NotBlank
    private ResultStatusEnum status;

}
