package com.quizplatform.quiz.business.repository;

import com.quizplatform.common.business.repository.BaseRepository;
import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends BaseRepository<QuizEntity, Long> { }