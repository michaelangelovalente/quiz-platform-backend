package com.quizplatform.results.business.repository;

import com.quizplatform.results.business.domain.entity.ResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultRepository extends JpaRepository<ResultEntity, Long> {
    Optional<ResultEntity> findBySessionId(Long sessionId);
}
