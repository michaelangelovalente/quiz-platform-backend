package com.quizplatform.common.business.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Common Base Entity that exposes public slug/uuid
 * Use for Main Entities in a module for request instead of the Entity ID
 *  -----
 *  Example usage:
 *  GET /quiz/{publicID}: Correct
 *  GET /quiz/{id}: Incorrect -> exposes  system info
 *
 * @param <ID> The type of the entity's identifier.
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BasePublicEntity<ID>  extends BaseEntity<ID> {
    @Builder.Default
    @Column(name = "public_id", unique = true, nullable = false, updatable = false)
    private UUID publicId = UUID.randomUUID();
}