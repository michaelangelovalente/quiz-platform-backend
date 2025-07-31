package com.quizplatform.quiz.business.domain.entity;

import com.quizplatform.common.business.domain.entity.BasePublicEntity;
import com.quizplatform.common.system.utils.CommonUtils;
import com.quizplatform.quiz.business.domain.enums.QuizDifficultyEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizEntity extends BasePublicEntity<Long> {

    @Column(nullable = false, unique = true)
    private String title;

    @Column
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizDifficultyEnum difficulty;

    @Column(columnDefinition = "TEXT")
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
    @Column(name = "updated_at", nullable = false)
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

    // Overrided  generated setter for questions to maintain encapsulation
    public void setQuestions(List<QuestionEntity> questions) {
        this.questions.clear();
        if (CommonUtils.nonEmptyNorNull(questions)) {
            questions.forEach(this::addQuestion);
        }
    }

}
