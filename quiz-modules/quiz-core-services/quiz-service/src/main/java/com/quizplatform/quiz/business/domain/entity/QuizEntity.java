package com.quizplatform.quiz.business.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="quizzes")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Quiz title cannot be null or empty")
    @Column(nullable = false, unique = true)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String difficulty;

    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Max(value = 180, message = "Time limit must not exceed 180 minutes")
    @Column(nullable = false)
    private Integer timeLimit;

    @Min(value = 0, message = "Passing score must be at least 0")
    @Max(value = 100, message = "Passing score must not exceed 100")
    @Column(nullable = false)
    private Integer passingScore;

    @Column(nullable = false)
    private String status;

    private String createdBy;

    // @Builder.Default used to initialize collections -->  avoid NullPointerExceptions
    @Builder.Default
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuestionEntity> questions = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relationship helper methods
    public void addQuestion(QuestionEntity question) {
        this.questions.add(question);
        question.setQuiz(this);
    }

    public void removeQuestion(QuestionEntity question) {
        this.questions.remove(question);
        question.setQuiz(null);
    }

    // Override the generated setter for questions to maintain encapsulation
    public void setQuestions(List<QuestionEntity> questions) {
        this.questions.clear();
        if (questions != null) {
            questions.forEach(this::addQuestion);
        }
    }

}
