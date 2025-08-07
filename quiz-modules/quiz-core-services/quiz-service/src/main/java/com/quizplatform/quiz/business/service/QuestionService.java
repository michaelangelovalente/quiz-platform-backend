package com.quizplatform.quiz.business.service;

import com.quizplatform.common.business.service.AbstractBaseService;
import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
import com.quizplatform.quiz.business.domain.enums.QuestionTypeEnum;
import com.quizplatform.quiz.business.repository.QuestionRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionService extends AbstractBaseService<QuestionEntity, Long> {
    @Getter
    private final QuestionRepository repository;

    @Transactional(readOnly = true)
    public List<QuestionEntity> findByQuizId(Long quizId) {
        log.debug("Finding questions for quiz with ID: {}", quizId);
        return repository.findByQuizId(quizId);
    }

    @Transactional(readOnly = true)
    public List<QuestionEntity> findByType(QuestionTypeEnum type) {
        log.debug("Finding questions by type: {}", type);
        return repository.findByType(type);
    }

    @Transactional(readOnly = true)
    public Long countByQuizId(Long quizId) {
        log.debug("Counting questions for quiz with ID: {}", quizId);
        return repository.countByQuizId(quizId);
    }

    @Transactional(readOnly = true)
    public List<QuestionEntity> findByMinimumPoints(Integer minPoints) {
        log.debug("Finding questions with minimum points: {}", minPoints);
        return repository.findByMinimumPoints(minPoints);
    }

    @Transactional(readOnly = true)
    public List<QuestionEntity> findByQuizIdAndType(Long quizId, QuestionTypeEnum type) {
        log.debug("Finding questions for quiz ID: {} and type: {}", quizId, type);
        return repository.findByQuizIdAndType(quizId, type);
    }

    @Transactional(readOnly = true)
    public Integer getTotalPointsByQuizId(Long quizId) {
        log.debug("Calculating total points for quiz with ID: {}", quizId);
        Integer totalPoints = repository.getTotalPointsByQuizId(quizId);
        return totalPoints != null ? totalPoints : 0;
    }

    @Transactional
    public QuestionEntity validateAndSave(QuestionEntity question) {
        log.info("Validating and saving question: {}", question.getText());
        
        if (question.getPoints() == null || question.getPoints() <= 0) {
            throw new IllegalArgumentException("Question points must be greater than 0");
        }
        
        if (question.getText() == null || question.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        
        if (question.getType() == null) {
            throw new IllegalArgumentException("Question type must be specified");
        }
        
        if (question.getCorrectAnswer() == null || question.getCorrectAnswer().isEmpty()) {
            throw new IllegalArgumentException("Question must have at least one correct answer");
        }
        
        return repository.save(question);
    }

    @Transactional
    public List<QuestionEntity> validateAndSaveAll(List<QuestionEntity> questions) {
        log.info("Validating and saving {} questions", questions.size());
        
        questions.forEach(this::validateQuestion);
        
        return repository.saveAll(questions);
    }

    private void validateQuestion(QuestionEntity question) {
        if (question.getPoints() == null || question.getPoints() <= 0) {
            throw new IllegalArgumentException("Question points must be greater than 0");
        }
        
        if (question.getText() == null || question.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        
        if (question.getType() == null) {
            throw new IllegalArgumentException("Question type must be specified");
        }
        
        if (question.getCorrectAnswer() == null || question.getCorrectAnswer().isEmpty()) {
            throw new IllegalArgumentException("Question must have at least one correct answer");
        }
    }
}