package com.quizplatform.quiz.business.controller;

import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import com.quizplatform.quiz.business.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/quizzes")
@CrossOrigin(origins = "*") // For development - should be configured properly in production
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Health check for quiz service
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "service", "quiz-service",
            "timestamp", LocalDateTime.now(),
            "version", "0.0.1-SNAPSHOT"
        );
    }

    @GetMapping
    public ResponseEntity<List<QuizEntity>> getAllQuizzes() {
        List<QuizEntity> quizzes = quizService.findAll();
        return ResponseEntity.ok(quizzes);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<QuizEntity> getQuizEntityById(@PathVariable String id) {
//        Optional<QuizEntity> quiz = quizService.findById(id);
//        return quiz.map(ResponseEntity::ok)
//                  .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    public ResponseEntity<QuizEntity> createQuizEntity(@RequestBody QuizEntity quiz) {
//        try {
//            QuizEntity savedQuizEntity = quizService.save(quiz);
//            return ResponseEntity.status(HttpStatus.CREATED).body(savedQuizEntity);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<QuizEntity> updateQuizEntity(@PathVariable String id, @RequestBody QuizEntity quiz) {
//        if (quizService.existsById(id)) {
//            return ResponseEntity.notFound().build();
//        }
//
//        quiz.setId(id);
//        QuizEntity updatedQuizEntity = quizService.save(quiz);
//        return ResponseEntity.ok(updatedQuizEntity);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteQuizEntity(@PathVariable String id) {
//        if (quizService.existsById(id)) {
//            return ResponseEntity.notFound().build();
//        }
//
//        quizService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/stats")
//    public ResponseEntity<Map<String, Object>> getQuizEntityStats() {
//        long totalQuizEntityzes = quizService.count();
//        return ResponseEntity.ok(Map.of(
//            "totalQuizEntityzes", totalQuizEntityzes,
//            "timestamp", LocalDateTime.now()
//        ));
//    }
}