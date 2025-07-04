//package com.quizplatform.quiz.domain.dto;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//public record Quiz(
//    String id,
//    String title,
//    String description,
//    String difficulty,
//    Integer timeLimit,
//    Integer passingScore,
//    List<Question> questions,
//    String status,
//    String createdBy,
//    LocalDateTime createdAt,
//    LocalDateTime updatedAt
//) {
//
//    // Compact constructor for validation
//    public Quiz {
//        if (title == null || title.trim().isEmpty()) {
//            throw new IllegalArgumentException("Quiz title cannot be null or empty");
//        }
//        if (timeLimit != null && (timeLimit < 1 || timeLimit > 180)) {
//            throw new IllegalArgumentException("Time limit must be between 1 and 180 minutes");
//        }
//        if (passingScore != null && (passingScore < 0 || passingScore > 100)) {
//            throw new IllegalArgumentException("Passing score must be between 0 and 100");
//        }
//    }
//
//    // Factory method for creating new quiz
//    public static Quiz createNew(String title, String description, String difficulty) {
//        return new Quiz(
//            java.util.UUID.randomUUID().toString(),
//            title,
//            description,
//            difficulty != null ? difficulty : "MEDIUM",
//            30, // default 30 minutes
//            70, // default 70% passing score
//            List.of(),
//            "DRAFT",
//            "system", // will be replaced with actual user
//            LocalDateTime.now(),
//            LocalDateTime.now()
//        );
//    }
//
//    // Method to create updated version
//    public Quiz withUpdatedTimestamp() {
//        return new Quiz(id, title, description, difficulty, timeLimit,
//                       passingScore, questions, status, createdBy, createdAt, LocalDateTime.now());
//    }
//
//    // Method to update status
//    public Quiz withStatus(String newStatus) {
//        return new Quiz(id, title, description, difficulty, timeLimit,
//                       passingScore, questions, newStatus, createdBy, createdAt, LocalDateTime.now());
//    }
//
//    // Method to update ID (for persistence)
//    public Quiz withId(String newId) {
//        return new Quiz(newId, title, description, difficulty, timeLimit,
//                       passingScore, questions, status, createdBy, createdAt, updatedAt);
//    }
//
//    // Method to set ID for updates
//    public Quiz setId(String newId) {
//        return withId(newId);
//    }
//}