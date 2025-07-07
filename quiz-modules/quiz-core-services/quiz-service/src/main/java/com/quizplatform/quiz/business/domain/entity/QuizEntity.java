package com.quizplatform.quiz.business.domain.entity;

import com.quizplatform.quiz.business.domain.enums.QuizDifficultyEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    // Tells Persistence layer to expect UUID (TODO: now handled by DB. Eval if better to handle App layer ?)
    @Column(name = "public_id", updatable = false, nullable = false, unique = true)
    private UUID publicId;

    @Column(nullable = false, unique = true)
    private String title;

    @Column
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizDifficultyEnum difficulty;

    //    @Column(columnDefinition = "TEXT")
    @Column(length = 500)
    private String description;


    @Column(nullable = false)
    private Integer timeLimit;

    @Column(nullable = false)
    private Integer passingScore;

    @Column(nullable = false)
    private String status;

    private String createdBy;

    // @Builder.Default used to initialize collections -->  avoid NullPointerExceptions
    @Builder.Default
    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<QuestionEntity> questions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, updatable = false)
    private OffsetDateTime updatedAt;

    // Relationship helper methods
    public void addQuestion(QuestionEntity question) {
        this.questions.add(question);
        question.setQuiz(this);
    }

    public void removeQuestion(QuestionEntity question) {
        this.questions.remove(question);
        question.setQuiz(null);
    }

    // Overrided the generated setter for questions to maintain encapsulation
    public void setQuestions(List<QuestionEntity> questions) {
        this.questions.clear();
        if (questions != null) {
            questions.forEach(this::addQuestion);
        }
    }

}
