package com.quizplatform.quiz.business.service;

import com.quizplatform.common.business.service.AbstractBaseService;
import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
import com.quizplatform.quiz.business.repository.QuestionRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionService extends AbstractBaseService<QuestionEntity, Long> {
    @Getter
    private final QuestionRepository repository;
}