package com.quizplatform.quiz.business.domain.entity;

import com.quizplatform.quiz.business.domain.enums.QuestionTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;


@Entity
@Table(name = "questions")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionTypeEnum type;

    // For simple collections of basic types, @ElementCollection?
    // Creates a separate table (question_options) to hold the options.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option")
    private List<String> options;

    //    @Column(nullable = false, columnDefinition = "TEXT")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "question_correct_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "correct_answer", nullable = false)
    private List<String> correctAnswer;

    @Column(nullable = false)
    private Integer points;

    @Column(columnDefinition = "TEXT")
    private String explanation;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id")
    private QuizEntity quiz;
}
