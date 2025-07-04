//package com.quizplatform.quiz.domain.dto;
//
//import java.util.List;
//
//public record Question(
//    String id,
//    String text,
//    QuestionType type,
//    List<String> options,
//    String correctAnswer,
//    Integer points,
//    String explanation
//) {
//
//    // Compact constructor for validation
//    public Question {
//        if (text == null || text.trim().isEmpty()) {
//            throw new IllegalArgumentException("Question text cannot be null or empty");
//        }
//        if (type == null) {
//            throw new IllegalArgumentException("Question type cannot be null");
//        }
//        if (points == null || points < 1) {
//            throw new IllegalArgumentException("Question points must be at least 1");
//        }
//    }
//
//    // Factory method for multiple choice question
//    public static Question createMultipleChoice(String text, List<String> options, String correctAnswer) {
//        return new Question(
//            java.util.UUID.randomUUID().toString(),
//            text,
//            QuestionType.MULTIPLE_CHOICE,
//            options,
//            correctAnswer,
//            1,
//            null
//        );
//    }
//
//    // Factory method for true/false question
//    public static Question createTrueFalse(String text, boolean correctAnswer) {
//        return new Question(
//            java.util.UUID.randomUUID().toString(),
//            text,
//            QuestionType.TRUE_FALSE,
//            List.of("True", "False"),
//            String.valueOf(correctAnswer),
//            1,
//            null
//        );
//    }
//
//    // Factory method for text question
//    public static Question createTextQuestion(String text, String correctAnswer) {
//        return new Question(
//            java.util.UUID.randomUUID().toString(),
//            text,
//            QuestionType.TEXT,
//            List.of(),
//            correctAnswer,
//            1,
//            null
//        );
//    }
//
//    /**
//     * Question types supported by the system
//     */
//    public enum QuestionType {
//        MULTIPLE_CHOICE,
//        TRUE_FALSE,
//        TEXT,
//        CODE // for future code questions
//    }
//}