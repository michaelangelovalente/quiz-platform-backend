package com.quizplatform.results.business.domain.mapper;

import com.quizplatform.results.business.domain.dto.ResultRequestDto;
import com.quizplatform.results.business.domain.dto.ResultResponseDto;
import com.quizplatform.results.business.domain.entity.ResultEntity;
import org.springframework.stereotype.Component;

@Component
public class ResultMapper {

    public ResultEntity toEntity(ResultRequestDto dto) {
        ResultEntity result = new ResultEntity();
        result.setSessionId(dto.getSessionId());
        result.setScore(dto.getScore());
        result.setStatus(dto.getStatus());
        result.setDetailsJson(dto.getDetailsJson());
        return result;
    }

    public ResultResponseDto toDTO(ResultEntity result) {
        ResultResponseDto dto = new ResultResponseDto();
        dto.setId(result.getId());
        dto.setSessionId(result.getSessionId());
        dto.setScore(result.getScore());
        dto.setStatus(result.getStatus());
        dto.setDetailsJson(result.getDetailsJson());
        dto.setCreatedAt(result.getCreatedAt());
        return dto;
    }
}
