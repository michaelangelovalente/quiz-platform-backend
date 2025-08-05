package com.quizplatform.common.business.service;

import com.quizplatform.common.business.domain.entity.BasePublicEntity;
import com.quizplatform.common.business.repository.BasePublicRepository;
import com.quizplatform.common.business.repository.BaseRepository;
import com.quizplatform.common.system.utils.CommonUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Common Service layer for entities that extend BasePublicEntity
 * Includes all BaseEntity operations plus BasePublicEntity-specific operations
 *
 * @param <E>  The entity type managed by this service, extending BasePublicEntity.
 * @param <ID> The type of the entity's identifier.
 */
public abstract class AbstractBasePublicService<E extends BasePublicEntity<ID>, ID>
        extends AbstractBaseService<E, ID>
        implements BaseService<E, ID> {

    /**
     * Smart casting method to get BasePublicRepository from BaseRepository
     * Provides type safety with runtime validation
     */
    @SuppressWarnings("unchecked")
    protected BasePublicRepository<E, ID> getPublicRepository() {
        BaseRepository<E, ID> repo = getRepository();
        if (repo instanceof BasePublicRepository) {
            return (BasePublicRepository<E, ID>) repo;
        }
        throw new IllegalStateException(
                "Repository must implement BasePublicRepository for BasePublicEntity services. " +
                "Found: " + repo.getClass().getSimpleName()
        );
    }

    @Transactional
    public Optional<E> findByPublicId(UUID publicId) {
        Objects.requireNonNull(publicId, "Public Id cannot be null");
        return getPublicRepository().findByPublicId(publicId)
                .map(entity -> (E) entity);
    }

    @Transactional
    public void deleteByPublicId(UUID publicId) {
        if (!existsByPublicId(publicId)) {
            throw new EntityNotFoundException(
                    String.format("Cannot delete - Entity not found with publicId: %s", publicId)
            );
        }
        getPublicRepository().deleteByPublicId(publicId);
    }

    public int deleteAllByPublicIds(List<UUID> publicIds) {
        if(CommonUtils.isEmptyOrNull(publicIds)){
            throw new NullPointerException("List of PublicIds cannot be null for batch deletion");
        }
        publicIds.forEach(publicId -> Objects.requireNonNull(publicId, "Public ID cannot be null for batch deletion operation"));
        List<E> entitiesToDelete = getPublicRepository().findAllByPublicIdIn(publicIds);
        getPublicRepository().deleteAll(entitiesToDelete);
        return entitiesToDelete.size();
    }

    public boolean existsByPublicId(UUID publicId) {
        return getPublicRepository().existsByPublicId(publicId);
    }
}