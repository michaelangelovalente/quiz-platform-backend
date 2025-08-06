package com.quizplatform.common.business.api;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.common.business.domain.dto.response.BaseResponse;
import com.quizplatform.common.business.domain.dto.response.GenericResponseDto;
import com.quizplatform.common.business.domain.entity.BasePublicEntity;
import com.quizplatform.common.business.service.AbstractBasePublicService;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Common Controller for entities that extend BasePublicEntity
 * Includes all BaseEntity operations plus BasePublicEntity-specific operations
 *
 * @param <E>           The entity type managed by this controller, extending BasePublicEntity.
 * @param <RequestDTO>  The DTO type for incoming requests (Query/Request).
 * @param <ResponseDTO> The DTO type for outgoing responses (Response/Status).
 * @param <FilterDTO>   The DTO type for filtering operations.
 * @param <ID>          The type of the entity's identifier.
 */
@Slf4j
public abstract class BasePublicController<E extends BasePublicEntity<ID>, ID, RequestDTO extends BaseDto, ResponseDTO extends BaseDto, FilterDTO extends BaseDto>
        extends BaseController<E, ID, RequestDTO, ResponseDTO, FilterDTO> {

    protected abstract AbstractBasePublicService<E, ID> getService();


    public BaseResponse<ResponseDTO> findByPublicId(UUID publicId) {
        log.info("Retrieving resource {} with public ID: {}", getResourceName(), publicId);
        return executeWithErrorHandling(
                () -> getService().findByPublicId(publicId)
                        .map(e -> BaseResponse.success(getMapper().entityToResponse(e)))
                        .orElse(BaseResponse.notFound(String.format("%s %s", getResourceName(), publicId.toString()))),
                String.format("Failed to retrieve %s", getResourceName())
        );
    }

    public BaseResponse<ResponseDTO> updateByPublicId(
            UUID publicId,
            RequestDTO dto) {

        return executeWithErrorHandling(() -> {
            E updated = getService().updateByPublicId(publicId, entity ->
                    getMapper().updateEntityFromRequest(dto, entity)
            );

            log.info("{} updated with id: {}", getResourceName(), publicId);
            return BaseResponse.success(getMapper().entityToResponse(updated));
        }, String.format("Failed to update %s", getResourceName()));
    }

    public BaseResponse<GenericResponseDto> existsByPublic(UUID publicId) {
        return BaseResponse.exists(getService().existsByPublicId(publicId));
    }

//    public BaseResponse<GenericResponseDto> deleteByPublicId(UUID publicId) {
//        return executeWithErrorHandling(() -> {
//            if (!getService().existsByPublicId(publicId)) {
//                return BaseResponse.notFound(String.format("%s with id: %s", getResourceName(), publicId.toString()));
//            }
//
//            getService().deleteByPublicId(publicId);
//            log.info("{} deleted with publicId: {}", getResourceName(), publicId);
//            return BaseResponse.deleted(getResourceName());
//        }, String.format("Failed to delete %s", getResourceName()));
//    }



}