package com.quizplatform.common.business.repository;

import com.quizplatform.common.business.domain.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity<ID>, ID> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {
//    List<T> findAllByCreatedAtAfter(Instant date);

//    @Modifying
//    @Query("UPDATE #{#entityName} e SET e.updatedAt = :now WHERE e.id = :id")
//    void touch(@Param("id") ID id, @Param("now") Instant now);
}
