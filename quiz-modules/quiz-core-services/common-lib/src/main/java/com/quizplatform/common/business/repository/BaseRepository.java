package com.quizplatform.common.business.repository;

import com.quizplatform.common.business.domain.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity<ID>, ID> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> { }
