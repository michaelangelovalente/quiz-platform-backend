package com.quizplatform.common.business.repository;

import com.quizplatform.common.business.domain.entity.BasePublicEntity;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface BasePublicRepository<E extends BasePublicEntity<ID>, ID> extends BaseRepository<E, ID> {
    Optional<E> findByPublicId(UUID publicId);
    void deleteByPublicId(UUID publicId);
    boolean existsByPublicId(UUID publicId);

    // Batch ops
    void deleteAllByPublicIdIn(List<UUID> publicIds);
    List<E> findAllByPublicIdIn(List<UUID> publicIds);

}
