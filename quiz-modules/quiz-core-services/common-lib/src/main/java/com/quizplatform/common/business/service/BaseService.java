package com.quizplatform.common.business.service;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.common.business.domain.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * Common Service layer defines common business operations that must be implemented for quiq platform
 *
 * @param <E>  The entity type managed by this controller.
 * @param <ID> The type of the entity's identifier.
 */
public interface BaseService<E extends BaseEntity<ID>, ID> {

    @Transactional
    E save(E entity);

    @Transactional
    List<E> saveAll(List<E> entities);


    @Transactional(readOnly = true)
    Optional<E> findById(ID id);

//    @Transactional(readOnly = true)
//   Optional<? extends BasePublicEntity<ID>> findByPublicId(UUID publicId);


    @Transactional(readOnly = true)
    Page<E> findAll(Pageable pageable);

    @Transactional(readOnly = true)
    <FilterDTO extends BaseDto> Page<E> findAllWithFilter(FilterDTO filter, Pageable pageable);

    @Transactional(readOnly = true)
    <FilterDTO extends BaseDto> List<E> findAllWithFilter(FilterDTO filter);


    @Transactional
    E update(ID id, Consumer<E> updateFunction);

//    @Transactional
//    E update(UUID publicId, Consumer<E> updateFunction);

    @Transactional
    void deleteById(ID id);

//    @Transactional
//    void deleteByPublicId(UUID id);

    @Transactional
    int deleteAllByIds(List<ID> ids);

//    @Transactional
//    int deleteAllByPublicIds(List<UUID> ids);

    @Transactional(readOnly = true)
    boolean existsById(ID id);

//    @Transactional(readOnly = true)
//    boolean existsByPublicId(UUID id);

    @Transactional(readOnly = true)
    long count();

    @Transactional(readOnly = true)
    <FilterDTO extends BaseDto> long countWithFilter(FilterDTO filter);

    @Transactional
    <R> Optional<R> findAndTransform(ID id, Function<E, R> transformer);
}
