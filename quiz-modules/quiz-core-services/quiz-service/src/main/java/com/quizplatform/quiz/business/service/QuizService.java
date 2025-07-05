package com.quizplatform.quiz.business.service;

import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
//        UUID id = quiz.getId();
//        if (Objects.isNull(id)) {
//            // Create new quiz
//            QuizEntity newQuizEntity = QuizEntity.createNew(quiz.getTitle(), quiz.description(), quiz.difficulty())
//                    .withUpdatedTimestamp();
//            quizStorage.put(newQuizEntity.id(), newQuizEntity);
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
                .id(UUID.randomUUID())
                .title("Java Basics")
                .description("Test your knowledge of Java fundamentals")
                .difficulty("BEGINNER")
                .timeLimit(30)
                .passingScore(70)
                .status("PUBLISHED")
                .createdBy("admin-1")
                .questions(List.of())
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        quizStorage.put(quiz1.getId(), quiz1);

        // Create sample quiz 2
        QuizEntity quiz2 = QuizEntity.builder()
                .id(UUID.randomUUID())
                .title("Spring Boot Advanced")
                .description("Advanced concepts in Spring Boot framework")
                .difficulty("ADVANCED")
                .timeLimit(45)
                .passingScore(80)
                .questions(List.of())
                .status("DRAFT")
                .createdBy("admin")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        quizStorage.put(quiz2.getId(), quiz2);

        // Create sample quiz 3
        QuizEntity quiz3 = QuizEntity.builder()
                .id(UUID.randomUUID())
                .title("Microservices Architecture")
                .description("Understanding microservices design patterns")
                .difficulty("INTERMEDIATE")
                .timeLimit(60)
                .passingScore(75)
                .questions(List.of())
                .status("PUBLISHED")
                .createdBy("admin")
                .createdAt(LocalDateTime.now().minusHours(3))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();
        quizStorage.put(quiz3.getId(), quiz3);
    }
}