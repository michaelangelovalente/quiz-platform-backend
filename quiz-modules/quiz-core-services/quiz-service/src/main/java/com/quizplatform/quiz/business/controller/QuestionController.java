package com.quizplatform.quiz.business.controller;

import com.quizplatform.common.business.api.BaseController;
import com.quizplatform.common.business.domain.dto.request.BasePageableRequest;
import com.quizplatform.common.business.domain.dto.response.BaseListResponse;
import com.quizplatform.common.business.domain.dto.response.BaseResponse;
import com.quizplatform.common.business.domain.dto.response.GenericResponseDto;
import com.quizplatform.quiz.business.domain.dto.filter.QuestionFilterDto;
import com.quizplatform.quiz.business.domain.dto.request.QuestionRequestDto;
import com.quizplatform.quiz.business.domain.dto.response.QuestionResponseDto;
import com.quizplatform.quiz.business.domain.dto.response.QuestionReviewResponseDto;
import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
import com.quizplatform.quiz.business.mapper.QuestionMapper;
import com.quizplatform.quiz.business.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    @Operation(summary = "Create a new question", description = "Create a new question with the provided details")
    public BaseResponse<QuestionResponseDto> createQuestion(@Valid @RequestBody QuestionRequestDto question) {
        return super.create(question);
    }

    @PostMapping("/batch")
    @Operation(summary = "Create multiple questions", description = "Batch create multiple questions")
    public BaseListResponse<QuestionResponseDto> createQuestions(@Valid @RequestBody List<QuestionRequestDto> questions) {
        return super.saveAll(questions);
    }

    @GetMapping
    @Operation(summary = "Get all questions", description = "Retrieve a paginated list of all questions")
    public BaseListResponse<QuestionResponseDto> getAllQuestions(
            @Parameter(description = "Pagination parameters") BasePageableRequest pageRequest) {
        return super.findAll(new QuestionFilterDto(), pageRequest);
    }

//    @GetMapping("/all")
//    @Operation(summary = "Get all questions (no pagination)", description = "Retrieve all questions without pagination")
//    public BaseListResponse<QuestionResponseDto> getAllQuestions() {
//        return super.findAll();
//    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID", description = "Retrieve a question by its internal ID")
    public BaseResponse<QuestionResponseDto> getQuestionById(
            @Parameter(description = "Internal ID of the question") @PathVariable Long id) {
        return super.findById(id);
    }

//    @GetMapping("/{id}/review")
//    @Operation(summary = "Get question with answers for review", description = "Retrieve a question with correct answers for review purposes")
//    public BaseResponse<QuestionReviewResponseDto> getQuestionForReview(
//            @Parameter(description = "Internal ID of the question") @PathVariable Long id) {
//        log.info("Retrieving question with ID {} for review", id);
//        try {
//            QuestionEntity question = service.findById(id)
//                    .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
//            QuestionReviewResponseDto response = mapper.entityToReviewResponse(question);
//            return BaseResponse.success(response);
//        } catch (Exception e) {
//            log.error("Failed to retrieve question with ID {} for review", id, e);
//            return BaseResponse.error("REVIEW_ERROR", e.getMessage());
//        }
//    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a question", description = "Update question data")
    public BaseResponse<QuestionResponseDto> updateQuestion(
            @Parameter(description = "Internal ID of the question") @PathVariable Long id,
            @Valid @RequestBody QuestionRequestDto questionRequestDto) {
        return super.update(id, questionRequestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a question", description = "Delete a question by its internal ID")
    public BaseResponse<GenericResponseDto> deleteQuestion(
            @Parameter(description = "Internal ID of the question") @PathVariable Long id) {
        return super.deleteById(id);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "Delete multiple questions", description = "Batch delete multiple questions by their IDs")
    public BaseResponse<GenericResponseDto> deleteQuestions(
            @Parameter(description = "List of question IDs to delete") @RequestBody List<Long> ids) {
        return super.deleteAll(ids);
    }

    @GetMapping("/exists/{id}")
    @Operation(summary = "Check if question exists", description = "Check if a question exists by its internal ID")
    public BaseResponse<GenericResponseDto> existsById(
            @Parameter(description = "Internal ID of the question") @PathVariable Long id) {
        return super.exists(id);
    }

    @GetMapping("/count")
    @Operation(summary = "Count all questions", description = "Get the total count of all questions")
    public BaseResponse<GenericResponseDto> countAllQuestions() {
        return super.countAll();
    }

    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Get questions by quiz ID", description = "Retrieve all questions belonging to a specific quiz")
    public BaseListResponse<QuestionResponseDto> getQuestionsByQuizId(
            @Parameter(description = "Internal ID of the quiz") @PathVariable Long quizId) {
        log.info("Retrieving questions for quiz with ID: {}", quizId);
        try {
            List<QuestionEntity> questions = service.findByQuizId(quizId);
            List<QuestionResponseDto> responses = mapper.listEntityToListResponse(questions);
            return BaseListResponse.success(responses);
        } catch (Exception e) {
            log.error("Failed to retrieve questions for quiz with ID: {}", quizId, e);
            throw new RuntimeException("Failed to retrieve questions for quiz", e);
        }
    }

    @GetMapping("/quiz/{quizId}/review")
    @Operation(summary = "Get questions with answers by quiz ID for review", description = "Retrieve all questions with correct answers for a specific quiz for review purposes")
    public BaseListResponse<QuestionReviewResponseDto> getQuestionsByQuizIdForReview(
            @Parameter(description = "Internal ID of the quiz") @PathVariable Long quizId) {
        log.info("Retrieving questions for review for quiz with ID: {}", quizId);
        try {
            List<QuestionEntity> questions = service.findByQuizId(quizId);
            List<QuestionReviewResponseDto> responses = questions.stream()
                    .map(mapper::entityToReviewResponse)
                    .toList();
            return BaseListResponse.success(responses);
        } catch (Exception e) {
            log.error("Failed to retrieve questions for review for quiz with ID: {}", quizId, e);
            throw new RuntimeException("Failed to retrieve questions for review", e);
        }
    }
}