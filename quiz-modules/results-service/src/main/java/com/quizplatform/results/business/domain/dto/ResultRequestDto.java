package com.quizplatform.results.business.domain.dto;

import com.quizplatform.results.business.domain.enums.ResultStatusEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultRequestDto {

    private Long id; // opzionale per update

    @NotNull(message = "sessionId is required")
    private Long sessionId;

    @NotNull(message = "score is required")
    @DecimalMin(value = "0.0", message = "score must be non-negative")
    private Double score;

    private String detailsJson;

    @NotNull(message = "status is required")
    private ResultStatusEnum status;
}