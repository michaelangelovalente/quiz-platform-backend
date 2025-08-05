package com.quizplatform.quiz.business.mapper;

import com.quizplatform.common.business.mapper.BaseMapper;
import com.quizplatform.common.system.utils.CommonUtils;
import com.quizplatform.quiz.business.domain.dto.filter.QuestionFilterDto;
import com.quizplatform.quiz.business.domain.dto.request.QuestionRequestDto;
import com.quizplatform.quiz.business.domain.dto.response.QuestionResponseDto;
import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class QuestionMapper implements BaseMapper<QuestionEntity, QuestionRequestDto, QuestionResponseDto, QuestionFilterDto> {

    @Override
    public QuestionResponseDto entityToResponse(QuestionEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        return QuestionResponseDto.builder()
                .id(entity.getId())
                .text(entity.getText())
                .type(entity.getType())
                .options(entity.getOptions())
                .points(entity.getPoints())
                .build();
    }

    @Override
    public QuestionEntity responseToEntity(QuestionResponseDto dto) {
        return null;
    }

    @Override
    public QuestionEntity requestToEntity(QuestionRequestDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }

        return QuestionEntity.builder()
                .text(dto.text())
                .type(dto.type())
                .options(dto.options())
                .correctAnswer(dto.correctAnswer())
                .points(dto.points())
                .explanation(dto.explanation())
                .build();
    }

    @Override
    public List<QuestionResponseDto> listEntityToListResponse(List<QuestionEntity> list) {
        if (CommonUtils.isEmptyOrNull(list)) {
            return List.of();
        }
        return list.stream()
                .map(this::entityToResponse)
                .toList();
    }

    @Override
    public List<QuestionEntity> listRequestToListEntity(List<QuestionRequestDto> list) {
        if (CommonUtils.isEmptyOrNull(list)) {
            return List.of();
        }
        return list.stream()
                .map(this::requestToEntity)
                .toList();
    }

    @Override
    public void updateEntityFromRequest(QuestionRequestDto dto, QuestionEntity entity) {
        if (CommonUtils.anyNull(dto, entity)) {
            return;
        }

        if (Objects.nonNull(dto.text()) && !CommonUtils.isEmptyOrBlank(dto.text())) {
            entity.setText(dto.text());
        }
        if (Objects.nonNull(dto.type())) {
            entity.setType(dto.type());
        }
        if (CommonUtils.nonEmptyNorNull(dto.options())) {
            entity.setOptions(dto.options());
        }
        if (CommonUtils.nonEmptyNorNull(dto.correctAnswer())) {
            entity.setCorrectAnswer(dto.correctAnswer());
        }
        if (Objects.nonNull(dto.points())) {
            entity.setPoints(dto.points());
        }
        if (Objects.nonNull(dto.explanation()) && !CommonUtils.isEmptyOrBlank(dto.explanation())) {
            entity.setExplanation(dto.explanation());
        }
    }

}
