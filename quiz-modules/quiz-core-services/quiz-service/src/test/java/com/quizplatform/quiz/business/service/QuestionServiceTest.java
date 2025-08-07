package com.quizplatform.quiz.business.service;

import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import com.quizplatform.quiz.business.domain.enums.QuestionTypeEnum;
import com.quizplatform.quiz.business.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    private QuestionEntity sampleQuestion;
    private QuizEntity sampleQuiz;

    @BeforeEach
    void setUp() {
        sampleQuiz = QuizEntity.builder()
                .id(1L)
                .title("Java Basics")
                .build();

        sampleQuestion = QuestionEntity.builder()
                .id(1L)
                .text("What is Java?")
                .type(QuestionTypeEnum.MULTIPLE_CHOICE)
                .options(Arrays.asList("Programming Language", "Coffee", "Island"))
                .correctAnswer(Arrays.asList("Programming Language"))
                .points(10)
                .explanation("Java is a programming language")
                .quiz(sampleQuiz)
                .build();
    }

    @Test
    @DisplayName("Should find question by ID successfully")
    void findById_ShouldReturnQuestion_WhenQuestionExists() {
        // Given
        when(questionRepository.findById(1L)).thenReturn(Optional.of(sampleQuestion));

        // When
        Optional<QuestionEntity> result = questionService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getText()).isEqualTo("What is Java?");
        assertThat(result.get().getType()).isEqualTo(QuestionTypeEnum.MULTIPLE_CHOICE);
        verify(questionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when question not found")
    void findById_ShouldReturnEmpty_WhenQuestionNotExists() {
        // Given
        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<QuestionEntity> result = questionService.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(questionRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find questions by quiz ID")
    void findByQuizId_ShouldReturnQuestions_WhenQuestionsExist() {
        // Given
        List<QuestionEntity> expectedQuestions = Arrays.asList(sampleQuestion);
        when(questionRepository.findByQuizId(1L)).thenReturn(expectedQuestions);

        // When
        List<QuestionEntity> result = questionService.findByQuizId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getText()).isEqualTo("What is Java?");
        verify(questionRepository).findByQuizId(1L);
    }

    @Test
    @DisplayName("Should find questions by type")
    void findByType_ShouldReturnQuestions_WhenQuestionsExist() {
        // Given
        List<QuestionEntity> expectedQuestions = Arrays.asList(sampleQuestion);
        when(questionRepository.findByType(QuestionTypeEnum.MULTIPLE_CHOICE)).thenReturn(expectedQuestions);

        // When
        List<QuestionEntity> result = questionService.findByType(QuestionTypeEnum.MULTIPLE_CHOICE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(QuestionTypeEnum.MULTIPLE_CHOICE);
        verify(questionRepository).findByType(QuestionTypeEnum.MULTIPLE_CHOICE);
    }

    @Test
    @DisplayName("Should count questions by quiz ID")
    void countByQuizId_ShouldReturnCount_WhenQuestionsExist() {
        // Given
        when(questionRepository.countByQuizId(1L)).thenReturn(5L);

        // When
        Long count = questionService.countByQuizId(1L);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(questionRepository).countByQuizId(1L);
    }

    @Test
    @DisplayName("Should get total points by quiz ID")
    void getTotalPointsByQuizId_ShouldReturnTotalPoints() {
        // Given
        when(questionRepository.getTotalPointsByQuizId(1L)).thenReturn(100);

        // When
        Integer totalPoints = questionService.getTotalPointsByQuizId(1L);

        // Then
        assertThat(totalPoints).isEqualTo(100);
        verify(questionRepository).getTotalPointsByQuizId(1L);
    }

    @Test
    @DisplayName("Should save question successfully")
    void save_ShouldReturnSavedQuestion() {
        // Given
        when(questionRepository.save(any(QuestionEntity.class))).thenReturn(sampleQuestion);

        // When
        QuestionEntity result = questionService.save(sampleQuestion);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("What is Java?");
        verify(questionRepository).save(sampleQuestion);
    }

    @Test
    @DisplayName("Should find questions by quiz ID and type")
    void findByQuizIdAndType_ShouldReturnFilteredQuestions() {
        // Given
        List<QuestionEntity> expectedQuestions = Arrays.asList(sampleQuestion);
        when(questionRepository.findByQuizIdAndType(1L, QuestionTypeEnum.MULTIPLE_CHOICE))
                .thenReturn(expectedQuestions);

        // When
        List<QuestionEntity> result = questionService.findByQuizIdAndType(1L, QuestionTypeEnum.MULTIPLE_CHOICE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(QuestionTypeEnum.MULTIPLE_CHOICE);
        verify(questionRepository).findByQuizIdAndType(1L, QuestionTypeEnum.MULTIPLE_CHOICE);
    }

    @Test
    @DisplayName("Should find questions by minimum points")
    void findByMinimumPoints_ShouldReturnQuestionsAboveThreshold() {
        // Given
        List<QuestionEntity> expectedQuestions = Arrays.asList(sampleQuestion);
        when(questionRepository.findByMinimumPoints(5)).thenReturn(expectedQuestions);

        // When
        List<QuestionEntity> result = questionService.findByMinimumPoints(5);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPoints()).isGreaterThanOrEqualTo(5);
        verify(questionRepository).findByMinimumPoints(5);
    }
}