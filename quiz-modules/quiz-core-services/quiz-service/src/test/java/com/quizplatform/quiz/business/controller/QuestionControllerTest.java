//package com.quizplatform.quiz.business.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.quizplatform.common.business.domain.dto.response.BaseResponse;
//import com.quizplatform.quiz.business.domain.dto.request.QuestionRequestDto;
//import com.quizplatform.quiz.business.domain.dto.response.QuestionResponseDto;
//import com.quizplatform.quiz.business.domain.dto.response.QuestionReviewResponseDto;
//import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
//import com.quizplatform.quiz.business.domain.entity.QuizEntity;
//import com.quizplatform.quiz.business.domain.enums.QuestionTypeEnum;
//import com.quizplatform.quiz.business.mapper.QuestionMapper;
//import com.quizplatform.quiz.business.service.QuestionService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(QuestionController.class)
//class QuestionControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private QuestionService questionService;
//
//    @MockBean
//    private QuestionMapper questionMapper;
//
//    private QuestionEntity sampleQuestion;
//    private QuestionRequestDto sampleRequestDto;
//    private QuestionResponseDto sampleResponseDto;
//    private QuestionReviewResponseDto sampleReviewResponseDto;
//
//    @BeforeEach
//    void setUp() {
//        QuizEntity sampleQuiz = QuizEntity.builder()
//                .id(1L)
//                .title("Java Basics")
//                .build();
//
//        sampleQuestion = QuestionEntity.builder()
//                .id(1L)
//                .text("What is Java?")
//                .type(QuestionTypeEnum.MULTIPLE_CHOICE)
//                .options(Arrays.asList("Programming Language", "Coffee", "Island"))
//                .correctAnswer(Arrays.asList("Programming Language"))
//                .points(10)
//                .explanation("Java is a programming language")
//                .quiz(sampleQuiz)
//                .build();
//
//        sampleRequestDto = QuestionRequestDto.builder()
//                .text("What is Java?")
//                .type(QuestionTypeEnum.MULTIPLE_CHOICE)
//                .options(Arrays.asList("Programming Language", "Coffee", "Island"))
//                .correctAnswer(Arrays.asList("Programming Language"))
//                .points(10)
//                .explanation("Java is a programming language")
//                .build();
//
//        sampleResponseDto = QuestionResponseDto.builder()
//                .id(1L)
//                .text("What is Java?")
//                .type(QuestionTypeEnum.MULTIPLE_CHOICE)
//                .options(Arrays.asList("Programming Language", "Coffee", "Island"))
//                .points(10)
//                .build();
//
//        sampleReviewResponseDto = QuestionReviewResponseDto.builder()
//                .id(1L)
//                .text("What is Java?")
//                .type(QuestionTypeEnum.MULTIPLE_CHOICE)
//                .options(Arrays.asList("Programming Language", "Coffee", "Island"))
//                .correctAnswer(Arrays.asList("Programming Language"))
//                .points(10)
//                .explanation("Java is a programming language")
//                .build();
//    }
//
//    @Test
//    @DisplayName("Should create question successfully")
//    void createQuestion_ShouldReturnCreatedQuestion() throws Exception {
//        // Given
//        when(questionMapper.requestToEntity(any())).thenReturn(sampleQuestion);
//        when(questionService.save(any())).thenReturn(sampleQuestion);
//        when(questionMapper.entityToResponse(any())).thenReturn(sampleResponseDto);
//
//        // When & Then
//        mockMvc.perform(post("/api/v1/questions")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data.text").value("What is Java?"))
//                .andExpect(jsonPath("$.data.type").value("MULTIPLE_CHOICE"))
//                .andExpect(jsonPath("$.data.points").value(10));
//    }
//
//    @Test
//    @DisplayName("Should get question by ID successfully")
//    void getQuestionById_ShouldReturnQuestion() throws Exception {
//        // Given
//        when(questionService.findById(1L)).thenReturn(Optional.of(sampleQuestion));
//        when(questionMapper.entityToResponse(any())).thenReturn(sampleResponseDto);
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/questions/{id}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data.id").value(1))
//                .andExpect(jsonPath("$.data.text").value("What is Java?"));
//    }
//
//    @Test
//    @DisplayName("Should return 404 when question not found")
//    void getQuestionById_ShouldReturnNotFound_WhenQuestionNotExists() throws Exception {
//        // Given
//        when(questionService.findById(999L)).thenReturn(Optional.empty());
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/questions/{id}", 999L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("QUESTION 999 not found"));
//    }
//
//    @Test
//    @DisplayName("Should get question for review with correct answers")
//    void getQuestionForReview_ShouldReturnQuestionWithAnswers() throws Exception {
//        // Given
//        when(questionService.findById(1L)).thenReturn(Optional.of(sampleQuestion));
//        when(questionMapper.entityToReviewResponse(any())).thenReturn(sampleReviewResponseDto);
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/questions/{id}/review", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data.correctAnswer[0]").value("Programming Language"))
//                .andExpect(jsonPath("$.data.explanation").value("Java is a programming language"));
//    }
//
//    @Test
//    @DisplayName("Should get questions by quiz ID")
//    void getQuestionsByQuizId_ShouldReturnQuestions() throws Exception {
//        // Given
//        List<QuestionEntity> questions = Arrays.asList(sampleQuestion);
//        List<QuestionResponseDto> responseList = Arrays.asList(sampleResponseDto);
//
//        when(questionService.findByQuizId(1L)).thenReturn(questions);
//        when(questionMapper.listEntityToListResponse(questions)).thenReturn(responseList);
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/questions/quiz/{quizId}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data").isArray())
//                .andExpect(jsonPath("$.data[0].text").value("What is Java?"));
//    }
//
//    @Test
//    @DisplayName("Should update question successfully")
//    void updateQuestion_ShouldReturnUpdatedQuestion() throws Exception {
//        // Given
//        when(questionService.update(eq(1L), any())).thenReturn(sampleQuestion);
//        when(questionMapper.entityToResponse(any())).thenReturn(sampleResponseDto);
//
//        // When & Then
//        mockMvc.perform(put("/api/v1/questions/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data.text").value("What is Java?"));
//    }
//
//    @Test
//    @DisplayName("Should delete question successfully")
//    void deleteQuestion_ShouldReturnSuccessResponse() throws Exception {
//        // Given
//        when(questionService.existsById(1L)).thenReturn(true);
//
//        // When & Then
//        mockMvc.perform(delete("/api/v1/questions/{id}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("QUESTION deleted successfully"));
//    }
//
//    @Test
//    @DisplayName("Should return error when deleting non-existent question")
//    void deleteQuestion_ShouldReturnError_WhenQuestionNotExists() throws Exception {
//        // Given
//        when(questionService.existsById(999L)).thenReturn(false);
//
//        // When & Then
//        mockMvc.perform(delete("/api/v1/questions/{id}", 999L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("QUESTION with id: 999 not found"));
//    }
//
//    @Test
//    @DisplayName("Should check if question exists")
//    void existsById_ShouldReturnExistenceStatus() throws Exception {
//        // Given
//        when(questionService.existsById(1L)).thenReturn(true);
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/questions/exists/{id}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data.exists").value(true));
//    }
//
//    @Test
//    @DisplayName("Should get total count of questions")
//    void countAllQuestions_ShouldReturnCount() throws Exception {
//        // Given
//        when(questionService.count()).thenReturn(10L);
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/questions/count"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data.count").value(10));
//    }
//
//    @Test
//    @DisplayName("Should create multiple questions in batch")
//    void createQuestions_ShouldReturnCreatedQuestions() throws Exception {
//        // Given
//        List<QuestionRequestDto> requestList = Arrays.asList(sampleRequestDto);
//        List<QuestionEntity> entityList = Arrays.asList(sampleQuestion);
//        List<QuestionResponseDto> responseList = Arrays.asList(sampleResponseDto);
//
//        when(questionMapper.listRequestToListEntity(any())).thenReturn(entityList);
//        when(questionService.saveAll(any())).thenReturn(entityList);
//        when(questionMapper.listEntityToListResponse(any())).thenReturn(responseList);
//
//        // When & Then
//        mockMvc.perform(post("/api/v1/questions/batch")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestList)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data").isArray())
//                .andExpect(jsonPath("$.data[0].text").value("What is Java?"));
//    }
//}