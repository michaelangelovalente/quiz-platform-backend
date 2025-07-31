package com.quizplatform.quiz.business.controller;

import com.quizplatform.common.business.api.BaseController;
import com.quizplatform.quiz.business.domain.dto.filter.QuestionFilterDto;
import com.quizplatform.quiz.business.domain.dto.request.QuestionRequestDto;
import com.quizplatform.quiz.business.domain.dto.response.QuestionResponseDto;
import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
import com.quizplatform.quiz.business.mapper.QuestionMapper;
import com.quizplatform.quiz.business.service.QuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questions")
@CrossOrigin(origins = "*") // For development - should be configured properly in production
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Question Management", description = "APIs for managing questions")
public class QuestionController extends BaseController<QuestionEntity, Long, QuestionRequestDto, QuestionResponseDto, QuestionFilterDto> {

    @Getter private final QuestionService service;
    @Getter private final QuestionMapper mapper;

    private static final String RESOURCE_NAME = "QUESTION";

    @Override
    public String getResourceName(){
        return RESOURCE_NAME;
    }


}