package com.quizplatform.quiz.business.mapper;

import com.quizplatform.common.business.mapper.BaseMapper;
import com.quizplatform.common.system.utils.CommonUtils;
import com.quizplatform.quiz.business.domain.dto.filter.QuizFilterDto;
import com.quizplatform.quiz.business.domain.dto.request.QuizRequestDto;
import com.quizplatform.quiz.business.domain.dto.response.QuizResponseDto;
import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class QuizMapper implements BaseMapper<QuizEntity, QuizRequestDto, QuizResponseDto, QuizFilterDto> {

    private final QuestionMapper questionMapper;

    public QuizMapper(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @Override
    public QuizResponseDto entityToResponse(QuizEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        return QuizResponseDto.builder()
                .publicId(entity.getPublicId())
                .title(entity.getTitle())
                .category(entity.getCategory())
                .difficulty(entity.getDifficulty())
                .description(entity.getDescription())
                .timeLimit(entity.getTimeLimit())
                .passingScore(entity.getPassingScore())
                .questions(CommonUtils.nonEmptyNorNull(entity.getQuestions()) ? 
                    entity.getQuestions().stream()
                            .map(questionMapper::entityToResponse)
                            .toList() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public QuizEntity responseToEntity(QuizResponseDto dto) {
        return null;
    }

    @Override
    public QuizEntity requestToEntity(QuizRequestDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }

        QuizEntity quiz = QuizEntity.builder()
                .title(dto.title())
                .category(dto.category())
                .difficulty(dto.difficulty())
                .description(dto.description())
                .timeLimit(dto.timeLimit())
                .passingScore(dto.passingScore())
                .status(dto.status())
                .createdBy(dto.createdBy())
                .build();

        if(CommonUtils.nonEmptyNorNull(dto.questions())){
            dto.questions().forEach(
                    questionDto -> {
                        QuestionEntity questionEntity = questionMapper.requestToEntity(questionDto);
                        quiz.addQuestion(questionEntity);
                    }
            );
        }

        return quiz;
    }

    @Override
    public List<QuizResponseDto> listEntityToListResponse(List<QuizEntity> list) {
        if (CommonUtils.isEmptyOrNull(list)) {
            return List.of();
        }
        return list.stream()
                .map(this::entityToResponse)
                .toList();
    }

    @Override
    public List<QuizEntity> listRequestToListEntity(List<QuizRequestDto> list) {
        if (CommonUtils.isEmptyOrNull(list)) {
            return List.of();
        }
        return list.stream()
                .map(this::requestToEntity)
                .toList();
    }

    @Override
    public void updateEntityFromRequest(QuizRequestDto dto, QuizEntity entity) {
        if (CommonUtils.anyNull(dto, entity)) {
            return;
        }

        if (Objects.nonNull(dto.title())) {
            entity.setTitle(dto.title());
        }
        if (Objects.nonNull(dto.category())) {
            entity.setCategory(dto.category());
        }
        if (Objects.nonNull(dto.difficulty())) {
            entity.setDifficulty(dto.difficulty());
        }
        if (Objects.nonNull(dto.description()) && !CommonUtils.isEmptyOrBlank(dto.description())) {
            entity.setDescription(dto.description());
        }
        if (Objects.nonNull(dto.timeLimit())) {
            entity.setTimeLimit(dto.timeLimit());
        }
        if (Objects.nonNull(dto.passingScore())) {
            entity.setPassingScore(dto.passingScore());
        }
        if (Objects.nonNull(dto.status()) && !CommonUtils.isEmptyOrBlank(dto.status())) {
            entity.setStatus(dto.status());
        }
        if (Objects.nonNull(dto.createdBy()) && !CommonUtils.isEmptyOrBlank(dto.createdBy())) {
            entity.setCreatedBy(dto.createdBy());
        }
    }


    @Override
    public UUID extractPublicId(QuizEntity entity) {
        return BaseMapper.super.extractPublicId(entity);
    }
}
