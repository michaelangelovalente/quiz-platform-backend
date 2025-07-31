package com.quizplatform.quiz.business.service;

import com.quizplatform.common.business.service.AbstractBaseService;
import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import com.quizplatform.quiz.business.repository.QuizRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService extends AbstractBaseService<QuizEntity, Long> {

    @Getter private final QuizRepository repository;

    @Override
    public Page<QuizEntity> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public <R> Optional<R> findAndTransform(Long aLong, Function<QuizEntity, R> transformer) {
        return Optional.empty();
    }
}