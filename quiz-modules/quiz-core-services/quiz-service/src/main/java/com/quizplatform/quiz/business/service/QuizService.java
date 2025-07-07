package com.quizplatform.quiz.business.service;

import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import com.quizplatform.quiz.business.domain.enums.QuestionTypeEnum;
import com.quizplatform.quiz.business.domain.enums.QuizDifficultyEnum;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuizService {

    // In-memory storage for first tests
    private final Map<UUID, QuizEntity> quizStorage = new ConcurrentHashMap<>();

    // Initialize with some sample data
    public QuizService() {
        initializeSampleData();
    }

    public List<QuizEntity> findAll() {
        return new ArrayList<>(quizStorage.values());
    }

//    public Optional<QuizEntity> findById(String id) {
//        return Optional.ofNullable(quizStorage.get(id));
//    }
//
//    public QuizEntity save(QuizEntity quiz) {
//        UUID id = quiz.getPublicId();
//        if (Objects.isNull(id)) {
//            // Create new quiz
//            QuizEntity newQuizEntity = QuizEntity.createNew(quiz.getTitle(), quiz.getDescription(), quiz.getDifficulty())
//                    .withUpdatedTimestamp();
//            quizStorage.put(newQuizEntity.getPublicId(), newQuizEntity);
//            return newQuizEntity;
//        } else {
//            // Update existing quiz
//            QuizEntity updatedQuizEntity = quiz.withUpdatedTimestamp();
//            quizStorage.put(id, updatedQuizEntity);
//            return updatedQuizEntity;
//        }
//    }
//
//    public boolean existsById(String id) {
//        return !quizStorage.containsKey(id);
//    }
//
//    public void deleteById(String id) {
//        quizStorage.remove(id);
//    }
//
//    public long count() {
//        return quizStorage.size();
//    }

    /**
     * Initialize sample data for testing
     */
    private void initializeSampleData() {
        // Create sample quiz 1
        QuizEntity quiz1 = QuizEntity.builder()
                .publicId(UUID.randomUUID())
                .title("Java Basics")
                .description("Test your knowledge of Java fundamentals")
                .difficulty(QuizDifficultyEnum.EASY)
                .timeLimit(30)
                .passingScore(70)
                .status("PUBLISHED")
                .createdBy("admin-1")
                .createdAt(OffsetDateTime.now().minusDays(2))
                .updatedAt(OffsetDateTime.now().minusDays(1))
                .build();

        QuestionEntity question1 = QuestionEntity.builder()
                .text("What is the default value of a boolean in Java?")
                .type(QuestionTypeEnum.SINGLE_CHOICE)
                .options(List.of("true", "false", "null", "0"))
                .correctAnswer(List.of("false"))
                .points(10)
                .explanation("The default value for a boolean primitive type is false.")
                .quiz(quiz1)
                .build();
        quiz1.addQuestion(question1);
        quizStorage.put(quiz1.getPublicId(), quiz1);

        // Create sample quiz 2
        QuizEntity quiz2 = QuizEntity.builder()
                .publicId(UUID.randomUUID())
                .title("Spring Boot Advanced")
                .description("Advanced concepts in Spring Boot framework")
                .difficulty(QuizDifficultyEnum.HARD)
                .timeLimit(45)
                .passingScore(80)
                .status("DRAFT")
                .createdBy("admin")
                .createdAt(OffsetDateTime.now().minusHours(2))
                .updatedAt(OffsetDateTime.now().minusHours(1))
                .build();
        quizStorage.put(quiz2.getPublicId(), quiz2);

        // Create sample quiz 3
        QuizEntity quiz3 = QuizEntity.builder()
                .publicId(UUID.randomUUID())
                .title("Microservices Architecture")
                .description("Understanding microservices design patterns")
                .difficulty(QuizDifficultyEnum.MEDIUM)
                .timeLimit(60)
                .passingScore(75)
                .status("PUBLISHED")
                .createdBy("admin")
                .createdAt(OffsetDateTime.now().minusHours(3))
                .updatedAt(OffsetDateTime.now().minusHours(2))
                .build();
        quizStorage.put(quiz3.getPublicId(), quiz3);
    }
}