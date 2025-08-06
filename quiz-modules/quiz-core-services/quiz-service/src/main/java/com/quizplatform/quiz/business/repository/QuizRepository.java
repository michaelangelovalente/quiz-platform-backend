package com.quizplatform.quiz.business.repository;

import com.quizplatform.common.business.repository.BasePublicRepository;
import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRepository extends BasePublicRepository<QuizEntity, Long> {
    
    @Query("SELECT q FROM QuizEntity q LEFT JOIN FETCH q.questions WHERE q.publicId = :publicId")
    Optional<QuizEntity> findByPublicId(@Param("publicId") UUID publicId);
    
    @Query("SELECT DISTINCT q FROM QuizEntity q LEFT JOIN FETCH q.questions")
    List<QuizEntity> findAllWithQuestions();
    
}