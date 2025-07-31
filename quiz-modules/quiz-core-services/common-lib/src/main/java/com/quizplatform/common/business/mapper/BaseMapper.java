package com.quizplatform.common.business.mapper;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.common.business.domain.dto.response.BaseListResponse;
import com.quizplatform.common.business.domain.entity.BaseEntity;
import com.quizplatform.common.business.domain.entity.BasePublicEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Common operations Mapper interface that defines the basic mapping operations.
 *
 * @param <E> The entity type managed by this controller.
 * @param <RequestDTO> The DTO type for incoming requests (Query/Request).
 * @param <ResponseDTO> The DTO type for outgoing responses (Response/Status).
 */
public interface BaseMapper<E extends BaseEntity<?>, RequestDTO extends BaseDto, ResponseDTO extends BaseDto, FilterDTO extends BaseDto> {

    ResponseDTO entityToResponse(E entity);
    E requestToEntity(RequestDTO dto);
    List<ResponseDTO> listEntityToListResponse(List<E> list);
    List<E> listRequestToListEntity(List<RequestDTO> list);

    void updateEntityFromRequest(RequestDTO dto, E entity);

    default UUID extractPublicId(E entity) {
        if (entity instanceof BasePublicEntity<?> publicEntity) {
            return publicEntity.getPublicId();
        }
        throw new UnsupportedOperationException(
                String.format("Entity does not support public ID: %s",entity.getClass().getSimpleName())
        );
    }

    default BaseListResponse<ResponseDTO> pageToBaseListResponse(Page<E> page) {
        List<ResponseDTO> content = page.getContent().stream()
                .map(this::entityToResponse)
                .toList();

        return BaseListResponse.success(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

}
