package com.quizplatform.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Quiz Service Application
 * 
 * Microservice responsible for quiz management and administration.
 * Handles CRUD operations for quizzes, question banks, and quiz publishing.
 */
@SpringBootApplication
public class QuizServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizServiceApplication.class, args);
    }
}