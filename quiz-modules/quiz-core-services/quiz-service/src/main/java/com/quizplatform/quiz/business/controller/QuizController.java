package com.quizplatform.quiz.business.controller;

import com.quizplatform.common.business.api.BaseController;
import com.quizplatform.common.business.domain.dto.response.BaseListResponse;
import com.quizplatform.common.business.domain.dto.response.BaseResponse;
import com.quizplatform.quiz.business.domain.dto.filter.QuizFilterDto;
import com.quizplatform.quiz.business.domain.dto.request.QuizRequestDto;
import com.quizplatform.quiz.business.domain.dto.response.QuizResponseDto;
import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import com.quizplatform.quiz.business.mapper.QuizMapper;
import com.quizplatform.quiz.business.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quizzes")
@CrossOrigin(origins = "*") // For development - should be configured properly in production
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quiz Management", description = "APIs for managing quizzes")
public class QuizController extends BaseController<QuizEntity, Long, QuizRequestDto, QuizResponseDto, QuizFilterDto> {
    @Getter final QuizService service;
    @Getter final QuizMapper mapper;

    private static final String RESOURCE_NAME = "QUIZ" ;

    @Override
    public String getResourceName(){
        return RESOURCE_NAME;
    }

    @PostMapping
    @Operation(summary = "Create a new quiz", description = "Create a new quiz with the provided details")
    public BaseResponse<QuizResponseDto> createQuiz(@Valid @RequestBody QuizRequestDto quiz) {
        log.info("Creating new quiz with title: {}", quiz.title());
        return super.create(quiz);
    }


    @GetMapping
    @Operation(summary = "Get all quizzes", description = "Retrieve a list of all quizzes")
    public BaseListResponse<QuizResponseDto> getAllQuizzes() {
        log.info("Fetching all quizzes");
        return super.findAll();
    }

//    @GetMapping("/{publicId}")
//    @Operation(summary = "Get quiz by public ID", description = "Retrieve a quiz by its public ID")
//    public BaseResponse<QuizResponseDto> getQuizById(
//            @Parameter(description = "Public ID of the quiz") @PathVariable UUID publicId) {
//        log.info("Fetching quiz with public ID: {}", publicId);
//        return super.findById()
//    }

}