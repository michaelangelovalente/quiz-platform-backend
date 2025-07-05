package com.quizplatform.quiz.business.domain.entity;

import com.quizplatform.quiz.business.domain.enums.QuestionTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "questions")
//@EqualsAndHashCode
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Question text cannot be null or empty")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @NotNull(message = "Question type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionTypeEnum type;

    // For simple collections of basic types, @ElementCollection is a good fit.
    // It creates a separate table (question_options) to hold the options.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option")
    private List<String> options;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String correctAnswer;

    @NotNull(message = "Question points must be defined")
    @Min(value = 1, message = "Question points must be at least 1")
    @Column(nullable = false)
    private Integer points;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id")
    private QuizEntity quiz;

}
