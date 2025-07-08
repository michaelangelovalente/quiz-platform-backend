package com.quizplatform.results.business.domain.dto;

import com.quizplatform.results.business.domain.enums.ResultStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResultResponseDto {
    private Long Id;
    private Long sessionId;
    private Double score;
    private String detailsJson;
    private ResultStatusEnum status;
    private LocalDateTime createdAt;

}
