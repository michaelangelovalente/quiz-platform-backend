package com.quizplatform.quiz.business.controller;

import com.quizplatform.common.business.api.BasePublicController;
import com.quizplatform.common.business.domain.dto.response.BaseListResponse;
import com.quizplatform.common.business.domain.dto.response.BaseResponse;
import com.quizplatform.common.business.domain.dto.response.GenericResponseDto;
import com.quizplatform.quiz.business.domain.dto.filter.QuizFilterDto;
import com.quizplatform.quiz.business.domain.dto.request.QuizRequestDto;
import com.quizplatform.quiz.business.domain.dto.response.QuizResponseDto;
import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import com.quizplatform.quiz.business.mapper.QuizMapper;
import com.quizplatform.quiz.business.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quizzes")
@CrossOrigin(origins = "*") // For development - should be configured properly in production
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quiz Management", description = "APIs for managing quizzes")
public class QuizController extends BasePublicController<QuizEntity, Long, QuizRequestDto, QuizResponseDto, QuizFilterDto> {
    @Getter
    final QuizService service;
    @Getter
    final QuizMapper mapper;

    private static final String RESOURCE_NAME = "QUIZ";

    @Override
    public String getResourceName() {
        return RESOURCE_NAME;
    }

    @PostMapping
    @Operation(summary = "Create a new quiz", description = "Create a new quiz with the provided details")
    public BaseResponse<QuizResponseDto> createQuiz(@Valid @RequestBody QuizRequestDto quiz) {
        return super.create(quiz);
    }

    // TODO: Implement filter parameters in getAllQuizzes endpoint
//    /**
//     * Finds quizzes by difficulty level
//     *
//     * @param difficulty the difficulty level
//     * @return list of quizzes with the specified difficulty
//     */
//    @GetMapping
//    @Operation(summary = "Get all quizzes with optional filters", description = "Retrieve a list of quizzes with optional difficulty, status, category filters and sorting by creation date")
//    public BaseListResponse<QuizResponseDto> getAllQuizzes(
//            @Parameter(description = "Filter by difficulty level")
//            @RequestParam(required = false) QuizDifficultyEnum difficulty,
//            @Parameter(description = "Filter by quiz status")
//            @RequestParam(required = false) String status,
//            @Parameter(description = "Filter by quiz category")
//            @RequestParam(required = false) String category,
//            @Parameter(description = "Only return published quizzes")
//            @RequestParam(defaultValue = "false") boolean publishedOnly) {
//        return super.findAll();
//    }
    @GetMapping
    @Operation(summary = "Get all quizzes", description = "Retrieve a list of all quizzes")
    public BaseListResponse<QuizResponseDto> getAllQuizzes() {
        return super.findAll();
    }

    @GetMapping("/{publicId}")
    @Operation(summary = "Get quiz by public ID", description = "Retrieve a quiz by its public ID")
    public BaseResponse<QuizResponseDto> getQuizByPublicId(
            @Parameter(description = "Public ID of the quiz") @PathVariable UUID publicId) {
        return super.findByPublicId(publicId);
    }


    @PutMapping("/{publicId}/update")
    @Operation(summary = "Update a quiz", description = "Update quiz data")
    public BaseResponse<QuizResponseDto> updateQuiz(
            @Parameter(description = "Public ID of the quiz") @PathVariable UUID publicId,
            @Valid @RequestBody QuizRequestDto quizRequestDto
    ) {
        return super.updateByPublicId(publicId, quizRequestDto);
    }

    @PatchMapping("/{publicId}/archive")
    @Operation(summary = "Archive a quiz", description = "Change quiz status to ARCHIVED")
    public BaseResponse<QuizResponseDto> archiveQuiz(
            @Parameter(description = "Public ID of the quiz to archive") @PathVariable UUID publicId
    ) {
        log.info("Archiving quiz with public ID: {}", publicId);
        try {
            QuizEntity archivedQuiz = service.archiveQuiz(publicId);
            QuizResponseDto response = mapper.entityToResponse(archivedQuiz);
            return BaseResponse.success(response);
        } catch (Exception e) {
            log.error("Failed to archive quiz with public ID: {}", publicId, e);
            return BaseResponse.error("ARCHIVE_ERROR", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a quiz", description = "Internal quiz deletion operation. (For public deletion, see archive operation)")
    public BaseResponse<GenericResponseDto> deleteQuiz(
            @Parameter(description = "Internal ID of the quiz") @PathVariable Long id) {
        return super.deleteById(id);
    }


    /**
     * Checks if a quiz exists by its public ID
     *
     * @param publicId the public ID to check
     * returns true if quiz exists, false otherwise
     */
    @GetMapping("/exists/{publicId}")
    @Operation(summary = "Check if quiz exists", description = "Check if a quiz exists by its public ID")
    public BaseResponse<GenericResponseDto> existsByPublicId(
            @Parameter(description = "Public ID of the quiz") @PathVariable UUID publicId) {
        return super.existsByPublic(publicId);
    }

//     Counts total number of quizzes
//     Counts quizzes by status
//    count() {
//    }
}