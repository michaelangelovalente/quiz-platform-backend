package com.quizplatform.common.business.service;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.common.business.domain.entity.BaseEntity;
import com.quizplatform.common.business.domain.entity.BasePublicEntity;
import com.quizplatform.common.business.repository.BasePublicRepository;
import com.quizplatform.common.business.repository.BaseRepository;
import com.quizplatform.common.system.utils.CommonUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Common Service layer defines common business behavior/logicc throughout quiq platform
 *
 * @param <E>  The entity type managed by this controller.
 * @param <ID> The type of the entity's identifier.
 */
public abstract class AbstractBaseService<E extends BaseEntity<ID>, ID>
        implements BaseService<E, ID> {

    protected abstract BaseRepository<E, ID> getRepository();

    @Override
    @Transactional
    public E save(E entity) {
        return getRepository().save(entity);
    }

    @Override
    @Transactional
    public List<E> saveAll(List<E> entities) {
        return getRepository().saveAll(entities);
    }

    @Override
    public Optional<E> findById(ID id) {
        return getRepository().findById(id);
    }


    @Override
    public Optional<BasePublicEntity<ID>> findByPublicId(UUID publicId) {
        Objects.requireNonNull(publicId, "Public Id cannot be null");
        return this.getBasePublicRepository().findByPublicId(publicId);
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    /**
     * Daefault implementation - override in concrete services for custom filtering
     * ---------------------------------
     * Default behavior - just return all
     * this method in concrete services to implement filtering
     */
    @Override
    @Transactional
    public <FilterDTO extends BaseDto> Page<E> findAllWithFilter(
            FilterDTO filter,
            Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    public <FilterDTO extends BaseDto> List<E> findAllWithFilter(FilterDTO filter) {
        return getRepository().findAll();
    }

    @Override
    @Transactional
    public E update(ID id, Consumer<E> updateFunction) {
        return findById(id)
                .map(entity -> {
                    updateFunction.accept(entity);
                    return save(entity);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        "Entity not found with id: " + id));
    }


    @Override
    @Transactional
    public void deleteById(ID id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException(
                    "Cannot delete - Entity not found with id: " + id
            );
        }
        getRepository().deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByPublicId(UUID publicId) {
        if (!existsByPublicId(publicId)) {
            throw new EntityNotFoundException(
                    "Cannot delete - Entity not found with publicId: " + publicId
            );
        }
        getBasePublicRepository().deleteByPublicId(publicId);
    }

    @Override
    @Transactional
    public int deleteAllByIds(List<ID> ids) {
        List<E> entitiesToDelete = getRepository().findAllById(ids);
        getRepository().deleteAll(entitiesToDelete);
        return entitiesToDelete.size();
    }

    @Override
    public int deleteAllByPublicIds(List<UUID> publicIds) {
        if(CommonUtils.isEmptyOrNull(publicIds)){
            throw new NullPointerException("List of PublicIds cannot be null for batch deletion");
        }
        publicIds.forEach(publicId -> Objects.requireNonNull(publicId, "Public ID cannot be null for batch deletion operation"));
        List<BasePublicEntity<ID>> entitiesToDelete = getBasePublicRepository().findAllByPublicIdIn(publicIds);
        getBasePublicRepository().deleteAll(entitiesToDelete);
        return entitiesToDelete.size();
    }

    @Override
    public boolean existsById(ID id) {
        return getRepository().existsById(id);
    }

    @Override
    public boolean existsByPublicId(UUID publicId) {
        return getBasePublicRepository().existsByPublicId(publicId);
    }

    @Override
    public long count() {
        return getRepository().count();
    }

    @Override
    @Transactional
    public <FilterDTO extends BaseDto> long countWithFilter(FilterDTO filter) {
        // Default implementation - count all
        // Override in concrete services if needeed
        return count();
    }

    @Override
    @Transactional
    public <R> Optional<R> findAndTransform(ID id, Function<E, R> transformer) {
        return findById(id).map(transformer);
    }


    // Helper methods
    @SuppressWarnings("unchecked")
    public BasePublicRepository<BasePublicEntity<ID>, ID> getBasePublicRepository() {
        return Optional.of(getRepository())
                .filter(BasePublicRepository.class::isInstance)
                .map(BasePublicRepository.class::cast)
                .orElseThrow();
    }
}